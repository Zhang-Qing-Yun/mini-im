package com.qingyun.im.common.enums;

/**
 * @description： id生成器的类型
 * @author: 張青云
 * @create: 2023-03-13 14:55
 **/
public enum IDGeneratorType {
    DEFAULT(0),
    UUID(1),
    SNOW_FLAKE(2),
    REDIS(3);

    //  类型编号
    int type;

    IDGeneratorType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
