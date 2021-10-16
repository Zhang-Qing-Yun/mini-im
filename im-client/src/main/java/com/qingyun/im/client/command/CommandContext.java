package com.qingyun.im.client.command;

import com.qingyun.im.client.command.handle.CommandHandle;
import com.qingyun.im.common.exception.IMException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @description： 维护命令和处理器之间的关系
 * @author: 張青云
 * @create: 2021-09-24 20:24
 **/
@Component
public class CommandContext implements ApplicationContextAware {
    //  应用上下文
    private ApplicationContext applicationContext;

    //  用来维护命令和处理器之间的一一对应关系
    private final Map<Command, CommandHandle> handleMap;

    public CommandContext() {
        handleMap = new HashMap<>();
    }

    /**
     * 初始化handleMap
     */
    @PostConstruct  // 该注解是在postProcessBeforeInitialization处进行处理的
    private void init() {
        //  获取所有的命令
        Command[] commands = Command.values();
        //  获取所有的处理器
        String[] handleNames = applicationContext.getBeanNamesForType(CommandHandle.class);
        for (String handleName: handleNames) {
            CommandHandle handle = applicationContext.getBean(handleName, CommandHandle.class);
            for (Command command: commands) {
                if (handle.isCare(command.getCommandKey())) {
                    handleMap.put(command, handle);
                    break;
                }
            }
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 会去命令对应的处理器
     * @param command 命令
     * @return 命令处理器
     */
    public CommandHandle getCommandHandle(Command command) {
        return handleMap.get(command);
    }

    /**
     * 调用具体的处理器去执行该命令
     * @param commandValue 命令
     * @return 是否执行成功
     */
    public boolean invokeHandle(String commandValue) {
        //  根据命令关键字获取Command
        Command command = null;
        try {
            command = Command.getCommandByKey(commandValue);
        } catch (IMException e) {
            System.out.println(e.getMsg());
            return false;
        }
        //  获取相应的处理器
        CommandHandle handle = getCommandHandle(command);

        try {
            handle.process(commandValue);
            return true;
        } catch(RuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("错误，请重试！" + "提示：" + e.getMessage());
            return false;
        }
        return false;
    }
}
