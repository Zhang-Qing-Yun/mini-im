package com.qingyun.im.client.task;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.sender.MsgTimeoutTimerManager;
import com.qingyun.im.client.sender.CommonSender;
import com.qingyun.im.common.constants.ClientConstants;
import com.qingyun.im.common.entity.ProtoMsg;

import java.util.Timer;

/**
 * @description： 消息超时重传定时器；<br/>
 *              一条消息对应一个定时器，而每个定时器对应一个线程，所以这里会涉及到大量的线程；
 * @author: 張青云
 * @create: 2021-11-13 15:52
 **/
public class MsgTimeoutTimer extends Timer {
    private MsgTimeoutTask task;

    public MsgTimeoutTimer(ProtoMsg.Message msg, ClientSession session,
                           CommonSender sender, MsgTimeoutTimerManager manager) {
        task = new MsgTimeoutTask(manager, sender, session, msg);
        //  开启定时任务
        this.scheduleAtFixedRate(task, ClientConstants.REPEAT_INTERVAL, ClientConstants.REPEAT_INTERVAL);
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        super.cancel();
    }
}
