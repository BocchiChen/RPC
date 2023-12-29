package com.cxy.rpc.server.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcService {
    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "1.0";
}
