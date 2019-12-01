package org.patform.context.factory;

import org.patform.bean.BeanDefinition;
import org.patform.bean.RootBeanDefinition;
import org.patform.bean.wrapper.BeanWrapperImpl;
import org.patform.context.registry.BeanDefinitionRegistry;
import org.patform.exception.BeansException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author leber
 * date 2019/10/28
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    private static Logger logger = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

    private boolean canOverWrite = true;

    //bean定义信息
    private Map<String, BeanDefinition> beanDefinitions = Collections.synchronizedMap(new HashMap<>());

    //
    private List<String> beanDefinitionNames = new ArrayList<>();


    public DefaultListableBeanFactory(BeanFactory parent) {
        super(parent);
    }

    @Override
    protected Object resolveValueIfNecessary(String beanName, RootBeanDefinition beanDefinition, BeanWrapperImpl beanWrapper, String name, Object value) {
        return value;
    }

    @Override
    protected Map<String, Object> findMatchingBeans(Class<?> propertyType) {

        HashMap<String, Object> result = new HashMap<>();
        String[] beanDefinitionNames = getBeanDefinitionNames(propertyType);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = getBean(beanDefinitionName);
            result.put(beanDefinitionName, bean);
        }
        return result;
    }

    @Override
    public RootBeanDefinition getBeanDefinition(String beanName) {
        return (RootBeanDefinition) beanDefinitions.get(beanName);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws BeansException {
        BeanDefinition old = beanDefinitions.get(name);
        if (old != null && !canOverWrite) {
            throw new BeansException("已定义该beanName: " + name);
        }
        beanDefinitions.put(name, beanDefinition);
        beanDefinitionNames.add(name);
    }

    @Override
    boolean containBeanDefinition(String name) {
        return beanDefinitionNames.contains(name);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitions.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionNames.toArray(new String[0]);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitionNames.contains(name);
    }


    @Override
    public String[] getBeanDefinitionNames(Class<?> type) {

        ArrayList<String> matched = new ArrayList<>();
        for (String bdn : beanDefinitionNames) {
            RootBeanDefinition beanDefinition = getBeanDefinition(bdn);
            if (type.isAssignableFrom(beanDefinition.getBeanClass())) {
                matched.add(bdn);
            }
        }
        return matched.toArray(new String[0]);
    }



    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requireType, boolean includeProtoType, boolean includeFactoryBeans) {
        HashMap<String, T> result = new HashMap<>();
        String[] beanDefinitionNames = getBeanDefinitionNames(requireType);
        String[] singletonNames = getSingletonNames(requireType);
        for (String singletonName : singletonNames) {
            result.put(singletonName, getBean(singletonName,requireType));
        }
        return result;
    }


    /**
     * 初始化单例对象
     */
    @Override
    public void preInstantiateSingletons() {

        //
        if (logger.isInfoEnabled()) {
            logger.info("Pre-instantiating singletons in factory [" + this + "]");
        }
        for (String beanDefinitionName : beanDefinitionNames) {
            if (containBeanDefinition(beanDefinitionName)) {
                RootBeanDefinition bd = getBeanDefinition(beanDefinitionName);
                if (bd.isSingleton() && !bd.isLazyInit()) {
                    getBean(beanDefinitionName);
                }
            }
        }

    }
}
