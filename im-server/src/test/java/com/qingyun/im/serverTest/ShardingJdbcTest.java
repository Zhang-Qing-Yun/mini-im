package com.qingyun.im.serverTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyun.im.common.entity.Msg;
import com.qingyun.im.server.Mapper.MsgMapper;
import com.qingyun.im.server.Mapper.UserMapper;
import com.qingyun.im.server.ServerApplication;
import com.qingyun.im.server.entity.User;
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

    @Autowired
    private UserMapper userMapper;

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

    @Test
    public void getUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", "qingyun");
        wrapper.select("id");
        User user = userMapper.selectOne(wrapper);
        System.out.println(user.getId());
    }

    @Test
    public void existTest() {
        QueryWrapper<Msg> oldMsgWrapper = new QueryWrapper<>();
        oldMsgWrapper.eq("id", 9276935569932289L);
        oldMsgWrapper.eq("to_user_id", 2L);
        oldMsgWrapper.select("id");
        Msg oldMsg = msgMapper.selectOne(oldMsgWrapper);
        System.out.println(oldMsg == null);
    }
}
