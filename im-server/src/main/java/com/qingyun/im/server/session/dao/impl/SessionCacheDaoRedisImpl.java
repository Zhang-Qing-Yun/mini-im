package com.qingyun.im.server.session.dao.impl;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.constants.RedisPrefix;
import com.qingyun.im.server.session.dao.SessionCacheDao;
import com.qingyun.im.server.session.entity.SessionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-13 20:29
 **/
@Component
public class SessionCacheDaoRedisImpl implements SessionCacheDao {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void save(SessionCache sessionCache) {
        String key = RedisPrefix.SESSION_CACHE + sessionCache.getSessionId();
        //  将value序列化成json
        String value = JSON.toJSONString(sessionCache);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public SessionCache get(String sessionId) {
        String value = (String) redisTemplate.opsForValue().get(RedisPrefix.SESSION_CACHE + sessionId);
        SessionCache sessionCache = null;
        if (!StringUtils.isEmpty(value)) {
            sessionCache = JSON.parseObject(value, SessionCache.class);
        }
        return sessionCache;
    }

    @Override
    public void remove(String sessionId) {
        String key = RedisPrefix.SESSION_CACHE + sessionId;
        redisTemplate.delete(key);
    }
}
