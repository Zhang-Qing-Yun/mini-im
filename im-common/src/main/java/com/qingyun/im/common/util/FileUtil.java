package com.qingyun.im.common.util;

import java.io.File;
import java.io.IOException;

/**
 * @description： 文件操作相关的工具类
 * @author: 張青云
 * @create: 2021-10-16 18:58
 **/
public final class FileUtil {

    /**
     * 判断指定文件路径是否存在
     */
    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 创建指定路径的文件
     */
    public static boolean createFile(String path) throws IOException {
        if (isExist(path)) {
            return false;
        }
        File file = new File(path);
        return file.createNewFile();
    }

    /**
     * 创建指定路径的目录
     */
    public static boolean createDir(String path) {
        if (isExist(path)) {
            return false;
        }
        return new File(path).mkdirs();
    }
}
