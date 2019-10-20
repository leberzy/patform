package org.patform.exception;

public class BeansException extends RuntimeException {
    public BeansException(String s, Throwable ex) {
        super(s, ex);
    }
}
