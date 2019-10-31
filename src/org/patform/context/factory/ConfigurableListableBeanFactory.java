package org.patform.context.factory;

public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, ConfigurableBeanFactory, AutowireCapableBeanFactory {

	/**
	 * Ensure that all non-lazy-init singletons are instantiated, also considering
	 * FactoryBeans. Typically invoked at the end of factory setup, if desired.
	 */
	void preInstantiateSingletons();

}