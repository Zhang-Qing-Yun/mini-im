package com.qingyun.im.client.msgCache.persistence;

import com.qingyun.im.common.entity.ProtoMsg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @description： 处理未读消息缓存超过指定大小的情况
 * @author: 張青云
 * @create: 2021-10-17 13:31
 **/
public interface OverflowHandle {
    /**
     * 处理未读消息缓存超过指定大小的情况
     * @return 如果成功则返回持久化消息的条数，否则返回-1
     */
    int handle(LinkedHashMap<String, Integer> order, HashMap<String, Set<ProtoMsg.Message>> msgHolder);


    /**
     * 同步阻塞式持久化消息
     * @param persistence 持久化系统
     * @param messages 要持久化的消息
     * @return 如果消息全部持久化成功则返回true，任意一条消息持久化失败则返回false
     */
    default boolean blockPersist(Persistence persistence, Set<ProtoMsg.Message> messages) {
        //  同步持久化
        for (ProtoMsg.Message message: messages) {
            if (!persistence.persistMessage(message)) {
                return false;
            }
        }
        return true;
    }
}
