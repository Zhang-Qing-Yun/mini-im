package com.qingyun.im.serverTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyun.im.common.entity.Msg;
import com.qingyun.im.server.Mapper.MsgMapper;
import com.qingyun.im.server.ServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description： 测试分库分表
 * @author: 張青云
 * @create: 2021-11-15 14:25
 **/
@SpringBootTest(classes = ServerApplication.class)
@RunWith(SpringRunner.class)
public class ShardingJdbcTest {
    @Autowired
    private MsgMapper msgMapper;

    @Test
    public void insertTest() {
        Msg offline = new Msg();
        offline.setId(9276935569932288L)
                .setFromUsername("qingyun")
                .setToUsername("qiang")
                .setToUserId(2L)
                .setContext("hello")
                .setSendTime(System.currentTimeMillis())
                .setMsgStatus(0)
                .setGmtCreate(LocalDateTime.now())
                .setGmtUpdate(LocalDateTime.now());
        msgMapper.insert(offline);
    }

    @Test
    public void getTest() {
        QueryWrapper<Msg> w = new QueryWrapper<>();
        w.eq("to_user_id", 10002L);
        List<Msg> msgs = msgMapper.selectList(w);
        System.out.println(msgs);
    }
}
