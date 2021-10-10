package com.qingyun.im.server.router.eventAction;

import org.apache.curator.framework.recipes.cache.ChildData;

/**
 * @description： 监听到事件时的相关操作
 * @author: 張青云
 * @create: 2021-10-09 18:54
 **/
public interface EventAction {

    void action(ChildData data);
}
