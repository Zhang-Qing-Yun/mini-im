package com.qingyun.im.client.sender;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.protoBuilder.LogoutMsgBuilder;
import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-07 16:15
 **/
@Component
public class LogoutSender extends BaseSender {
    private ClientSession session;

    private CountDownLatch latch = null;

    @Autowired
    public LogoutSender(ClientSession session) {
        super(session);
        this.session = session;
    }

    /**
     * 发送退出消息
     */
    public void sendLogoutMsg() {
        //  构建消息
        ProtoMsg.Message msg = LogoutMsgBuilder.buildLogoutMsg(session.getSessionId());
        super.sendMsg(msg);
        //  同步阻塞至消息发送完成
        latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            /*
            * 发送退出消息的作用是让服务端清除该连接保存在路由层的记录；
            * 即使是发送不成功，也不会有太大的影响，因为该用户下次上线时会进行清除上次连接的痕迹；
            * 所以这里不是必须成功，而是最好成功。
            * */
        }
    }

    @Override
    protected void sendSucceed(ProtoMsg.Message message) {
        latch.countDown();
    }
}
