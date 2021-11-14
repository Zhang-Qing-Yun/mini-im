package com.qingyun.im.common.constants;

/**
 * @description： 客户端常量
 * @author: 張青云
 * @create: 2021-10-16 18:50
 **/
public interface ClientConstants {
    //  存放持久化消息的文件的目录
    String FILE_PATH_PREFIX = "message/";

    //  持久化消息的后缀名
    String FILE_SUFFIX = ".out";

    //  消息最大重发次数
    int REPEAT_SEND_COUNT = 5;

    //  重发间隔（超时时间），单位毫秒
    long REPEAT_INTERVAL = 3000;

    //  防重时间（最大重试时间 + 最大网络时间 + 冗余安全时间），单位毫秒
    long AVOID_REPEAT_INTERVAL = REPEAT_SEND_COUNT * REPEAT_INTERVAL + 5000 + 5000;

    //  防重集合本地持久化的路径
    String REPEAT_PATH = "repeat/";

    //  防重集合要持久化到的文件
    String REPEAT_FILE = "repeat.txt";

    //  防重集合文件里使用的分割符
    String SPLIT = ":";

    //  清理防重集合的周期，单位毫秒
    long CLEAR_UP_INTERVAL = REPEAT_INTERVAL * 10;
}
