package com.qingyun.im.auth.controller;


import com.qingyun.im.auth.service.RelationService;
import com.qingyun.im.common.entity.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 張青云
 * @since 2022-12-05
 */
@RestController
@RequestMapping("/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;

    @GetMapping("/getFriendAsk")
    public R getFriendAsk(String username) {
        //  好友请求列表
        List<String> friendAsk = relationService.getFriendAsk(username);
        return R.ok().data("friendAsk", friendAsk);
    }

    @GetMapping("/askFriend")
    public R askFriend(String username1, String username2) {
        boolean result = relationService.askFriend(username1, username2);
        if (result) {
            //  当前的好友列表
            List<String> friendList = relationService.getFriendList(username1);
            return R.ok().data("friendList", friendList);
        }
        return R.error();
    }

    @GetMapping("/ackFriend")
    public R ackFriend(String username1, String username2) {
        boolean result = relationService.ackFriend(username1, username2);
        if (result) {
            //  当前的好友列表
            List<String> friendList = relationService.getFriendList(username1);
            return R.ok().data("friendList", friendList);
        }
        return R.error();
    }

    @GetMapping("/getFriendList")
    public R getFriendList(String username) {
        List<String> friendList = relationService.getFriendList(username);
        return R.ok().data("friendList", friendList);
    }
}

