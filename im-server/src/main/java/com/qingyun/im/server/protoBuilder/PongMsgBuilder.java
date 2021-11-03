package com.qingyun.im.server.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建pong消息
 * @author: 張青云
 * @create: 2021-11-04 00:40
 **/
public class PongMsgBuilder {
    public static ProtoMsg.Message buildPongMsg() {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder().setType(ProtoMsg.Message.Type.PongType);
        return mb.build();
    }
}
