package org.patform.bean.wrapper;

import org.patform.bean.PropertyValue;
import org.patform.bean.PropertyValues;
import org.patform.bean.exception.BeanWrapperException;
import org.patform.context.util.BeanUtils;
import sun.security.pkcs.PKCS8Key;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author leber
 */
public class BeanWrapperImpl implements BeanWrapper {

    private Object object;

    private String nestedPath = "";

    private Map<String, BeanWrapperImpl> nestedBeanWrappers;

    private CachedIntrospectionResults cachedIntrospectionResults;
    //------------------------------------------


    public BeanWrapperImpl() {
    }

    public BeanWrapperImpl(Object object) {
        setWrappedInstance(object);
    }

    public BeanWrapperImpl(Object object, String nestedPath) {
        setWrappedInstance(object);
        this.nestedPath = nestedPath;
    }

    public BeanWrapperImpl(Class<?> beanClass) {
        Object instance = BeanUtils.instantiateClass(beanClass);
        setWrappedInstance(instance);
    }

    //----------------------implement of bean wrapper
    @Override
    public void setWrappedInstance(Object instance) {
        this.object = instance;
        this.nestedBeanWrappers = null;
        if (this.cachedIntrospectionResults == null || !cachedIntrospectionResults.getBeanClass().equals(instance.getClass())) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(instance.getClass());
        }
    }

    @Override
    public Class<?> getWrappedClass() {
        return object.getClass();
    }


    @Override
    public Object getPropertyValue(String propertyName) throws InvocationTargetException, IllegalAccessException {
        if (isNestProperty(propertyName)) {
            //先获取最下层beanWrapper 再获取值
            BeanWrapperImpl beanWrapper = getBeanWrapperForPropertyPath(propertyName);
            beanWrapper.getPropertyValue(getFinalPath(propertyName));
        }
        String[] tokens = getPropertyNameTokens(propertyName);
        return getPropertyValue(tokens[0], tokens[1], tokens[2]);
    }

    /**
     * 设置数组、集合等字段类型内的值
     *
     * @param propertyName
     * @param actualName
     * @param key
     * @param value
     */
    private void setPropertyValue(String propertyName, String actualName, String key, Object value) throws InvocationTargetException, IllegalAccessException {
        //设置数组，集合等字段内的值
        Object propertyValue = getPropertyValue(actualName);

        if (Objects.isNull(propertyName)) {
            throw new BeanWrapperException("未初始化值");
        } else if (propertyValue.getClass().isArray()) {
            Object[] values = (Object[]) propertyValue;
            values[Integer.parseInt(key)] = value;
        } else if (propertyValue instanceof List) {
            List<Object> values = (List<Object>) propertyValue;
            values.set(Integer.parseInt(key), value);
        } else if (propertyValue instanceof Set) {
            Set<Object> set = (Set<Object>) propertyValue;
            set.add(value);
        } else if (propertyValue instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) propertyValue;
            map.put(key, value);
        } else {
            throw new BeanWrapperException("不支持的类型.");
        }
    }

    //解析复杂名
    String[] getPropertyNameTokens(String propertyName) {

        String actualName = propertyName;
        String key = null;
        if (propertyName.contains("[") && propertyName.endsWith("]")) {
            actualName = propertyName.replace("^(\\w+)\\[", "$1");
            key = propertyName.replace("\\w+\\[(\\w+)\\]", "$1");
        }

        String canonicalName = actualName;
        if (key != null) {
            canonicalName += "[" + key + "]";
        }
        return new String[]{canonicalName, actualName, key};
    }

    /**
     * 获取包括嵌套字段的值
     *
     * @param propertyName
     * @param actualName
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object getPropertyValue(String propertyName, String actualName, String key) throws IllegalAccessException, InvocationTargetException {
        //获取属性值
        PropertyDescriptor pd = getPropertyDescriptor(actualName);
        Method readMethod = pd.getReadMethod();
        Object value = readMethod.invoke(object);
        if (Objects.isNull(value)) {
            return null;
        }else if (value.getClass().isArray()) {
            Object[] arr = (Object[]) value;
            return arr[Integer.parseInt(key)];
        } else if (value instanceof List) {
            List list = (List) value;
            return list.get(Integer.parseInt(key));
        } else if (value instanceof Set) {
            Set set = (Set) value;
            Iterator it = set.iterator();
            int index = Integer.parseInt(key);
            for (int i = 0; it.hasNext(); i++) {
                Object ele = it.next();
                if (i == index) {
                    return ele;
                }
            }
        } else if (value instanceof Map) {
            Map map = (Map) value;
            return map.get(key);
        } else {
            return value;
        }
        return null;
    }

    @Override
    public void setPropertyValue(String name, Object value) throws InvocationTargetException, IllegalAccessException {

        String[] tokens = getPropertyNameTokens(name);
        setPropertyValue(tokens[0],tokens[1],tokens[2],value);
    }

    @Override
    public void setPropertyValue(PropertyValue propertyValue) throws InvocationTargetException, IllegalAccessException {
        setPropertyValue(propertyValue.getName(), propertyValue.getValue());
    }

    @Override
    public void setPropertyValues(PropertyValues pvs) throws InvocationTargetException, IllegalAccessException {
        PropertyValue[] propertyValues = pvs.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            setPropertyValue(propertyValue);
        }
    }

    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws InvocationTargetException, IllegalAccessException {
        PropertyValue[] propertyValues = pvs.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            setPropertyValue(propertyValue);
        }
    }

    @Override
    public boolean isReadableProperty(String name) {
        PropertyDescriptor propertyDescriptor = cachedIntrospectionResults.getPropertyDescriptor(name);
        return propertyDescriptor.getReadMethod() != null;
    }

    @Override
    public boolean isWritableProperty(String name) throws InvocationTargetException, IllegalAccessException {
        if (isNestProperty(name)) {
            PropertyDescriptor pd = getPropertyDescriptor(name);
            return pd.getWriteMethod() != null;
        }
        PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(name);
        return pd.getWriteMethod() != null;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return cachedIntrospectionResults.getBeanInfo().getPropertyDescriptors();
    }

    /**
     * 属性描述器
     * @param name 字段名
     * @return pd
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public PropertyDescriptor getPropertyDescriptor(String name) throws InvocationTargetException, IllegalAccessException {
        if (isNestProperty(name)) {
            //嵌套属性 [多层依赖]
            BeanWrapperImpl wrapper = getBeanWrapperForPropertyPath(name);
            return wrapper.getPropertyDescriptor(getFinalPath(name));
        }
        return cachedIntrospectionResults.getPropertyDescriptor(name);
    }

    /**
     * 递归获取最下层的beanWrapperImpl 对象
     * @param nestedPropertyPath aaa.bbb.ccc
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private BeanWrapperImpl getBeanWrapperForPropertyPath(String nestedPropertyPath) throws InvocationTargetException, IllegalAccessException {

        int index = nestedPropertyPath.indexOf(NESTED_PROPERTY_SEPARATOR);
        if (index > 0) {
            //拿到当前层属性值 aaa.bbb.ccc-->aaa
            String actualName = nestedPropertyPath.substring(0, index);
            BeanWrapperImpl beanWrapper = getNestedProperty(actualName);
            //继续下一层  aaa.bbb.ccc -->> bbb.ccc
            String nestedPath = nestedPropertyPath.substring(index + 1);
            return beanWrapper.getBeanWrapperForPropertyPath(nestedPath);
        } else {
            //递归出口
            return this;
        }
    }

    /**
     * 获取属性值
     * @param actualName 字段名
     * @return beanWrapperImpl
     * @throws InvocationTargetException er
     * @throws IllegalAccessException e
     */
    private BeanWrapperImpl getNestedProperty(String actualName) throws InvocationTargetException, IllegalAccessException {
        if (Objects.isNull(nestedBeanWrappers)) {
            nestedBeanWrappers = new HashMap<>();
        }
        String[] tokens = getPropertyNameTokens(actualName);
        Object pv = getPropertyValue(tokens[0], tokens[1], tokens[2]);
        BeanWrapperImpl beanWrapper = nestedBeanWrappers.get(actualName);
        if (Objects.isNull(beanWrapper)) {
            //缓存起来
            beanWrapper = new BeanWrapperImpl(pv, this.nestedPath + actualName + NESTED_PROPERTY_SEPARATOR);
            nestedBeanWrappers.put(actualName, beanWrapper);
        }
        return beanWrapper;
    }

    /**
     * 获取嵌套路径的最后一段 aaa.bbb.ccc -->> ccc
     * @param name
     * @return
     */
    private String getFinalPath(String name) {
        if (name.lastIndexOf(NESTED_PROPERTY_SEPARATOR) > 0) {
            int index = name.lastIndexOf(NESTED_PROPERTY_SEPARATOR);
            return name.substring(index + 1);
        }
        return name;
    }
}
