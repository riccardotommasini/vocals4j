package it.polimi.rsp.vocals.core.annotations.services;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Catalog {
    String host();
    int port() default 4000;
}
