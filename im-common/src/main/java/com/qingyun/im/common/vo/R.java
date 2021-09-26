package com.qingyun.im.common.vo;

import com.qingyun.im.common.enums.ResultType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description： 封装返回结果
 * @author: 張青云
 * @create: 2021-09-25 16:28
 **/
@Data
public class R {
    private Boolean success;  // 是否成功
    private Integer code;  // 状态码
    private String message;  // 提示信息
    private Map<String, Object> data = new HashMap<>();  // 返回的数据

    //  私有化构造方法，通过静态方法来获取R对象
    private R(){
    }

    //  执行成功返回相应的对象
    public static R ok(){
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultType.SUCCESS.getCode());
        r.setMessage(ResultType.SUCCESS.getMsg());
        return r;
    }

    //  执行失败返回相应的对象
    public static R error(){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultType.FAIL.getCode());
        r.setMessage(ResultType.FAIL.getMsg());
        return r;
    }

    //  设置当前对象的success值
    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    //  向原来的data里添加数据
    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    //  设置一个新的data
    public R data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
