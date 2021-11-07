package com.qingyun.im.client.imClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.command.CommandContext;
import com.qingyun.im.client.config.AttributeConfig;
import com.qingyun.im.client.handle.ChatMsgHandle;
import com.qingyun.im.client.handle.ExceptionHandler;
import com.qingyun.im.client.handle.HeartBeatHandle;
import com.qingyun.im.client.handle.ShakeHandRespHandle;
import com.qingyun.im.client.loadBalancer.LoadBalancer;
import com.qingyun.im.client.pojo.UserInfo;
import com.qingyun.im.client.sender.ShakeHandSender;
import com.qingyun.im.client.task.CommandScan;
import com.qingyun.im.common.codec.ProtobufDecoder;
import com.qingyun.im.common.codec.ProtobufEncoder;
import com.qingyun.im.common.constants.HeartBeatConstants;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.entity.R;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.enums.LoadBalancerType;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.util.HttpClient;
import com.qingyun.im.common.util.LogoUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @description： 客户端
 * @author: 張青云
 * @create: 2021-10-01 15:19
 **/
@Component
@Slf4j
public class ImClient {
    //  用于读取并处理命令的线程
    private Thread commandThread;

    //  用于和Netty Server通信的channel
    private Channel channel;

    //  连接已重试的次数
    private int retryCount = 0;

    //  负载均衡策略，默认使用随机负载均衡策略
    private int loadBalancerType = LoadBalancerType.RANDOM.getType();

    //  锁
    private final Object o = new Object();

    private EventLoopGroup group;

    private Bootstrap b;

    @Value("${auth.address}")
    private String authAddress;

    @Value("${auth.getFriendListUrl}")
    private String getFriendListUrl;

    @Value("${auth.loginUrl}")
    private String loginUrl;

    @Autowired
    private CommandScan commandScan;

    @Autowired
    private CommandContext commandContext;

    @Autowired
    private ClientSession session;

    @Autowired
    private AttributeConfig attribute;

    @Autowired
    private ShakeHandSender handSender;

    @Autowired
    private ShakeHandRespHandle handRespHandle;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private FriendList friendList;

    @Autowired
    private ChatMsgHandle chatMsgHandle;

    @Autowired
    private HeartBeatHandle heartBeatHandle;

    @Autowired
    private ExceptionHandler exceptionHandler;


    public ImClient() {

    }


