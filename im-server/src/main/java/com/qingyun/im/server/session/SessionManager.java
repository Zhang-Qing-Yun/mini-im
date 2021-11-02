package com.qingyun.im.server.session;

import com.qingyun.im.server.router.ImWorker;
import com.qingyun.im.server.session.dao.SessionCacheDao;
import com.qingyun.im.server.session.dao.UserCacheDao;
import com.qingyun.im.server.session.entity.SessionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： session管理器
 * @author: 張青云
 * @create: 2021-10-13 18:52
 **/
@Component
public class SessionManager {
    //  本地保存session
    private final ConcurrentHashMap<String, ServerSession> localSessions = new ConcurrentHashMap<>();

    @Autowired
    private ImWorker imWorker;

    @Autowired
    private SessionCacheDao sessionCacheDao;

    @Autowired
    private UserCacheDao userCacheDao;


    /**
     * 保存session
     */
    public void addLocalSession(String sessionId, ServerSession session) {
        localSessions.put(sessionId, session);
    }

    /**
     * 删除一个session
     */
    public void removeLocalSession(String sessionId) {
        localSessions.remove(sessionId);
    }

    /**
     * 获取与当前服务器建立连接的session
     * @param sessionId id
     * @return 如果没有则返回null
     */
    public ServerSession getLocalSession(String sessionId) {
        return localSessions.get(sessionId);
    }

    /**
     * 保存session（在本地和分布式缓存各保存一份）
     */
    public synchronized void saveSessionCache(String sessionId, ServerSession session) {
        //  1.在本地保存session
        addLocalSession(sessionId, session);

        //  创建sessionCache
        SessionCache sessionCache = new SessionCache(sessionId, session.getUsername(), imWorker.getImNode());
        //  2.将session保存到redis当中
        sessionCacheDao.save(sessionCache);
        //  3.缓存用户的session信息
        userCacheDao.addSession(session.getUsername(), sessionCache);
    }

    /**
     * 获取某个用户的session信息
     */
    public SessionCache getUserSessionCache(String username) {
        return userCacheDao.getSession(username);
    }
}
