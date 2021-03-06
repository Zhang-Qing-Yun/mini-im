package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.annotation.LoginRequired;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.msgCache.AvoidRepeatManager;
import com.qingyun.im.client.msgCache.MsgCacheManager;
import com.qingyun.im.client.sender.LogoutSender;
import com.qingyun.im.client.sender.MsgTimeoutTimerManager;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 处理退出命令
 * @author: 張青云
 * @create: 2021-11-07 16:00
 **/
@Component
@LoginRequired
@Slf4j
public class LogoutCommandHandle implements CommandHandle {
    @Autowired
    private MsgCacheManager manager;

    @Autowired
    private AvoidRepeatManager avoidRepeatManager;

    @Autowired
    private LogoutSender logoutSender;

    @Autowired
    private MsgTimeoutTimerManager timeoutTimerManager;

    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.LOGOUT.getCommandKey());
    }

    @Override
    public boolean isCorrect(String commandValue) {
        String[] parseValue = commandValue.trim().split(" ");
        return parseValue.length == 1;
    }

    @Override
    public void process(String commandValue) throws Exception {
        //  判断命令格式是否正确
        if (!isCorrect(commandValue)) {
            throw new IMException(Exceptions.PARSE_ERROR.getCode(), Exceptions.PARSE_ERROR.getMessage());
        }

        //  持久化内存中的消息和防重集合
        System.out.println("正在保存内存中的消息，请不要关闭程序");
        manager.persistMsg();
        avoidRepeatManager.finishAndPersist();
        //  处理正在发送中的消息（即没有收到ack的消息），这里阻塞死等所有的消息都被ack或者超过重传次数（发送失败）
        timeoutTimerManager.await();
        System.out.println("保存完毕");
        //  向服务端发送退出消息
        logoutSender.sendLogoutMsg();
        log.info("退出成功");
        //  关闭程序
        System.exit(0);
    }
}
