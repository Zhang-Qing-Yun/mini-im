package com.qingyun.im.client.handle;

import com.qingyun.im.client.msgCache.MsgCacheManager;
import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理聊天消息
 * @author: 張青云
 * @create: 2021-10-21 18:03
 **/
@Component
@ChannelHandler.Sharable
public class ChatMsgHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {

    @Autowired
    private MsgCacheManager msgManager;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.MsgType)) {
            //  如果不是聊天消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  将消息添加到本地消息缓存中
        msgManager.addMsg(msg);
    }
}
