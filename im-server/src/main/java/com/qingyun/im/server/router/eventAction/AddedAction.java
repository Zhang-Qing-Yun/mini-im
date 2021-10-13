package com.qingyun.im.server.router.eventAction;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.server.router.ImWorker;
import com.qingyun.im.server.router.Router;
import com.qingyun.im.server.router.RouterMap;
import com.qingyun.im.server.util.SpringContextUtil;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @description： 监听到结点增加即有服务器上线时的操作
 * @author: 張青云
 * @create: 2021-10-09 18:56
 **/
@Component("addedAction")
public class AddedAction implements EventAction {

    @Autowired
    private ImWorker imWorker;

    @Autowired
    private RouterMap routerMap;

    @Autowired
    private SpringContextUtil contextUtil;

    @Override
    public void action(ChildData data) {
        //  获取新上线的结点的信息
        byte[] payload = data.getData();
        ImNode remoteNode = JSON.parseObject(payload, ImNode.class);
        long remoteId = imWorker.getIdByPath(data.getPath());
        remoteNode.setId(remoteId);

        //  判断是否为当前结点
        if (remoteNode.equals(EventAction.getLocalNode(imWorker))) {
            return;
        }
        //  判断是否已经处理过了
        Router router = routerMap.getRouterByNodeId(remoteId);
        if (router != null && router.getRemoteNode().equals(EventAction.getLocalNode(imWorker))) {
            return;
        }
        doAfterAdd(remoteNode, router);
    }

    /**
     * 检测到结点上线时的具体逻辑
     * @param remoteNode 远程节点信息
     * @param router 在路由表中该结点对应的转发器，如果不为空，需要先关闭
     */
    private void doAfterAdd(ImNode remoteNode, Router router) {
        if (router != null) {
            //  关闭老连接
            router.stopConnecting();
        }
        //  获取单例Router
        ApplicationContext context = contextUtil.getContext();
        router = context.getBean(Router.class);
        router.init(remoteNode);
        //  与先上线的结点建立连接
        try {
            router.doConnect();
        } catch (Exception e) {
            //  如果连接失败，则直接放弃连接。新上线的结点在收不到当前结点的Notification时，就会超时报错，启动失败。
            router.stopConnecting();
            return;
        }
        //  发送连接成功
        router.sendConnectNotification();
        //  将该router保存起来
        routerMap.addCandidate(remoteNode.getId(), router);
    }
}
