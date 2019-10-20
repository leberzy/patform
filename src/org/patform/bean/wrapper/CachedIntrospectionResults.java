package org.patform.bean.wrapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author leber
 */
public class CachedIntrospectionResults {


    private static HashMap<Class,CachedIntrospectionResults> classCache = new HashMap<>();

    private BeanInfo beanInfo;

    private Map<String,PropertyDescriptor> propertyDescriptionMap;

    public CachedIntrospectionResults(Class<?> clazz) {

        try {
            this.beanInfo = Introspector.getBeanInfo(clazz);

            this.propertyDescriptionMap = new HashMap<>();
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                this.propertyDescriptionMap.put(pd.getName(), pd);
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    public static CachedIntrospectionResults forClass(Class<?> beanClass) {

        CachedIntrospectionResults result = classCache.get(beanClass);
        if (Objects.isNull(result)) {
            result = new CachedIntrospectionResults(beanClass);
            classCache.put(beanClass, result);
        }
        return result;
    }

    BeanInfo getBeanInfo() {
        return beanInfo;
    }

    Class<?> getBeanClass() {
       return beanInfo.getBeanDescriptor().getClass();
    }

    PropertyDescriptor getPropertyDescriptor(String propertyName) {
        if (Objects.isNull(propertyDescriptionMap.get(propertyName))) {
            throw new RuntimeException("No property '" + propertyName + "' in class [" + getBeanClass().getName() + "]", null);
        }
        return this.propertyDescriptionMap.get(propertyName);

    }

}
