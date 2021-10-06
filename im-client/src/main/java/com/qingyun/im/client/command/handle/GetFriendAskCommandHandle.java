package com.qingyun.im.client.command.handle;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.client.annotation.LoginRequired;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.util.HttpClient;
import com.qingyun.im.common.vo.R;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description： 处理查看好友请求命令
 * @author: 張青云
 * @create: 2021-10-06 15:27
 **/
@Component
@LoginRequired
public class GetFriendAskCommandHandle implements CommandHandle{
    @Value("${auth.address}")
    private String authAddress;

    @Value("${auth.getFriendAskUrl}")
    private String getFriendAskUrl;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private ClientSession session;



    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.FRIEND_ASK.getCommandKey());
    }

    @Override
    public boolean isCorrect(String commandValue) {
        String[] parseValue = commandValue.trim().split(" ");
        return parseValue.length == 1;
    }

    @Override
    public void process(String commandValue) throws Exception {
        //  判断命令格式是否正确
        if (!isCorrect(commandValue)) {
            throw new IMException(Exceptions.PARSE_ERROR.getCode(), Exceptions.PARSE_ERROR.getMessage());
        }

        //  获取当前登录用户
        String username = session.getUserInfo().getUsername();
        //  发HTTP请求登录的过程
        String url = authAddress + getFriendAskUrl;
        Map<String, String> param = new HashMap<>();
        param.put("username", username);
        Response response = HttpClient.get(okHttpClient, param, url);
        //  解析结果
        R result = JSON.parseObject(response.body().string(), R.class);
        if (!result.getSuccess()) {
            System.out.println(result.getMessage());
            throw new IMException(Exceptions.GET_ASK.getCode(), Exceptions.GET_ASK.getMessage());
        }

        //  用户的好友请求列表
        List<String> friendAsk = (List<String>) result.getData().get("friendAsk");
        if (friendAsk == null || friendAsk.size() == 0) {
            System.out.println("暂无好友请求！");
            return;
        }
        System.out.println("好友请求列表如下：");
        for(String ask: friendAsk) {
            System.out.println("用户名：" + ask);
        }
    }
}
