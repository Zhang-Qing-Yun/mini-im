package com.qingyun.im.client.handle;

import com.qingyun.im.client.imClient.ImClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理发生异常的情况
 * @author: 張青云
 * @create: 2021-11-05 22:19
 **/
@Component
@Slf4j
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ImClient imClient;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("发生异常，与服务器断开连接");
        log.info("正在尝试重连");
        imClient.restart();
    }
}
