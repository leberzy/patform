package org.patform.exception;

public class BeansException extends RuntimeException {

    public BeansException() {
    }

    public BeansException(String message) {
        super(message);
    }

    public BeansException(String s, Throwable ex) {
        super(s, ex);
    }
}
