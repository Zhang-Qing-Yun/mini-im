package com.qingyun.im.server.router.eventAction;

import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.server.router.ImWorker;
import org.apache.curator.framework.recipes.cache.ChildData;

/**
 * @description： 监听到事件时的相关操作
 * @author: 張青云
 * @create: 2022-12-09 18:54
 **/
public interface EventAction {

    /**
     * 发生对应事件时的操作
     * @param data 引起事件的数据
     */
    void action(ChildData data);

    /**
     * 获取当前结点的信息
     * @return 当前结点信息
     */
    static ImNode getLocalNode(ImWorker imWorker) {
        return imWorker.getImNode();
    }
}
