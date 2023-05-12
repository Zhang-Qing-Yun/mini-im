package com.qingyun.im.common.idGenerator;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;

/**
 * @description： 使用雪花算法来生成分布式全局唯一id，使用之前需要调用init方法
 * @author: 張青云
 * @create: 2022-12-20 09:27
 **/
public class SnowFlake extends AbstractIDGenerator {
    /**
     * 起始时间戳
     */
    private final static long START_STAMP = 1634694247108L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12;  // 序列号占用的位数
    private final static long MACHINE_BIT = 5;  // 机器标识占用的位数
    private final static long DATA_CENTER_BIT = 5;  // 数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private long dataCenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStamp = -1L;//上一次时间戳


    /**
     * 初始化
     * @param dataCenterId 数据中心的编号
     * @param machineId 该机器在数据中心中的编号
     */
    public void init(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    @Override
    public long generatorLongID() {
        synchronized (this) {
            long currStamp = getNewStamp();
            //  判断是否发生了时钟回拨
            if (currStamp < lastStamp) {
                throw new IMRuntimeException(Exceptions.CLOCK_BACK.getCode(), Exceptions.CLOCK_BACK.getMessage());
            }

            if (currStamp == lastStamp) {
                //  相同毫秒内，序列号自增
                sequence = (sequence + 1) & MAX_SEQUENCE;
                //  同一毫秒的序列数已经达到最大，则阻塞到下一个毫秒
                if (sequence == 0L) {
                    currStamp = getNextMill();
                }
            } else {
                //不同毫秒内，序列号置为0
                sequence = 0L;
            }

            lastStamp = currStamp;

            return (currStamp - START_STAMP) << TIMESTAMP_LEFT //  时间戳部分
                    | dataCenterId << DATA_CENTER_LEFT       //  数据中心部分
                    | machineId << MACHINE_LEFT             //  机器标识部分
                    | sequence;                             //  序列号部分
        }
    }

    /**
     * 获取下一个毫秒的时间戳
     */
    private long getNextMill() {
        long mill = getNewStamp();
        //  通过CPU忙循环的方式等待
        while (mill <= lastStamp) {
            mill = getNewStamp();
        }
        return mill;
    }

    /**
     * 获取当前时间的时间戳，这里依赖于机器时钟
     */
    private long getNewStamp() {
        return System.currentTimeMillis();
    }
}
