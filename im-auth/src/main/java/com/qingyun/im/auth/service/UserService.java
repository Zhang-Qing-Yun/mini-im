package com.qingyun.im.auth.service;

import com.qingyun.im.auth.vo.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 張青云
 * @since 2022-11-23
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册。会先对password进行加密，然后向数据库中增加一个用户。
     * @param username 用户名
     * @param password 密码
     * @return 是否注册成功。要求用户名不能重复，否则返回false
     */
    Boolean insert(String username, String password);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 查询结果，如果没有该用户则返回null
     */
    User selectByUsername(String username);

    /**
     * 根据用户名查询用户id
     * @param username 用户名
     * @return 用户id
     */
    Long selectIdByUsername(String username);

    /**
     * 根据ids批量查询用户名
     * @param ids 一系列用户id
     * @return 用户名
     */
    List<String> selectUsernamesByIds(List<Long> ids);

    /**
     * 查询该username是否已经被使用过了
     * @param username 用户名
     * @return 查询结果
     */
    Boolean hasUsername(String username);

    /**
     * 验证用户名和密码
     * @param username 用户名
     * @param password 原始密码
     * @return 验证是否通过，即是否可以登录
     */
    Boolean login(String username, String password);
}
