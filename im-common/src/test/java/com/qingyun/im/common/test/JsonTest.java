package com.qingyun.im.common.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qingyun.im.common.entity.Notification;
import com.qingyun.im.common.entity.ServerInfo;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-10 19:41
 **/
public class JsonTest {
    @Test
    public void jsonTest() {
        Notification<ServerInfo> notification = new Notification<>(Notification.CONNECT_FINISHED, new ServerInfo("1", 2));
        String json = JSON.toJSONString(notification);
        Notification<ServerInfo> object = JSON.parseObject(json, new TypeReference<Notification<ServerInfo>>(){});
        System.out.println(object.getData());
        System.out.println(object.getData().getClass());
    }

    @Test
    public void mapTest() {
        ConcurrentHashMap<Long, Long> map = new ConcurrentHashMap<>();
        System.out.println(map.remove(1L));
    }
}
