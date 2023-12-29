package com.cxy.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Rpc Client 配置属性类
 *
 * @author cxy
 * @version 1.0
 * @ClassName RpcClientProperties
 */

@Data
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {
    private String loadBalance;

    private String serialization;

    private String transport;

    private String registry;

    private String registryAddr;

    private Integer timeout;

    public RpcClientProperties() {
        this.loadBalance = "random";
        this.serialization = "HESSIAN";
        this.transport = "netty";
        this.registry = "zookeeper";
        this.registryAddr = "127.0.0.1:2181";
        this.timeout = 5000;
    }
}
