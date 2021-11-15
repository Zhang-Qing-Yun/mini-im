package com.qingyun.im.auth.controller;

import com.qingyun.im.auth.service.OfflineMsgService;
import com.qingyun.im.common.entity.Msg;
import com.qingyun.im.common.entity.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-15 21:05
 **/
@RestController
@RequestMapping("/offline")
public class OfflineMsgController {
    @Autowired
    private OfflineMsgService offlineMsgService;

    @GetMapping("/getOfflineMsg")
    public R getOfflineMsg(String username) {
        List<Msg> offlineMsg = offlineMsgService.getAllOfflineMsg(username);
        return R.ok().data("offlineMsg", offlineMsg);
    }

    @GetMapping("/ackOfflineMsg")
    public R ackOfflineMsg(String username) {
        offlineMsgService.deleteAllOfflineMsg(username);
        return R.ok();
    }
}
