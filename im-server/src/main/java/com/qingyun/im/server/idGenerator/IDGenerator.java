package com.qingyun.im.server.idGenerator;

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

    static IDGenerator getInstance(int type) {
        if (type == IDGeneratorType.UUID.getType()) {
            return new UUIDGenerator();
        }

        return new UUIDGenerator();
    }
}
