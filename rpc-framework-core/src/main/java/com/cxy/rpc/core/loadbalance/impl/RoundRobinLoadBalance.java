package com.cxy.rpc.core.loadbalance.impl;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡算法
 *
 * @author cxy
 * @version 1.0
 * @ClassName RoundRobinLoadBalance
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    private static final int MAX_REPEATED_TIMES = 10;

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request) {
        return invokers.get(getAndIncrement() % invokers.size());
    }

    /**
     * 返回当前值并加一，通过 CAS 原子更新，当当前值到达 {@link Integer#MAX_VALUE} 时，重新设值为 0
     *
     * @return 返回当前的值
     */
    public final int getAndIncrement() {
        int prev, next;
        int times = 0;
        do {
            prev = atomicInteger.get();
            next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
            times++;
            // 持续重试更新
        } while(!atomicInteger.compareAndSet(prev, next) && times != MAX_REPEATED_TIMES);
        // 尝试加锁更新
        if (times == MAX_REPEATED_TIMES) {
            synchronized (atomicInteger) {
                prev = atomicInteger.get();
                next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
                atomicInteger.set(next);  // 直接设置新值，不再使用compareAndSet
            }
        }
        return prev;
    }
}
