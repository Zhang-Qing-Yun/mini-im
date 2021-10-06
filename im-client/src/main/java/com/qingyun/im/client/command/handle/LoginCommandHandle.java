package com.qingyun.im.client.command.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.util.HttpClient;
import com.qingyun.im.common.vo.R;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description： 处理登录命令
 * @author: 張青云
 * @create: 2021-09-25 10:11
 **/
@Component
public class LoginCommandHandle implements CommandHandle {
    //  用户名
    private String username;
    //  密码
    private String password;

    @Value("${auth.address}")
    private String authAddress;

    @Value("${auth.loginUrl}")
    private String loginUrl;

    @Autowired
    private OkHttpClient okHttpClient;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.LOGIN.getCommandKey());
    }

    @Override
    public boolean isCorrect(String commandValue) {
        String[] parseValue = commandValue.trim().split(" ");
        return parseValue.length == 3;
    }

    @Override
    public void process(String commandValue) throws Exception {
        //  判断命令格式是否正确
        if (!isCorrect(commandValue)) {
            throw new IMException(Exceptions.PARSE_ERROR.getCode(), Exceptions.PARSE_ERROR.getMessage());
        }

        //  解析该命令
        String[] parseValue = commandValue.trim().split(" ");
        this.username = parseValue[1];
        this.password = parseValue[2];
        //  发HTTP请求登录的过程
        String url = authAddress + loginUrl;
        JSONObject param = new JSONObject();
        param.put("username", username);
        param.put("password", password);
        Response response = HttpClient.post(okHttpClient, param.toString(), url);
        //  判断是否登陆成功
        R result = JSON.parseObject(response.body().string(), R.class);
        if (!result.getSuccess()) {
            System.out.println(result.getMessage());
            throw new IMException(Exceptions.LOGIN_ERROR.getCode(), Exceptions.LOGIN_ERROR.getMessage());
        }
        //  TODO：解析结果
        System.out.println(result);
    }

    public String getUsername() {
        return username;
    }
}
