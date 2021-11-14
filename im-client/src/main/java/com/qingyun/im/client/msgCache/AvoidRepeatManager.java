package com.qingyun.im.client.msgCache;

import com.qingyun.im.client.task.ClearUpTask;
import com.qingyun.im.common.constants.ClientConstants;
import com.qingyun.im.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description： 用于避免消息重复
 * @author: 張青云
 * @create: 2021-11-13 10:35
 **/
@Component
@Slf4j
public class AvoidRepeatManager {
    /**
     * 保存已经接收到的消息，key为消息id，value为过期时间（超过该时间可以认为是不会再收到重复消息）；
     * 为了防止该Map无限增大，占用过多内存，需要定时清理这个Map；
     */
    private final ConcurrentHashMap<Long, Long> recvMsg = new ConcurrentHashMap<>();

    //  定时任务（内部聚合了一个线程）
    private final Timer clearUpTime;


    public AvoidRepeatManager() {
        //  启动一个定时任务
        clearUpTime = new Timer("clearUpTime");
        clearUpTime.scheduleAtFixedRate(new ClearUpTask(recvMsg), ClientConstants.REPEAT_INTERVAL, ClientConstants.CLEAR_UP_INTERVAL);
    }

    /**
     * 判断是否已经接收过了指定消息
     * @param msgId 消息id即序列号
     */
    public boolean contains(long msgId) {
        return recvMsg.containsKey(msgId);
    }

    /**
     * 增加一条在接下来的时间里需要防重的消息
     * @param msgId 消息id
     * @return 如果已存在则返回false，不存在则添加成功返回true
     */
    public synchronized boolean add(long msgId) {
        if (!recvMsg.containsKey(msgId)) {
            //  过期时间
            long expires = System.currentTimeMillis() + ClientConstants.AVOID_REPEAT_INTERVAL;
            recvMsg.put(msgId, expires);
            return true;
        }
        return false;
    }

    /**
     * 本地持久化防重集合，并停止清理任务。客户端停止时使用。
     */
    public synchronized void finishAndPersist() {
        //  关闭定时任务
        clearUpTime.cancel();

        if (recvMsg.isEmpty()) {
            return;
        }
        String split = ClientConstants.SPLIT;
        BufferedWriter writer = null;
        try {
            //  创建文件路径
            FileUtil.createDir(ClientConstants.REPEAT_PATH);
            FileUtil.createFile(ClientConstants.REPEAT_PATH + ClientConstants.REPEAT_FILE);
            //  持久化
            File file = new File(ClientConstants.REPEAT_PATH + ClientConstants.REPEAT_FILE);
            writer = new BufferedWriter(new FileWriter(file));
            Iterator<Map.Entry<Long, Long>> iterator = recvMsg.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Long> one = iterator.next();
                String line = one.getKey() + split + one.getValue();
                writer.write(line);
                if (iterator.hasNext()) {
                    writer.newLine();
                }
            }
            recvMsg.clear();
            log.info("防重集合持久化完成");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("防重集合持久化失败");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从持久化中加载防重集合
     */
    public synchronized void initFromPersistence() {
        //  如果磁盘文件不存在则直接返回
        if (!FileUtil.isExist(ClientConstants.REPEAT_PATH + ClientConstants.REPEAT_FILE)) {
            return;
        }

        File file = new File(ClientConstants.REPEAT_PATH + ClientConstants.REPEAT_FILE);
        BufferedReader reader = null;
        String split = ClientConstants.SPLIT;
        long now = System.currentTimeMillis();
        try {
            //  按行读取文件，每一行都是防重集合里的一项
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] splitArr = line.split(split);
                recvMsg.put(Long.parseLong(splitArr[0]), now + ClientConstants.AVOID_REPEAT_INTERVAL);
            }
            log.info("防重集合加载完成");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("防重集合加载失败");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //  删除该文件
            file.delete();
        }
    }
}
