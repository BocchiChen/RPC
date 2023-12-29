package com.cxy.rpc.server.transport.http;
import com.cxy.rpc.core.factory.SingletonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DispatcherServlet 实现类，用来接受 http 请求
 *
 * @author cxy
 * @version 1.0
 * @ClassName DispatcherServlet
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * 当前处理器数量
     */
    private final int cpuNum = Runtime.getRuntime().availableProcessors();

    // 创建线程池
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(cpuNum * 2, cpuNum * 2, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpRpcRequestHandler handler = SingletonFactory.getInstance(HttpRpcRequestHandler.class);
        threadPool.submit(new Thread(() -> handler.handle(req, resp)));
    }
}
