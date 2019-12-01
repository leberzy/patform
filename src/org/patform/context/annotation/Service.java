package org.patform.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Component 的派生注解 实现该注解的语义时应实现Component的语义
 *
 * @author leber
 * @date 2019-12-01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {
    String value() default "";
}
