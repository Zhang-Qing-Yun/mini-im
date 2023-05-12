package com.qingyun.im.server;

import com.qingyun.im.server.imServer.ImServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @description： Netty Server主启动类
 * @author: 張青云
 * @create: 2022-12-05 19:37
 **/
@SpringBootApplication
@MapperScan("com.qingyun.im.server.Mapper")
public class ServerApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ServerApplication.class, args);
        ImServer server = context.getBean(ImServer.class);
        server.start();
    }
}
