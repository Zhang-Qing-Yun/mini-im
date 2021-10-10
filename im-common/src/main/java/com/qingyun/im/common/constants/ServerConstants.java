package com.qingyun.im.common.constants;

/**
 * @description： 服务端使用到的一些常数
 * @author: 張青云
 * @create: 2021-10-09 10:35
 **/
public interface ServerConstants {
    //  工作节点的父路径
    String MANAGE_PATH = "/im/nodes";

    //  工作节点的路径前缀
    String PATH_PREFIX = MANAGE_PATH + "/seq-";
    String PATH_PREFIX_NO_STRIP =  "seq-";
}
