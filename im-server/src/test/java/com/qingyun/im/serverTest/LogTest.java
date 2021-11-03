package com.qingyun.im.serverTest;

import com.qingyun.im.server.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-03 18:15
 **/
@SpringBootTest(classes = ServerApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class LogTest {

    @Test
    public void logTest() {
        log.info("{}", 1);
    }
}
