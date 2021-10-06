package com.qingyun.im.server.imServer;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.server.config.AttributeConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： NettyServer
 * @author: 張青云
 * @create: 2021-10-05 19:42
 **/
@Component
public class ImServer {

    //  线程组
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    //  启动引导类
    private ServerBootstrap b;

    @Autowired
    private AttributeConfig attribute;


    public ImServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        initBootstrap();
    }

    /**
     * 初始化Bootstrap
     */
    private void initBootstrap() {
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //  TODO:添加handle
                    }
                });
    }

    /**
     * 启动NettyServer
     */
    public void start() {
        //  绑定端口,并以同步阻塞的方式启动Netty
        ChannelFuture future = null;
        try {
            future = b.bind(attribute.getPort()).sync();
            //  TODO:向路由层发布
            //  TODO:注册该Server下线时的钩子函数(清除在路由层的信息等操作)

            //  等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new IMRuntimeException(Exceptions.START_FAIL.getCode(), Exceptions.START_FAIL.getMessage());
        } finally {
            //  关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
