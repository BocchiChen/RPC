package com.cxy.rpc.core.registry.zk;

import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.exception.RpcException;
import com.cxy.rpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * Zookeeper 实现服务注册中心类
 *
 * @author cxy
 * @version 1.0
 * @ClassName ZookeeperServiceRegistry
 * @see org.apache.curator.framework.CuratorFramework
 * @see org.apache.curator.x.discovery.ServiceDiscovery
 */
@Slf4j
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private static final int SESSION_TIMEOUT = 60 * 1000;

    private static final int CONNECT_TIMEOUT = 15 * 1000;

    private static final int BASE_SLEEP_TIME = 3 * 1000;

    private static final int MAX_RETRY = 10;

    private static final String BASE_PATH = "/cxy_rpc";

    private CuratorFramework client;

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    public ZooKeeperServiceRegistry(String registryAddress) {
        try {
            client = CuratorFrameworkFactory
                    .newClient(registryAddress, SESSION_TIMEOUT, CONNECT_TIMEOUT,
                            new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRY));
            client.start();

            serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(new JsonInstanceSerializer<>(ServiceInfo.class))
                    .basePath(BASE_PATH)
                    .build();
            serviceDiscovery.start();
        } catch (Exception e) {
            log.error("An error occurred while starting the zookeeper registry: ", e);
        }
    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        try {
            ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance.<ServiceInfo>builder()
                    .name(serviceInfo.getServiceName())
                    .address(serviceInfo.getAddress())
                    .port(serviceInfo.getPort())
                    .payload(serviceInfo)
                    .build();
            serviceDiscovery.registerService(serviceInstance);
            log.info("Successfully registered [{}] service.", serviceInstance.getName());
        } catch (Exception e) {
            throw new RpcException(String.format("An error occurred when rpc server registering [%s] service.",
                    serviceInfo.getServiceName()), e);
        }
    }

    @Override
    public void unregister(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance.<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .address(serviceInfo.getAddress())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
        log.warn("Successfully unregistered {} service.", serviceInstance.getName());
    }

    @Override
    public void destroy() throws Exception {
        serviceDiscovery.close();
        client.close();
        log.info("Destroy zookeeper registry completed.");
    }
}
