package com.cxy.rpc.provider.service.impl;
import com.cxy.rpc.api.service.AbstractService;

//@Rpc(interfaceClass = AbstractService.class)
public class AbstractServiceImpl extends AbstractService {
    @Override
    public String abstractHello(String name) {
        return "abstract hello " + name;
    }
}