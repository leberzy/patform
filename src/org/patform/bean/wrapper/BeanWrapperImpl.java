package org.patform.bean.wrapper;

import org.patform.bean.PropertyValue;
import org.patform.bean.PropertyValues;
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

    private Map<String, BeanWrapper> nestedBeanWrappers;

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
    public Object getPropertyValue(String propertyName) {


        return null;
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

    private Object getPropertyValue(String propertyName, String actualName, String key) throws IllegalAccessException, InvocationTargetException {
        //获取属性值
        PropertyDescriptor pd = getPropertyDescriptor(actualName);
        Method readMethod = pd.getReadMethod();
        Object value = readMethod.invoke(object);
        if (value.getClass().isArray()) {
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
    public void setPropertyValue(String name, Object value) {

    }

    @Override
    public void setPropertyValue(PropertyValue propertyValue) {

    }

    @Override
    public void setPropertyValues(PropertyValues pvs) {

    }

    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) {

    }

    @Override
    public boolean isReadableProperty(String name) {
        return false;
    }

    @Override
    public boolean isWritableProperty(String name) {
        return false;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return new PropertyDescriptor[0];
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String name) {
        return null;
    }
}
