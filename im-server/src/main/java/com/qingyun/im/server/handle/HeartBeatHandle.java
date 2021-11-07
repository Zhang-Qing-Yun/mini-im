package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.server.protoBuilder.PongMsgBuilder;
import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description： 服务端处理心跳
 * @author: 張青云
 * @create: 2021-11-04 00:25
 **/
@ChannelHandler.Sharable
@Component
@Slf4j
public class HeartBeatHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.PingType)) {
            //  如果不是Ping消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }
//        log.info("接收到【sessionId：{}】的心跳消息", msg.getSessionId());
        //  回送Pong消息
        ProtoMsg.Message pongMsg = PongMsgBuilder.buildPongMsg();
        ctx.writeAndFlush(pongMsg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                //  对于转发器不做处理（即转发器不使用心跳）
                if (ctx.channel().attr(Router.ROUTER_KEY).get() != null) {
                    super.userEventTriggered(ctx, evt);
                    return;
                }
                //  在规定时间内没有收到客户端发送的数据, 主动断开连接
                String sessionId = ctx.channel().attr(SessionManager.SESSION_ID_KEY).get();
                log.info("长时间未收到客户端【{}】的心跳消息，断开连接！", sessionId);
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
