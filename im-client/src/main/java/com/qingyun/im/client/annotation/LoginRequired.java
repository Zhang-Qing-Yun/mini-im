package com.qingyun.im.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description： 需要登录才能执行该命令
 * @author: 張青云
 * @create: 2022-12-06 19:23
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LoginRequired {
}
