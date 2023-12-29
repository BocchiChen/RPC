package com.cxy.rpc.core.discovery.zk;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.discovery.ServiceDiscovery;
import com.cxy.rpc.core.exception.RpcException;
import com.cxy.rpc.core.loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Zookeeper 实现服务发现实现类
 *
 * @author cxy
 * @version 1.0
 * @ClassName ZookeeperServiceDiscovery
 * @see org.apache.curator.framework.CuratorFramework
 * @see org.apache.curator.x.discovery.ServiceDiscovery
 * @see org.apache.curator.x.discovery.ServiceCache
 */
@Slf4j
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final int SESSION_TIMEOUT = 60 * 1000;

    private static final int CONNECT_TIMEOUT = 15 * 1000;

    private static final int BASE_SLEEP_TIME = 3 * 1000;

    private static final int MAX_RETRY = 10;

    private static final String BASE_PATH = "/cxy_rpc";

    private LoadBalance loadBalance;

    private CuratorFramework client;

    private org.apache.curator.x.discovery.ServiceDiscovery<ServiceInfo> serviceDiscovery;

    private final Map<String, ServiceCache<ServiceInfo>> serviceCacheMap = new ConcurrentHashMap<>();

    private final Map<String, List<ServiceInfo>> serviceMap = new ConcurrentHashMap<>();

    public ZooKeeperServiceDiscovery(String registryAddress, LoadBalance loadBalance) {
        try {
            this.loadBalance = loadBalance;
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
            log.error("An error occurred while starting the zookeeper discovery: ", e);
        }
    }

    @Override
    public ServiceInfo discover(RpcRequest request) {
        try {
            return loadBalance.select(getServices(request.getServiceName()), request);
        } catch (Exception e) {
            throw new RpcException(String.format("Remote service discovery did not find service %s.",
                    request.getServiceName()), e);
        }
    }

    @Override
    public List<ServiceInfo> getServices(String serviceName) throws Exception {
        if(!serviceMap.containsKey(serviceName)){
            ServiceCache<ServiceInfo> serviceCache = serviceDiscovery.serviceCacheBuilder()
                    .name(serviceName)
                    .build();
            serviceCache.addListener(new ServiceCacheListener() {
                @Override
                public void cacheChanged() {
                    log.info("The service [{}] cache has changed. The current number of service samples is {}."
                            , serviceName, serviceCache.getInstances().size());
                    // 更新本地缓存的服务列表
                    serviceMap.put(serviceName, serviceCache.getInstances().stream()
                            .map(ServiceInstance::getPayload)
                            .collect(Collectors.toList()));
                }

                @Override
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                    // 当连接状态发生改变时，只打印提示信息，保留本地缓存的服务列表
                    log.info("The client {} connection status has changed. The current status is: {}."
                            , client, connectionState);
                }
            });
            serviceCache.start();
            serviceCacheMap.put(serviceName, serviceCache);
            serviceMap.put(serviceName, serviceCacheMap.get(serviceName).getInstances()
                    .stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList()));
        }
        return serviceMap.get(serviceName);
    }

    @Override
    public void destroy() throws Exception {
        for (ServiceCache<ServiceInfo> serviceCache : serviceCacheMap.values()) {
            if (serviceCache != null) {
                serviceCache.close();
            }
        }
        if (serviceDiscovery != null) {
            serviceDiscovery.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
