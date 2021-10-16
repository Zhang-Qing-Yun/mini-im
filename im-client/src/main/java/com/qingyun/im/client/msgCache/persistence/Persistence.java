package com.qingyun.im.client.msgCache.persistence;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.exception.IMException;

import java.util.List;

/**
 * @description： 消息持久化系统
 * @author: 張青云
 * @create: 2021-10-16 16:18
 **/
public interface Persistence {
    /**
     * 持久化一条消息，使用序列号作为消息的唯一标识
     */
    boolean persistMessage(ProtoMsg.Message msg);

    /**
     * 从持久化中读取一条消息并删除
     * @param username 该消息的发送方
     * @param sequence 消息的序列号
     * @return 指定序列号和发送方的对应消息，如果没有则返回null
     */
    ProtoMsg.Message getMessageAndDelete(String username, long sequence) throws IMException;

    /**
     * 读取来自指定用户的全部消息并删除
     * @param username 用户名
     * @return 消息，没有则返回null
     */
    List<ProtoMsg.Message> getMessageWithFriendAndDelete(String username) throws IMException;
}
