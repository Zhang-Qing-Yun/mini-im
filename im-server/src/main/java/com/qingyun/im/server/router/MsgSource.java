package com.qingyun.im.server.router;

/**
 * @description： 服务端收到消息的来源
 * @author: 張青云
 * @create: 2022-02-21 01:00
 **/
public interface MsgSource {
    /**
     * 向消息来源回送消息
     */
    void writeAndFlush(Object pkg);
}
