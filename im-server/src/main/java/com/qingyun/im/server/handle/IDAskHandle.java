package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.idGenerator.SnowFlake;
import com.qingyun.im.server.protoBuilder.IDRespMsgBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 生成消息的序列号（唯一ID）
 * @author: 張青云
 * @create: 2021-11-13 00:47
 **/
@Component
@ChannelHandler.Sharable
public class IDAskHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private SnowFlake idGenerator;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.IDAskType)) {
            ctx.fireChannelRead(msg);
            return;
        }

        //  使用雪花算法生成id
        long sequence = idGenerator.generatorLongID();
        //  向请求方发送应答消息
        ProtoMsg.Message message = IDRespMsgBuilder.buildIDRespMsg(sequence);
        ctx.writeAndFlush(message);
    }
}
