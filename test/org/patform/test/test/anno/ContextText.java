package org.patform.test.test.anno;


import org.patform.context.factory.AnnotationApplicationContext;

public class ContextText {

    public static void main(String[] args) {

        AnnotationApplicationContext context = new AnnotationApplicationContext();
        Object userService = context.getBean("userService");
        System.out.println(userService);
        HelloService helloService = context.getBean("helloService", HelloService.class);
        helloService.hello();
    }


}
