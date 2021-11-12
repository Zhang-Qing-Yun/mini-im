package com.qingyun.im.client.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建ID申请消息
 * @author: 張青云
 * @create: 2021-11-13 01:42
 **/
public class IDAskMsgBuilder {
    public static ProtoMsg.Message buildIDAskMsg() {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.IDAskType);
        return mb.build();
    }
}
