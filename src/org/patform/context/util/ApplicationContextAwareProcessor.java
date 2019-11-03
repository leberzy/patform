package org.patform.context.util;

import org.patform.context.factory.ApplicationContext;

/**
 * 容器上下文 bean后置处理工具
 *
 * @author leber
 * date 2019-11-03
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    protected ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessorBeforeInitialization(Object existBean, String name) {

        if (existBean instanceof ApplicationContextAware) {
            ApplicationContextAware contextAware = (ApplicationContextAware) existBean;
            contextAware.setApplicationContext(this.applicationContext);
        }
        return existBean;
    }

    @Override
    public Object postProcessorAfterInitialization(Object existBean, String name) {
        return existBean;
    }
}
