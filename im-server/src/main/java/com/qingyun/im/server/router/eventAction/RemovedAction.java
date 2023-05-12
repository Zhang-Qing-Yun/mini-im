package com.qingyun.im.server.router.eventAction;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.server.router.ImWorker;
import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.router.RouterMap;
import com.qingyun.im.server.router.manager.WaitManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 发生结点删除即有结点下线时的操作时的操作
 * @author: 張青云
 * @create: 2022-12-09 18:58
 **/
@Component("removedAction")
@Slf4j
public class RemovedAction implements EventAction{
    @Autowired
    private WaitManager waitManager;

    @Autowired
    private RouterMap routerMap;

    @Autowired
    private ImWorker imWorker;


    @Override
    public void action(ChildData data) {
        //  获取下线结点的信息
        byte[] payload = data.getData();
        ImNode remoteNode = JSON.parseObject(payload, ImNode.class);
        long remoteId = imWorker.getIdByPath(data.getPath());
        remoteNode.setId(remoteId);

        //  判断是否为当前结点
        ImNode localNode = EventAction.getLocalNode(imWorker);
        if (remoteNode.equals(localNode)) {
            return;
        }
        //  判断当前结点是否在建立互联的过程中
        if (!localNode.isReady()) {
            routerMap.removeCandidate(remoteId);
            waitManager.decrease(remoteId);
        }
        doAfterRemove(remoteNode);
    }

    /**
     * 检测到结点下线时的具体逻辑
     * @param remoteNode 下线结点
     */
    private void doAfterRemove(ImNode remoteNode) {
        long id = remoteNode.getId();
        //  获取与该结点的转发器
        Router router = routerMap.getRouterByNodeId(id);
        if (router != null) {
            //  关闭与该远程结点的连接
            router.stopConnecting();
            //  更新转发表
            routerMap.clearRouter(id);
            log.info("检测到结点{}下线，已删除对应的转发器", id);
        }
    }
}
