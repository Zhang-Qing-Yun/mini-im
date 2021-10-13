package com.qingyun.im.common.concurrent;

import com.google.common.util.concurrent.*;
import com.qingyun.im.common.util.ThreadUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @description： 处理CallbackTask
 * @author: 張青云
 * @create: 2021-10-13 12:29
 **/
public class CallbackTaskScheduler {
    static ListeningExecutorService gPool = null;

    static {
        //  获取混合型线程池
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        gPool = MoreExecutors.listeningDecorator(jPool);
    }

    private CallbackTaskScheduler() {
    }


    /**
     * 添加CallbackTask任务
     * @param executeTask 任务
     * @param <R> 返回值类型
     */
    public static <R> void add(CallbackTask<R> executeTask) {
        ListenableFuture<R> future = gPool.submit(new Callable<R>() {
            public R call() throws Exception {
                R r = executeTask.execute();
                return r;
            }
        });

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });
    }
}
