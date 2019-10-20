package org.patform.bean.wrapper;

import org.patform.bean.PropertyValue;
import org.patform.bean.PropertyValues;

import java.beans.PropertyDescriptor;

/**
 * @author leber
 */
public interface BeanWrapper {

    String NESTED_PROPERTY_SEPARATOR = ".";


    void setWrappedInstance(Object instance);

    Class<?> getWrappedClass();

    Object getPropertyValue(String propertyName);

    void setPropertyValue(String name,Object value);

    void setPropertyValue(PropertyValue propertyValue);

    void setPropertyValues(PropertyValues pvs);

    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown);

    boolean isReadableProperty(String name);

    boolean isWritableProperty(String name);


    PropertyDescriptor[] getPropertyDescriptors();

    PropertyDescriptor getPropertyDescriptor(String name);
}
