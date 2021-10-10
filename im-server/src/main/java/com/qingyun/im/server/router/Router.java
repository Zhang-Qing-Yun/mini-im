package com.qingyun.im.server.router;

import com.qingyun.im.common.entity.Notification;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.server.config.AttributeConfig;
import com.qingyun.im.server.entity.ImNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @description： 每一个Router都和一台Server保持长连接，用来转发消息。
 * @author: 張青云
 * @create: 2021-10-09 21:07
 **/
@Component
@Scope("prototype")
public class Router {
    //  与该Router连接的Server的节点信息
    private ImNode remoteNode;

    //  与Server的通道
    private Channel channel;

    //  重试次数
    private int retryCount = 0;

    private Bootstrap b;
    private EventLoopGroup group;

    @Autowired
    private AttributeConfig attribute;

    @Autowired
    private ImWorker imWorker;


    /**
     * 执行初始化操作
     * @param remoteNode
     */
    public void init(ImNode remoteNode) {
        this.remoteNode = remoteNode;
        group = new NioEventLoopGroup();
        initBootstrap();
        retryCount = attribute.getMaxRetryCount();
    }

    /**
     * 初始化Bootstrap
     */
    private void initBootstrap() {
        b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //  TODO：添加handle
                    }
                });
    }

    /**
     * 连接Server
     */
    public Channel doConnect() throws Exception {
        //  设置Server的ip和端口号
        b.remoteAddress(remoteNode.getIp(), remoteNode.getPort());
        //  开始连接远程Server
        CompletableFuture<io.netty.channel.Channel> f = new CompletableFuture<>();
        b.connect().addListener((ChannelFutureListener) future -> {
            //  连接成功
            if (future.isSuccess()) {
                f.complete(future.channel());
                retryCount = 0;
            } else {
                //  是否需要继续重试
                if (retryCount >= attribute.getMaxRetryCount()) {
                    throw new IMException(Exceptions.CONNECT_ERROR.getCode(), Exceptions.CONNECT_ERROR.getMessage());
                }
                //  尝试重新连接
                group.schedule(() -> {
                    retryCount++;
                    Channel channel = doConnect();
                    f.complete(channel);
                    return channel;
                }, attribute.getRetryInterval(), TimeUnit.MILLISECONDS);
            }
        });
        //  阻塞,获取channel
        channel =  f.get();
        return channel;
    }

    /**
     * 向Server发送连接成功通知
     */
    public void sendConnectNotification() {
        Notification<ImNode> notification = new Notification<>(imWorker.getImNode());
        notification.setType(Notification.CONNECT_FINISHED);
        //  TODO：发送连接成功通知
    }

    /**
     * 向Server发送数据
     */
    public void writeAndFlush(Object pkg) {
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(pkg);
    }
}
