package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理客户端退出
 * @author: 張青云
 * @create: 2021-11-07 16:28
 **/
@Component
@ChannelHandler.Sharable
@Slf4j
public class LogoutHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private SessionManager sessionManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.LogoutType)) {
            //  如果不是退出消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  删除在路由层的痕迹
        sessionManager.removeSession(msg.getSessionId());
        //  关闭连接
        ctx.close();
        log.info("已关闭与【sessionId：{}】的连接", msg.getSessionId());
    }
}
