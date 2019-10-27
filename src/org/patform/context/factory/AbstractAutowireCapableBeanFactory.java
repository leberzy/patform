package org.patform.context.factory;

import org.patform.bean.MutablePropertyValues;
import org.patform.bean.PropertyValue;
import org.patform.bean.RootBeanDefinition;
import org.patform.bean.wrapper.BeanWrapperImpl;
import org.patform.context.util.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * 实现AbstractBeanFactory及AutowireCapableBeanFactory 的规范
 * 主要包括createBean()创建bean  及 autowire dependency  依赖注入
 *
 * @author leber
 * date 2019/10/27
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {


    @Override
    public RootBeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

    /**
     * 调用方法已实现了单例及原型模式的处理，此处需要实现构造注入，及普通依赖注入，调用Aware事件，以及前置后置处理器
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @Override
    Object createBean(String beanName, RootBeanDefinition beanDefinition) {


        BeanWrapperImpl beanWrapper = null;
        //注入模式为构造注入
        if (beanDefinition.getAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR) {
            //
        } else {
            //名称注入及类型注入 或不进行依赖注入

            beanWrapper = new BeanWrapperImpl(beanDefinition.getBeanClass());
        }
        Object bean = beanWrapper.getInstance();
        //注入
        populateBean(beanName, beanDefinition, beanWrapper);
        try {
            //事件发布及事件监听的调用
            //前置方法
            applyPostProcessorBeforeInitialization(bean, beanName);
            //初始化方法
            invokeInitMethods(bean, beanName, beanDefinition);
            //后置处理方法
            applyPostProcessorAfterInitialization(bean, beanName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    protected abstract void invokeInitMethods(Object bean, String beanName, RootBeanDefinition beanDefinition);

    /**
     * 实现依赖注入
     *
     * @param beanName       beanName
     * @param beanDefinition definition
     * @param beanWrapper    wrapper
     */
    protected void populateBean(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper) {

        MutablePropertyValues pvs = beanDefinition.getPropertyValues();
        MutablePropertyValues deepCopy = new MutablePropertyValues(pvs);
        //注入 仅仅将值包装为propertyValue设置到MutablePropertyValues中
        if (beanDefinition.getAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME) {
            autowireByName(beanName, beanDefinition, beanWrapper, deepCopy);
        } else if (beanDefinition.getAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE) {
            autowireByType(beanName, beanDefinition, beanWrapper, deepCopy);
        }
        //
        dependencyCheck(beanName, beanDefinition, beanWrapper, deepCopy);
        //真正的实现属性设值 【内省的方式】
        applyPropertyValues(beanName, beanDefinition, beanWrapper, deepCopy);


    }

    /**
     * 内省设值
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     * @param pvs
     */
    protected void applyPropertyValues(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper, MutablePropertyValues pvs) {

        if (pvs == null) {
            return;
        }
        //最后检查属性值
        MutablePropertyValues deepCopy = new MutablePropertyValues();
        for (PropertyValue propertyValue : pvs.getPropertyValues()) {
            Object v = resolveValueIfNecessary(beanName, beanDefinition, beanWrapper, propertyValue.getName(), propertyValue.getValue());
            PropertyValue pv = new PropertyValue(propertyValue.getName(), v);
            deepCopy.addPropertyValue(pv);
        }
        //内省设值
        beanWrapper.setPropertyValues(deepCopy);
    }

    protected abstract Object resolveValueIfNecessary(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper, String name, Object value);

    /**
     * 依赖检查 检查注入的值是否在PropertyValue中都能找到
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     * @param deepCopy
     */
    protected void dependencyCheck(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper, MutablePropertyValues deepCopy) {

        //注入类型
        int checkModel = beanDefinition.getDependencyCheck();
        if (checkModel == RootBeanDefinition.DEPENDENCY_CHECK_NO) {
            return;
        }

        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null
                    && !ignoreDependencyTypes.contains(pd.getPropertyType())
                    && deepCopy.getPropertyValue(pd.getName()) == null) {
                boolean simpleProperty = BeanUtils.isSimpleProperty(pd.getPropertyType());
                boolean flag = checkModel == RootBeanDefinition.DEPENDENCY_CHECK_ALL
                        || (checkModel == RootBeanDefinition.DEPENDENCY_CHECK_SIMPLE && simpleProperty)
                        || (checkModel == RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS && !simpleProperty);
                if (flag) {
                    throw new RuntimeException(beanName + " 对应的属性" + pd.getName() + "未找到值注入。");
                }
            }
        }
    }

    /**
     * 按照类型注入
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     * @param deepCopy
     */
    protected void autowireByType(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper, MutablePropertyValues deepCopy) {

        //
        Set<String> propertyNames = unsatisfiedObjectProperties(beanDefinition, beanWrapper);
        for (String propertyName : propertyNames) {
            Class<?> propertyType = beanWrapper.getPropertyDescriptor(propertyName).getPropertyType();
            Map<String, Object> map = findMatchingBeans(propertyType);
            if (map.size() == 1) {
                Set<String> keys = map.keySet();
                String key = keys.iterator().next();
                Object value = map.get(key);
                deepCopy.addPropertyValue(new PropertyValue(propertyName, value));
            } else if (map.size() > 1) {
                throw new RuntimeException("匹配类型找到了多个Bean Instance。。。");
            } else {
                System.out.println("未找到注入的bean");
            }
        }

    }

    protected abstract Map<String, Object> findMatchingBeans(Class<?> propertyType);

    /**
     * 按名称注入时 将对应名称的bean放置在PropertyValues中
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     * @param deepCopy
     */
    protected void autowireByName(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper, MutablePropertyValues deepCopy) {
        //需要注入外部注入的属性
        Set<String> propertyNames = unsatisfiedObjectProperties(beanDefinition, beanWrapper);
        for (String propertyName : propertyNames) {
            if (contains(propertyName)) {
                Object value = getBean(propertyName);
                deepCopy.addPropertyValue(new PropertyValue(propertyName, value));
            }
        }
    }

    /**
     * 需要注入的属性
     *
     * @param beanDefinition
     * @param beanWrapper
     * @return
     */
    private Set<String> unsatisfiedObjectProperties(RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper) {

        TreeSet<String> set = new TreeSet<>();
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            if (Objects.nonNull(pd.getWriteMethod()) && !ignoreDependencyTypes.contains(pd.getPropertyType())
                    && !BeanUtils.isSimpleProperty(pd.getPropertyType())
                    && beanDefinition.getPropertyValues().getPropertyValue(propertyName) == null) {
                set.add(propertyName);
            }
        }
        return set;
    }


    @Override
    boolean containBeanDefinition(String name) {
        return false;
    }

    @Override
    void destroySingleton(String beanName, Object instance) {

    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, int dependencyCheck) {
        return null;
    }

    @Override
    public void autowireBeanProperties(Object existBean, int autowireMode, int dependencyCheck) {

    }

    @Override
    public Object applyPostProcessorBeforeInitialization(Object existBean, String name) {
        return null;
    }

    @Override
    public Object applyPostProcessorAfterInitialization(Object existBean, String name) {
        return null;
    }
}
