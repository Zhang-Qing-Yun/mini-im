package com.qingyun.im.auth.serviceTest;

import com.qingyun.im.auth.service.OfflineMsgService;
import com.qingyun.im.common.entity.Msg;
import com.qingyun.im.common.entity.ProtoMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-15 21:09
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class OfflineTEst {
    @Autowired
    private OfflineMsgService offlineMsgService;

    @Test
    public void getTest() {
        List<Msg> offlineMsg = offlineMsgService.getAllOfflineMsg("qiang");
        for (Msg message: offlineMsg) {
            System.out.println(message.getContext());
        }
    }

    @Test
    public void deleteTest() {
        offlineMsgService.deleteAllOfflineMsg("qiang");
    }
}
