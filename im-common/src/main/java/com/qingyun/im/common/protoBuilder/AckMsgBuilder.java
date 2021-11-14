package com.qingyun.im.common.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建Ack消息
 * @author: 張青云
 * @create: 2021-11-13 19:54
 **/
public class AckMsgBuilder {
    public static ProtoMsg.Message buildAckMsg(long sequence, String to, String from) {
        ProtoMsg.Ack.Builder ab = ProtoMsg.Ack.newBuilder()
                .setTo(to)
                .setFrom(from);

        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.AckType)
                .setSequence(sequence);
        mb.setAck(ab);
        return mb.build();
    }
}
