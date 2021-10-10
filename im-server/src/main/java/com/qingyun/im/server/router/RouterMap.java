package com.qingyun.im.server.router;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： 路由转发表，key是Server的id（由Zk生成），value是连向该Server的一个客户端Router，用来执行转发操作
 * @author: 張青云
 * @create: 2021-10-09 21:04
 **/
@Component
public class RouterMap {
    private ConcurrentHashMap<Long, Router> routerMap = new ConcurrentHashMap<>();

    /**
     * 向转发表中添加一条记录
     * @param id Server的id
     * @param router 转发器
     */
    public void addRouter(long id, Router router) {
        routerMap.put(id, router);
    }

    /**
     * 从转发表中删除一条记录
     * @param id Server的id
     * @param router 转发器
     */
    public void removeRouter(long id, Router router) {
        routerMap.remove(id);
    }
}
