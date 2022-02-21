package com.qingyun.im.server.OfflineHandle;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.server.router.MsgSource;
import com.qingyun.im.server.session.ServerSession;

/**
 * @description： 处理离线消息，将存储离线消息的业务逻辑从handle中异步解耦
 * @author: 張青云
 * @create: 2021-11-15 19:08
 **/
public interface OfflineHandle {
    /**
     * 处理离线消息，将存储离线消息的业务逻辑从Netty的handle中异步解耦，防止影响其它事件的处理
     * @param message 离线消息
     * @param msgSource 该服务器与离线消息的发送方之间的会话连接
     */
    void handleOfflineMsg(ProtoMsg.Message message, MsgSource msgSource);
}
