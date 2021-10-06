package com.qingyun.im.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description： 属性配置
 * @author: 張青云
 * @create: 2021-10-05 19:46
 **/
@Component
@Data
public class AttributeConfig {
    @Value("${im.server.port}")
    private int port;
}
