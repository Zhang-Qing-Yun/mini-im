package com.qingyun.im.client.handle;

import com.qingyun.im.client.msgCache.AvoidRepeatManager;
import com.qingyun.im.client.msgCache.MsgCacheManager;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.protoBuilder.AckMsgBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理聊天消息
 * @author: 張青云
 * @create: 2022-12-21 18:03
 **/
@Component
@ChannelHandler.Sharable
@Slf4j
public class ChatMsgHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private MsgCacheManager msgManager;

    @Autowired
    private AvoidRepeatManager avoidRepeatManager;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.MsgType)) {
            //  如果不是聊天消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  进行防重过滤，如果防重集合中已有则不再接收该消息
        if (avoidRepeatManager.contains(msg.getSequence())) {
            log.info("接收到重复消息【{}：{}】，已过滤", msg.getSessionId(), msg.getMsg().getContext());
            return;
        }
        //  添加到防重集合中
        avoidRepeatManager.add(msg.getSequence());
        //  将消息添加到本地消息缓存中，相当于接收该消息
        msgManager.addMsg(msg);
        //  回送ack消息，注意对于ack消息来说，接收者为原消息的发送方
        ProtoMsg.Msg message = msg.getMsg();
        ProtoMsg.Message ackMsg = AckMsgBuilder.buildAckMsg(msg.getSequence(), message.getFrom(), message.getTo());
        ctx.writeAndFlush(ackMsg);
    }
}
