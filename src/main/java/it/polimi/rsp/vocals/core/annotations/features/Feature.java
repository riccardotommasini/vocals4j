package it.polimi.rsp.vocals.core.annotations.features;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {

    String name();

    String ns() default "UNKNOWN";


}
