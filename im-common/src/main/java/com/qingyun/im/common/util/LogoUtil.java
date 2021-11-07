package com.qingyun.im.common.util;


import java.util.Collections;

/**
 * @description： 打印一些Logo
 * @author: 張青云
 * @create: 2021-11-06 21:41
 **/
public final class LogoUtil {
    /**
     * 打印分隔符
     */
    public static void printSplitLine() {
        System.out.println(String.join("", Collections.nCopies(30, "-")));
    }
}
