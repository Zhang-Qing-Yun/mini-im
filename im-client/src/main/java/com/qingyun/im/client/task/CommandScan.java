package com.qingyun.im.client.task;

import com.qingyun.im.client.command.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Scanner;

/**
 * @description： 读取用户输入的命令
 * @author: 張青云
 * @create: 2021-10-01 15:28
 **/
@Component
public class CommandScan implements Runnable{
    @Autowired
    private CommandContext commandContext;


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            //  读取一条命令
            String commandValue = scanner.nextLine();
            if (StringUtils.isEmpty(commandValue)) {
                continue;
            }
            commandContext.invokeHandle(commandValue);
        }
    }
}
