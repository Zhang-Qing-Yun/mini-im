package com.qingyun.im.common.exception;

/**
 * @description： 自定义异常
 * @author: 張青云
 * @create: 2021-09-25 10:28
 **/
public class IMRuntimeException extends RuntimeException{
    //  状态码
    private Integer code;

    //  错误提示
    private String msg;

    public IMRuntimeException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