    @PostConstruct  // 在BeanPostProcessor的前置处理器处被执行
    private void init() {
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
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(HeartBeatConstants.READER_IDLE, 0, 0))
                                .addLast("decoder", new ProtobufDecoder())
                                .addLast("encoder", new ProtobufEncoder())
                                .addLast("heartBeatHandle", heartBeatHandle)
                                .addLast("handRespHandle", handRespHandle)
                                .addLast("chatMsgHandle", chatMsgHandle)
                                .addLast("exceptionHandler", exceptionHandler);
                    }
                });
    }

    /**
     * 初始化命令线程
     */
    private void initCommandThread() {
        if (commandThread != null) {
            commandThread.stop();
            System.out.println("******重连以后请前先输入help命令******");
        }
        Scanner scanner = new Scanner(System.in);
        commandScan.setScanner(scanner);
        commandThread = new Thread(commandScan);
        commandThread.setName("命令线程");
    }

    /**
     * 启动客户端
     */
    public void start() {
        //  执行登录命令并连接到服务器
        invokeLoginCommand();
        //  设置ClientSession
        session.setLogin(true);
        session.setUserInfo(UserInfo.getInstance());
        session.setConnected(true);
        //  加载好友列表
        try {
            List<String> list = getFriendList();
            friendList.initFriendList(list);
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.GET_FRIEND_LIST.getCode(), Exceptions.GET_FRIEND_LIST.getMessage());
        }
        //  发送握手消息
        handSender.sendShakeHandMsg();
        //  阻塞
        try {
            await();
        } catch (InterruptedException e) {
            throw new IMRuntimeException(Exceptions.INTERRUPT.getCode(), Exceptions.INTERRUPT.getMessage());
        }
        log.info("客户端成功连接到【{}】服务器", session.getImNode().getId());
        System.out.println("成功连接到服务器，可以输入命令了");
        LogoUtil.printSplitLine();
        //  启动命令线程
        initCommandThread();
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
        boolean isSuccess = false;
        while (!isSuccess) {
            if (!UserInfo.getInstance().isInit()) {
                //  获取用户名和密码
                scanUser();
            }
            UserInfo userInfo = UserInfo.getInstance();
            String commandValue = Command.LOGIN.getCommandKey() + " " +
                    userInfo.getUsername() + " " + userInfo.getPassword();
            isSuccess = commandContext.invokeHandle(commandValue);
        }
        UserInfo.getInstance().setInit(true);
    }

    /**
     * 与NettyServer建立连接
     * @return 建立连接后用于通信的channel，如果没有连接成功则返回null
     */
    protected Channel doConnect(String ip, int port) throws Exception {
        CompletableFuture<Channel> f = new CompletableFuture<>();
        b.connect(ip, port).addListener((ChannelFutureListener) future -> {
            //  连接成功
            if (future.isSuccess()) {
                f.complete(future.channel());
                retryCount = 0;
            } else {
                //  是否需要继续重试
                if (retryCount >= attribute.getMaxRetryCount()) {
                    f.complete(null);
                    return;
//                    throw new IMException(Exceptions.CONNECT_ERROR.getCode(), Exceptions.CONNECT_ERROR.getMessage());
                }
                //  尝试重新连接
                group.schedule(() -> {
                    retryCount++;
                    Channel channel = doConnect(ip, port);
                    f.complete(channel);
                    return channel;
                }, attribute.getRetryInterval(), TimeUnit.MILLISECONDS);
            }
        });
        //  阻塞,获取channel
        return f.get();
    }

    /**
     * 登录并选择一台服务器进行连接
     * @return 连接到的服务器的channel
     */
    public Channel loginAndGetNode(String username, String password) throws Exception {
        //  发HTTP请求登录的过程
        String url = authAddress + loginUrl;
        JSONObject param = new JSONObject();
        param.put("username", username);
        param.put("password", password);
        Response response = HttpClient.post(okHttpClient, param.toString(), url);
        //  判断是否登陆成功
        String string = response.body().string();
        R result = JSON.parseObject(string, R.class);
        if (!result.getSuccess()) {
            System.out.println(result.getMessage());
            throw new IMException(Exceptions.LOGIN_ERROR.getCode(), Exceptions.LOGIN_ERROR.getMessage());
        }
        //  获取Server列表
        String nodes = JSON.parseObject(JSON.parseObject(string).getString("data")).getString("imNodes");
        List<ImNode> imNodes = JSON.parseArray(nodes, ImNode.class);
        if (imNodes == null || imNodes.size() == 0) {
            throw new IMRuntimeException(Exceptions.NO_SERVER.getCode(), Exceptions.NO_SERVER.getMessage());
        }
        //  获取负载均衡策略
        LoadBalancer loadBalancer = LoadBalancer.getInstance(loadBalancerType);
        //  选择一台Server
        ImNode imNode = null;
        Channel channel = null;
        while (!imNodes.isEmpty()) {
            imNode = loadBalancer.select(imNodes, username);
            if (imNode.isReady()) {
                //  处理Server下线但是ZK上还没来得及删除的情况
                channel = doConnect(imNode.getIp(), imNode.getPort());
                if (channel != null) {
                    session.setImNode(imNode);
                    session.setChannel(channel);
                    this.channel = channel;
                    break;
                } else {
                    imNodes.remove(imNode);
                }
            } else {
                //  从集合中删除不可用结点
                imNodes.remove(imNode);
            }
        }
        if (imNode == null) {
            throw new IMRuntimeException(Exceptions.NO_SERVER.getCode(), Exceptions.NO_SERVER.getMessage());
        }
        return channel;
    }

    /**
     * 重新启动
     */
    public void restart() {
        //  关闭心跳任务
        handRespHandle.cancelHeartBeat();
        //  断开与原服务器的连接
        session.getChannel().close();
        //  重新选择一台服务器并建立连接
        start();
    }

    /**
     * 获取好友列表
     * @return 好友列表
     */
    protected List<String> getFriendList() throws Exception{
        //  获取当前登录用户
        String username = session.getUserInfo().getUsername();
        //  发HTTP请求登录的过程
        String url = authAddress + getFriendListUrl;
        Map<String, String> param = new HashMap<>();
        param.put("username", username);
        Response response = HttpClient.get(okHttpClient, param, url);
        //  解析结果
        R result = JSON.parseObject(response.body().string(), R.class);
        if (!result.getSuccess()) {
            System.out.println(result.getMessage());
            throw new IMException(Exceptions.GET_FRIEND_LIST.getCode(), Exceptions.GET_FRIEND_LIST.getMessage());
        }

        //  好友列表
        return (List<String>) result.getData().get("friendList");
    }


    public int getLoadBalancerType() {
        return loadBalancerType;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setLoadBalancerType(int loadBalancerType) {
        this.loadBalancerType = loadBalancerType;
    }

    private void await() throws InterruptedException {
        synchronized (o) {
            o.wait();
        }
    }

    public void go() {
        synchronized (o) {
            o.notify();
        }
    }
}
