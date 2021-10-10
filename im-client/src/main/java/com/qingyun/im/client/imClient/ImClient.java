package com.qingyun.im.client.imClient;

import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.command.CommandContext;
import com.qingyun.im.client.config.AttributeConfig;
import com.qingyun.im.client.pojo.UserInfo;
import com.qingyun.im.client.task.CommandScan;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @description： 客户端
 * @author: 張青云
 * @create: 2021-10-01 15:19
 **/
@Component
public class ImClient {
    //  与该客户端连接的Netty服务端的ip地址
    private String serverIP;

    //  与该客户端连接的Netty服务端的端口
    private int serverPort;

    //  用于读取并处理命令的线程
    private Thread commandThread;

    //  用于和Netty Server通信的channel
    private Channel channel;

    //  连接已重试的次数
    private int retryCount = 0;

    private EventLoopGroup group;

    private Bootstrap b;

    @Autowired
    private CommandScan commandScan;

    @Autowired
    private CommandContext commandContext;

    @Autowired
    private ClientSession session;

    @Autowired
    private AttributeConfig attribute;


    public ImClient() {

    }

    @PostConstruct  // 在BeanPostProcessor的前置处理器处被执行
    private void init() {
        commandThread = new Thread(commandScan);
        commandThread.setName("命令线程");
        group = new NioEventLoopGroup();
        initBootstrap();
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
     * 启动客户端
     */
    public void start() {
        //  获取用户名和密码
        scanUser();
        //  执行登录命令
        invokeLoginCommand();
        //  设置ClientSession
        session.setLogin(true);
        session.setUserInfo(UserInfo.getInstance());
        //  连接Netty Server
//        try {
//            this.channel = doConnect();
//        } catch (Exception e) {
//            //  当连接出现问题时,直接退出
//            System.out.println("无法连接Server!");
//            throw new IMRuntimeException(Exceptions.CONNECT_ERROR.getCode(), Exceptions.CONNECT_ERROR.getMessage());
//        }
//        session.setConnected(true);
//        session.setChannel(channel);
        //  启动命令线程
        commandThread.start();
    }

    /**
     * 用来读取用户名和密码
     */
    private void scanUser() {
        Scanner sc = new Scanner(System.in);
        UserInfo userInfo = UserInfo.getInstance();
        System.out.println("请输入用户名：");
        userInfo.setUsername(sc.nextLine());
        System.out.println("请输入密码：");
        userInfo.setPassword(sc.nextLine());
    }


    /**
     * 执行登录命令
     */
    private void invokeLoginCommand() {
        UserInfo userInfo = UserInfo.getInstance();
        String commandValue = Command.LOGIN.getCommandKey() + " " +
                userInfo.getUsername() + " " + userInfo.getPassword();
        boolean isSuccess = false;
        while (!isSuccess) {
            isSuccess = commandContext.invokeHandle(commandValue);
        }
    }

    /**
     * 与NettyServer建立连接
     */
    protected Channel doConnect() throws Exception {
        CompletableFuture<Channel> f = new CompletableFuture<>();
        b.connect(serverIP, serverPort).addListener((ChannelFutureListener) future -> {
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
        return f.get();
    }
}
