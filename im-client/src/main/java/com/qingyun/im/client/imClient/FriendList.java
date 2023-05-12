package com.qingyun.im.client.imClient;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @description： 好友列表的本地缓存
 * @author: 張青云
 * @create: 2023-01-14 19:57
 **/
@Component
public class FriendList {
    //  好友列表
    private Set<String> friendList = new HashSet<>();

    /**
     * 初始化设置好友列表
     */
    public synchronized void initFriendList(Collection<String> friendList) {
        if (friendList == null || friendList.isEmpty()) {
            return;
        }
        //  防御式拷贝
        for (String friend: friendList) {
            this.friendList.add(friend);
        }
    }

    /**
     * 判断是否与某人为好友关系
     */
    public boolean isFriend(String friend) {
        return friendList.contains(friend);
    }

    /**
     * 获取好友列表
     */
    public Set<String> getFriendList() {
        Set<String> result = new HashSet<>(friendList.size());
        //  防御式拷贝
        for (String friend: friendList) {
            result.add(friend);
        }
        return result;
    }

}
