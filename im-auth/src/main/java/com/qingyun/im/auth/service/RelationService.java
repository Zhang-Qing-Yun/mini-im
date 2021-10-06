package com.qingyun.im.auth.service;

import com.qingyun.im.auth.vo.Relation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingyun.im.auth.vo.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 張青云
 * @since 2021-10-05
 */
public interface RelationService extends IService<Relation> {
    /**
     * 判断两人是否是好友关系
     * @param username1 用户1的username
     * @param username2 用户2的username
     * @return 是否为好友关系
     */
    boolean isFriend(String username1, String username2);

    /**
     * 发送加好友请求
     * @param username1 请求的发送方
     * @param username2 被请求的用户
     * @return 是否成功
     */
    boolean askFriend(String username1, String username2);

    /**
     * 确认好友请求
     * @param username1 确认好友请求的用户,即被请求的用户
     * @param username2 发送好友请求的用户
     * @return 是否成功
     */
    boolean ackFriend(String username1, String username2);

    /**
     * 查询好友请求
     * @param username 用户名
     * @return 属于该用户的好友请求，没有则返回null
     */
    List<String> getFriendAsk(String username);
}
