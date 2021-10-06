package com.qingyun.im.client.test.start;

import com.qingyun.im.client.pojo.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-01 16:34
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class StartTest {

    @Test
    public void getUserInfoTest() {
        Scanner sc = new Scanner(System.in);
        UserInfo userInfo = UserInfo.getInstance();
        System.out.println("请输入用户名：");
        userInfo.setUsername(sc.nextLine());
        System.out.println("请输入密码：");
        userInfo.setPassword(sc.nextLine());
        System.out.println(UserInfo.getInstance());
    }
}
