package com.qingyun.im.client.handle;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理握手应答
 * @author: 張青云
 * @create: 2021-10-13 23:36
 **/
@Component
public class ShakeHandRespHandle extends SimpleChannelInboundHandler<ProtoMsg.Message>{
    @Autowired
    private ClientSession session;

    @Autowired
    private ImClient imClient;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.ShakeHandRespType)) {
            //  如果不是握手应答消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }
        //  为session设置id
        session.setSessionId(msg.getSessionId());
        //  解除ImClient的阻塞
        imClient.go();
    }
}
