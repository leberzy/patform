package org.patform.bean;

/**
 * value holder.
 *
 * @author leber
 */
public class ValueHolder {

    private Object value;
    private Class<?> type;

    public ValueHolder(Object value) {
        this.value = value;
    }

    public ValueHolder(Object value, Class<?> type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
