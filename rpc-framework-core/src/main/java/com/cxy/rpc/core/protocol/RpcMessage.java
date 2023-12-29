package com.cxy.rpc.core.protocol;

import lombok.Data;

@Data
public class RpcMessage {
    private MessageHeader messageHeader;

    private Object body;
}
