package com.qingyun.im.client.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建聊天消息
 * @author: 張青云
 * @create: 2021-10-14 21:17
 **/
public class ChatMsgBuilder {

    public static ProtoMsg.Message buildChatMsg(String from, String to, String context,
                                                String sessionId, long sequence) {
        ProtoMsg.Msg.Builder cb = ProtoMsg.Msg.newBuilder()
                .setFrom(from)
                .setTo(to)
                .setContext(context)
                .setDatetime(System.currentTimeMillis());

        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.MsgType)
                .setSessionId(sessionId)
                .setSequence(sequence);
        mb.setMsg(cb.build());
        return mb.build();
    }
}
