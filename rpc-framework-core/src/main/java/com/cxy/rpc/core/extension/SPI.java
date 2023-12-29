package com.cxy.rpc.core.extension;

import java.lang.annotation.*;

/**
 * SPI 注解，被标注的类表示为需要加载的扩展类接口
 *
 * @author cxy
 * @version 1.0
 * @ClassName SPI
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {
}
