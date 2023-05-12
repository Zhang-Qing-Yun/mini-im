package com.qingyun.im.auth.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @description： 自动填充，这里是用来填充时间
 * @author: 張青云
 * @create: 2022-11-24 09:18
 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    //  使用mp实现添加操作，这个方法执行
    public void insertFill(MetaObject metaObject) {
        //  第一个参数：属性名；第二个参数：要填充的属性值；第三个参数：元数据
        this.setFieldValByName("gmtCreate", LocalDateTime.now(), metaObject);
        this.setFieldValByName("gmtUpdate", LocalDateTime.now(), metaObject);
    }

    @Override
    //  使用mp实现修改操作，这个方法执行
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtUpdate", LocalDateTime.now(), metaObject);
    }
}
