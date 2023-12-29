package com.cxy.rpc.core.loadbalance.impl;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request) {
        return invokers.get(random.nextInt(invokers.size()));
    }
}
