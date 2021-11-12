package com.qingyun.im.server.imServer;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.codec.ProtobufDecoder;
import com.qingyun.im.common.codec.ProtobufEncoder;
import com.qingyun.im.common.constants.HeartBeatConstants;
import com.qingyun.im.common.constants.ServerConstants;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.enums.IDGeneratorType;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.idGenerator.IDGenerator;
import com.qingyun.im.common.idGenerator.SnowFlake;
import com.qingyun.im.common.util.IOUtil;
import com.qingyun.im.server.config.AttributeConfig;
import com.qingyun.im.server.handle.*;
import com.qingyun.im.server.router.ImWorker;
import com.qingyun.im.server.router.manager.WaitManager;
import com.qingyun.im.common.zk.CuratorZKClient;
import com.qingyun.im.server.router.zk.ZKListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description： NettyServer
 * @author: 張青云
 * @create: 2021-10-05 19:42
 **/
@Component
@Slf4j
public class ImServer {
    //  分布式id生成策略
    private int idGeneratorType = IDGeneratorType.UUID.getType();

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
    private SnowFlake idGenerator;

    @Autowired
    private ZKListener listener;

    @Autowired
    private CuratorZKClient curatorZKClient;

    @Autowired
    private WaitManager waitManager;

    @Autowired
    private NotificationHandler notificationHandler;

    @Autowired
    private ShakeHandReqHandle handReqHandle;

    @Autowired
    private IDAskHandle idAskHandle;

    @Autowired
    private ChatMsgHandle chatMsgHandle;

    @Autowired
    private HeartBeatHandle heartBeatHandle;

    @Autowired
    private LogoutHandle logoutHandle;

    @Autowired
    private ExceptionHandler exceptionHandler;


    public ImServer() {
        ip = IOUtil.getHostAddress();
        b = new ServerBootstrap();
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
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(HeartBeatConstants.READER_IDLE, 0, 0))
                                .addLast("decoder", new ProtobufDecoder())
                                .addLast("encoder", new ProtobufEncoder())
                                .addLast("heartBeatHandle", heartBeatHandle)
                                .addLast("notificationHandler", notificationHandler)
                                .addLast("handReqHandle", handReqHandle)
                                .addLast("idAskHandle", idAskHandle)
                                .addLast("chatMsgHandle", chatMsgHandle)
                                .addLast("logoutHandle", logoutHandle)
                                .addLast("exceptionHandler", exceptionHandler);
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
            //  2.获取当前该集群中除了当前节点外的其它结点（这里获取到的是该路径下的结点的名字）
            //  创建父结点
            imWorker.createParentIfNeeded(ServerConstants.MANAGE_PATH);
            List<String> children = curatorZKClient.getChildren(ServerConstants.MANAGE_PATH);
            //  3.向注册中心发布当前结点，即创建一个临时结点
            imWorker.setLocalNode(ip, attribute.getPort());
            imWorker.init();
            //  4.开始监听集群变化，即监听Zk结点的变化
            listener.setListener();
            //  5.阻塞，直到与所有的Server都建立了双向连接为止
            waitManager.init(getWaitNodesByNames(children));
            waitManager.await(attribute.getMaxStartTime());
            //  判断是否是超时退出
            if (!waitManager.isCanGo()) {
                log.info("等待建立结点互联的过程中超时");
                throw new IMException(Exceptions.START_FAIL.getCode(), Exceptions.START_FAIL.getMessage());
            }
            //  6.注册该Server下线时的钩子函数，执行一些关闭前的操作
            Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownTask()));
            //  7.设置雪花算法生成器的机器编号
            idGenerator.init(0, imWorker.getImNode().getId());
            //  8.更新ZK结点的状态为可用，可以接收客户端连接了
            imWorker.getImNode().setReady(true);
            curatorZKClient.setNodeData(imWorker.getPathRegistered(), JSON.toJSONBytes(imWorker.getImNode()));
            log.info("结点{}启动成功", imWorker.getImNode().getId());
            //  9.等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IMRuntimeException(Exceptions.START_FAIL.getCode(), Exceptions.START_FAIL.getMessage());
        } finally {
            //  关闭与ZK的连接（会删除临时结点），这时其它结点会监听到该结点下线，更新其路由表
            curatorZKClient.closeConnection();
            //  关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("结点{}下线", imWorker.getImNode().getId());
        }
    }

    /**
     * 根据结点路径的集合获取要等待的结点
     * @param names 路径集合
     * @return 要等待的结点的集合
     */
    private CopyOnWriteArraySet<Long> getWaitNodesByNames(List<String> names) {
        CopyOnWriteArraySet<Long> waitNodes = new CopyOnWriteArraySet<>();
        for (String path: names) {
            waitNodes.add(imWorker.getIdByName(path));
        }
        return waitNodes;
    }


    /**
     * Netty Server下线时的操作
     */
    final class ShutdownTask implements Runnable {

        @Override
        public void run() {
            //  关闭与ZK的连接（会删除临时结点），这时其它结点会监听到该结点下线，更新其路由表
            curatorZKClient.closeConnection();
            //  关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public int getIdGeneratorType() {
        return idGeneratorType;
    }

    public void setIdGeneratorType(int idGeneratorType) {
        this.idGeneratorType = idGeneratorType;
    }
}
