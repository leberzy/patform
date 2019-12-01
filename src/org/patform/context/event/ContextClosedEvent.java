package org.patform.context.event;

import org.patform.context.factory.ApplicationContext;
import org.patform.context.resource.ApplicationEvent;

/**
 * 容器关闭事件
 *
 * @author leber
 * date 2019-12-01
 */
public class ContextClosedEvent extends ApplicationEvent {
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }

    public ApplicationContext getApplicationContext() {
        return (ApplicationContext) source;
    }
}
