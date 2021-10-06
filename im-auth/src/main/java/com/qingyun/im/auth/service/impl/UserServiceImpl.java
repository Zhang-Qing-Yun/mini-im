package com.qingyun.im.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyun.im.auth.vo.User;
import com.qingyun.im.auth.mapper.UserMapper;
import com.qingyun.im.auth.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 張青云
 * @since 2021-09-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public Boolean insert(String username, String password) {
        //  验证用户名是否可用
        if (hasUsername(username)) {
            return false;
        }
        //  对密码进行加密
        password = passwordEncoder.encode(password);
        User user = new User(username, password);
        int insert = userMapper.insert(user);
        return insert == 1;
    }

    @Override
    public User selectByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        return user;
    }

    @Override
    public Long selectIdByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.select("id");
        return userMapper.selectOne(wrapper).getId();
    }

    @Override
    public List<String> selectUsernamesByIds(List<Long> ids) {
        return userMapper.selectUsernamesByIds(ids);
    }

    @Override
    public Boolean hasUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.select("username");
        User user = userMapper.selectOne(wrapper);
        return user != null;
    }

    @Override
    public Boolean login(String username, String password) {
        User user = selectByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
