package org.patform.context.factory;

import org.patform.exception.NoSuchBeanDefinitionException;

/**
 * the special of container for beans.
 *
 * @author leber
 */
public interface BeanFactory {


    Object getBean(String beanName);


    <T> T getBean(String beanName, Class<T> requireType);


    boolean contains(String beanName);


    boolean isSingleton(String beanName);


    String[] getAliases(String name) throws NoSuchBeanDefinitionException;

}
