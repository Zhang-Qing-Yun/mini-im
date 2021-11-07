package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ImClient imClient;


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
        imClient.loginAndGetNode(username, password);
    }

    public String getUsername() {
        return username;
    }
}
