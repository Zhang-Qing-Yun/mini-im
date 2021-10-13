package com.qingyun.im.auth.service;

import com.qingyun.im.common.entity.ImNode;

import java.util.List;

/**
 * @description： 有关ZK注册中心的业务逻辑
 * @author: 張青云
 * @create: 2021-10-12 21:31
 **/
public interface ZKService {
    /**
     * 获取IM集群的所有Netty Server
     * @return 所有Server，如果没有则返回一个空集合
     */
    List<ImNode> getAllNode();
}
