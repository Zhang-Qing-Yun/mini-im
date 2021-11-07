package com.qingyun.im.server.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qingyun.im.common.entity.Notification;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.server.router.ImWorker;
import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.router.RouterMap;
import com.qingyun.im.server.router.manager.WaitManager;
import com.qingyun.im.server.util.SpringContextUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @description： 用来处理服务器节点间通知的handle
 * @author: 張青云
 * @create: 2021-10-10 19:09
 **/
@Component
@ChannelHandler.Sharable
@Slf4j
public class NotificationHandler extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private SpringContextUtil contextUtil;

    @Autowired
    private RouterMap routerMap;

    @Autowired
    private WaitManager waitManager;

    @Autowired
    private ImWorker imWorker;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.NotificationType)) {
            //  如果不是通知类型的消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  获取通知
        ProtoMsg.Notification notificationMsg = msg.getNotification();
        String json = notificationMsg.getJson();
        Notification<ImNode> notification = JSON.parseObject(json, new TypeReference<Notification<ImNode>>(){});

        //  连接成功类型的通知
        if (notification.getType() == Notification.CONNECT_FINISHED) {
            //  阻塞，直至完成等待集合的初始化
            waitManager.awaitInit();
            //  为了实现结点互联，需要创建转发器并与对方连接
            ApplicationContext context = contextUtil.getContext();
            Router router = context.getBean(Router.class);
            router.init(notification.getData());
            //  与对方建立连接
            try {
                router.doConnect();
            } catch (Exception e) {
                //  如果连接失败，则直接放弃连接。新上线的结点在收不到当前结点的Notification时，就会超时报错，启动失败。
                router.stopConnecting();
                return;
            }
            //  发送CONNECT_ACK
            router.sendConnectAckNotification();
            //  将该router保存起来
            routerMap.addCandidate(notification.getData().getId(), router);
        }

        //  CONNECT_ACK
        if (notification.getType() == Notification.CONNECT_ACK) {
            //  更新转发表
            long id = notification.getData().getId();
            routerMap.toFullRouter(id);
            log.info("结点{}创建了到结点{}的转发器", imWorker.getImNode().getId(), notification.getData().getId());
            //  标识这个channel所对应的是一个转发器
            ctx.channel().attr(Router.ROUTER_KEY).set(id);
            //  回送ACK_ACK
            Router router = routerMap.getRouterByNodeId(id);
            router.sendAckAckNotification();
        }

        //  ACK_ACK
        if (notification.getType() == Notification.ACK_ACK) {
            ImNode node = notification.getData();
            //  更新转发表
            routerMap.toFullRouter(node.getId());
            //  标识这个channel所对应的是一个转发器
            ctx.channel().attr(Router.ROUTER_KEY).set(node.getId());
            log.info("结点{}创建了到结点{}的转发器，两者完成了三次握手，成功建立了双向连接",
                    imWorker.getImNode().getId(), notification.getData().getId());
            //  更新等待集合
            waitManager.decrease(node.getId());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
