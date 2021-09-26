package com.qingyun.im.common.enums;

/**
 * @description： 返回的结果的类型
 * @author: 張青云
 * @create: 2021-09-25 16:30
 **/
public enum ResultType {
    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    private final int code;
    private final String msg;

    ResultType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
