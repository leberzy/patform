package org.patform.context.event;

import org.patform.context.factory.AbstractApplicationContext;
import org.patform.context.factory.ApplicationContext;
import org.patform.context.resource.ApplicationEvent;

/**
 * @author leber
 * date 2019-12-01
 * Desc: 发布容器上下文刷新事件使用
 */
public class ContextRefreshedEvent extends ApplicationEvent {
    public ContextRefreshedEvent(ApplicationContext source) {
        super(source);
    }


    /**
     * source便是容器上下文
     *
     * @return
     */
    ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }

}
