package com.qingyun.im.server.OfflineHandle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyun.im.common.concurrent.CallbackTask;
import com.qingyun.im.common.concurrent.CallbackTaskScheduler;
import com.qingyun.im.common.entity.Msg;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.protoBuilder.AckMsgBuilder;
import com.qingyun.im.server.Mapper.MsgMapper;
import com.qingyun.im.server.Mapper.UserMapper;
import com.qingyun.im.server.entity.User;
import com.qingyun.im.server.session.LocalSession;
import com.qingyun.im.server.session.ServerSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @description： 通过线程池来将存储离线消息的业务逻辑从handle中异步解耦
 * @author: 張青云
 * @create: 2021-11-15 19:12
 **/
@Component
@Primary
public class OfflineThreadPoolHandle implements OfflineHandle {
    @Autowired
    private MsgMapper msgMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public void handleOfflineMsg(ProtoMsg.Message message, ServerSession localSession) {
        CallbackTaskScheduler.addIOTarget(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                ProtoMsg.Msg offlineMsg = message.getMsg();
                //  根据消息接收者的用户名去查询其id
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username", offlineMsg.getTo());
                wrapper.select("id");
                User user = userMapper.selectOne(wrapper);
                Long toUserId = user.getId();

                //  查询数据库中是否已经存在了该条数据，如果存在则不再继续插入
                QueryWrapper<Msg> oldMsgWrapper = new QueryWrapper<>();
                oldMsgWrapper.eq("id", message.getSequence());
                oldMsgWrapper.eq("to_user_id", toUserId);  // 加上分片字段，防止进行跨库跨表查询
                oldMsgWrapper.select("id");
                Msg oldMsg = msgMapper.selectOne(oldMsgWrapper);
                if (oldMsg != null) {
                    return true;
                }

                //  创建一个Msg对象
                Msg msg = new Msg();
                msg.setId(message.getSequence())
                        .setFromUsername(offlineMsg.getFrom())
                        .setToUsername(offlineMsg.getTo())
                        .setToUserId(toUserId)
                        .setContext(offlineMsg.getContext())
                        .setSendTime(offlineMsg.getDatetime())
                        .setMsgStatus(0)
                        .setGmtCreate(LocalDateTime.now())
                        .setGmtUpdate(LocalDateTime.now());
                //  向数据库中插入一条数据
                int result = msgMapper.insert(msg);
                return result == 1;
            }

            @Override
            public void onBack(Boolean result) {
                if (result) {
                    //  如果向数据库中插入成功或数据库中已存在，则向客户端回送ACK消息
                    ProtoMsg.Message ackMsg = AckMsgBuilder.buildAckMsg(message.getSequence(), message.getMsg().getFrom(),
                            message.getMsg().getTo());
                    localSession.writeAndFlush(ackMsg);
                } else {
                    //  如果插入失败不做任何处理，等待客户端重传即可
                }
            }

            @Override
            public void onException(Throwable t) {
                //  出现异常也不做任何处理，等待客户端重传即可
            }
        });
    }
}
