package com.qingyun.im.server.router.eventAction;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.stereotype.Component;

/**
 * @description： 监听到结点增加即有服务器上线时的操作
 * @author: 張青云
 * @create: 2021-10-09 18:56
 **/
@Component("addedAction")
public class AddedAction implements EventAction{

    @Override
    public void action(ChildData data) {

    }
}
