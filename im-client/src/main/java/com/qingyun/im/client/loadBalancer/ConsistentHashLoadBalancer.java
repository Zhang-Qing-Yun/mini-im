package com.qingyun.im.client.loadBalancer;

import com.qingyun.im.common.entity.ImNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description： 一致性哈希负载均衡算法
 * @author: 張青云
 * @create: 2021-10-20 18:41
 **/
public class ConsistentHashLoadBalancer implements LoadBalancer {
    //  由所有服务端所构成的哈希环
    private ConsistentHashSelector selector;


    @Override
    public ImNode select(List<ImNode> imNodes, String username) {
        //  服务端列表的hash值，当服务列表发生变化时hash值也会发生变化
        int identityHashCode = imNodes.hashCode();

        //  检查服务列表是否发生了变化
        //  如果发生了变化则重新构建哈希环，但是原来已有的节点的位置(hash值)不会发生变化
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selector = new ConsistentHashSelector(imNodes, 160, identityHashCode);
        }

        //  从哈希环上获取结点
        String[] imNode = selector.select(username).split(":");
        for (ImNode node: imNodes) {
            if (node.getIp().equals(imNode[0]) && node.getPort() == Integer.parseInt(imNode[1])) {
                return node;
            }
        }
        return imNodes.get(0);
    }


    /**
     * 哈希环
     */
    private static class ConsistentHashSelector {
        //  使用TreeMap来充当哈希环
        private final TreeMap<Long, String> virtualInvokers;
        //  标志，用来检查该哈希环对应的服务的所有提供者的列表是否发生改变
        private final int identityHashCode;

        ConsistentHashSelector(List<ImNode> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            //  初始化哈希环
            for (ImNode imNode : invokers) {
                //  真实节点的ip和端口号
                String invoker = imNode.getIp() + ":" + imNode.getPort();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        //  添加一个虚拟节点
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        /**
         * 使用md5对key进行一次计算，降低冲突概率
         */
        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        /**
         * 计算hash值，该算法来自于Dubbo
         */
        static long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        /**
         * 从哈希环上选择一个节点
         */
        public String select(String username) {
            //  先对请求id计算md5，再计算hash值，然后选择大于等于该hash值的第一个节点
            byte[] digest = md5(username);
            return selectForKey(hash(digest, 0));
        }

        /**
         * 在哈希环上选择大于等于某个值的第一个结点
         */
        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }

        public int getIdentityHashCode() {
            return identityHashCode;
        }
    }
}
