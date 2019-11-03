package org.patform.context.factory;

import org.patform.bean.RootBeanDefinition;
import org.patform.context.util.BeanPostProcessor;
import org.patform.exception.BeansException;
import org.patform.exception.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author leber
 * date 2019/10/19
 */
public abstract class AbstractBeanFactory implements ConfigurableBeanFactory {
    //获取工厂Bean
    public static final String FACTORY_BEAN_PREFIX = "&";

    private BeanFactory parentBeanFactory;

    protected Set<Class<?>> ignoreDependencyTypes = new HashSet<>();

    private List<BeanPostProcessor> beanProcessor = new ArrayList<>();

    private final Map<String, Object> singletonCache = Collections.synchronizedMap(new HashMap<>());

    private Map<String, String> aliasNames = Collections.synchronizedMap(new HashMap<>());


    public AbstractBeanFactory() {
        this.ignoreDependencyTypes.add(BeanFactory.class);
    }

    public AbstractBeanFactory(BeanFactory parentBeanFactory) {
        this();
        this.parentBeanFactory = parentBeanFactory;
    }

    //----------------------------------实现BeanFactory------------------------------------------

    @Override
    public Object getBean(String beanName) {
        String name = transFormsBeanName(beanName);
        Object sharedBean = singletonCache.get(name);
        if (Objects.nonNull(sharedBean)) {
            return getObjectForSharedInstance(name, sharedBean);
        } else {
            //创建bean
            RootBeanDefinition beanDefinition = null;
            try {
                beanDefinition = getBeanDefinition(name);
            } catch (NoSuchBeanDefinitionException ex) {
                if (Objects.nonNull(parentBeanFactory)) {
                    return this.parentBeanFactory.getBean(name);
                }
                throw ex;
            }
            if (beanDefinition.isSingleton()) {
                //创建单例模式
                synchronized (this.singletonCache) {
                    sharedBean = this.singletonCache.get(name);
                    if (Objects.isNull(sharedBean)) {//double check
                        //创建bean 并缓存
                        sharedBean = createBean(name, beanDefinition);
                        this.singletonCache.put(name, sharedBean);
                    }
                }
                return getObjectForSharedInstance(beanName, sharedBean);
            } else {
                //创建原型bean（原型模式）
                return createBean(name, beanDefinition);
            }
        }
    }


    @Override
    public <T> T getBean(String beanName, Class<T> requireType) {
        T bean = (T) getBean(beanName);
        if (requireType.isAssignableFrom(bean.getClass())) {
            return bean;
        }
        throw new NoSuchBeanDefinitionException("not has this type bean definition");
    }

    @Override
    public boolean contains(String beanName) {
        String name = transFormsBeanName(beanName);
        if (singletonCache.containsKey(name)) {
            return true;
        }
        //是否有bean定义
        if (containBeanDefinition(name)) {
            return true;
        }
        //检查父工厂
        if (Objects.nonNull(parentBeanFactory)) {
            return parentBeanFactory.contains(beanName);
        }
        return false;
    }

    @Override
    public boolean isSingleton(String beanName) {
        String name = transFormsBeanName(beanName);
        try {
            Class<?> beanClass;
            boolean isSingleton = false;
            //检查缓存
            Object bean = this.singletonCache.get(name);
            if (Objects.nonNull(bean)) {
                isSingleton = true;
                beanClass = bean.getClass();
            } else {
                //检查定义
                RootBeanDefinition bf = getBeanDefinition(name);
                beanClass = bf.getBeanClass();
                isSingleton = bf.isSingleton();
            }
            //检查是否是工厂bean
            if (FactoryBean.class.isAssignableFrom(beanClass) && !isFactoryDereference(beanName)) {
                FactoryBean factoryBean = (FactoryBean) getBean(FACTORY_BEAN_PREFIX + beanName);
                isSingleton = factoryBean.isSingleton();
            }
            return isSingleton;
        } catch (Exception e) {
            //检查父容器
            if (Objects.nonNull(parentBeanFactory)) {
                return parentBeanFactory.isSingleton(beanName);
            }
            throw e;
        }
    }

