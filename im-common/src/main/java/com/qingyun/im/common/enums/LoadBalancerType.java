package com.qingyun.im.common.enums;

/**
 * @description： 负载均衡策略
 * @author: 張青云
 * @create: 2021-10-12 22:16
 **/
public enum LoadBalancerType {
    RANDOM(1);

    //  类型编号
    int type;

    LoadBalancerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
