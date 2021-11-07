package com.qingyun.im.client.test.persistence;

import com.qingyun.im.client.ClientApplication;
import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.msgCache.persistence.Persistence;
import com.qingyun.im.client.pojo.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-07 14:07
 **/
@SpringBootTest(classes = ClientApplication.class)
@RunWith(SpringRunner.class)
public class FileTest {
    @Autowired
    private Persistence persistence;

    @Autowired
    private ClientSession session;

    @Before
    public void init() {
        UserInfo.getInstance().setUsername("qingyun");
        session.setUserInfo(UserInfo.getInstance());
    }

    /*
    * 对于相对文件路径，单元测试是在当前模块下，而主程序是在当前项目下
    * */
    @Test
    public void getUsernamesWithMessageTest() throws Exception {
        Set<String> usernames = persistence.getUsernamesWithMessage();
        System.out.println(usernames);
    }
}
