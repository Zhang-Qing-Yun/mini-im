package com.qingyun.im.client.sender;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.common.concurrent.CallbackTask;
import com.qingyun.im.common.concurrent.CallbackTaskScheduler;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @description： 发送消息的基类
 * @author: 張青云
 * @create: 2021-10-13 12:33
 **/
public abstract class BaseSender {

    private ClientSession session;

    public BaseSender(ClientSession session) {
        this.session = session;
    }

    /**
     * 异步发送消息
     * @param message 消息
     */
    public void sendMsg(ProtoMsg.Message message) {
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {

            @Override
            public Boolean execute() throws Exception {
                if (!session.isLogin()) {
                    throw new IMException(Exceptions.NO_LOGIN.getCode(), Exceptions.NO_LOGIN.getMessage());
                }
                if (!session.isConnected()) {
                    throw new IMException(Exceptions.NO_CONNECT.getCode(), Exceptions.NO_CONNECT.getMessage());
                }

                boolean[] result = {false};
                ChannelFuture f = session.writeAndFlush(message);
                f.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            result[0] = true;
                        }
                    }
                });
                try {
                    //  同步等待
                    f.sync();
                } catch (Exception e) {
                    result[0] = false;
                    throw new IMException(Exceptions.SEND_MESSAGE_ERROR.getCode(), Exceptions.SEND_MESSAGE_ERROR.getMessage());
                }
                return result[0];
            }

            @Override
            public void onBack(Boolean result) {
                if (result) {
                    BaseSender.this.sendSucceed(message);
                } else {
                    BaseSender.this.sendFailed(message);
                }
            }

            @Override
            public void onException(Throwable t) {
                BaseSender.this.sendException(message);
            }
        });
    }

    /**
     * 发送成功后的回调
     */
    protected void sendSucceed(ProtoMsg.Message message) {

    }

    /**
     * 发送失败后的回调
     */
    protected void sendFailed(ProtoMsg.Message message) {

    }

    /**
     * 发送出错后的回调
     */
    protected void sendException(ProtoMsg.Message message) {

    }
}
