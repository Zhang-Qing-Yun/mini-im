package com.qingyun.im.serverTest;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.common.constants.ServerConstants;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.zk.CuratorZKClient;
import com.qingyun.im.server.ServerApplication;
import com.qingyun.im.server.router.ImWorker;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-01 17:56
 **/
@SpringBootTest(classes = ServerApplication.class)
@RunWith(SpringRunner.class)
public class ZKTest {
    //  Zk curator 客户端
    private CuratorFramework client = null;

    @Autowired
    private CuratorZKClient curatorZKClient;

    @Autowired
    private ImWorker imWorker;

    @Before
    public void init() {
        client = curatorZKClient.getClient();
    }

    @Test
    public void checkExistTest() throws Exception {
        Stat stat = client.checkExists().forPath(ServerConstants.MANAGE_PATH);
        System.out.println(stat);
    }

    @Test
    public void createParentIfNeededTest() throws Exception {
        client.create()
                .creatingParentsIfNeeded()
//                .withProtection()
                .withMode(CreateMode.PERSISTENT)
                .forPath(ServerConstants.MANAGE_PATH);
        Stat stat = client.checkExists().forPath(ServerConstants.MANAGE_PATH);
        System.out.println(stat);
    }

    @Test
    public void createNode() throws Exception {
        ImNode node = new ImNode("127.0.0.1", 10000);
        byte[] payload = JSON.toJSONBytes(node);
        String pathRegistered = client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(ServerConstants.PATH_PREFIX, payload);
        node.setId(imWorker.getIdByPath(pathRegistered));
        System.out.println(pathRegistered);
        System.out.println(node);
    }
}
