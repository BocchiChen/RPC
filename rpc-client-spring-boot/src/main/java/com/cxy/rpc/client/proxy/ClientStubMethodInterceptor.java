package com.cxy.rpc.client.proxy;

import com.cxy.rpc.client.config.RpcClientProperties;
import com.cxy.rpc.client.transport.RpcClient;
import com.cxy.rpc.core.discovery.ServiceDiscovery;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 基于 Cglib 动态代理的客户端方法调用处理器类
 *
 * @author cxy
 * @version 1.0
 * @ClassName ClientStubMethodInterceptor
 */
public class ClientStubMethodInterceptor implements MethodInterceptor {

    /**
     * 服务发现中心
     */
    private final ServiceDiscovery serviceDiscovery;

    /**
     * Rpc客户端
     */
    private final RpcClient rpcClient;

    /**
     * Rpc 客户端配置属性
     */
    private final RpcClientProperties properties;

    /**
     * 服务名称：接口-版本
     */
    private final String serviceName;

    public ClientStubMethodInterceptor(ServiceDiscovery serviceDiscovery, RpcClient rpcClient, RpcClientProperties properties, String serviceName) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcClient = rpcClient;
        this.properties = properties;
        this.serviceName = serviceName;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // 执行远程方法调用
        return RemoteMethodCall.remoteCall(serviceDiscovery, rpcClient, serviceName, properties, method, args);
    }
}