package org.patform.context.event;

import org.patform.context.resource.ApplicationEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * 事件监听器工具类
 *
 * @author leber
 * date 2019-11-03
 */
public class ApplicationEventMulticasterImpl implements ApplicationEventMulticaster {


    private Set<ApplicationListener> listeners = new HashSet<>();

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        for (ApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }
}
