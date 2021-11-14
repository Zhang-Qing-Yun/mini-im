package com.qingyun.im.client.task;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： 清理防重集合的任务
 * @author: 張青云
 * @create: 2021-11-13 13:41
 **/
public class ClearUpTask extends TimerTask {
    //  需要进行清理的防重集合
    private ConcurrentHashMap<Long, Long> recvMsg;

    public ClearUpTask(ConcurrentHashMap<Long, Long> recvMsg) {
        this.recvMsg = recvMsg;
    }

    @Override
    public void run() {
        //  获取当前时间
        long now = System.currentTimeMillis();
        //  遍历Map，删除掉过期的项
        Iterator<Map.Entry<Long, Long>> iterator = recvMsg.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Long> one = iterator.next();
            Long expires = one.getValue();
            if (expires <= now) {
                iterator.remove();
            }
        }
    }
}
