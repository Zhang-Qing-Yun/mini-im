package com.qingyun.im.client.handle;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.client.task.HeatBeatTask;
import com.qingyun.im.common.constants.HeartBeatConstants;
import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description： 处理握手应答
 * @author: 張青云
 * @create: 2021-10-13 23:36
 **/
@Component
@ChannelHandler.Sharable
public class ShakeHandRespHandle extends SimpleChannelInboundHandler<ProtoMsg.Message>{
    private volatile ScheduledFuture<?> heartBeat;

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
        String sessionId = msg.getSessionId();
        session.setSessionId(sessionId);
        //  解除ImClient的阻塞
        imClient.go();

        //  启动定时任务，用于发送心跳消息
        startHeartBeat(ctx, sessionId);
    }

    /**
     * 启动心跳任务
     * @param ctx 要往哪个通道发送心跳
     * @param sessionId sessionId
     */
    public void startHeartBeat(ChannelHandlerContext ctx, String sessionId) {
        heartBeat = ctx.executor().scheduleAtFixedRate(new HeatBeatTask(ctx, sessionId),
                0, HeartBeatConstants.PING_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 取消心跳任务
     */
    public void cancelHeartBeat() {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
    }
}
