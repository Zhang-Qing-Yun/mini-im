package com.qingyun.im.server.imServer;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.codec.ProtobufDecoder;
import com.qingyun.im.common.codec.ProtobufEncoder;
import com.qingyun.im.common.constants.ServerConstants;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.util.IOUtil;
import com.qingyun.im.server.config.AttributeConfig;
import com.qingyun.im.server.router.ImWorker;
import com.qingyun.im.server.router.manager.WaitManager;
import com.qingyun.im.server.router.zk.CuratorZKClient;
import com.qingyun.im.server.router.zk.ZKListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

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

    //  本地ip地址
    private String ip;

    @Autowired
    private AttributeConfig attribute;

    @Autowired
    private ImWorker imWorker;

    @Autowired
    private ZKListener listener;

    @Autowired
    private CuratorZKClient curatorZKClient;

    @Autowired
    private WaitManager waitManager;


    public ImServer() {
        ip = IOUtil.getHostAddress();
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
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //  TODO:添加handle
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new ProtobufDecoder())
                                .addLast("encoder", new ProtobufEncoder());
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
            //  1.开始接收连接请求
            future = b.bind(ip, attribute.getPort()).sync();
            //  2.获取当前该集群中除了当前节点外的其它结点
            List<String> children = curatorZKClient.getChildren(ServerConstants.MANAGE_PATH);
            //  3.向注册中心发布当前结点，即创建一个临时结点
            imWorker.setLocalNode(ip, attribute.getPort());
            imWorker.init();
            //  4.开始监听集群变化，即监听Zk结点的变化
            listener.setListener();
            //  TODO：5.阻塞，直到与所有的Server都建立了双向连接为止
            waitManager.init(new CopyOnWriteArraySet<>(children));
            waitManager.await(attribute.getMaxStartTime());
            //  判断是否是超时退出
            if (!waitManager.isCanGo()) {
                throw new IMException(Exceptions.START_FAIL.getCode(), Exceptions.START_FAIL.getMessage());
            }

            //  TODO：注册该Server下线时的钩子函数(清除在路由层的信息等操作)

            //  更新ZK结点的状态为可用，可以接收客户端连接了
            imWorker.getImNode().setReady(true);
            curatorZKClient.setNodeData(imWorker.getPathRegistered(), JSON.toJSONBytes(imWorker.getImNode()));
            //  等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            //  TODO；没有启动成功则删除一切痕迹
            throw new IMRuntimeException(Exceptions.START_FAIL.getCode(), Exceptions.START_FAIL.getMessage());
        } finally {
            //  关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
