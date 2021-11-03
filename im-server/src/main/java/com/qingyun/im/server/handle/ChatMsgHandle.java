package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.router.RouterMap;
import com.qingyun.im.server.session.ServerSession;
import com.qingyun.im.server.session.SessionManager;
import com.qingyun.im.server.session.dao.SessionCacheDao;
import com.qingyun.im.server.session.entity.SessionCache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理聊天消息
 * @author: 張青云
 * @create: 2021-10-14 21:53
 **/
@Component
@ChannelHandler.Sharable
public class ChatMsgHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SessionCacheDao sessionCacheDao;

    @Autowired
    private RouterMap routerMap;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.MsgType)) {
            //  如果不是聊天消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        //  获取目标用户的session
        String toUsername = msg.getMsg().getTo();
        SessionCache userSessionCache = sessionManager.getUserSessionCache(toUsername);
        //  用户不在线
        if (userSessionCache == null) {
            //  处理离线消息
            handleOfflineMsg(msg);
            return;
        }

        //  目标用户的sessionId
        String sessionId = userSessionCache.getSessionId();
        ServerSession serverSession;
        SessionCache sessionCache;
        //  判断接收者是否与当前Server建立连接
        if ((serverSession = sessionManager.getLocalSession(sessionId)) != null) {
            if (serverSession.isActive()) {
                //  直接转发
                serverSession.writeAndFlush(msg);
            } else {
                //  与客户端断线了
                handleOfflineMsg(msg);
            }
            return;
        }
        //  将消息转发给目的用户所连接的服务器
        sessionCache = sessionCacheDao.get(sessionId);
        transferMsg(sessionCache, msg);
    }

    /**
     * 将消息转发给目的Server
     */
    private void transferMsg(SessionCache sessionCache, ProtoMsg.Message msg) {
        //  获取接收者所连接的Server
        ImNode imNode = sessionCache.getImNode();
        //  获取转发器
        Router router = routerMap.getRouterByNodeId(imNode.getId());

        /*
        * 正常情况下，该结点拥有到其它所有结点的转发器；
        * 如果转发器为null，则说明目标用户所在的服务器下线了，此时Redis中所保存的SessionCache是脏数据；
        * 然后我们将发给目标用户的消息当作离线消息处理；
        * 当目标用户发现自己和服务端断连后会重连，然后会拉取离线消息
        * */
        if (router == null) {
            handleOfflineMsg(msg);
            return;
        }
        //  转发消息
        router.writeAndFlush(msg);
    }

    /**
     * 处理离线消息
     */
    private void handleOfflineMsg(ProtoMsg.Message msg) {
        //  TODO：离线消息
    }
}
