package com.qingyun.im.common.test;

import com.qingyun.im.common.idGenerator.SnowFlake;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-15 00:05
 **/
public class IDTest {
    private SnowFlake snowFlake = new SnowFlake();

    @Before
    public void init() {
        snowFlake.init(0, 0);
    }

    @Test
    public void getIDTest() {
        System.out.println(snowFlake.generatorLongID());
    }

    @Test
    public void nextIDTest() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = format.format(new Date(9276935569932288L >> 22));
        System.out.println(s);
    }
}
