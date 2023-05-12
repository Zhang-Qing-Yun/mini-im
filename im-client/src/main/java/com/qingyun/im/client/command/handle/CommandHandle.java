package com.qingyun.im.client.command.handle;

/**
 * @description： 命令处理器
 * @author: 張青云
 * @create: 2022-11-24 19:37
 **/
public interface CommandHandle {
    /**
     * 根据命令关键字判断该处理器是否可以处理该命令
     * @param commandValue 命令或命令关键字
     * @return 判断结果
     */
    boolean isCare(String commandValue);

    /**
     * 判断命令的格式是否正确
     * @param commandValue 命令，必须是该处理器能处理的命令，即isCare函数的返回值为true
     * @return 判断结果，如果不是正确的命令格式则返回false
     */
    boolean isCorrect(String commandValue);

    /**
     * 解析并处理该命令
     * @param commandValue 命令
     * @throws Exception 处理过程中出错时抛出的异常
     */
    void process(String commandValue) throws Exception;

    /**
     * 获取命令关键字，即不带参数的命令
     * @param commandValue 命令
     * @return 命令关键字
     */
    static String getCommandKey(String commandValue) {
        return commandValue.trim().split(" ")[0];
    }
}
