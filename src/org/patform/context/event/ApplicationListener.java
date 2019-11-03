package org.patform.context.event;

import org.patform.context.resource.ApplicationEvent;

/**
 * 容器上下文监听响应事件
 * @author leber
 * date 2019-11-03
 */
public interface ApplicationListener extends EventListener {

    /**
     * 事件监听响应
     * @param event
     */
    void onApplicationEvent(ApplicationEvent event);
}
