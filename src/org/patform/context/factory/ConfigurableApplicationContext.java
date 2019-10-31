package org.patform.context.factory;


import org.patform.context.factory.exception.ApplicationContextException;
import org.patform.context.resource.BeanFactoryPostProcessor;
import org.patform.exception.BeansException;

/**
 * @author leber
 * date 2019/10/31
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
    /**
     * Set the parent of this application context.
     * <p>Note that the parent shouldn't be changed: It should only be set outside
     * a constructor if it isn't available when an object of this class is created,
     * for example in case of WebApplicationContext setup.
     */
    void setParent(ApplicationContext parent);

    /**
     * Add a new BeanFactoryPostProcessor that will get applied to the internal
     * bean factory of this application context on refresh, before any of the
     * bean definitions get evaluated. To be invoked during context configuration.
     */
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);

    /**
     * Load or refresh the persistent representation of the configuration,
     * which might an XML file, properties file, or relational database schema.
     * @throws BeansException if the bean factory could not be initialized
     */
    void refresh() throws BeansException;

    /**
     * 一般返回DefaultListableBeanFactory实例 作为上下文持有的内部具体容器
     *
     * Return the internal bean factory of this application context.
     * Can be used to access specific functionality of the factory.
     * <p>Note that this is just guaranteed to return a non-null instance
     * <i>after</i> the context has been refreshed at least once.
     * <p>Note: Do not use this to post-process the bean factory; singletons
     * will already have been instantiated before. Use a BeanFactoryPostProcessor
     * to intercept the bean factory setup process before beans get touched.
     */
    ConfigurableListableBeanFactory getBeanFactory();

    /**
     * Close this application context, releasing all resources and locks that the
     * implementation might hold. This includes disposing all cached singleton beans.
     * <p>Note: Does <i>not</i> invoke close on a parent context.
     */
    void close() throws ApplicationContextException;

}
