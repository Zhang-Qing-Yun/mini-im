package com.qingyun.im.common.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建聊天消息
 * @author: 張青云
 * @create: 2022-12-14 21:17
 **/
public class ChatMsgBuilder {

    public static ProtoMsg.Message buildChatMsg(String from, String to, String context,
                                                String sessionId, long sequence, long datetime) {
        ProtoMsg.Msg.Builder cb = ProtoMsg.Msg.newBuilder()
                .setFrom(from)
                .setTo(to)
                .setContext(context)
                .setDatetime(datetime);

        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.MsgType)
                .setSessionId(sessionId)
                .setSequence(sequence);
        mb.setMsg(cb.build());
        return mb.build();
    }
}
