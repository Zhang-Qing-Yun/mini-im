package com.qingyun.im.server.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 用来构建通知类型消息的工具类
 * @author: 張青云
 * @create: 2021-10-10 16:22
 **/
public class NotificationMsgBuilder {
    /**
     * 构建通知类型的消息
     * @param json 通知的内容，要求是json格式
     * @return 消息
     */
    public static ProtoMsg.Message buildNotification(String json) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.NotificationType);   //设置消息类型

        //  设置具体内容
        ProtoMsg.Notification.Builder rb =
                ProtoMsg.Notification.newBuilder()
                        .setJson(json);
        mb.setNotification(rb.build());
        return mb.build();
    }
}
