package com.qingyun.im.server.handle;

import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.router.RouterMap;
import com.qingyun.im.server.session.ServerSession;
import com.qingyun.im.server.session.SessionManager;
import com.qingyun.im.server.session.dao.SessionCacheDao;
import com.qingyun.im.server.session.entity.SessionCache;
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

        String sessionId = msg.getSessionId();
        ServerSession serverSession;
        SessionCache sessionCache;
        //  判断接收者是否与当前Server建立连接
        if ((serverSession = sessionManager.getLocalSession(sessionId)) != null) {
            if (serverSession.isActive()) {
                //  直接转发
                serverSession.writeAndFlush(msg);
            } else {
                handleOfflineMsg(msg);
            }
            return;
        }
        //  判断接收者是否在线
        if ((sessionCache = sessionCacheDao.get(sessionId)) != null) {
            transferMsg(sessionCache, msg);
            return;
        }
        //  处理离线消息
        handleOfflineMsg(msg);
    }

    /**
     * 将消息转发给目的Server
     */
    private void transferMsg(SessionCache sessionCache, ProtoMsg.Message msg) {
        //  获取接收者所连接的Server
        ImNode imNode = sessionCache.getImNode();
        //  获取转发器
        Router router = routerMap.getRouterByNodeId(imNode.getId());
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
