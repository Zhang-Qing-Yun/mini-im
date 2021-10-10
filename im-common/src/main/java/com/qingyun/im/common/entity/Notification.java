package com.qingyun.im.common.entity;

import lombok.Data;

/**
 * @description： 服务期间的通知类消息
 * @author: 張青云
 * @create: 2021-10-09 22:09
 **/
@Data
public class Notification<T> {
    //  与新结点连接成功
    public static final int CONNECT_FINISHED = 1;
    //  接收到CONNECT_FINISHED的ack
    public static final int CONNECT_ACK = 2;
    //  收到CONNECT_ACK的ack
    public static final int ACK_ACK = 3;

    //  通知的类型
    private int type;
    //  数据
    private T data;

    public Notification() {
    }

    public Notification(T data) {
        this.data = data;
    }

    public Notification(int type, T data) {
        this.type = type;
        this.data = data;
    }
}
