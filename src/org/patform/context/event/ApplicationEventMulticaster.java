package org.patform.context.event;

/**
 * 容器上下文事件helper
 *
 * @author leber
 * date 2019-11-03
 */
public interface ApplicationEventMulticaster extends ApplicationListener {

    void addApplicationListener(ApplicationListener listener);

    void removeApplicationListener(ApplicationListener listener);

    void removeAllListeners();

}
