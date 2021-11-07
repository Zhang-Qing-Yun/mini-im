package com.qingyun.im.server.router.manager;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

/**
 * @description： 管理当前Server要等待的结点的集合
 * @author: 張青云
 * @create: 2021-10-09 23:33
 **/
@Component
public class WaitManager {
    //  要等待的结点的集合
    private CopyOnWriteArraySet<Long> waitNodes;

    //  锁，用于阻塞至完成结点互联
    private final Object o = new Object();

    //  相当于锁，用于阻塞至完成等待集合的初始化完成
    private final CompletableFuture<Boolean> initLock = new CompletableFuture<>();

    //  是否可以放行
    private boolean canGo = false;

    public void init(CopyOnWriteArraySet<Long> waitNodes) {
        this.waitNodes = waitNodes;
        //  初始化完成
        initLock.complete(true);
    }

    /**
     * 增加一个等待节点
     */
    public void increase(Long id) {
        waitNodes.add(id);
    }

    /**
     * 取消一个等待结点
     * @param id 要取消的结点id。如果该结点不在等待集合当中，不做任何操作！
     */
    public void decrease(Long id) {
        synchronized (o) {
            boolean ok = waitNodes.remove(id);
            if (!ok) {
                return;
            }
            //  当等待集合为空时放行
            if (waitNodes.isEmpty()) {
                letGo();
                canGo = true;
            }
        }
    }

    /**
     * 阻塞
     * @param maxWaitTime 最大等待时间
     */
    public void await(long maxWaitTime) throws InterruptedException {
        synchronized (o) {
            if (waitNodes != null && !waitNodes.isEmpty()) {
                o.wait(maxWaitTime);
            } else {
                canGo = true;
            }
        }
    }

    /**
     * 放行
     */
    public void letGo() {
        o.notify();
    }

    public boolean isCanGo() {
        return canGo;
    }

    /**
     * 阻塞至初始化完成
     */
    public void awaitInit() {
        try {
            initLock.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IMRuntimeException(Exceptions.START_FAIL.getCode(), Exceptions.START_FAIL.getMessage());
        }
    }
}
