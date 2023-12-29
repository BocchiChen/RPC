package com.cxy.rpc.core.loadbalance.impl;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.ServiceInfo;
import com.cxy.rpc.core.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性哈希负载均衡算法 <p>
 * 参考：<br>
 * <a href="https://github.com/apache/dubbo/blob/2d9583adf26a2d8bd6fb646243a9fe80a77e65d5/dubbo-cluster/src/main/java/org/apache/dubbo/rpc/cluster/loadbalance/ConsistentHashLoadBalance.java">dubbo一致性哈希算法</a><br>
 * <a href="https://cn.dubbo.apache.org/zh-cn/docsv2.7/dev/source/loadbalance/#23-consistenthashloadbalance">dubbo一致性哈希原理</a>
 *
 * @author cxy
 * @version 1.0
 * @ClassName ConsistentHashLoadBalance
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final Map<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request) {
        String method = request.getMethodName();
        String key = request.getServiceName() + "." + method;
        // 返回哈希码，无论hashCode()是否被改写
        int identityHashCode = System.identityHashCode(invokers);
        ConsistentHashSelector selector = selectors.get(key);
        // 如果为 null，表示之前没有缓存过，如果 hashcode 不一致，表示缓存的服务列表发生变化
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(key, new ConsistentHashSelector(invokers, 160, identityHashCode));
            selector = selectors.get(key);
        }
        String selectKey = key;
        // 具有相同参数值的请求将会被分配给同一个服务提供者。ConsistentHashLoadBalance 不关系权重
        if (request.getParameterValues() != null && request.getParameterValues().length > 0) {
            selectKey += Arrays.stream(request.getParameterValues());
        }
        return selector.select(selectKey);
    }

    /**
     * 一致性哈希选择器（包含各服务信息）
     */
    private final static class ConsistentHashSelector {

        /**
         * 使用 TreeMap 存储虚拟节点（virtualInvokers 需要提供高效的查询操作，因此选用 TreeMap 作为存储结构）
         */
        private final TreeMap<Long, ServiceInfo> virtualInvokers;

        /**
         * invokers 的原始哈希码
         */
        private final int identityHashCode;

        /**
         * 构建一个 ConsistentHashSelector 对象
         *
         * @param invokers         存储虚拟节点
         * @param replicaNumber    虚拟节点数，默认为 160
         * @param identityHashCode invokers 的原始哈希码
         */
        private ConsistentHashSelector(List<ServiceInfo> invokers, int replicaNumber, int identityHashCode) {
            // 对ConsistentHashSelector class加锁更新
            synchronized (ConsistentHashSelector.class) {
                this.virtualInvokers = new TreeMap<>();
                this.identityHashCode = identityHashCode;
                for (ServiceInfo invoker : invokers) {
                    String address = invoker.getAddress();
                    for (int i = 0; i < replicaNumber / 4; i++) {
                        // 对 address + i 进行 md5 运算，得到一个长度为16的字节数组
                        byte[] digest = md5(address + i);
                        // h = 0 时，取 digest 中下标为 0 ~ 3 的4个字节进行位运算
                        // h = 1 时，取 digest 中下标为 4 ~ 7 的4个字节进行位运算
                        // h = 2, h = 3 时过程同上
                        for (int h = 0; h < 4; h++) {
                            long m = hash(digest, h);
                            virtualInvokers.put(m, invoker);
                        }
                    }
                }
            }
        }

        private byte[] md5(String key){
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            return md.digest();
        }

        /**
         * 根据摘要生成 hash 值
         *
         * @param digest md5摘要内容
         * @param number 当前索引数
         * @return hash 值 （32 bits）
         */
        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public ServiceInfo select(String key) {
            byte[] digest = md5(key);
            return selectForKey(hash(digest, 0));
        }

        private ServiceInfo selectForKey(long hash) {
            // 找到 TreeMap 中查找第一个节点值大于或等于当前 hash 的 Invoker
            Map.Entry<Long, ServiceInfo> entry = virtualInvokers.ceilingEntry(hash);
            // 如果 hash 大于 Invoker 在圆环上最大的位置，此时 entry = null，需要将 TreeMap 的头节点赋值给 entry
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }

    }
}
