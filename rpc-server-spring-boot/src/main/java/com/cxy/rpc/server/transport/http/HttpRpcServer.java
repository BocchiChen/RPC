package com.cxy.rpc.server.transport.http;

import com.cxy.rpc.core.exception.RpcException;
import com.cxy.rpc.server.transport.RpcServer;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 基于 HTTP 通信协议实现的 Rpc Server 类
 *
 * @author cxy
 * @version 1.0
 * @ClassName HttpRpcServer
 */
public class HttpRpcServer implements RpcServer {

    @Override
    public void start(Integer port) {
        try {
            // 创建 Tomcat 实例
            Tomcat tomcat = new Tomcat();

            // 获取 Tomcat 的服务器实例
            Server server = tomcat.getServer();
            // 获取 Tomcat 服务器中名为 "Tomcat" 的服务实例
            Service service = server.findService("Tomcat");

            // 创建一个 Connector 实例，用于处理客户端和服务器之间的通信
            Connector connector = new Connector();
            connector.setPort(port);

            String hostname = InetAddress.getLocalHost().getHostAddress();

            StandardEngine engine = new StandardEngine();
            engine.setDefaultHost(hostname);

            StandardHost host = new StandardHost();
            host.setName(hostname);

            String contextPath = "";
            Context context = new StandardContext();
            context.setPath(contextPath);
            context.addLifecycleListener(new Tomcat.FixContextListener());

            host.addChild(context);
            engine.addChild(host);

            service.setContainer(engine);
            service.addConnector(connector);

            tomcat.addServlet(contextPath, "dispatcher", new DispatcherServlet());
            context.addServletMappingDecoded("/*", "dispatcher");

            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException | UnknownHostException e) {
            throw new RpcException("Tomcat server failed to start.", e);
        }
    }

}
