package com.qingyun.im.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @description： 离线消息
 * @author: 張青云
 * @create: 2021-11-15 14:16
 **/
@Data
@Accessors(chain = true)  // 链式编程
public class Msg {
    //  消息的全局唯一id
    private Long id;

    //  消息发送者的用户id
    private String fromUsername;

    //  消息接收者的用户名
    private String toUsername;

    //  消息接收者的id
    private Long toUserId;

    private String context;

    private Long sendTime;

    private int msgStatus;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtUpdate;
}
