package com.qingyun.im.client.sender;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.protoBuilder.ShakeHandReqMsgBuilder;
import com.qingyun.im.common.entity.ProtoMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 发送握手消息
 * @author: 張青云
 * @create: 2021-10-13 13:50
 **/
@Component
public class ShakeHandSender extends BaseSender {

    private ClientSession session;

    @Autowired
    public ShakeHandSender(ClientSession session) {
        super(session);
        this.session = session;
    }

    /**
     * 发送握手消息
     */
    public void sendShakeHandMsg() {
        //  构建消息
        ProtoMsg.Message pkg = ShakeHandReqMsgBuilder.buildShakeHandReqMsg(session.getUserInfo().getUsername());
        super.sendMsg(pkg);
    }
}
