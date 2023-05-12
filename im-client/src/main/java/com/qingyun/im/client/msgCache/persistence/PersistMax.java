package com.qingyun.im.client.msgCache.persistence;

import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description： 将未读消息数量最多的好友的全部消息持久化，从而释放更大的内存
 * @author: 張青云
 * @create: 2023-02-17 16:16
 **/
@Component
@Primary
public class PersistMax implements OverflowHandle {
    @Autowired
    private Persistence persistence;


    @Override
    public int handle(LinkedHashMap<String, Integer> order, HashMap<String, Set<ProtoMsg.Message>> msgHolder) {
        //  获取未读消息数量最多的好友
        int max = Integer.MIN_VALUE;
        String username = null;
        Iterator<Map.Entry<String, Integer>> iterator = order.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() > max) {
                max = entry.getValue();
                username = entry.getKey();
            }
        }

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
