package com.qingyun.im.common.constants;

/**
 * @description： 心跳相关的常量
 * @author: 張青云
 * @create: 2021-11-03 23:25
 **/
public interface HeartBeatConstants {
    /**
     * 发送Ping消息的时间间隔，单位毫秒
     */
    long PING_INTERVAL = 3000;

    /**
     * 读空闲的时间，单位秒
     */
    int READER_IDLE = (int) (PING_INTERVAL / 1000) * 4;
}
