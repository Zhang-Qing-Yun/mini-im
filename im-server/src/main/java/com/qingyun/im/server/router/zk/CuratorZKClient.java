package com.qingyun.im.server.router.zk;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @description： 使用Curator充当Zk客户端
 * @author: 張青云
 * @create: 2021-10-08 20:44
 **/
public class CuratorZKClient {
    //  zk 服务端集群地址
    private String connectString;

    //  session 超时时间
    private int timeOut = 60000;

    //  zkClient 重试间隔时间
    private int baseSleepTimeMs = 5000;

    //  zkClient 重试次数
    private int retryCount = 3;

    private static CuratorFramework client;

    private static boolean isInit = false;

    public CuratorZKClient(String connectString) {
        this.connectString = connectString;
    }

    public CuratorZKClient(String connectString, int timeOut, int baseSleepTimeMs, int retryCount) {
        this.connectString = connectString;
        this.timeOut = timeOut;
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.retryCount = retryCount;
    }

    /**
     * 初始化client
     * @return 当前实例
     */
    public synchronized CuratorZKClient init() {
        if (null == client) {
            //  创建客户端
            client = CuratorFrameworkFactory
                    .builder()
                    .connectString(connectString)
                    .sessionTimeoutMs(timeOut)
                    .retryPolicy(new ExponentialBackoffRetry(baseSleepTimeMs, retryCount))
                    .build();
            //  启动客户端实例,连接服务器
            client.start();
            isInit = true;
        }
        return this;
    }

    public CuratorFramework getClient() {
        if (!isInit) {
            init();
        }
        return this.client;
    }

    /**
     * 创建永久节点
     * @param zkPath 该结点在ZK中的路径
     * @param data 该结点的数据
     */
    public void createNode(String zkPath, String data) {
        try {
            // 创建一个永久节点，节点的数据为payload
            byte[] payload = null;
            if (data != null) {
                payload = data.getBytes(StandardCharsets.UTF_8);
            }
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath, payload);
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 删除结点
     * @param zkPath 结点的路径
     */
    public void deleteNode(String zkPath) {
        try {
            if (!isNodeExist(zkPath)) {
                return;
            }
            client.delete() .forPath(zkPath);
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 判断指定路径上的结点是否存在
     * @param zkPath 结点的路径
     * @return 结果
     */
    public boolean isNodeExist(String zkPath) {
        try {
            Stat stat = client.checkExists().forPath(zkPath);
            return null != stat;
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 创建一个临时有序结点
     * @param srcPath 该结点在ZK上的路径
     * @return 路径，该路径包含了ZK自动添加的序号
     */
    public String createEphemeralSeqNode(String srcPath) {
        try {
            // 创建一个临时有序节点
            String path = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(srcPath);
            return path;
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 获取某个结点下的所有子结点的路径
     * @param parentPath 父结点
     * @return 子结点路径
     */
    public List<String> getChildren(String parentPath) {
        try {
            return client.getChildren().forPath(parentPath);
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }

    /**
     * 修改节点数据
     */
    public void setNodeData(String path, byte[] payload) {
        if (!isNodeExist(path)) {
            return;
        }
        try {
            client.setData().forPath(path, payload);
        } catch (Exception e) {
            throw new IMRuntimeException(Exceptions.ZK_NODE.getCode(), Exceptions.ZK_NODE.getMessage());
        }
    }


    /**
     * 关闭与ZK服务端的连接
     */
    public void closeConnection() {
        if (null != client) {
            client.close();
        }
    }
}
