package com.qingyun.im.client.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理心跳消息
 * @author: 張青云
 * @create: 2021-11-03 22:10
 **/
@Component
@Slf4j
public class HeartBeatHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private ShakeHandRespHandle shakeHandRespHandle;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.PongType)) {
            //  如果不是Pong消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        log.info("客户端接收到服务端发送的Pong消息");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                shakeHandRespHandle.cancelHeartBeat();
                //  TODO：通过心跳发现断连
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
}
