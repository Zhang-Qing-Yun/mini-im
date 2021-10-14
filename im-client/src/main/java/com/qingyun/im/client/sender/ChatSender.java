package com.qingyun.im.client.sender;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.protoBuilder.ChatMsgBuilder;
import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 发送聊天消息
 * @author: 張青云
 * @create: 2021-10-14 20:58
 **/
@Component
public class ChatSender extends BaseSender {
    private ClientSession session;

    @Autowired
    public ChatSender(ClientSession session) {
        super(session);
        this.session = session;
    }

    /**
     * 发送聊天消息
     */
    public void sendChatMsg(String to, String context) {
        //  TODO：生成消息的序列号
        long sequence = System.currentTimeMillis();
        //  构建聊天消息
        ProtoMsg.Message chatMsg = ChatMsgBuilder.buildChatMsg(session.getUserInfo().getUsername(), to, context,
                session.getSessionId(), sequence);
        super.sendMsg(chatMsg);
    }
}
