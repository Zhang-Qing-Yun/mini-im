package com.qingyun.im.common.exception;


/**
 * @description：
 * @author: 張青云
 * @create: 2021-09-25 14:51
 **/
public class IMException extends Exception{
    //  状态码
    private Integer code;

    //  错误提示
    private String msg;

    public IMException(Integer code, String msg) {
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
