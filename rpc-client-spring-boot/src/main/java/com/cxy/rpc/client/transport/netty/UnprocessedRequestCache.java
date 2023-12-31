package com.cxy.rpc.client.transport.netty;

import com.cxy.rpc.core.protocol.RpcMessage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存未处理完成的 rpc 请求
 *
 * @author cxy
 * @version 1.0
 * @ClassName UnprocessedRequestCache
 * @deprecated 缓存并处理响应信息的功能已经由 {@link com.cxy.rpc.client.handler.RpcResponseHandler} 实现。
 */
@Deprecated
public class UnprocessedRequestCache {

    /**
     * 缓存未处理的请求响应
     */
    private static final Map<Integer, CompletableFuture<RpcMessage>> UNPROCESSED_REQUESTS = new ConcurrentHashMap<>();

    public void processResponse() {
        // do something for processing response message
    }

}
