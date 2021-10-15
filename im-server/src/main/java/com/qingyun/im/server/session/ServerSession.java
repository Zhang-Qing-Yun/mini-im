package com.qingyun.im.server.session;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-13 18:53
 **/
public interface ServerSession {
    /**
     * 向该session所对应的连接发送消息
     * @param pkg 消息
     */
    void writeAndFlush(Object pkg);

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
