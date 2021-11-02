package com.qingyun.im.server.session.dao;

import com.qingyun.im.server.session.entity.SessionCache;

/**
 * @description： 记录登录用户的信息
 * @author: 張青云
 * @create: 2021-11-02 09:53
 **/
public interface UserCacheDao {
    /**
     * 将用户username的信息添加到缓存
     */
    void addSession(String username, SessionCache session);

    /**
     * 获取某个用户的Session信息
     */
    SessionCache getSession(String username);

    /**
     * 删除某个用户的Session信息
     */
    void removeSession(String username);
}
