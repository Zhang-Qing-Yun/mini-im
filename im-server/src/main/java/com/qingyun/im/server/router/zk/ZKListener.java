package com.qingyun.im.server.router.zk;

import com.qingyun.im.common.constants.ServerConstants;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.server.router.eventAction.EventAction;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @description： ZK监听器，监听节点变化（即集群变化）
 * @author: 張青云
 * @create: 2021-10-09 16:37
 **/
@Component
public class ZKListener {
    private CuratorFramework client = null;

    private boolean isDone = false;

    @Autowired
    private CuratorZKClient curatorZKClient;

    @Autowired
    @Qualifier("addedAction")
    private EventAction addedAction;

    @Autowired
    @Qualifier("removedAction")
    private EventAction removedAction;


    @PostConstruct
    private void init() {
        this.client = curatorZKClient.getClient();
    }

    /**
     * 设置监听器，监听ZK结点的变化
     */
    public synchronized void setListener() {
        if (isDone) {
            return;
        }
        isDone = true;
        try {
            //  订阅节点的增加和删除事件
            PathChildrenCache childrenCache = new PathChildrenCache(client, ServerConstants.MANAGE_PATH, true);
            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client,
                                       PathChildrenCacheEvent event) throws Exception {
                    ChildData data = event.getData();
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            addedAction.action(data);
                            break;
                        case CHILD_REMOVED:
                            removedAction.action(data);
                            break;
                        default:
                            break;
                    }
                }
            };

            childrenCache.getListenable().addListener(childrenCacheListener);
            //  同步初始化
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_LISTENER.getCode(), Exceptions.ZK_LISTENER.getMessage());
        }
    }

}
