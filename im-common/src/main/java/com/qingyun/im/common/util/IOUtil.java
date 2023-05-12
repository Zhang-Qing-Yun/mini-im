package com.qingyun.im.common.util;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMRuntimeException;

import java.net.InetAddress;

/**
 * @description： 有关IO操作的工具类
 * @author: 張青云
 * @create: 2022-12-09 14:31
 **/
public final class IOUtil {
    /**
     * 获取本地IP地址
     * @return IP地址
     */
    public static String getHostAddress() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            throw new IMRuntimeException(Exceptions.ADDR_ERROR.getCode(), Exceptions.ADDR_ERROR.getMessage());
        }
        return ip;
    }
}
