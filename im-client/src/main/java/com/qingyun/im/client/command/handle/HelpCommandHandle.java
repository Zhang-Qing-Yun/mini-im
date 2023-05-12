package com.qingyun.im.client.command.handle;

import com.qingyun.im.client.command.Command;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.springframework.stereotype.Component;

/**
 * @description： 处理help命令，打印所有的命令以及该命令的作用
 * @author: 張青云
 * @create: 2022-11-24 19:58
 **/
@Component
public class HelpCommandHandle implements CommandHandle {
    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.HELP.getCommandKey());
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

        //  获取所有的命令
        Command[] commands = Command.values();
        for(Command one: commands) {
            System.out.println(one.getCommandKey() + ":    " + one.getDescribe());
        }
    }
}
