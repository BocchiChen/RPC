package com.cxy.rpc.provider;

import com.cxy.rpc.server.annotation.RpcComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RpcComponentScan(basePackages = {"com.cxy.rpc.provider"})
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
