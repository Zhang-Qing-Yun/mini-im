package com.qingyun.im.server.config;

import com.qingyun.im.common.enums.IDGeneratorType;
import com.qingyun.im.common.idGenerator.IDGenerator;
import com.qingyun.im.common.idGenerator.SnowFlake;
import com.qingyun.im.common.zk.CuratorZKClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description： 服务端主配置类
 * @author: 張青云
 * @create: 2021-10-05 19:42
 **/
@Configuration
public class ServerConfig {

    @Value("${im.zk.connectString}")
    private String connectString;


    @Bean
    public CuratorZKClient curatorZKClient() {
        return new CuratorZKClient(connectString).init();
    }

    @Bean
    public SnowFlake idGenerator() {
        return (SnowFlake) IDGenerator.getInstance(IDGeneratorType.SNOW_FLAKE.getType());
    }
}
