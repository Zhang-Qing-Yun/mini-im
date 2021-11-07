package com.qingyun.im.client.handle;

import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandler;
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
@ChannelHandler.Sharable
public class HeartBeatHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private ImClient imClient;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.PongType)) {
            //  如果不是Pong消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

//        log.info("客户端接收到服务端发送的Pong消息");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                //  重新选择一台服务器并建立连接
                imClient.restart();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
