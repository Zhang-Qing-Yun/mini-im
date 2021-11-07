package com.qingyun.im.server.handle;

import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.router.RouterMap;
import com.qingyun.im.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理服务端发生异常的情况
 * @author: 張青云
 * @create: 2021-11-06 10:47
 **/
@ChannelHandler.Sharable
@Component
@Slf4j
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private RouterMap routerMap;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //  判断是否是转发器发生异常
        Long nodeId = ctx.channel().attr(Router.ROUTER_KEY).get();
        if (nodeId != null) {
            //  从转发器集合中删除
            Router router = routerMap.getRouterByNodeId(nodeId);
            if (router != null) {
                //  关闭与该远程结点的连接
                router.stopConnecting();
                //  更新转发表
                routerMap.removeRouter(nodeId);
                log.info("检测到结点{}下线，已删除对应的转发器", nodeId);
            }
            return;
        }
        /*
        * 当发生异常时，服务端只是简单的关闭与客户端的连接；
        * 具体的重连操作则由客户端来完成；
        * 这里也不去删除客户端连接在路由层的痕迹，留给重连握手时完成；
        * */
        String sessionId = ctx.channel().attr(SessionManager.SESSION_ID_KEY).get();
        log.info("与客户端【sessionId：{}】发生异常，已关闭与客户端的连接", sessionId);
        ctx.close();
    }
}
