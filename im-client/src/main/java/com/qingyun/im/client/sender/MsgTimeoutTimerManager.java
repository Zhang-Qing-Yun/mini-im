package com.qingyun.im.client.sender;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.task.MsgTimeoutTimer;
import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： 管理消息超时重传的定时任务
 * @author: 張青云
 * @create: 2021-11-13 18:23
 **/
@Component
public class MsgTimeoutTimerManager {
    //  管理所有的超时重传消息的定时器
    private final Map<Long, MsgTimeoutTimer> msgTimeoutTimers = new ConcurrentHashMap<>();

    //  用于重传消息的消息发送器
    private CommonSender sender;

    //  锁，用于阻塞线程至所有的消息都被ack或者不再进行重传
    private final Object o = new Object();

    //  是否处于阻塞等待状态
    private volatile boolean isWait = false;

    @Autowired
    private ClientSession session;

    @PostConstruct
    private void init() {
        this.sender = new CommonSender(session);
    }

    /**
     * 为一条消息添加超时重传定时任务
     */
    public synchronized void add(ProtoMsg.Message msg) {
        if (msg == null || msgTimeoutTimers.containsKey(msg.getSequence())) {
            return;
        }
        MsgTimeoutTimer timer = new MsgTimeoutTimer(msg, session, sender, this);
        msgTimeoutTimers.put(msg.getSequence(), timer);
    }

    /**
     * 从超时管理器中删除消息对应的定时器，并停止定时器
     * @param sequence 消息的id
     */
    public synchronized void remove(long sequence) {
        if (!msgTimeoutTimers.containsKey(sequence)) {
            return;
        }
        MsgTimeoutTimer timer = msgTimeoutTimers.remove(sequence);
        timer.cancel();
        if (msgTimeoutTimers.isEmpty() && isWait) {
            letGo();
        }
    }

    /**
     * 删除所有正在等待ack的消息及其定时器
     */
    public synchronized void removeAll() {
        Set<Long> keys = msgTimeoutTimers.keySet();
        for (long sequence: keys) {
            remove(sequence);
        }
    }

    /**
     * 阻塞至没有消息需要等待ack
     */
    public void await() {
        //  如果集合为空则不需要等待
        if (msgTimeoutTimers.isEmpty()) {
            return;
        }
        synchronized (o) {
            try {
                isWait = true;
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 放行
     */
    public void letGo() {
        synchronized (o) {
            o.notify();
        }
    }
}
