package com.qingyun.im.server.protoBuilder;

import com.qingyun.im.common.entity.ProtoMsg;

/**
 * @description： 构建握手应答消息
 * @author: 張青云
 * @create: 2022-12-13 17:07
 **/
public class ShakeHandRespMsgBuilder {

    public static ProtoMsg.Message buildHandRespMsg(String sessionId) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.Message.Type.ShakeHandRespType)
                .setSessionId(sessionId);
        ProtoMsg.ShakeHandResp resp = ProtoMsg.ShakeHandResp.newBuilder().build();
        mb.setShakeHandResp(resp);
        return mb.build();
    }
}
