package org.patform.context.factory;

import org.patform.bean.BeanDefinition;

import java.util.Map;

/**
 * @author leber
 * date now
 */
public interface ListableBeanFactory extends BeanFactory {

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanDefinitionNames(Class<?> type);

    <T> Map<String, T> getBeansOfType(Class<T> requireType, boolean includeProtoType, boolean includeFactoryBeans);


}
