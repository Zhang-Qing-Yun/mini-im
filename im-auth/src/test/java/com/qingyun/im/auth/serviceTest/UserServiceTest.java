package com.qingyun.im.auth.serviceTest;

import com.qingyun.im.auth.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-09-24 10:14
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void insertTest() {
        userService.insert("qiang", "123456");
    }

    @Test
    public void hasUsernameTest() {
        System.out.println(userService.hasUsername("qingyun1"));
    }

    @Test
    public void selectByUsernameTest() {
        System.out.println(userService.selectByUsername("qingyun"));
        System.out.println(userService.selectByUsername("qingyun1"));
    }

    @Test
    public void selectUsernamesByIdsTest() {
        System.out.println(userService.selectUsernamesByIds(Arrays.asList(1L, 2L)));
    }
}
