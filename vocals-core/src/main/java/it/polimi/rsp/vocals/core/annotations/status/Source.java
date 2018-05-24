package it.polimi.rsp.vocals.core.annotations.status;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be put on serivces models to ensure
 * the creation of an endpoint that allows their retrieval
 *
 * **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Source {


}
