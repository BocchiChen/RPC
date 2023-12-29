package com.cxy.rpc.client.proxy;

import com.cxy.rpc.client.config.RpcClientProperties;
import com.cxy.rpc.client.transport.RpcClient;
import com.cxy.rpc.core.discovery.ServiceDiscovery;
import com.cxy.rpc.core.util.ServiceUtil;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端代理工厂类，返回服务代理类
 *
 * @author cxy
 * @version 1.0
 * @ClassName ClientStubProxyFactory
 */
public class ClientStubProxyFactory {

    private final ServiceDiscovery serviceDiscovery;

    private final RpcClient rpcClient;

    private final RpcClientProperties rpcClientProperties;


    public ClientStubProxyFactory(ServiceDiscovery serviceDiscovery, RpcClient rpcClient, RpcClientProperties rpcClientProperties) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcClient = rpcClient;
        this.rpcClientProperties = rpcClientProperties;
    }

    private static final Map<String, Object> proxyMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz, String version){
        return (T) proxyMap.computeIfAbsent(ServiceUtil.serviceKey(clazz.getName(), version), serviceName -> {
            if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
                return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                        new ClientStubInvocationHandler(serviceDiscovery, rpcClient, rpcClientProperties, serviceName));
            } else { // 使用 CGLIB 动态代理
                Enhancer enhancer = new Enhancer();
                enhancer.setClassLoader(clazz.getClassLoader());
                enhancer.setSuperclass(clazz);
                enhancer.setCallback(new ClientStubMethodInterceptor(serviceDiscovery, rpcClient, rpcClientProperties, serviceName));
                return enhancer.create();
            }
        });
    }
}
