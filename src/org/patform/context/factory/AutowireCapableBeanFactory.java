package org.patform.context.factory;

/**
 * @author leber
 */
public interface AutowireCapableBeanFactory extends BeanFactory {


    Object autowire(Class<?> beanClass, int autowireMode, int dependencyCheck);

    void autowireBeanProperties(Object existBean, int autowireMode, int dependencyCheck);

    Object applyPostProcessorBeforeInitialization(Object existBean, String name);

    Object applyPostProcessorAfterInitialization(Object existBean, String name);


}
