package com.qingyun.im.server.router;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.constants.ServerConstants;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.zk.CuratorZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： Netty Server结点和Zk的桥梁，执行一些与Zk相关的操作。
 * @author: 張青云
 * @create: 2021-10-08 19:44
 **/
@Component
public class ImWorker {
    //  当前Netty Server结点的信息，即注册在ZK节点里的信息
    //  每个Server对应一个ImWorker、一个ImNode
    private ImNode localNode = null;

    //  保存当前Server节点在Zk的路径，在ZK上创建后返回（带序号）
    private String pathRegistered = null;

    //  Zk curator 客户端
    private CuratorFramework client = null;

    //  是否初始化过
    private boolean isInit = false;

    @Autowired
    private CuratorZKClient curatorZKClient;


    public ImWorker() {
        localNode = new ImNode();
    }

    /**
     * 在ZK上为当前Server创建一个结点
     */
    public synchronized void init() {
        if(isInit) {
            return;
        }
        this.isInit = true;

        if (client == null) {
            this.client = curatorZKClient.getClient();
        }

        //  创建父结点
        createParentIfNeeded(ServerConstants.MANAGE_PATH);

        //  创建一个临时有序节点，节点的payload为当前Server实例的ImNode
        try {
            byte[] payload = JSON.toJSONBytes(localNode);
            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ServerConstants.PATH_PREFIX, payload);

            //  为node设置id
            localNode.setId(getIdByPath(pathRegistered));
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 设置结点的ip和端口号
     */
    public void setLocalNode(String ip, int port) {
        localNode.setIp(ip);
        localNode.setPort(port);
    }

    /**
     * 创建父结点（持久结点）
     * @param managePath 父结点路径
     */
    private void createParentIfNeeded(String managePath) {
        try {
            Stat stat = client.checkExists().forPath(managePath);
            if (null == stat) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 根据结点在ZK的路径来获取ZK为该结点生成的序号
     * @param path 结点的路径
     * @return 序号
     */
    public long getIdByPath(String path) {
        String sid = null;
        int index = path.lastIndexOf(ServerConstants.PATH_PREFIX);
        if (index >= 0) {
            index += ServerConstants.PATH_PREFIX.length();
            sid = index <= path.length() ? path.substring(index) : null;
        }

        if (null == sid) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }

        return Long.parseLong(sid);
    }

    /**
     * 返回本地的节点信息
     * @return 本地的节点信息
     */
    public ImNode getImNode() {
        return localNode;
    }

    /**
     * 获取结点在Zk上的地址（含序号）
     */
    public String getPathRegistered() {
        return pathRegistered;
    }
}
