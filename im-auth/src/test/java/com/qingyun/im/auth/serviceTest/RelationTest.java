package com.qingyun.im.auth.serviceTest;

import com.qingyun.im.auth.service.RelationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description：
 * @author: 張青云
 * @create: 2022-12-05 23:35
 **/
 @SpringBootTest
 @RunWith(SpringRunner.class)
public class RelationTest {

     @Autowired
     private RelationService relationService;

     @Test
     public void askFiendTest() {
         relationService.askFriend("qiang", "qingyun");
     }

     @Test
    public void getFriendAskTest() {
         System.out.println(relationService.getFriendAsk("qingyun"));
     }

     @Test
    public void ackFriendTest() {
         relationService.ackFriend("qingyun", "qiang");
     }

     @Test
    public void isFriendTest() {
         System.out.println(relationService.isFriend("qiang", "qingyun"));
         System.out.println(relationService.isFriend("qingyun", "qiang"));
     }
}
