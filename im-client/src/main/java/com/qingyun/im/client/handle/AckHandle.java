package com.qingyun.im.client.handle;

import com.qingyun.im.client.sender.MsgTimeoutTimerManager;
import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理ack消息
 * @author: 張青云
 * @create: 2021-11-13 20:11
 **/
@Component
@ChannelHandler.Sharable
@Slf4j
public class AckHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private MsgTimeoutTimerManager timeoutManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.AckType)) {
            //  如果不是ack消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  将对应的消息从超时重传管理器中删除（可能会接收到重复的ack消息）
        timeoutManager.remove(msg.getSequence());
        log.info("消息【{}】确认成功发送", msg.getSequence());
    }
}
