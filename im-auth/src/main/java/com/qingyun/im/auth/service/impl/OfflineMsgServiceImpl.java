package com.qingyun.im.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyun.im.auth.mapper.MsgMapper;
import com.qingyun.im.auth.service.OfflineMsgService;
import com.qingyun.im.auth.service.UserService;
import com.qingyun.im.common.entity.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-15 20:47
 **/
@Service
public class OfflineMsgServiceImpl implements OfflineMsgService {
    @Autowired
    private UserService userService;

    @Autowired
    private MsgMapper msgMapper;

    @Override
    public List<Msg> getAllOfflineMsg(String username) {
        //  查询用户的id
        Long userId = userService.selectIdByUsername(username);
        //  查询该用户所有的离线消息
        QueryWrapper<Msg> wrapper = new QueryWrapper<>();
        wrapper.eq("to_user_id", userId);
        wrapper.select("id", "from_username", "to_username", "context", "send_time");
        List<Msg> messages = msgMapper.selectList(wrapper);
        return messages;
    }

    @Override
    public void deleteAllOfflineMsg(String username) {
        //  查询用户的id
        Long userId = userService.selectIdByUsername(username);

        QueryWrapper<Msg> wrapper = new QueryWrapper<>();
        wrapper.eq("to_user_id", userId);
        msgMapper.delete(wrapper);
    }
}
