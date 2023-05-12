package com.qingyun.im.client.config;

import com.qingyun.im.client.annotation.LoginRequired;
import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.pojo.UserInfo;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description： 通过AOP的方式执行登录和连接检查
 * @author: 張青云
 * @create: 2022-12-06 18:44
 **/
@Component
@Aspect
public class LoginAndConnectAspect {

    @Autowired
    private ClientSession session;

    @Pointcut("execution(* com.qingyun.im.client.command.handle..*.process(..))")
    public void commandHandle() {
    }

    @Around("commandHandle()")
    public Object loginCheck(ProceedingJoinPoint pjp) throws Throwable {
        //  获取目标类
        Object target = pjp.getTarget();
        //  查看是否被注解
        Class<?> clazz = target.getClass();
        LoginRequired annotation = clazz.getAnnotation(LoginRequired.class);
        //  进行登录检查
        if (annotation != null) {
            UserInfo userInfo = session.getUserInfo();
            if (!session.isLogin() || userInfo == null) {
                //  不再继续执行目标方法
                throw new IMException(Exceptions.NO_LOGIN.getCode(), Exceptions.NO_LOGIN.getMessage());
            }
        }
        //  执行目标方法
        return pjp.proceed();
    }
}
