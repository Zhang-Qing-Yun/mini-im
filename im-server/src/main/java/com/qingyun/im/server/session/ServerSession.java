package com.qingyun.im.server.session;

import com.qingyun.im.server.router.MsgSource;

/**
 * @description：
 * @author: 張青云
 * @create: 2022-12-13 18:53
 **/
public interface ServerSession extends MsgSource {
//    /**
//     * 向该session所对应的连接发送消息
//     * @param pkg 消息
//     */
//    void writeAndFlush(Object pkg);

    /**
     * 获取用户名
     */
    String getUsername();

    /**
     * 获取sessionId
     */
    String getSessionId();

    /**
     * 连接是否可用
     */
    boolean isActive();
}
