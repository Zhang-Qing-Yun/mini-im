package com.qingyun.im.client;

import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.common.enums.LoadBalancerType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @description： 客户端主启动类
 * @author: 張青云
 * @create: 2021-09-25 13:01
 **/
@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ClientApplication.class, args);
        ImClient client = context.getBean(ImClient.class);
        //  设置负载均衡策略
        client.setLoadBalancerType(LoadBalancerType.RANDOM.getType());
        client.start();
    }
}
