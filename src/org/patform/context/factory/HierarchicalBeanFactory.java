package org.patform.context.factory;

/**
 * @author leber
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    /**
     * get parent bean factory
     * @return
     */
    BeanFactory getParentBeanFactory();
}
