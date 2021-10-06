package com.qingyun.im.client.imClient;

import com.qingyun.im.client.pojo.UserInfo;
import com.qingyun.im.common.vo.ServerInfo;
import io.netty.channel.Channel;
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
    //  是否已经使用用户名和密码登录
    private boolean isLogin = false;

    //  是否已经和NettyServer建立了连接
    private boolean isConnected = false;

    //  登录的用户信息
    private UserInfo userInfo;

    //  连接的Netty Server的信息
    private ServerInfo serverInfo;

    //  与Netty Server建立的channel
    private Channel channel;

}
