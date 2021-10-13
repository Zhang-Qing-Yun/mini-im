package com.qingyun.im.common.concurrent;

/**
 * @description： 异步带返回值的任务
 * @author: 張青云
 * @create: 2021-10-13 09:58
 **/
public interface CallbackTask<R> {
    /**
     * 执行该任务
     */
    R execute() throws Exception;

    /**
     * 执行成功回调
     */
    void onBack(R r);

    /**
     * 执行失败的回调
     */
    void onException(Throwable t);

}
