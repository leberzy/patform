package org.patform.test.test.anno;

import org.patform.context.annotation.Component;

@Component
public class HelloService {
    public void hello() {
        System.out.println("hello world!");
    }
}
