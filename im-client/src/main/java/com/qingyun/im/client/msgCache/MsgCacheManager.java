package com.qingyun.im.client.msgCache;

import com.qingyun.im.client.config.AttributeConfig;
import com.qingyun.im.client.msgCache.persistence.OverflowHandle;
import com.qingyun.im.client.msgCache.persistence.Persistence;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description： 本地缓存消息并按接收到的顺序
 * @author: 張青云
 * @create: 2021-10-16 16:14
 **/
@Component
public class MsgCacheManager {
    //  三个作用：记录有哪些好友的未读消息（内存中）；对有未读消息的好友按照最后一条消息的接收时间排序；记录这些好友对应的未读消息条数
    //  key为好友名，value为该好友发送的未读消息条数（只包括内存中的）
    private final LinkedHashMap<String, Integer> order = new LinkedHashMap<>();

    //  用来保存客户端已接收到但是还未被查阅的消息
    private final HashMap<String, Set<ProtoMsg.Message>> msgHolder = new HashMap<>();

    //  记录存在未读消息的好友名，包括内存中和持久化中
    private final HashSet<String> friendsWithMsg = new HashSet<>();

    //  内存中缓存的消息条数
    private volatile int count = 0;

    @Autowired
    private AttributeConfig attribute;

    @Autowired
    private OverflowHandle overflowHandle;

    @Autowired
    private Persistence persistence;


    /**
     * 缓存一条消息
     */
    public synchronized void addMsg(ProtoMsg.Message message) {
        //  消息的发送方
        String username = message.getMsg().getFrom();

        //  更新未读好友列表
        friendsWithMsg.add(username);

        //  判断是否超过内存阈值
        if (count >= attribute.getCacheMessageSize()) {
            //  进行持久化（同步阻塞）
            int n = overflowHandle.handle(order, msgHolder);
            if (n > 0) {
                count = count - n;
            }
        }

        //  更新内存缓存
        if (order.containsKey(username)) {
            Integer size = order.remove(username);
            Set<ProtoMsg.Message> set = msgHolder.get(username);
            set.add(message);
            order.put(username, size+1);
        } else {
            HashSet<ProtoMsg.Message> set = new HashSet<>();
            set.add(message);
            msgHolder.put(username, set);
            order.put(username, 1);
        }
        count++;
    }

    /**
     * 读取某个好友发来的全部未读消息
     * @param username 好友的用户名
     * @return 未读消息，没有则返回null
     */
    public synchronized Collection<ProtoMsg.Message> readMsgFromFriend(String username) throws IMException {
        if (!friendsWithMsg.contains(username)) {
            return null;
        }

        //  从持久化系统中读取
        Set<ProtoMsg.Message> msgInPersistence = persistence.getMessageByUsernameAndDelete(username);
        //  从内存中读取
        Set<ProtoMsg.Message> msgCache = msgHolder.get(username);
        //  合并
        HashSet<ProtoMsg.Message> result = new HashSet<>();
        if (msgInPersistence != null && !msgInPersistence.isEmpty()) {
            result.addAll(msgInPersistence);
        }
        if (msgCache != null && !msgCache.isEmpty()) {
            result.addAll(msgCache);
        }
        //  更新变量
        if (msgCache != null) {
            count = count - msgCache.size();
        }
        friendsWithMsg.remove(username);
        order.remove(username);
        msgHolder.remove(username);

        return result;
    }

    /**
     * 获取存在未读消息的好友名
     */
    public synchronized Collection<String> getFriendsOfMsg() {
        HashSet<String> result = new HashSet<>();
        for (String username: friendsWithMsg) {
            result.add(username);
        }
        return result;
    }

    /**
     * 持久化内存中的消息，持久化完成后清空内存
     */
    public synchronized void persistMsg() {
        //  持久化内存中的消息
        for (Map.Entry<String, Set<ProtoMsg.Message>> entry: msgHolder.entrySet()) {
            Set<ProtoMsg.Message> messages = entry.getValue();
            for (ProtoMsg.Message message: messages) {
                persistence.persistMessage(message);
            }
        }

        //  清空内存
        msgHolder.clear();
        order.clear();
        friendsWithMsg.clear();
    }

    /**
     * 从持久化中加载消息，加载完成后删除持久化系统中的内容
     */
    public synchronized void initFromPersistence() {
        try {
            //  获取存在未读消息的好友
            Set<String> usernames = persistence.getUsernamesWithMessage();
            if (usernames == null || usernames.isEmpty()) {
                return;
            }
            friendsWithMsg.addAll(usernames);
            //  从持久化中向内存加载数据
            for (String username: usernames) {
                //  当持久化中的消息过多则只加载一部分，防止撑爆内存
                if (count >= attribute.getCacheMessageSize()) {
                    break;
                }
                Set<ProtoMsg.Message> messages = persistence.getMessageByUsernameAndDelete(username);
                msgHolder.put(username, messages);
                count += messages.size();
                order.put(username, messages.size());
            }
        } catch (IMException e) {
            throw new IMRuntimeException(Exceptions.LOAD_PERSISTENCE_FAIL.getCode(),
                    Exceptions.LOAD_PERSISTENCE_FAIL.getMessage());
        }
    }
}
