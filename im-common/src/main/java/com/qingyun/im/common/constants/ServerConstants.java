package com.qingyun.im.common.constants;

/**
 * @description： 服务端使用到的一些常数
 * @author: 張青云
 * @create: 2023-03-09 10:35
 **/
public interface ServerConstants {
    //  工作节点的父路径
    String MANAGE_PATH = "/im/nodes";

    //  工作节点的路径前缀
    String PATH_PREFIX = MANAGE_PATH + "/seq-";
    String PATH_PREFIX_NO_STRIP =  "seq-";

    /**
     * 用一张表来存多少个用户的离线消息；
     * 假设每个用户平均有500条离线消息，且每张表的数据不超过500W条，则每张表可以保存1W个用户的离线消息；
     */
    long USER_COUNT_PRE_TABLE = 10000;
}
