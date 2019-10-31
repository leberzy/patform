package org.patform.context.factory;

import org.patform.context.resource.ApplicationEvent;
import org.patform.context.resource.MessageSource;
import org.patform.context.resource.ResourceLoader;

/**
 * @author leber
 * date 2019/10/31
 */

public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, AutowireCapableBeanFactory, ResourceLoader, MessageSource {


    ApplicationContext getParent();

    long getStartupTime();

    String getDisplayName();

    /**
     * 发布事件
     */
    void publishEvent(ApplicationEvent event);
}
