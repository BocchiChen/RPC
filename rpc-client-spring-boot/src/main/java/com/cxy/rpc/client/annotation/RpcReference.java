package com.cxy.rpc.client.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcReference {

    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "1.0";

    String loadBalance() default "";

    String mock() default "";

    int timeout() default 0;

}
