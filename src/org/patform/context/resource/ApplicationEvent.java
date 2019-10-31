package org.patform.context.resource;

import java.util.EventObject;

/**
 * @author leber
 * date 2019/10/31
 */
public abstract class ApplicationEvent extends EventObject {

    private long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ApplicationEvent(Object source) {
        super(source);
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
