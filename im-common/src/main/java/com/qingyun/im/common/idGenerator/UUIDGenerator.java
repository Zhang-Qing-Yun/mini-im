package com.qingyun.im.common.idGenerator;

import java.util.UUID;

/**
 * @description： 使用UUID来生成全局唯一id
 * @author: 張青云
 * @create: 2022-12-13 14:39
 **/
public class UUIDGenerator implements IDGenerator {


    @Override
    public String generatorID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
