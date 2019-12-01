package org.patform.context.registry;

/**
 * @author leber
 * date 2019-12-01
 */
public abstract class AbstractBeanDefinitionReader {

    /**
     * 一般容器来实现BeanDefinitionRegistry的注册功能
     */
    private BeanDefinitionRegistry beanFactory;

    private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        this.beanFactory = beanFactory;
    }


    public BeanDefinitionRegistry getBeanFactory() {
        return beanFactory;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
}
