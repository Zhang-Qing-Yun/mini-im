package com.qingyun.im.common.entity;

import lombok.Data;

/**
 * @description： Netty服务端的信息
 * @author: 張青云
 * @create: 2021-10-01 15:07
 **/
@Data
public class ServerInfo {
    //  服务端ip地址
    private String ip;

    //  Netty端口
    private int port;

    public ServerInfo() {
    }

    public ServerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}