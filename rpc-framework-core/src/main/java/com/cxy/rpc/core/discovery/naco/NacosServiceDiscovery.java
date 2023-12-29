package com.cxy.rpc.core.discovery.naco;

import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.discovery.ServiceDiscovery;
import com.cxy.rpc.core.exception.RpcException;
import com.cxy.rpc.core.loadbalance.LoadBalance;
import com.cxy.rpc.core.util.ServiceUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Nacos 实现服务发现实现类
 *
 * @author cxy
 * @version 1.0
 * @ClassName NacosServiceDiscovery
 * @see NamingService
 * @see com.alibaba.nacos.api.naming.pojo.Instance
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {

    private NamingService namingService;

    private LoadBalance loadBalance;

    private final Map<String, List<ServiceInfo>> serviceMap = new ConcurrentHashMap<>();

    public NacosServiceDiscovery(String registryAddr, LoadBalance loadBalance) {
        try {
            this.loadBalance = loadBalance;
            this.namingService = NamingFactory.createNamingService(registryAddr);
        } catch (NacosException e) {
            log.error("An error occurred while starting the nacos discovery: ", e);
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
    public List<ServiceInfo> getServices(String serverName) throws Exception {
        try {
            if (!serviceMap.containsKey(serverName)) {
                List<ServiceInfo> instances = namingService.getAllInstances(serverName).stream()
                        .map(instance -> ServiceUtil.toServiceInfo(instance.getMetadata()))
                        .collect(Collectors.toList());
                serviceMap.put(serverName, instances);

                namingService.subscribe(serverName, event -> {
                    NamingEvent namingEvent = (NamingEvent) event;
                    log.info("The service [{}] cache has changed. The current number of service samples is {}."
                            , serverName, namingEvent.getInstances().size());

                    // 更新本地服务列表缓存
                    serviceMap.put(namingEvent.getServiceName(), namingEvent.getInstances().stream()
                            .map(instance -> ServiceUtil.toServiceInfo(instance.getMetadata()))
                            .collect(Collectors.toList()));
                });
            }
            return serviceMap.get(serverName);
        } catch (NacosException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        namingService.shutDown();
        log.info("Destroy nacos discovery completed.");
    }
}
