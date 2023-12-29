package com.cxy.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {
    private Object returnResult;

    private Exception exception;
}
