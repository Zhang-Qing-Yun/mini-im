package com.qingyun.im.auth.controller;


import com.qingyun.im.auth.service.UserService;
import com.qingyun.im.auth.vo.User;
import com.qingyun.im.common.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public R register(@RequestBody User user) {
        if (userService.insert(user.getUsername(), user.getPassword())) {
            return R.ok();
        }
        return R.error();
    }

    @PostMapping("/login")
    public R login(@RequestBody User user) {
        if (!userService.login(user.getUsername(), user.getPassword())) {
            return null;
        }
        //  TODO：返回NettyServer的信息
        return R.ok();
    }

    @GetMapping("/test")
    public String test(String pwd) {
        return pwd;
    }
}

