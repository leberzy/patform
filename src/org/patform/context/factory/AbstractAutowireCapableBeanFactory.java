package org.patform.context.factory;

import org.patform.bean.ConstructorArgumentValues;
import org.patform.bean.MutablePropertyValues;
import org.patform.bean.PropertyValue;
import org.patform.bean.RootBeanDefinition;
import org.patform.bean.ValueHolder;
import org.patform.bean.wrapper.BeanWrapperImpl;
import org.patform.context.util.BeanPostProcessor;
import org.patform.context.util.BeanUtils;
import org.patform.exception.BeansException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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

    private static Logger logger = LoggerFactory.getLogger(AbstractAutowireCapableBeanFactory.class);


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
            //构造注入
            beanWrapper = autowireConstructor(beanName, beanDefinition);
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

    /**
     * 构造注入
     *
     * @param beanName
     * @param beanDefinition
     */
    private BeanWrapperImpl autowireConstructor(String beanName, RootBeanDefinition beanDefinition) {

        ConstructorArgumentValues cavs = beanDefinition.getConstructorArgumentValues();
        ConstructorArgumentValues resolvedValues = new ConstructorArgumentValues();
        int minNumOfArgument = 0;
        if (Objects.nonNull(cavs)) {
            minNumOfArgument = cavs.getNrOfArguments();
            //indexValue
            for (Map.Entry<Integer, ValueHolder> entry : cavs.getIndexedArgumentValues().entrySet()) {
                Integer index = entry.getKey();
                if (index < 0) {
                    throw new BeansException("构造方法参数位置异常 index = " + index);
                }
                if (minNumOfArgument < index) {
                    minNumOfArgument = index + 1;
                }
                resolvedValues.addIndexedArgumentValue(index, entry.getValue().getValue());
            }
            //genericArgumentValue
            for (ValueHolder holder : cavs.getGenericArgumentValues()) {
                resolvedValues.addGenericArgumentValue(holder.getValue(), holder.getType());
            }
        }
        Constructor<?>[] cons = beanDefinition.getBeanClass().getConstructors();
        Arrays.sort(cons, Comparator.comparing(Constructor::getParameterCount, Comparator.reverseOrder()));

        Constructor constructorToUse = null;
        Object[] argsToUse = null;
        int minTypeDiffWeight = Integer.MAX_VALUE;
        for (Constructor<?> con : cons) {

            if (con.getParameterCount() < minNumOfArgument) {
                throw new BeansException("参数个数不匹配");
            }
            Class<?>[] parameterTypes = con.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                ValueHolder holder = resolvedValues.getIndexedArgumentValue(i, parameterTypes[i]);
                if (holder != null) {
                    args[i] = holder.getValue();
                } else {
                    Map<String, Object> matchingBeans = findMatchingBeans(parameterTypes[i]);
                    if (matchingBeans != null && matchingBeans.size() != 1) {
                        throw new BeansException("歧义注入。");
                    } else if (matchingBeans.size() == 1) {
                        Object val = matchingBeans.values().iterator().next();
                        args[i] = val;
                    }
                }
            }
            //
            int diffValWeight = getTypeDifferenceWeight(parameterTypes, args);
            if (diffValWeight < minTypeDiffWeight) {
                minTypeDiffWeight = diffValWeight;
                argsToUse = args;
                constructorToUse = con;
            }
        }
        if (constructorToUse == null) {
            throw new BeansException("未找到合适的初始化构造方法。");
        }
        BeanWrapperImpl bw = new BeanWrapperImpl();
        bw.setWrappedInstance(BeanUtils.instantiateClass(constructorToUse, argsToUse));
        return bw;

    }

    private int getTypeDifferenceWeight(Class<?>[] parameterTypes, Object[] args) {
        int result = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                return Integer.MAX_VALUE;
            }
            if (args[i] != null) {
                Class<?> superclass = args[i].getClass().getSuperclass();
                while (superclass != null) {
                    if (superclass.isAssignableFrom(parameterTypes[i])) {
                        result++;
                    } else {
                        superclass = null;
                    }
                }
            }
        }
        return result;
    }


    /**
     * 调用就初始化方法 该方法木有参数
     *
     * @param bean
     * @param beanName
     * @param beanDefinition
     */
    protected void invokeInitMethods(Object bean, String beanName, RootBeanDefinition beanDefinition) {
        String initMethod = beanDefinition.getInitMethod();
        if (Objects.isNull(initMethod)) {
            return;
        }
        try {

            Method method = bean.getClass().getMethod(initMethod);
            method.invoke(bean);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(beanName + " 调用初始化方法失败.");
        }
    }

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

    /**
     * 销毁bean 调用销毁方法
     * @param beanName
     * @param instance
     */
    @Override
    void destroySingleton(String beanName, Object instance) {
        RootBeanDefinition beanDefinition = getBeanDefinition(beanName);
        String destroyMethod = beanDefinition.getDestroyMethod();
        if (destroyMethod != null) {
            try {
                Method method = instance.getClass().getMethod(destroyMethod);
                method.invoke(instance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param beanClass
     * @param autowireMode
     * @param dependencyCheck
     * @return
     */
    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, int dependencyCheck) {
        RootBeanDefinition db = new RootBeanDefinition(beanClass, autowireMode);
        db.setDependencyCheck(dependencyCheck);
        if (autowireMode == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR) {
            BeanWrapperImpl beanWrapper = autowireConstructor(beanClass.getName(), db);
            return beanWrapper.getInstance();
        }else {
            Object bean = BeanUtils.instantiateClass(beanClass);
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(bean);
            populateBean(beanClass.getName(),db,beanWrapper);
            return bean;
        }
    }

    /**
     *
     * @param existBean
     * @param autowireMode
     * @param dependencyCheck
     */
    @Override
    public void autowireBeanProperties(Object existBean, int autowireMode, int dependencyCheck) {
        RootBeanDefinition rbd = new RootBeanDefinition(existBean.getClass(), autowireMode);
        rbd.setDependencyCheck(dependencyCheck);
        populateBean(existBean.getClass().getName(), rbd, new BeanWrapperImpl(existBean));
    }

    @Override
    public Object applyPostProcessorBeforeInitialization(Object existBean, String name) {
        logger.debug("执行postProcessorBeforeInitialization");
        List<BeanPostProcessor> pps = getBeanProcessor();
        for (BeanPostProcessor pp : pps) {
            pp.postProcessorBeforeInitialization(existBean, name);
        }
        return existBean;
    }

    @Override
    public Object applyPostProcessorAfterInitialization(Object existBean, String name) {
        logger.debug("执行postProcessorAfterInitialization");
        List<BeanPostProcessor> beanProcessor = getBeanProcessor();
        for (BeanPostProcessor beanPostProcessor : beanProcessor) {
            beanPostProcessor.postProcessorAfterInitialization(existBean, name);
        }
        return existBean;
    }


    //-----------------------子类需要实现的方法--------------------------
    protected abstract Map<String, Object> findMatchingBeans(Class<?> propertyType);
}
