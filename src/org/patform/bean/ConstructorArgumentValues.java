package org.patform.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * the constructor method argument holder.
 *
 * @author leber
 */
public class ConstructorArgumentValues {

    private Map<Integer, ValueHolder> indexedArgumentValues = new HashMap<>();
    private Set<ValueHolder> genericArgumentValues = new HashSet<>();

    /**
     * Add argument value for the given index in the constructor argument list.
     *
     * @param index the index in the constructor argument list
     * @param value the argument value
     */
    public void addIndexedArgumentValue(int index, Object value) {
        indexedArgumentValues.put(index, new ValueHolder(value));
    }

    /**
     * Add argument value for the given index in the constructor argument list.
     *
     * @param index the index in the constructor argument list
     * @param value the argument value
     */
    public void addIndexedArgumentValue(int index, Object value, Class<?> type) {
        indexedArgumentValues.put(index, new ValueHolder(value, type));
    }

    public ValueHolder getIndexedArgumentValue(int index, Class<?> requiredType) {
        ValueHolder valueHolder = indexedArgumentValues.get(index);
        if (valueHolder != null) {
            if (requiredType == valueHolder.getType()) {
                return valueHolder;
            }
        }
        return null;
    }

    public Map<Integer, ValueHolder> getIndexedArgumentValues() {
        return indexedArgumentValues;
    }

    /**
     * Add generic argument value to be matched by type.
     *
     * @param value the argument value
     */
    public void addGenericArgumentValue(Object value) {
        genericArgumentValues.add(new ValueHolder(value));
    }

    /**
     * Add generic argument value to be matched by type.
     *
     * @param value the argument value
     */
    public void addGenericArgumentValue(Object value, Class<?> type) {
        genericArgumentValues.add(new ValueHolder(value, type));
    }

    /**
     * Look for a generic argument value that matches the given type.
     *
     * @param requiredType the type to match
     * @return the ValueHolder for the argument, or null if none set
     */
    public ValueHolder getGenericArgumentValue(Class<?> requiredType) {
        for (ValueHolder valueHolder : this.genericArgumentValues) {
            if (valueHolder.getType() == requiredType) {
                return valueHolder;
            }
        }
        return null;
    }

    /**
     * Return the set of generic argument values.
     *
     * @return Set of ValueHolders
     * @see ValueHolder
     */
    public Set<ValueHolder> getGenericArgumentValues() {
        return genericArgumentValues;
    }

    /**
     * Look for an argument value that either corresponds to the given index
     * in the constructor argument list or generically matches by type.
     *
     * @param index        the index in the constructor argument list
     * @param requiredType the type to match
     * @return the ValueHolder for the argument, or null if none set
     */
    public ValueHolder getArgumentValue(int index, Class<?> requiredType) {
        ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType);
        if (valueHolder == null) {
            valueHolder = getGenericArgumentValue(requiredType);
        }
        return valueHolder;
    }

    /**
     * Return the number of arguments held in this instance.
     */
    public int getNrOfArguments() {
        return indexedArgumentValues.size() + genericArgumentValues.size();
    }

    /**
     * Return if this holder does not contain any argument values,
     * neither indexed ones nor generic ones.
     */
    public boolean isEmpty() {
        return indexedArgumentValues.isEmpty() && genericArgumentValues.isEmpty();
    }


}
