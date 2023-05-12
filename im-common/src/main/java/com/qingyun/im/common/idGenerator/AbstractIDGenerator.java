package com.qingyun.im.common.idGenerator;

import com.qingyun.im.common.enums.IDGeneratorType;

/**
 * @description：
 * @author: 張青云
 * @create: 2022-12-20 09:28
 **/
public abstract class AbstractIDGenerator implements IDGenerator {
    @Override
    public String generatorID() {
        IDGenerator idGenerator = IDGenerator.getInstance(IDGeneratorType.DEFAULT.getType());
        return idGenerator.generatorID();
    }

    /**
     * 生成long类型的id
     * @return long类型的分布式全局唯一id
     */
    public abstract long generatorLongID();
}
