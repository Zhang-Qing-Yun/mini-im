package com.qingyun.im.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description： 加载一些配置信息
 * @author: 張青云
 * @create: 2021-10-05 13:38
 **/
@Component
@Data
public class AttributeConfig {
    //  最大重连次数
    @Value("${im.client.maxRetryCount:3}")
    private int maxRetryCount;

    //  两次重连的时间间隔,单位毫秒
    @Value("${im.client.retryInterval:1000}")
    private long retryInterval;

    //  单次发送消息的最大字符长度
    @Value("${im.client.messageMaxSize:500}")
    private int messageMaxSize;
}
