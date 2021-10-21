package com.qingyun.im.common.idGenerator;

import com.qingyun.im.common.enums.IDGeneratorType;

/**
 * @description： 分布式全局唯一ID生成器
 * @author: 張青云
 * @create: 2021-10-13 14:30
 **/
public interface IDGenerator {
    /**
     * 生成唯一id
     */
    String generatorID();

    /**
     * 根据类型编号获取具体的id生成器
     * @param type 类型标号
     * @return id生成器，默认使用雪花算法
     */
    static IDGenerator getInstance(int type) {
        if (type == IDGeneratorType.DEFAULT.getType()) {
            return new SnowFlake();
        }
        if (type == IDGeneratorType.UUID.getType()) {
            return new UUIDGenerator();
        } else if (type == IDGeneratorType.SNOW_FLAKE.getType()) {
            return new SnowFlake();
        } else if (type == IDGeneratorType.REDIS.getType()) {
            return RedisGenerator.getInstance();
        }

        return new SnowFlake();
    }
}
