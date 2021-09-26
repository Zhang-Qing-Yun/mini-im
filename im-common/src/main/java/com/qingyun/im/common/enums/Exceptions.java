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
    HTTP_ERROR(2000, "发送HTTP请求时出错");

    private final int code;
    private final String message;
}
