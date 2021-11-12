package com.qingyun.im.client.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @description： 处理服务端返回的ID申请的应答消息
 * @author: 張青云
 * @create: 2021-11-13 01:11
 **/
@Component
@ChannelHandler.Sharable
public class IDRespHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    //  相当于一把锁，用于阻塞消息发送直至从服务端获取到消息唯一id
    private CompletableFuture<Long> idWait = null;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.IDRespType)) {
            ctx.fireChannelRead(msg);
            return;
        }

        idWait.complete(msg.getSequence());
        idWait = null;
    }

    public void setIdWait(CompletableFuture<Long> idWait) {
        this.idWait = idWait;
    }
}
