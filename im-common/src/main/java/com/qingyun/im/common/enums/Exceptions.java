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
    NO_SERVER(1003, "没有可用服务器"),
    NO_CONNECT(1004, "当前没有与服务器连接"),
    SEND_MESSAGE_ERROR(1005, "发送消息失败"),
    CONCURRENT_ERROR(1006, "并发错误"),
    INTERRUPT(1007, "异常中断"),
    CREATE_FILE_ERROR(1008, "创建文件出错"),
    READ_FILE_ERROR(1009, "读取文件时失败"),
    GET_ID_ERROR(1010, "申请id失败"),
    HTTP_ERROR(2000, "发送HTTP请求时出错"),
    LOGIN_ERROR(2001, "登录失败"),
    REGISTER_ERROR(2002, "注册失败"),
    CONNECT_ERROR(2003, "连接失败"),
    START_FAIL(2004, "启动失败"),
    ASK_FRIEND_ERROR(2005, "发送好友请求失败"),
    ACK_FRIEND_ERROR(2006, "同意好友请求失败"),
    GET_ASK_LIST(2007, "获取用户的好友请求列表失败"),
    GET_FRIEND_LIST(2008, "获取用户的好友列表失败"),
    NOT_FRIEND(2009, "没有该好友"),
    LOAD_PERSISTENCE_FAIL(2010, "加载持久化消息失败"),
    ZK_NODE(3000, "操作Zookeeper结点时出错"),
    ZK_LISTENER(3001, "设置ZK监听器时出错"),
    ZK_READ_ERROR(3002, "读取ZK结点出错"),
    ADDR_ERROR(4000, "地址出错"),
    NO_MESSAGE(4001, "不识别的消息"),
    CLOCK_BACK(5000, "机器时钟发生了回拨");

    private final int code;
    private final String message;
}
