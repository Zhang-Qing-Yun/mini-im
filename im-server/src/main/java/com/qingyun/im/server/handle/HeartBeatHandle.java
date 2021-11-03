package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.server.protoBuilder.PongMsgBuilder;
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

        //  回送Pong消息
        ProtoMsg.Message pongMsg = PongMsgBuilder.buildPongMsg();
        ctx.writeAndFlush(pongMsg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                //  TODO：处理服务端检测到客户端断线
                //  在规定时间内没有收到客户端发送的数据, 主动断开连接
//                log.info("长时间未收到心跳消息，断开连接！");
//                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
