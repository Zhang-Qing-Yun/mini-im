package com.qingyun.im.client.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建退出消息
 * @author: 張青云
 * @create: 2021-11-07 16:17
 **/
public class LogoutMsgBuilder {
    public static ProtoMsg.Message buildLogoutMsg(String sessionId) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.LogoutType)
                .setSessionId(sessionId);
        return mb.build();
    }
}
