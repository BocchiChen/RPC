package com.cxy.rpc.core.discovery;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.extension.SPI;

import java.util.ArrayList;
import java.util.List;

@SPI
public interface ServiceDiscovery {
    ServiceInfo discover(RpcRequest request);

    default List<ServiceInfo> getServices(String serviceName) throws Exception {
        return new ArrayList<>();
    }

    void destroy() throws Exception;
}
