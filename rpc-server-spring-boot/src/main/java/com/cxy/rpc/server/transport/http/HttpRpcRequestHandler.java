package com.cxy.rpc.server.transport.http;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.RpcResponse;
import com.cxy.rpc.core.exception.RpcException;
import com.cxy.rpc.core.factory.SingletonFactory;
import com.cxy.rpc.server.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 基于 HTTP 协议的 RpcRequest 处理器
 *
 * @author cxy
 * @version 1.0
 * @ClassName HttpRpcRequestHandler
 */
@Slf4j
public class HttpRpcRequestHandler {

    private final RpcRequestHandler rpcRequestHandler;

    public HttpRpcRequestHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @SuppressWarnings("Duplicates")
    public void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
            // 读取客户端请求
            RpcRequest request = (RpcRequest) ois.readObject();
            log.debug("The server received message is {}.", request);
            // 创建一个 RpcResponse 对象来响应客户端
            RpcResponse response = new RpcResponse();
            // 处理请求
            try {
                // 获取请求的服务对应的实例对象反射调用方法的结果（调用本地方法）
                Object result = rpcRequestHandler.handleRpcRequest(request);
                response.setReturnResult(result);
            } catch (Exception e) {
                log.error("The service [{}], the method [{}] invoke failed!", request.getServiceName(), request.getMethodName());
                // 若不设置，堆栈信息过多，导致报错
                response.setException(new RpcException("Error in remote procedure call, " + e.getMessage()));
            }
            log.debug("The response is {}.", response);
            oos.writeObject(response);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("The http server failed to handle client rpc request.", e);
        }
    }

}
