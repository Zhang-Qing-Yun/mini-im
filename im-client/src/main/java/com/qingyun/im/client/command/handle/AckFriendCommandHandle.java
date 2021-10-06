package com.qingyun.im.client.command.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

/**
 * @description： 同意加好友请求的命令的关键字
 * @author: 張青云
 * @create: 2021-10-06 14:29
 **/
@Component
public class AckFriendCommandHandle implements CommandHandle{
    @Value("${auth.address}")
    private String authAddress;

    @Value("${auth.ackFriendUrl}")
    private String ackFriendUrl;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private ClientSession session;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.ACK_FRIEND.getCommandKey());
    }

    @Override
    public boolean isCorrect(String commandValue) {
        String[] parseValue = commandValue.trim().split(" ");
        return parseValue.length == 2;
    }

    @Override
    public void process(String commandValue) throws Exception {
        //  判断命令格式是否正确
        if (!isCorrect(commandValue)) {
            throw new IMException(Exceptions.PARSE_ERROR.getCode(), Exceptions.PARSE_ERROR.getMessage());
        }

        //  解析该命令
        String[] parseValue = commandValue.trim().split(" ");
        //  执行确认行为的人，即当前登录用户
        String username1 = session.getUserInfo().getUsername();
        //  发送好友请求的人，即需要被确认
        String username2 = parseValue[1];
        //  发HTTP请求登录的过程
        String url = authAddress + ackFriendUrl;
        JSONObject param = new JSONObject();
        param.put("username1", username1);
        param.put("username2", username2);
        Response response = HttpClient.call(okHttpClient, param.toString(), url);

        //  解析结果
        if(!response.isSuccessful()) {
            throw new IMException(Exceptions.HTTP_ERROR.getCode(), Exceptions.HTTP_ERROR.getMessage());
        }
        R result = JSON.parseObject(response.body().string(), R.class);
        if (!result.getSuccess()) {
            System.out.println(result.getMessage());
            throw new IMException(Exceptions.ACK_FRIEND_ERROR.getCode(), Exceptions.ACK_FRIEND_ERROR.getMessage());
        }

        System.out.println("已接受" + username2 + "的好友请求！");
    }
}
