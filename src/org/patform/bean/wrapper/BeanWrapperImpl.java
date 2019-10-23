package org.patform.bean.wrapper;

import org.patform.bean.PropertyValue;
import org.patform.bean.PropertyValues;
import org.patform.bean.exception.BeanWrapperException;
import org.patform.context.util.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    //-----------------------------setProperty------------------------------------------------


    @Override
    public void setPropertyValue(PropertyValue propertyValue)  {
        setPropertyValue(propertyValue.getName(), propertyValue.getValue());
    }

    @Override
    public void setPropertyValues(PropertyValues pvs)  {
        PropertyValue[] propertyValues = pvs.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            setPropertyValue(propertyValue);
        }
    }

    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)  {
        PropertyValue[] propertyValues = pvs.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            setPropertyValue(propertyValue);
        }
    }

    //解析复杂名
    @Override
    public void setPropertyValue(String name, Object value)  {
        if (isNestProperty(name)) {
            //嵌套字段
            BeanWrapperImpl nestedBean = getBeanWrapperForPropertyPath(name);
            nestedBean.setPropertyValue(getFinalPath(name),value);
        } else {
            //普通字段
            String[] tokens = getPropertyNameTokens(name);
            setPropertyValue(tokens[0],tokens[1],tokens[2],value);
        }
    }
    /**
     * 设置数组、集合等字段类型内的值
     *
     * @param propertyName 属性名
     * @param actualName
     * @param key
     * @param value
     */
    private void setPropertyValue(String propertyName, String actualName, String key, Object value)  {
        if (!propertyName.contains("[")) {
            PropertyDescriptor pd = getPropertyDescriptor(propertyName);
            try {
                pd.getWriteMethod().invoke(object, value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BeanWrapperException(e.getMessage());
            }
            return;
        }
        //设置数组，集合等字段内的值
        Object propertyValue = getPropertyValue(actualName);

        if (Objects.isNull(propertyValue)) {
            throw new BeanWrapperException("未初始化值");
        } else if (propertyValue.getClass().isArray()) {
            Object[] values = (Object[]) propertyValue;
            values[Integer.parseInt(key)] = value;
        } else if (propertyValue instanceof List) {
            List<Object> values = (List<Object>) propertyValue;
            int index = Integer.parseInt(key);
            if (values.size() > index) {
                values.set(index,key);
            } else if (values.size() == index) {
                values.add(value);
            }else {
                throw new IndexOutOfBoundsException();
            }
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


    //------------------------------getPropertyValue----------------------------

    @Override
    public Object getPropertyValue(String propertyName)  {
        if (isNestProperty(propertyName)) {
            //先获取最下层beanWrapper 再获取值
            BeanWrapperImpl beanWrapper = getBeanWrapperForPropertyPath(propertyName);
            beanWrapper.getPropertyValue(getFinalPath(propertyName));
        }
        String[] tokens = getPropertyNameTokens(propertyName);
        return getPropertyValue(tokens[0], tokens[1], tokens[2]);
    }


    /**
     * 获取包括嵌套字段的值
     *
     * @param specialName 规范名 field[key]
     * @param actualName  field
     * @param key         key
     * @return
     */
    private Object getPropertyValue(String specialName, String actualName, String key) {
        //获取属性值
        Object value = null;
        PropertyDescriptor pd = null;
        try {
            pd = getPropertyDescriptor(actualName);
            Method readMethod = pd.getReadMethod();
            value = readMethod.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeanWrapperException(e.getMessage());
        }
        if (Objects.isNull(key)) {
            return value;
        }

        if (Objects.isNull(value)) {
            return null;
        } else if (value.getClass().isArray()) {
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

    /**
     * 是否有getter方法
     * @param propertyName 属性名
     * @return
     */
    @Override
    public boolean isReadableProperty(String propertyName)  {
        PropertyDescriptor pd = getPropertyDescriptor(propertyName);
        return pd.getReadMethod() != null;
    }

    //--------------------------------读写方法--------------------------------
    /**
     * 是否有set方法
     * @param propertyName 属性名
     * @return
     */
    @Override
    public boolean isWritableProperty(String propertyName)  {
        PropertyDescriptor pd = getPropertyDescriptor(propertyName);
        return pd.getWriteMethod() != null;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return cachedIntrospectionResults.getBeanInfo().getPropertyDescriptors();
    }

    /**
     * 属性描述器
     *
     * @param propertyName 属性名
     * @return pd
     */
    @Override
    public PropertyDescriptor getPropertyDescriptor(String propertyName)  {
        if (isNestProperty(propertyName)) {
            //嵌套属性 [多层依赖]
            BeanWrapperImpl wrapper = getBeanWrapperForPropertyPath(propertyName);
            return wrapper.getPropertyDescriptor(getFinalPath(propertyName));
        }
        return cachedIntrospectionResults.getPropertyDescriptor(propertyName);
    }

    //-----------------------------------工具方法---------------------------------------------------
    /**
     * 解析词条 eg: aa[bb] --> 数组：arr[3]-->arr[3] arr  3         单列集合：list[2]--->list[2] list 2    双列集合：  map[key]-->map[key]  map  key
     * @param propertyName 属性名
     * @return
     */
    String[] getPropertyNameTokens(String propertyName) {

        String actualName = propertyName;
        String key = null;
        if (propertyName.contains("[") && propertyName.endsWith("]")) {
            actualName = propertyName.replaceAll("^(\\w+)\\[\\w+\\]$", "$1");
            key = propertyName.replaceAll("\\w+\\[(\\w+)\\]$", "$1");
        }

        String canonicalName = actualName;
        if (key != null) {
            canonicalName += "[" + key + "]";
        }
        return new String[]{canonicalName, actualName, key};
    }



    /**
     * 递归获取最下层的beanWrapperImpl 对象
     *
     * @param nestedPropertyPath aaa.bbb.ccc
     * @return
     */
    private BeanWrapperImpl getBeanWrapperForPropertyPath(String nestedPropertyPath){

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
     *
     * @param actualName 字段名
     * @return beanWrapperImpl
     */
    private BeanWrapperImpl getNestedProperty(String actualName)  {
        if (Objects.isNull(nestedBeanWrappers)) {
            nestedBeanWrappers = new HashMap<>();
        }
        String[] tokens = getPropertyNameTokens(actualName);
        Object pv = getPropertyValue(tokens[0], tokens[1], tokens[2]);
        if (Objects.isNull(pv)) {
            throw new BeanWrapperException("the nested property value is null.");
        }
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
     *
     * @param propertyName 属性名
     * @return
     */
    private String getFinalPath(String propertyName) {
        if (propertyName.lastIndexOf(NESTED_PROPERTY_SEPARATOR) > 0) {
            int index = propertyName.lastIndexOf(NESTED_PROPERTY_SEPARATOR);
            return propertyName.substring(index + 1);
        }
        return propertyName;
    }

    public Object getInstance() {
        return object;
    }
}
