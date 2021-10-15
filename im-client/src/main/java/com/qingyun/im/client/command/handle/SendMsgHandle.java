package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.annotation.LoginRequired;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.config.AttributeConfig;
import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.imClient.FriendList;
import com.qingyun.im.client.sender.ChatSender;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 给好友发送消息
 * @author: 張青云
 * @create: 2021-10-14 19:32
 **/
@Component
@LoginRequired
public class SendMsgHandle implements CommandHandle {
    @Autowired
    private ClientSession session;

    @Autowired
    private FriendList friendList;

    @Autowired
    private AttributeConfig attribute;

    @Autowired
    private ChatSender chatSender;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.SEND_MSG.getCommandKey());
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
        //  检查是否建立了连接
        if (session.getChannel() == null) {
            throw new IMException(Exceptions.NO_CONNECT.getCode(), Exceptions.NO_CONNECT.getMessage());
        }
        //  检查连接是否可用
        if (!session.getChannel().isActive()) {
            //  TODO：断线重连
        }

        //  解析命令
        String[] parseResult = commandValue.trim().split(" ");
        String toUsername = parseResult[1];
        String context = parseResult[2].substring(0, attribute.getMessageMaxSize());  // 限制单次发送的字符的个数

        //  判断是否为好友关系
        boolean isFriend = friendList.isFriend(toUsername);
        if (!isFriend) {
            throw new IMException(Exceptions.NOT_FRIEND.getCode(), Exceptions.NOT_FRIEND.getMessage());
        }

        //  发送消息
        chatSender.sendChatMsg(toUsername, context);
    }
}
