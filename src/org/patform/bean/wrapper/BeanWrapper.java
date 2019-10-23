package org.patform.bean.wrapper;

import org.patform.bean.PropertyValue;
import org.patform.bean.PropertyValues;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author leber
 */
public interface BeanWrapper {

    String NESTED_PROPERTY_SEPARATOR = ".";


    void setWrappedInstance(Object instance);

    Class<?> getWrappedClass();

    Object getPropertyValue(String propertyName) ;

    void setPropertyValue(String name, Object value);

    void setPropertyValue(PropertyValue propertyValue);

    void setPropertyValues(PropertyValues pvs);

    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown);

    boolean isReadableProperty(String name) ;

    boolean isWritableProperty(String name) throws InvocationTargetException, IllegalAccessException;


    PropertyDescriptor[] getPropertyDescriptors();

    PropertyDescriptor getPropertyDescriptor(String name) throws InvocationTargetException, IllegalAccessException;

    default boolean isNestProperty(String propertyName) {
        return propertyName.contains(NESTED_PROPERTY_SEPARATOR);
    }
}
