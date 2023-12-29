package com.cxy.rpc.server.annotation;

import com.cxy.rpc.server.spring.RpcBeanDefinitionRegistrar;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RpcBeanDefinitionRegistrar.class)
public @interface RpcComponentScan {
    /**
     * 扫描包路径
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * 扫描包路径
     */
    @AliasFor("value")
    String[] basePackages() default {};
}
