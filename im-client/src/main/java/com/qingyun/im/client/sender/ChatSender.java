package com.qingyun.im.client.sender;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.protoBuilder.ChatMsgBuilder;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.IDGeneratorType;
import com.qingyun.im.common.idGenerator.IDGenerator;
import com.qingyun.im.common.idGenerator.SnowFlake;
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

    /**
     * 用于产生消息id的ID生成器，这里使用雪花算法
     */
    private final SnowFlake idGenerator;

    @Autowired
    public ChatSender(ClientSession session) {
        super(session);
        this.session = session;
        idGenerator = (SnowFlake) IDGenerator.getInstance(IDGeneratorType.SNOW_FLAKE.getType());
    }

    /**
     * 发送聊天消息
     */
    public void sendChatMsg(String to, String context) {
        //  使用雪花算法生成消息的序列号
        long sequence = idGenerator.generatorLongID();
        //  构建聊天消息
        ProtoMsg.Message chatMsg = ChatMsgBuilder.buildChatMsg(session.getUserInfo().getUsername(), to, context,
                session.getSessionId(), sequence);
        super.sendMsg(chatMsg);
    }
}
