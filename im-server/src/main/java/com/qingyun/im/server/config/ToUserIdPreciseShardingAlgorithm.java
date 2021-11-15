package com.qingyun.im.server.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @description： 根据消息接收者的id范围对离线消息进行水平分表的分片策略
 * @author: 張青云
 * @create: 2021-11-15 13:45
 **/
public class ToUserIdPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    /**
     * 用一张表来存多少个用户的离线消息；
     * 假设每个用户平均有500条离线消息，且每张表的数据不超过500W条，则每张表可以保存1W个用户的离线消息；
     */
    private static final long USER_COUNT_PRE_TABLE = 10000;

    /**
     * @param tableNames 所有配置的表列表
     * @param shardingValue 分片字段的值
     * @return 选择的表
     */
    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<Long> shardingValue) {
        long toUserId = shardingValue.getValue();
        //  分配到第几张表
        long index = toUserId / USER_COUNT_PRE_TABLE + 1;
        for (String tableName: tableNames) {
            if (tableName.endsWith(String.valueOf(index))) {
                return tableName;
            }
        }
        throw new UnsupportedOperationException();
    }
}
