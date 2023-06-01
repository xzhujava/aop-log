package com.aop.aoplog.aspect;


import com.aop.aoplog.annotation.AutoLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 日志切面类
 *
 * @author zhangshuan
 * @version 1.0
 * @date 2023/6/1 11:48
 */
@Component
@Aspect
public class AutoLogAspect {
    @Pointcut("@annotation(com.aop.aoplog.annotation.AutoLog)")
    public void logPointCut() {
        //切入点，AutoLog
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        System.out.println("收到请求.....");

        //获取日志类型及操作类型
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AutoLog autoLog = method.getAnnotation(AutoLog.class);

        System.out.println("[环绕通知]:【日志类型】:" + autoLog.logType().getType() +
                ", 【操作类型】：" + autoLog.operateType().getType() +
                ", 【日志内容】:" + autoLog.value());
        //执行方法
        Object result = point.proceed();

        System.out.println("执行完毕.....");

        System.out.println("[环绕通知]: 执行结果：" + result.toString());
        //执行时长(毫秒)
        long times = System.currentTimeMillis() - beginTime;

        System.out.println("执行用时:" + times + "ms");
        return result.toString().concat(" [这是切面中添加的内容]");
    }
}
