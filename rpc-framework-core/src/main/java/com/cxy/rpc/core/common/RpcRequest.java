package com.cxy.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameterValues;
}
