package com.cxy.rpc.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInfo implements Serializable {
    private String appName;

    private String serviceName;

    private String version;

    private String address;

    private Integer port;
}
