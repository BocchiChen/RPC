package com.cxy.rpc.client.config;

import com.cxy.rpc.core.discovery.ServiceDiscovery;
import com.cxy.rpc.core.discovery.naco.NacosServiceDiscovery;
import com.cxy.rpc.core.loadbalance.LoadBalance;
import com.cxy.rpc.core.loadbalance.impl.ConsistentHashLoadBalance;
import com.cxy.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.cxy.rpc.core.loadbalance.impl.RoundRobinLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.boot.context.properties.bind.Binder;
import com.cxy.rpc.core.discovery.zk.ZooKeeperServiceDiscovery;

/**
 * RpcClient 自动配置类
 * <pre>
 *     1. ConditionalOnBean：是否存在某个某类或某个名字的Bean
 *     2. ConditionalOnMissingBean：是否缺失某个某类或某个名字的Bean
 *     3. ConditionalOnSingleCandidate：是否符合指定类型的Bean只有⼀个
 *     4. ConditionalOnClass：是否存在某个类
 *     5. ConditionalOnMissingClass：是否缺失某个类
 *     6. ConditionalOnExpression：指定的表达式返回的是true还是false
 *     7. ConditionalOnJava：判断Java版本
 *     8. ConditionalOnJndi：JNDI指定的资源是否存在
 *     9. ConditionalOnWebApplication：当前应⽤是⼀个Web应⽤
 *     10. ConditionalOnNotWebApplication：当前应⽤不是⼀个Web应⽤
 *     11. ConditionalOnProperty：Environment中是否存在某个属性
 *     12. ConditionalOnResource：指定的资源是否存在
 *     13. ConditionalOnWarDeployment：当前项⽬是不是以War包部署的⽅式运⾏
 *     14. ConditionalOnCloudPlatform：是不是在某个云平台上
 * </pre>
 *
 * @author cxy
 * @version 1.0
 * @ClassName RpcClientAutoConfiguration
 */

@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcClientAutoConfiguration {

    /**
     * 属性绑定的实现方式二：
     * - 创建 RpcClientProperties 对象，绑定到配置文件
     * - 如果使用此方法，可以直接给属性赋初始值
     *
     * @param environment 当前应用的环境（支持 yaml、properties 等文件格式）
     * @return 返回对应的绑定属性类
     * @deprecated 弃用，使用被 {@link org.springframework.boot.context.properties.ConfigurationProperties} 标注的属性类代替，
     * 生成 metadata。
     */
//    @Bean
    @Deprecated
    public RpcClientProperties rpcClientProperties(Environment environment){
        BindResult<RpcClientProperties> bind = Binder.get(environment).bind("rpc.client", RpcClientProperties.class);
        return bind.get();
    }

    @Autowired
    RpcClientProperties rpcClientProperties;

    @Bean(name = "loadBalance")
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "loadbalance", havingValue = "random", matchIfMissing = true)
    public LoadBalance randomLoadBalance(){
        return new RandomLoadBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "loadbalance", havingValue = "roundRobin")
    public LoadBalance roundRobinLoadBalance() {
        return new RoundRobinLoadBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "loadbalance", havingValue = "consistentHash")
    public LoadBalance consistentHashLoadBalance() {
        return new ConsistentHashLoadBalance();
    }

    @Bean(name = "serviceDiscovery")
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBean(LoadBalance.class)
    @ConditionalOnProperty(prefix = "rpc.client", name = "registry", havingValue = "zookeeper", matchIfMissing = true)
    public ServiceDiscovery zookeeperServiceDiscovery(@Autowired LoadBalance loadBalance) {
        return new ZooKeeperServiceDiscovery(rpcClientProperties.getRegistryAddr(), loadBalance);
    }

    @Bean(name = "serviceDiscovery")
    @ConditionalOnMissingBean
    @ConditionalOnBean(LoadBalance.class)
    @ConditionalOnProperty(prefix = "rpc.client", name = "registry", havingValue = "nacos")
    public ServiceDiscovery nacosServiceDiscovery(@Autowired LoadBalance loadBalance) {
        return new NacosServiceDiscovery(rpcClientProperties.getRegistryAddr(), loadBalance);
    }


}
