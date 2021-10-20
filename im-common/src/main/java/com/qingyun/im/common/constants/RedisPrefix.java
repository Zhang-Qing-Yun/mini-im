package com.qingyun.im.common.constants;

/**
 * @description： Redis前缀
 * @author: 張青云
 * @create: 2021-10-13 20:53
 **/
public interface RedisPrefix {

    //  sessionCache的前缀
    String SESSION_CACHE = "im:SessionCache:";

    //  分布式id的前缀
    String ID_GENERATOR = "im:IDGenerator:";
}
