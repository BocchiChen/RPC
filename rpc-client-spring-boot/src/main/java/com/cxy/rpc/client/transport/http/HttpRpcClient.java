package com.cxy.rpc.client.transport.http;

import com.cxy.rpc.client.common.RequestMetadata;
import com.cxy.rpc.client.transport.RpcClient;
import com.cxy.rpc.core.common.RpcResponse;
import com.cxy.rpc.core.protocol.RpcMessage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 基于 HTTP 通信协议实现的 Rpc Client 类
 *
 * @author cxy
 * @version 1.0
 * @ClassName HttpRpcClient
 */
public class HttpRpcClient implements RpcClient {
    @Override
    public RpcMessage sendRpcRequest(RequestMetadata requestMetadata) {
        try {
            URL url = new URL("http", requestMetadata.getServerAddr(), requestMetadata.getPort(), "/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(requestMetadata.getRpcMessage().getBody());
            oos.flush();
            oos.close();

            // 构造 RpcMessage 对象
            RpcMessage rpcMessage = new RpcMessage();

            InputStream is = httpURLConnection.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            // 阻塞读取
            RpcResponse response = (RpcResponse) ois.readObject();
            rpcMessage.setBody(response);
            return rpcMessage;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
