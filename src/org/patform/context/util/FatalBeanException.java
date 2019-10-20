package org.patform.context.util;

public class FatalBeanException extends Throwable {
    public FatalBeanException(String s, InstantiationException ex) {
        super(s, ex);
    }
}
