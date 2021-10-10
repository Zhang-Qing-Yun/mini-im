package com.qingyun.im.common.entity;

import lombok.Data;

/**
 * @description： 通知类消息
 * @author: 張青云
 * @create: 2021-10-09 22:09
 **/
@Data
public class Notification<T> {
    public static final int CONNECT_FINISHED = 1;

    //  通知的类型
    private int type;
    //  数据
    private T data;

    public Notification() {
    }

    public Notification(T data) {
        this.data = data;
    }
}