    @Override
    public String[] getAliases(String beanName) {

        String name = transFormsBeanName(beanName);
        if (singletonCache.containsKey(name) || containBeanDefinition(name)) {
            ArrayList<String> names = new ArrayList<>();

            for (Map.Entry<String, String> entry : this.aliasNames.entrySet()) {
                if (Objects.equals(entry.getValue(), name)) {
                    names.add(entry.getKey());
                }
            }
            return names.toArray(new String[0]);
        } else {
            if (Objects.nonNull(parentBeanFactory)) {
                return parentBeanFactory.getAliases(beanName);
            }
            throw new NoSuchBeanDefinitionException();
        }
    }


    //----------------------------------实现ConfigurableBeanFactory-------------------------------------------------


    @Override
    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public void ignoreDependencyType(Class type) {
        this.ignoreDependencyTypes.add(type);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanProcessor.add(beanPostProcessor);
    }

    @Override
    public void registerAlias(String beanName, String alias) throws BeansException {
        synchronized (this.aliasNames) {
            String name = aliasNames.get(alias);
            if (Objects.nonNull(name)) {
                throw new BeansException("");
            }
            aliasNames.put(alias, beanName);
        }
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) throws BeansException {
        String name = transFormsBeanName(beanName);
        addSingleton(name, singletonObject);
    }

    protected void addSingleton(String name, Object singletonObject) {
        synchronized (this.singletonCache) {
            Object bean = this.singletonCache.get(name);
            if (Objects.nonNull(bean)) {
                throw new BeansException();
            }
            singletonCache.put(name, singletonObject);
        }
    }

    @Override
    public void destroySingletons() {
        synchronized (this.singletonCache) {
            for (Map.Entry<String, Object> entry : singletonCache.entrySet()) {
                destroySingleton(entry.getKey());
            }
        }

    }

    protected void destroySingleton(String key) {

        Object bean = this.singletonCache.get(key);
        if (Objects.nonNull(bean)) {
            destroySingleton(key, bean);
            singletonCache.remove(key);
        }
    }


    //-----------------------------------工具方法-----------------------------------
    protected String transFormsBeanName(String name) {
        if (Objects.isNull(name)) {
            throw new NoSuchBeanDefinitionException("bean name not be null .");
        }
        if (name.startsWith(FACTORY_BEAN_PREFIX)) {
            return name.substring(FACTORY_BEAN_PREFIX.length());
        }
        //获取单例的名称
        String specialName = aliasNames.get(name);
        return specialName == null ? name : specialName;
    }

    protected boolean isFactoryDereference(String beanName) {
        return beanName.startsWith(FACTORY_BEAN_PREFIX);
    }

    /**
     * 处理工厂bean
     *
     * @param beanName
     * @param sharedBean
     * @return
     */
    protected Object getObjectForSharedInstance(String beanName, Object sharedBean) {
        if (isFactoryDereference(beanName) && !(sharedBean instanceof FactoryBean)) {
            throw new IllegalStateException("bean should be factory bean.");
        }
        if (sharedBean instanceof FactoryBean) {
            FactoryBean factoryBean = (FactoryBean) sharedBean;
            sharedBean = factoryBean.getObject();
        }
        return sharedBean;
    }

    public List<BeanPostProcessor> getBeanProcessor() {
        return beanProcessor;
    }


    //实现HierarchicalBeanFactory

    @Override
    public BeanFactory getParentBeanFactory() {
        return parentBeanFactory;
    }

    protected String[] getSingletonNames() {
        return singletonCache.keySet().toArray(new String[0]);
    }

    public String[] getSingletonNames(Class type) {
        Set keys = this.singletonCache.keySet();
        Set matches = new HashSet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String name = (String) itr.next();
            Object singletonObject = this.singletonCache.get(name);
            if (type == null || type.isAssignableFrom(singletonObject.getClass())) {
                matches.add(name);
            }
        }
        return (String[]) matches.toArray(new String[matches.size()]);
    }

    //-----------------子类需要实现的方法--------------------------

    public abstract RootBeanDefinition getBeanDefinition(String beanName);

    abstract Object createBean(String beanName, RootBeanDefinition beanDefinition);

    abstract boolean containBeanDefinition(String name);

    abstract void destroySingleton(String beanName, Object instance);
}
