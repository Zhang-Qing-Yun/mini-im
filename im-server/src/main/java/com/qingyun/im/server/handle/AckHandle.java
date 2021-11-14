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
 * @description： 处理ack消息
 * @author: 張青云
 * @create: 2021-11-13 20:29
 **/
@Component
@ChannelHandler.Sharable
public class AckHandle extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SessionCacheDao sessionCacheDao;

    @Autowired
    private RouterMap routerMap;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg == null || !msg.getType().equals(ProtoMsg.Message.Type.AckType)) {
            //  如果不是ack消息则直接透传
            ctx.fireChannelRead(msg);
            return;
        }

        /*
        * 对于ack消息，如果目的用户不在线则直接丢弃掉消息
        * */

        //  获取目标用户的session
        String toUsername = msg.getAck().getTo();
        SessionCache userSessionCache = sessionManager.getUserSessionCache(toUsername);
        //  用户不在线
        if (userSessionCache == null) {
            return;
        }
        //  目标用户的sessionId
        String sessionId = userSessionCache.getSessionId();
        ServerSession serverSession;
        SessionCache sessionCache;
        //  判断接收者是否与当前Server建立连接
        if ((serverSession = sessionManager.getLocalSession(sessionId)) != null) {
            //  直接转发
            serverSession.writeAndFlush(msg);
            return;
        }
        //  将ack消息转发给目的用户所连接的服务器
        sessionCache = sessionCacheDao.get(sessionId);
        transferMsg(sessionCache, msg);
    }

    /**
     * 将ack消息转发给目的Server
     */
    private void transferMsg(SessionCache sessionCache, ProtoMsg.Message msg) {
        //  获取接收者所连接的Server
        ImNode imNode = sessionCache.getImNode();
        //  获取转发器
        Router router = routerMap.getRouterByNodeId(imNode.getId());

        if (router == null || !router.isActive()) {
            return;
        }
        //  转发消息
        router.writeAndFlush(msg);
    }
}
