package com.qingyun.im.client.task;

import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.client.protoBuilder.PingMsgBuilder;
import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @description： 发送心跳消息
 * @author: 張青云
 * @create: 2021-11-03 21:50
 **/
@Slf4j
public class HeatBeatTask implements Runnable {
    private static final int MAX_ERR_COUNT = 2;

    private final ChannelHandlerContext ctx;

    private final String sessionId;

    private final ImClient imClient;

    private int errCount = 0;

    public HeatBeatTask(ChannelHandlerContext ctx, String sessionId, ImClient imClient) {
        this.ctx = ctx;
        this.sessionId = sessionId;
        this.imClient = imClient;
    }

    @Override
    public void run() {
        ProtoMsg.Message pingMsg = PingMsgBuilder.buildPingMsg(sessionId);
        ctx.writeAndFlush(pingMsg).addListener(future -> {
            if (future.isSuccess()) {
                errCount = 0;
//                log.info("客户端成功向服务端发送了心跳消息");
            } else {
//                log.error("客户端心跳消息发送失败");
                errCount++;
                if (errCount >= MAX_ERR_COUNT) {
//                    log.info("{}次连续心跳消息发送失败，执行重连", MAX_ERR_COUNT);
                    imClient.restart();
                }
            }
        });
    }
}
