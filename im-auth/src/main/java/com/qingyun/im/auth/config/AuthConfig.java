package com.qingyun.im.auth.config;

import com.qingyun.im.common.zk.CuratorZKClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-09-20 16:32
 **/
@Configuration
@MapperScan("com.qingyun.im.auth.mapper")
public class AuthConfig {
    @Value("${im.zk.connectString}")
    private String connectString;


    @Bean
    public CuratorZKClient curatorZKClient() {
        return new CuratorZKClient(connectString).init();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
