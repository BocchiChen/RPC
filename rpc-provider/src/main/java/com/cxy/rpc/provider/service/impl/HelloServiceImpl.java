package com.cxy.rpc.provider.service.impl;

import com.cxy.rpc.api.service.HelloService;
import com.cxy.rpc.server.annotation.RpcService;

@RpcService(interfaceClass = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
