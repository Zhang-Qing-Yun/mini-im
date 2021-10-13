package com.qingyun.im.common.enums;

/**
 * @description： id生成器的类型
 * @author: 張青云
 * @create: 2021-10-13 14:55
 **/
public enum IDGeneratorType {
    UUID(1);

    //  类型编号
    int type;

    IDGeneratorType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
