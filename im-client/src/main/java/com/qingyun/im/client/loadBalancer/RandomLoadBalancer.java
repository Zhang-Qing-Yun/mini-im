package com.qingyun.im.client.loadBalancer;

import com.qingyun.im.common.entity.ImNode;

import java.util.List;
import java.util.Random;

/**
 * @description： 使用随机负载均衡策略
 * @author: 張青云
 * @create: 2023-01-12 22:18
 **/
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public ImNode select(List<ImNode> imNodes, String username) {
        int index = new Random().nextInt(imNodes.size());
        return imNodes.get(index);
    }
}
