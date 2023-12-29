package com.cxy.rpc.core.registry.naco;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.exception.RpcException;
import com.cxy.rpc.core.registry.ServiceRegistry;
import com.cxy.rpc.core.util.ServiceUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Nacos 实现服务注册中心类
 *
 * @author cxy
 * @version 1.0
 * @ClassName NacosServiceRegistry
 * @see com.alibaba.nacos.api.naming.NamingService
 * @see com.alibaba.nacos.api.naming.pojo.Instance
 * @see com.alibaba.nacos.api.naming.NamingFactory
 */

@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    private NamingService namingService;

    public NacosServiceRegistry(String registryAddr) {
        try {
            namingService = NamingFactory.createNamingService(registryAddr);
        } catch (Exception e) {
            log.error("An error occurred while starting the nacos registry: ", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(ServiceInfo serviceInfo) throws Exception {
        try {
            Instance instance = new Instance();
            instance.setServiceName(serviceInfo.getServiceName());
            instance.setIp(serviceInfo.getAddress());
            instance.setPort(serviceInfo.getPort());
            instance.setHealthy(true);
            instance.setMetadata(ServiceUtil.toMap(serviceInfo));

            namingService.registerInstance(instance.getServiceName(), instance);

            log.info("Successfully registered [{}] service.", instance.getServiceName());
        } catch (Exception e) {
            throw new RpcException(String.format("An error occurred when rpc server registering [%s] service.",
                    serviceInfo.getServiceName()), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unregister(ServiceInfo serviceInfo){
        try {
            Instance instance = new Instance();
            instance.setServiceName(serviceInfo.getServiceName());
            instance.setIp(serviceInfo.getAddress());
            instance.setPort(serviceInfo.getPort());
            instance.setHealthy(true);
            instance.setMetadata(ServiceUtil.toMap(serviceInfo));

            namingService.deregisterInstance(instance.getServiceName(), instance);

            log.warn("Successfully unregistered {} service.", instance.getServiceName());
        } catch (NacosException e) {
            throw new RpcException(String.format("An error occurred when rpc server unregistering [%s] service.",
                    serviceInfo.getServiceName()), e);
        }
    }

    @Override
    public void destroy() throws Exception {
        namingService.shutDown();
        log.info("Destroy Nacos registry completed.");
    }
}
