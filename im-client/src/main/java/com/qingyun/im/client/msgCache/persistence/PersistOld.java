package com.qingyun.im.client.msgCache.persistence;

import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description： 将最后一条未读消息是最早的好友的所有未读消息持久化（最近最长时间未发消息）
 * @author: 張青云
 * @create: 2023-02-17 13:34
 **/
@Component
public class PersistOld implements OverflowHandle {
    @Autowired
    private Persistence persistence;


    @Override
    public int handle(LinkedHashMap<String, Integer> order, HashMap<String, Set<ProtoMsg.Message>> msgHolder) {
        //   获取最近最长时间未发消息的好友
        Iterator<Map.Entry<String, Integer>> iterator = order.entrySet().iterator();
        Map.Entry<String, Integer> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        String username = tail.getKey();
        //  获取该好友发送的全部消息
        Set<ProtoMsg.Message> messages = msgHolder.get(username);

        //  同步阻塞式持久化消息
        boolean b = blockPersist(persistence, messages);
        //  同步持久化，只有执行成功才删除内存中的消息，如果出错则让其继续留在内存中
        if (b) {
            //  从内存中删除已持久化的内容
            order.remove(username);
            msgHolder.remove(username);
            return messages.size();
        }
        return -1;
    }
}
