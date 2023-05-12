package com.qingyun.im.common.enums;

/**
 * @description： 负载均衡策略
 * @author: 張青云
 * @create: 2023-03-12 22:16
 **/
public enum LoadBalancerType {
    DEFAULT(0),
    RANDOM(1),
    ROUND_ROBIN(2),
    CON_HASH(3);

    //  类型编号
    int type;

    LoadBalancerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
