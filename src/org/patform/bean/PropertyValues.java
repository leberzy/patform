package org.patform.bean;

public interface PropertyValues {

    PropertyValue getPropertyValue(String name);

    PropertyValue[] getPropertyValues();

    boolean contains(String propertyName);

}
