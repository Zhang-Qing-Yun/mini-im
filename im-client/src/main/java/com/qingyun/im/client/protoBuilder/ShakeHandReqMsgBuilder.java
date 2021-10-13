package com.qingyun.im.client.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建握手请求消息
 * @author: 張青云
 * @create: 2021-10-13 00:10
 **/
public class ShakeHandReqMsgBuilder {

    public static ProtoMsg.Message buildShakeHandReqMsg(String username) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.ShakeHandReqType);
        ProtoMsg.ShakeHandReq.Builder sb = ProtoMsg.ShakeHandReq.newBuilder().setUsername(username);
        return mb.setShakeHandReq(sb.build()).build();
    }
}
