package com.qingyun.im.client.task;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.sender.MsgTimeoutTimerManager;
import com.qingyun.im.client.sender.CommonSender;
import com.qingyun.im.common.constants.ClientConstants;
import com.qingyun.im.common.entity.ProtoMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * @description： 定时任务
 * @author: 張青云
 * @create: 2021-11-13 16:14
 **/
@Slf4j
public class MsgTimeoutTask extends TimerTask {
    //  当前重试次数
    private int retryCount;

    //  需要进行超时重传的消息
    private final ProtoMsg.Message msg;

    private final ClientSession session;

    private final CommonSender sender;

    private final MsgTimeoutTimerManager manager;

    public MsgTimeoutTask(MsgTimeoutTimerManager manager, CommonSender sender,
                          ClientSession session, ProtoMsg.Message msg) {
        retryCount = 0;
        this.manager = manager;
        this.sender = sender;
        this.session = session;
        this.msg = msg;
    }

    @Override
    public void run() {
        retryCount++;
        //  超出重传次数限制
        if (retryCount > ClientConstants.REPEAT_SEND_COUNT) {
            sendFail();
            return;
        }

        //  如果当前正在重连中，则本次不进行消息的重传，等下一次重传时如果重连成功再重传
        if (!session.isConnected()){
            return;
        }
        //  重传消息
        sender.sendMsg(msg);
        log.info("重传消息【{}：{}】", msg.getSequence(), msg.getMsg().getContext());
    }

    /**
     * 消息多次重传仍未成功
     */
    private void sendFail() {
        //  从超时重传管理器中移除
        manager.remove(msg.getSequence());
        log.error("消息【{}：{}】发送失败", msg.getSequence(), msg.getMsg().getContext());
    }
}
