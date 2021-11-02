package com.qingyun.im.auth.serviceTest;

import com.qingyun.im.auth.service.ZKService;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.zk.CuratorZKClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-01 21:03
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZKTest {
    @Autowired
    private CuratorZKClient curatorZKClient;

    @Autowired
    private ZKService zkService;

    @Test
    public void getAllNodeTest() {
        List<ImNode> allNode = zkService.getAllNode();
        System.out.println(allNode);
    }
}
