package it.polimi.rsp.vocals.core.annotations.features;


import it.polimi.rsp.vocals.core.annotations.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RSPService {
    String endpoint();
    HttpMethod method() default HttpMethod.GET;
}
