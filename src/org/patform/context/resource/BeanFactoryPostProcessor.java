package org.patform.context.resource;

import org.patform.context.factory.ApplicationContext;
import org.patform.context.factory.ConfigurableListableBeanFactory;
import org.patform.exception.BeansException;

/**
 * @author leber
 * date 2019/10/31
 */
public interface BeanFactoryPostProcessor {

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     * @param beanFactory the bean factory used by the application context
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
