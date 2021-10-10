package com.qingyun.im.server.router.manager;

import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description： 管理当前Server要等待的结点的集合
 * @author: 張青云
 * @create: 2021-10-09 23:33
 **/
@Component
public class WaitManager {
    //  要等待的结点的集合
    private CopyOnWriteArraySet<Long> waitNodes;

    //  锁
    private final Object o = new Object();

    //  是否可以放行
    private boolean canGo = false;

    public void init(CopyOnWriteArraySet<Long> waitNodes) {
        this.waitNodes = waitNodes;
    }

    /**
     * 增加一个等待节点
     */
    public void increase(Long id) {
        waitNodes.add(id);
    }

    /**
     * 取消一个等待结点
     */
    public void decrease(Long id) {
        waitNodes.remove(id);
        //  当等待集合为空时放行
        if (waitNodes.isEmpty()) {
            letGo();
            canGo = true;
        }
    }

    /**
     * 阻塞
     * @param maxWaitTime 最大等待时间
     */
    public void await(long maxWaitTime) throws InterruptedException {
        o.wait(maxWaitTime);
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
}
