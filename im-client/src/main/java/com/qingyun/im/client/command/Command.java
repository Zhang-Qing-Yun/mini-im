package com.qingyun.im.client.command;

import com.qingyun.im.client.command.handle.CommandHandle;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;

/**
 * @description： 命令类型
 * @author: 張青云
 * @create: 2022-11-24 19:42
 **/
public enum Command {
    HELP("help", "提供帮助，获取所有的命令  【示例：help】"),
    LOGIN("login", "登录  【示例：login username password】"),
    ASK_FRIEND("askFriend", "添加好友  【示例：askFriend username】"),
    ACK_FRIEND("ackFriend", "同意加好友  【示例：ackFriend username】"),
    FRIEND_ASK("getFriendAsk", "查看所有的好友请求  【示例：getFriendAsk】"),
    ALL_FRIEND("allFriend", "查看好友列表  【示例：allFriend】"),
    SEND_MSG("send", "发送消息给好友  【示例：send username message】"),
    GET_FRIENDS_WITH_MSG("getFriendsWithMsg", "获取存在未读消息的全部好友  【示例：getFriendsWithMsg】"),
    GET_MSG("getMsg", "获取某个好友发来的未读消息  【示例：getMsg username】"),
    LOGOUT("logout", "退出  【示例：logout】");


    //  命令的关键字，只能是一个单词
    private final String commandKey;

    //  该命令的描述
    private final String describe;


    Command(String commandKey, String describe) {
        this.commandKey = commandKey;
        this.describe = describe;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public String getDescribe() {
        return describe;
    }

    /**
     * 根据命令的字面值获取对应的Command
     * @param value 命令
     * @return 对应的Command
     */
    public static Command getCommandByKey(String value) throws IMException {
        String commandKey = CommandHandle.getCommandKey(value);
        Command[] commands = values();
        for (Command command: commands) {
            if (commandKey.equals(command.getCommandKey())) {
                return command;
            }
        }
        //  没有该命令
        throw new IMException(Exceptions.NO_COMMAND.getCode(), Exceptions.NO_COMMAND.getMessage());
    }
}