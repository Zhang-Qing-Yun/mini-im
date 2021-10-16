package com.qingyun.im.client.msgCache;

import com.qingyun.im.client.config.AttributeConfig;
import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @description： 本地缓存消息并按接收到的顺序
 * @author: 張青云
 * @create: 2021-10-16 16:14
 **/
@Component
public class MsgCacheManager {
    private LinkedHashSet<String> order = new LinkedHashSet<>();

    @Autowired
    private AttributeConfig attribute;


    /**
     * 缓存一条消息
     */
    public synchronized void addMsg(ProtoMsg.Message message) {

    }

    /**
     * 读取某个好友发来的全部未读消息
     * @param username 好友的用户名
     * @return 未读消息
     */
    public Collection<ProtoMsg.Message> readMsgFromFriend(String username) {
        return null;
    }

    /**
     * 获取存在未读消息的好友名
     */
    public Collection<String> getFriendsOfMsg() {
        return null;
    }
}
