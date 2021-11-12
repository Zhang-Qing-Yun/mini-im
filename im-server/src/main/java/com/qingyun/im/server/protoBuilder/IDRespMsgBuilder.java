package com.qingyun.im.server.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 生成获取id的应答消息
 * @author: 張青云
 * @create: 2021-11-13 01:01
 **/
public class IDRespMsgBuilder {
    public static ProtoMsg.Message buildIDRespMsg(long sequence) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.IDRespType)
                .setSequence(sequence);
        return mb.build();
    }
}
