package org.patform.bean.exception;

import org.patform.bean.PropertyValue;
import org.patform.bean.PropertyValues;
import org.patform.bean.wrapper.BeanWrapper;
import org.patform.bean.wrapper.CachedIntrospectionResults;
import org.patform.context.util.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author leber
 */
public class BeanWrapperException extends RuntimeException {

    public BeanWrapperException() {
    }

    public BeanWrapperException(String message) {
        super(message);
    }
}
