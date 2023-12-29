package com.cxy.rpc.core.util;

import com.cxy.rpc.core.common.ServiceInfo;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

public class ServiceUtil {
    public static final Gson gson = new Gson();

    public static String serviceKey(String serviceName, String version){
        return String.join("-", serviceName, version);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map toMap(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return Collections.emptyMap();
        }
        Map map = gson.fromJson(gson.toJson(serviceInfo), Map.class);
        map.put("port", serviceInfo.getPort().toString());
        return map;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ServiceInfo toServiceInfo(Map map) {
        map.put("port", Integer.parseInt(map.getOrDefault("port", "0").toString()));
        return gson.fromJson(gson.toJson(map), ServiceInfo.class);
    }
}
