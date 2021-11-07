package com.qingyun.im.client.imClient;

import com.qingyun.im.client.pojo.UserInfo;
import com.qingyun.im.common.entity.ImNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @description： 记录客户端的连接信息
 * @author: 張青云
 * @create: 2021-10-03 16:21
 **/
@Component
@Data
public class ClientSession {
    //  连接的唯一标志
    private String sessionId = "";

    //  是否已经使用用户名和密码登录
    private boolean isLogin = false;

    //  是否已经和NettyServer建立了连接
    private boolean isConnected = false;

    //  登录的用户信息
    private UserInfo userInfo;

    //  连接的Netty Server的信息
    private ImNode imNode;

    //  与Netty Server建立的channel
    private Channel channel;

    /**
     * 向服务器发送数据
     * @param pkg 数据
     * @return 异步Future
     */
    public ChannelFuture writeAndFlush(Object pkg) {
        return channel.writeAndFlush(pkg);
    }
}
