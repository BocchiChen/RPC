package com.cxy.rpc.core.registry;

import com.cxy.rpc.core.common.ServiceInfo;

public interface ServiceRegistry {

    void register(ServiceInfo serviceInfo) throws Exception;

    void unregister(ServiceInfo serviceInfo) throws Exception;

    void destroy() throws Exception;
}
