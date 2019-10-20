package org.patform.context.factory;

import org.patform.bean.BeanDefinition;
import org.patform.context.util.BeanPostProcessor;
import org.patform.exception.BeansException;

/**
 * @author leber
 * date
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory {

    void setParentBeanFactory(BeanFactory parentBeanFactory);

    void ignoreDependencyType(Class type);

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    void registerAlias(String beanName, String alias) throws BeansException;

    void registerSingleton(String beanName, Object singletonObject) throws BeansException;

    void destroySingletons();
}
