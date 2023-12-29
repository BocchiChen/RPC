package com.cxy.rpc.server.spring;

import com.cxy.rpc.server.annotation.RpcComponentScan;
import com.cxy.rpc.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

@Slf4j
public class RpcBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        // 获取 RpcComponentScan 注解的属性和值
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(annotationMetadata.getAnnotationAttributes(RpcComponentScan.class.getName()));
        String[] basePackages = {};
        if (annotationAttributes != null) {
            // 此处去获取RpcComponentScan 注解的 basePackages 值
            basePackages = annotationAttributes.getStringArray("basePackages");
        }
        // 如果没有指定名称的话
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        // 创建一个浏览 RpcService 注解的 Scanner
        // 备注：此处可以继续扩展，例如扫描 spring bean 或者其他类型的 Scanner
        RpcClassPathBeanDefinitionScanner rpcServiceScanner = new RpcClassPathBeanDefinitionScanner(registry, RpcService.class);

        if (this.resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(this.resourceLoader);
        }

        // 扫描包下的所有 Rpc bean 并返回注册成功的数量（scan方法会调用register方法去注册扫描到的类并生成 BeanDefinition 注册到 spring 容器）
        int count = rpcServiceScanner.scan(basePackages);
//        log.info("The number of BeanDefinition scanned and registered by RpcServiceScanner is {}.", count);
    }
}
