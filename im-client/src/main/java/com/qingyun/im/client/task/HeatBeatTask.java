package com.qingyun.im.client.task;

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
    private final ChannelHandlerContext ctx;

    public HeatBeatTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        ProtoMsg.Message pingMsg = PingMsgBuilder.buildPingMsg();
        ctx.writeAndFlush(pingMsg).addListener(future -> {
            if (future.isSuccess()) {
                log.info("客户端成功向服务端发送了心跳消息");
            } else {
                log.error("客户端心跳消息发送失败");
            }
        });
    }
}
