package org.patform.bean.exception;

public class BeanDefinitionValidationException extends RuntimeException {
    public BeanDefinitionValidationException(String message) {
        super(message);
    }
}
