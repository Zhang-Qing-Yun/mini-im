package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.msgCache.MsgCacheManager;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @description： 获取来自某个好友的未读消息列表
 * @author: 張青云
 * @create: 2021-10-17 19:42
 **/
@Component
public class GetMsgCommandHandle implements CommandHandle {
    @Autowired
    private MsgCacheManager manager;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.GET_MSG.getCommandKey());
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
        String username = parseValue[1];

        Collection<ProtoMsg.Message> messages = manager.readMsgFromFriend(username);
        System.out.println("该好友发送的未读消息如下：");
        for (ProtoMsg.Message message: messages) {
            System.out.println(message.getMsg().getContext());
        }
    }
}
