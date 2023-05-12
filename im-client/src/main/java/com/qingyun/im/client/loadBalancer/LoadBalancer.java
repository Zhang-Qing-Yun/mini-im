package com.qingyun.im.client.loadBalancer;

import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.enums.LoadBalancerType;

import java.util.List;

/**
 * @description： 负载均衡
 * @author: 張青云
 * @create: 2023-01-12 22:02
 **/
public interface LoadBalancer {

    /**
     * 从给定集合中应用负载均衡策略选择一个
     * @param imNodes 全部元素
     * @param username 当前登录用户的用户名
     * @return 选择结果
     */
    ImNode select(List<ImNode> imNodes, String username);


    /**
     * 获取负载均衡器实例
     * @param type 类型
     * @return 负载均衡器
     */
    static LoadBalancer getInstance(int type) {
        if (type == LoadBalancerType.DEFAULT.getType()) {
            return new RandomLoadBalancer();
        }
        if (type == LoadBalancerType.RANDOM.getType()) {
            return new RandomLoadBalancer();
        } else if (type == LoadBalancerType.ROUND_ROBIN.getType()) {
            return new RoundRobinLoadBalancer();
        } else if (type == LoadBalancerType.CON_HASH.getType()) {
            return new ConsistentHashLoadBalancer();
        }
        return new RandomLoadBalancer();
    }
}
