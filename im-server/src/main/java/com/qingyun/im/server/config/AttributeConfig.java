package com.qingyun.im.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description： 属性配置
 * @author: 張青云
 * @create: 2022-12-05 19:46
 **/
@Component
@Data
public class AttributeConfig {
    @Value("${im.server.port}")
    private int port;

    //  最大重连次数
    @Value("${im.server.maxRetryCount:3}")
    private int maxRetryCount;

    //  两次重连的时间间隔,单位毫秒
    @Value("${im.client.retryInterval:500}")
    private long retryInterval;

    //  最大启动时间
    @Value("${im.server.maxStartTime}")
    private long maxStartTime;
}
