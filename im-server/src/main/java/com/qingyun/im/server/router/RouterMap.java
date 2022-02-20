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
    //  转发表
    private final ConcurrentHashMap<Long, Router> routerMap = new ConcurrentHashMap<>();
    //  正处于结点互联建立状态中的转发器集合
    private final ConcurrentHashMap<Long, Router> candidateMap = new ConcurrentHashMap<>();


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
     */
    public void removeRouter(long id) {
        routerMap.remove(id);
    }

    /**
     * 从转发表和准转发器表中删除某个Router的记录
     */
    public void clearRouter(long id) {
        candidateMap.remove(id);
        routerMap.remove(id);
    }

    /**
     * 从转发表中根据结点的id（由ZK生成的）获取对应的转发器
     * @param nodeId id
     * @return 转发器
     */
    public Router getRouterByNodeId(long nodeId) {
        Router candidateRouter = candidateMap.get(nodeId);
        if (candidateRouter != null) {
            return candidateRouter;
        }
        return routerMap.get(nodeId);
    }

    /**
     * 添加一个正处于节点互联建立过程中的转发器
     * @param nodeId 远程结点id
     * @param router 转发器
     */
    public void addCandidate(long nodeId, Router router) {
        candidateMap.put(nodeId, router);
    }

    /**
     * 将一个位于candidateMap中的元素转移到routerMap中
     * @param nodeId key
     */
    public synchronized void toFullRouter(long nodeId) {
        Router router = candidateMap.remove(nodeId);
        if (router == null) {
            return;
        }
        routerMap.put(nodeId, router);
    }

    /**
     * 删除一个正在建立中的router
     * @param nodeId 该router对应的结点的id
     */
    public void removeCandidate(long nodeId) {
        candidateMap.remove(nodeId);
    }
}
