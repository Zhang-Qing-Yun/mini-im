package com.qingyun.im.server.router.eventAction;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.stereotype.Component;

/**
 * @description： 发生结点删除即有结点下线时的操作时的操作
 * @author: 張青云
 * @create: 2021-10-09 18:58
 **/
@Component("removedAction")
public class RemovedAction implements EventAction{
    @Override
    public void action(ChildData data) {

    }
}
