package com.qingyun.im.server.session;

import io.netty.channel.Channel;
import lombok.Data;


/**
 * @description： 与当前服务器建立连接的 客户端-服务端 session
 * @author: 張青云
 * @create: 2022-12-13 14:19
 **/
@Data
public class LocalSession implements ServerSession{
    //  全局唯一id，用来标志一个客户端连接
    private String sessionId;

    //  该连接所对应的客户端用户名
    private String username;

    //  与客户端通信的channel
    private Channel channel;

    public LocalSession() {
    }

    public LocalSession(String username) {
        this.username = username;
    }

    public LocalSession(String sessionId, String username) {
        this.sessionId = sessionId;
        this.username = username;
    }

    @Override
    public synchronized void writeAndFlush(Object pkg) {
        channel.writeAndFlush(pkg);
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }
}
