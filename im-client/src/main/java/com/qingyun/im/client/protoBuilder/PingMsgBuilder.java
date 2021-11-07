package com.qingyun.im.client.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建ping消息
 * @author: 張青云
 * @create: 2021-11-03 21:53
 **/
public class PingMsgBuilder {
    public static ProtoMsg.Message buildPingMsg(String sessionId) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.PingType)
                .setSessionId(sessionId);
        return mb.build();
    }
}
