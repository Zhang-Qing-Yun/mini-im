package com.qingyun.im.client.loadBalancer;

import com.qingyun.im.common.entity.ImNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description： 轮询负载均衡策略
 * @author: 張青云
 * @create: 2021-10-20 14:30
 **/
public class RoundRobinLoadBalancer implements LoadBalancer {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public ImNode select(List<ImNode> imNodes, String username) {
        if(index.get() >= imNodes.size()) {
            index.set(index.get() % imNodes.size());
        }
        ImNode imNode = imNodes.get(index.get());
        index.incrementAndGet();
        return imNode;
    }
}
