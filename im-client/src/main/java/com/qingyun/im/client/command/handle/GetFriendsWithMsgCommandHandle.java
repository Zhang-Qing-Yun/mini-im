package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.msgCache.MsgCacheManager;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @description： 获取存在未读消息的全部好友
 * @author: 張青云
 * @create: 2022-12-17 19:41
 **/
@Component
public class GetFriendsWithMsgCommandHandle implements CommandHandle {
    @Autowired
    private MsgCacheManager manager;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.GET_FRIENDS_WITH_MSG.getCommandKey());
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

        Collection<String> friends = manager.getFriendsOfMsg();
        System.out.println("存在以下好友发送的未读消息：");
        for (String username: friends) {
            System.out.println("好友" + username);
        }
    }
}
