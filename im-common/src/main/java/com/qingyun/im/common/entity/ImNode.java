package com.qingyun.im.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description： 在注册中心（Zookeeper）上的一个结点里的内容。
 * @author: 張青云
 * @create: 2021-10-08 19:30
 **/
@Data
public class ImNode implements Serializable {
    private static final long serialVersionUID = -499010884211304846L;

    //  由ZK生成的有序id
    private long id;

    //  Netty Server的ip地址
    private String ip;

    //  Netty Server用于接收连接的端口号
    private int port;

    //  该Netty Server现在是否可用
    private boolean isReady = false;

    public ImNode() {

    }

    public ImNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImNode imNode = (ImNode) o;
        return id == imNode.id &&
                port == imNode.port &&
                ip.equals(imNode.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, port);
    }

    @Override
    public String toString() {
        return "ImNode{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
