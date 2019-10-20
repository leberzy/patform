package org.patform.bean;

public class PropertyValue {
    private String name;
    private Object value;

    public PropertyValue(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("property name not be null.");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PropertyValue{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
