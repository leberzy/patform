package org.patform.context.factory;

/**
 * @author leber
 * date 2019/10/29
 */

public interface ConfigurableListableBeanFactory extends ListableBeanFactory {
    /**
     * Ensure that all non-lazy-init singletons are instantiated, also considering
     * FactoryBeans. Typically invoked at the end of factory setup, if desired.
     * 初始化非懒加载单例对象
     */
    void preInstantiateSingletons();


}
