package org.patform.context.factory;

import org.patform.bean.RootBeanDefinition;
import org.patform.context.resource.StaticMessageSource;

/**
 * @author leber
 * @date 2019-12-01
 */
public class StaticApplicationContext extends AbstractApplicationContext {

    private DefaultListableBeanFactory beanFactory;


    public StaticApplicationContext() {
        this(null);
    }

    public StaticApplicationContext(ApplicationContext parent) {
        super(parent);
        this.beanFactory = new DefaultListableBeanFactory(parent);
        this.beanFactory.registerBeanDefinition(MESSAGE_SOURCE, new RootBeanDefinition(StaticMessageSource.class, RootBeanDefinition.AUTOWIRE_NO));
    }


    @Override
    protected void refreshBeanFactory() {

    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
