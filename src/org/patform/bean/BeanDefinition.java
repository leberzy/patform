package org.patform.bean;

/**
 * the special of bean definition.
 *
 * @author leber
 */
public interface BeanDefinition {

    MutablePropertyValues getPropertyValues();

    ConstructorArgumentValues getConstructorValues();

    String getResourceDescription();

}
