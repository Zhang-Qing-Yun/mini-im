package com.qingyun.im.auth.controller;


import com.qingyun.im.auth.service.UserService;
import com.qingyun.im.auth.service.ZKService;
import com.qingyun.im.auth.vo.User;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.enums.ResultType;
import com.qingyun.im.common.entity.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 張青云
 * @since 2021-09-23
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ZKService zkService;


    @PostMapping("/register")
    public R register(@RequestBody User user) {
        if (userService.insert(user.getUsername(), user.getPassword())) {
            return R.ok();
        }
        return R.error()
                .code(ResultType.REGISTER_FAIL.getCode())
                .message(ResultType.REGISTER_FAIL.getMsg());
    }

    @PostMapping("/login")
    public R login(@RequestBody User user) {
        if (!userService.login(user.getUsername(), user.getPassword())) {
            return R.error()
                    .code(ResultType.LOGIN_FAIL.getCode())
                    .message(ResultType.LOGIN_FAIL.getMsg());
        }
        //  返回NettyServer的信息
        List<ImNode> imNodes = zkService.getAllNode();
        return R.ok().data("imNodes", imNodes);
    }

}

