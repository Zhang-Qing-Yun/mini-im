package com.qingyun.im.common.idGenerator;

import com.qingyun.im.common.constants.RedisPrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * @description： 借助Redis来生成分布式唯一id，使用之前必须调用init方法来初始化，但是只需要初始化一次
 * @author: 張青云
 * @create: 2021-10-20 13:15
 **/
public class RedisGenerator extends AbstractIDGenerator {
    //  redis中的key
    private static final String KEY = RedisPrefix.ID_GENERATOR + "id";

    private RedisTemplate<String, Object> redisTemplate;

    private static RedisGenerator instance;

    //  是否初始化过了，只需要初始化一次
    private boolean isInit = false;


    private RedisGenerator() {

    }

    public static synchronized RedisGenerator getInstance() {
        if (instance == null) {
            instance = new RedisGenerator();
        }
        return instance;
    }


    /**
     * 初始化
     */
    public synchronized void init(RedisTemplate<String, Object> redisTemplate) {
        if (isInit) {
            return;
        }
        this.redisTemplate = redisTemplate;
        //  初始化值
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(KEY, redisTemplate.getConnectionFactory());
        redisAtomicLong.set(0L);
        isInit = true;
    }

    @Override
    public long generatorLongID() {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(KEY, redisTemplate.getConnectionFactory());
        return redisAtomicLong.incrementAndGet();
    }
}
