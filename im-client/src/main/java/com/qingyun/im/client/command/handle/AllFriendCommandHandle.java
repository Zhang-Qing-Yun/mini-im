package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.annotation.LoginRequired;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.imClient.FriendList;
import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @description： 执行查看好友列表命令
 * @author: 張青云
 * @create: 2022-12-06 21:05
 **/
@Component
@LoginRequired
public class AllFriendCommandHandle implements CommandHandle{
    @Autowired
    private FriendList friendList;

    @Autowired
    private ImClient imClient;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.ALL_FRIEND.getCommandKey());
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

        //  好友列表
        friendList.initFriendList(imClient.getFriendList());
        Set<String> list = friendList.getFriendList();
        if (list == null || list.size() == 0) {
            System.out.println("暂无好友！");
            return;
        }
        System.out.println("好友列表如下：");
        for(String friend: list) {
            System.out.println("好友：" + friend);
        }
    }
}
