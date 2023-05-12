package com.qingyun.im.client.sender;

import com.qingyun.im.client.handle.IDRespHandle;
import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.common.protoBuilder.ChatMsgBuilder;
import com.qingyun.im.client.protoBuilder.IDAskMsgBuilder;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description： 发送聊天消息
 * @author: 張青云
 * @create: 2023-02-14 20:58
 **/
@Component
public class ChatSender extends BaseSender {
    private ClientSession session;

    @Autowired
    private IDRespHandle idRespHandle;

    @Autowired
    private MsgTimeoutTimerManager manager;

    @Autowired
    public ChatSender(ClientSession session) {
        super(session);
        this.session = session;
    }

    /**
     * 发送聊天消息
     */
    public void sendChatMsg(String to, String context) throws IMException {
        //  阻塞式向服务端申请消息唯一id
        CompletableFuture<Long> idWait = new CompletableFuture<>();
        idRespHandle.setIdWait(idWait);
        //  向服务端发送ID申请消息，这里涉及到一次网络开销
        super.sendMsg(IDAskMsgBuilder.buildIDAskMsg());
        Long sequence = null;
        try {
            //  阻塞等待
            sequence = idWait.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IMException(Exceptions.GET_ID_ERROR.getCode(), Exceptions.GET_ID_ERROR.getMessage());
        }

        //  构建聊天消息
        ProtoMsg.Message chatMsg = ChatMsgBuilder.buildChatMsg(session.getUserInfo().getUsername(), to, context,
                session.getSessionId(), sequence, System.currentTimeMillis());
        //  将该消息添加到超时重传管理器
        manager.add(chatMsg);
        //  异步发送聊天消息
        super.sendMsg(chatMsg);
    }
}
