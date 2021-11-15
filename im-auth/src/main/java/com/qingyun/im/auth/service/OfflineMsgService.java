package com.qingyun.im.auth.service;

import com.qingyun.im.common.entity.Msg;

import java.util.List;

/**
 * @description： 处理有关离线消息的业务
 * @author: 張青云
 * @create: 2021-11-15 20:42
 **/
public interface OfflineMsgService {
    /**
     * 查询指定用户的所有的离线消息
     * @param username 指定用户的用户名
     * @return 离线消息
     */
    List<Msg> getAllOfflineMsg(String username);

    /**
     * 删除某人的所有的离线消息
     * @param username 指定用户的用户名
     */
    void deleteAllOfflineMsg(String username);
}
