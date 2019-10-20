package org.patform.bean;

import org.patform.bean.exception.BeanDefinitionValidationException;

public abstract class AbstractBeanDefinition implements BeanDefinition {

    private MutablePropertyValues propertyValues;
    private String resourceDescription;
    private boolean singleton = true;
    private boolean lazyInit = false;


    public AbstractBeanDefinition(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues != null ? propertyValues : new MutablePropertyValues();
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        return propertyValues;
    }


    @Override
    public String getResourceDescription() {
        return resourceDescription;
    }

    public void validate() {
        if (lazyInit && !singleton) {
            throw new BeanDefinitionValidationException("lazyInit bean should be single.");
        }
    }

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
}
