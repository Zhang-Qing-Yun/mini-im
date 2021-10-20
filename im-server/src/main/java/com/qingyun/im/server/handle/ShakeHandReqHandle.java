package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.idGenerator.IDGenerator;
import com.qingyun.im.server.imServer.ImServer;
import com.qingyun.im.server.protoBuilder.ShakeHandRespMsgBuilder;
import com.qingyun.im.server.session.LocalSession;
import com.qingyun.im.server.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理握手消息
 * @author: 張青云
 * @create: 2021-10-13 14:06
 **/
@Component
public class ShakeHandReqHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private ImServer imServer;

    @Autowired
    private SessionManager sessionManager;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.ShakeHandReqType)) {
            //  如果不是握手消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  获取握手消息
        ProtoMsg.ShakeHandReq handReq = msg.getShakeHandReq();
        String username = handReq.getUsername();
        //  创建session
        LocalSession localSession = new LocalSession(username);
        localSession.setChannel(ctx.channel());
        //  生成全局唯一id
        IDGenerator idGenerator = IDGenerator.getInstance(imServer.getIdGeneratorType());
        String sessionId = idGenerator.generatorID();
        localSession.setSessionId(sessionId);
        //  保存session
        sessionManager.saveSessionCache(sessionId, localSession);
        //  回送握手应答消息
        ProtoMsg.Message pkg = ShakeHandRespMsgBuilder.buildHandRespMsg(sessionId);
        localSession.writeAndFlush(pkg);
    }
}
