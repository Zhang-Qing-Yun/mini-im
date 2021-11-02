package com.qingyun.im.server.session.dao.impl;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.constants.RedisPrefix;
import com.qingyun.im.server.session.dao.UserCacheDao;
import com.qingyun.im.server.session.entity.SessionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-02 09:55
 **/
@Component
public class UserCacheDaoRedisImpl implements UserCacheDao {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addSession(String username, SessionCache sessionCache) {
        String key = RedisPrefix.USER_CACHE + username;
        //  将value序列化成json
        String value = JSON.toJSONString(sessionCache);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public SessionCache getSession(String username) {
        String value = (String) redisTemplate.opsForValue().get(RedisPrefix.USER_CACHE + username);
        SessionCache sessionCache = null;
        if (!StringUtils.isEmpty(value)) {
            sessionCache = JSON.parseObject(value, SessionCache.class);
        }
        return sessionCache;
    }

    @Override
    public void removeSession(String username) {
        String key = RedisPrefix.USER_CACHE + username;
        redisTemplate.delete(key);
    }
}
