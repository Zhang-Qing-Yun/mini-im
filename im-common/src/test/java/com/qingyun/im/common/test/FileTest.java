package com.qingyun.im.common.test;

import com.qingyun.im.common.constants.ClientConstants;
import com.qingyun.im.common.util.FileUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-16 19:04
 **/
public class FileTest {
    @Test
    public void fileTest() {
        try {
            FileUtil.createDir(ClientConstants.FILE_PATH_PREFIX);
            FileUtil.createFile(ClientConstants.FILE_PATH_PREFIX + "123.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exceptionTest() {

        try {
            throw new RuntimeException("123");
        } catch (RuntimeException e) {
            System.out.println("被捕获了");
        }
        System.out.println("0000");
    }
}
