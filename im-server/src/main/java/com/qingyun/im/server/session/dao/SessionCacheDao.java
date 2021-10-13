package com.qingyun.im.server.session.dao;

import com.qingyun.im.server.session.entity.SessionCache;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-13 20:28
 **/
public interface SessionCacheDao {
    /**
     * 将session信息保存到分布式缓存当中
     */
    void save(SessionCache s);

    /**
     * 从分布式缓存中获取session信息
     */
    SessionCache get(String sessionId);

    /**
     * 从分布式缓存中删除一个session缓存
     */
    void remove(String sessionId);
}
