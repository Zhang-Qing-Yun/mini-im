package com.qingyun.im.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-09-25 10:35
 **/
@AllArgsConstructor
@Getter
public enum Exceptions {

    NO_COMMAND(1000, "没有该命令"),
    PARSE_ERROR(1001, "命令解析时出错"),
    NO_LOGIN(1002, "当前没有登录"),
    HTTP_ERROR(2000, "发送HTTP请求时出错"),
    LOGIN_ERROR(2001, "登录失败"),
    REGISTER_ERROR(2002, "注册失败"),
    CONNECT_ERROR(2003, "连接失败"),
    START_FAIL(2004, "启动失败"),
    ASK_FRIEND_ERROR(2005, "发送好友请求失败"),
    ACK_FRIEND_ERROR(2006, "同意好友请求失败"),
    GET_ASK(2007, "获取用户的好友请求列表失败"),
    ZK_NODE(3000, "操作Zookeeper结点时出错"),
    ZK_LISTENER(3001, "设置ZK监听器时出错"),
    ADDR_ERROR(4000, "地址出错"),
    NO_MESSAGE(4001, "不识别的消息");

    private final int code;
    private final String message;
}