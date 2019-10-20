package org.patform.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MutablePropertyValues implements PropertyValues {

    List<PropertyValue> propertyValueList;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<>(10);
    }

    public MutablePropertyValues(PropertyValues other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("other PropertyValues not be null for deep copy.");
        }
        this.propertyValueList = new ArrayList<>(other.getPropertyValues().length);
        for (PropertyValue propertyValue : other.getPropertyValues()) {
            addPropertyValue(new PropertyValue(propertyValue.getName(), propertyValue.getValue()));
        }

    }

    public void addPropertyValue(PropertyValue propertyValue) {
        propertyValueList.add(propertyValue);
    }

    @Override
    public PropertyValue getPropertyValue(String name) {
        for (PropertyValue propertyValue : propertyValueList) {
            if (Objects.equals(propertyValue.getName(), name)) {
                return propertyValue;
            }
        }
        return null;
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return propertyValueList.toArray(new PropertyValue[0]);
    }

    @Override
    public boolean contains(String propertyName) {
        if (Objects.isNull(propertyName)) {
            return false;
        }
        for (PropertyValue pv : propertyValueList) {
            if (Objects.equals(pv.getName(), propertyName)) {
                return true;
            }
        }
        return false;
    }
}
