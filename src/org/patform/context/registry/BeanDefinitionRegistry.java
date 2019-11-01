package org.patform.context.registry;

import org.patform.bean.BeanDefinition;
import org.patform.exception.BeansException;
import org.patform.exception.NoSuchBeanDefinitionException;

/**
 * @author leber
 * date 2019/10/28
 */
public interface BeanDefinitionRegistry {

    /**
     *
     * @return
     */
    int getBeanDefinitionCount();

    /**
     *
     * @return
     */
    String[] getBeanDefinitionNames();

    /**
     *
     * @param name
     * @return
     */
    boolean containsBeanDefinition(String name);

    /**
     *
     * @param name
     * @return
     * @throws BeansException
     */
    BeanDefinition getBeanDefinition(String name) throws BeansException;

    /**
     * 注册beanDefinition
     * @param name
     * @param beanDefinition
     * @throws BeansException
     */
    void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws BeansException;

    /**
     * 别名
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    String[] getAliases(String name) throws NoSuchBeanDefinitionException;

    /**
     * 注册别名
     * @param name
     * @param alias
     * @throws BeansException
     */
    void registerAlias(String name, String alias) throws BeansException;


}
