# SpringBoot使用AOP实现简单的日志打印

> 自动记录日志的实现有两种方式：
> 
>1.通过监听器去监听，当访问到具体的类方法，通过aop切面去获取访问的方法，然后将日志记录下来
> 
>2.通过拦截器，编写一个类去继承HandlerInterceptorAdapter，重写preHandle，postHandle,然后在里面进行日志记录，编写的类加到spring容器里。

我这里采用第一种方式，基于AOP方式实现简单日志打印功能

### 前言(AOP是什么、为什么需要AOP)
#### AOP是什么
AOP（Aspect-OrientedProgramming，面向方面编程），可以说是OOP（Object-Oriented Programing，面向对象编程）的补充和完善。
本文提供Spring官方文档出处：Aspect Oriented Programming with Spring.

从官方文档上摘抄的解释就是：面向方面编程（AOP）是面向对象编程（OOP）补充的另一种提供思考程序结构补充。在OOP中模块化的关键单元是类，而在AOP模块的单位是一个方面。面对关注点，如事务管理跨越多个类型和对象切模块化。（这些关注经常被称为在AOP文学横切关注点。）

相关概念（只需做个大概的了解就好）----来自于官方文档直译
#### 为什么需要AOP
假如我们应用中有n个业务逻辑组件，每个业务逻辑组件又有m个方法，那现在我们的应用就一共包含了n*m个方法，我会抱怨方法太多...
现在，我有这样一个需求，每个方法都增加一个通用的功能，常见的如：事务处理，日志，权限控制...
最容易想到的方法，先定义一个额外的方法，实现该功能，然后再每个需要实现这个功能的地方去调用这个额外的方法。
这种做法的好处和坏处分别是：
* **好处**：可以动态地添加和删除在切面上的逻辑而不影响原来的执行代码
* **坏处**：一旦要修改，就要打开所有调用到的地方去修改

那我们用AOP的方式可以实现在不修改源方法代码的前提下，可以统一为原多个方法增加横切性质的“通用处理”

接下来 就基于AOP实现简单日志打印功能
### 定义一个注解
Annotation 注解的构成：

@interface 表示这是一个注解类， 不是interface，是注解类 定义注解用的

@Inherited 表示这个Annotation可以被继承

@Documented 表示这个Annotation可以被写入javadoc

@Target:表示注解的作用目标
```java
/**
 * {@link java.lang.annotation.ElementType}
 */
//接口、类、枚举、注解
@Target(ElementType.TYPE)
//字段、枚举的常量
@Target(ElementType.FIELD)
//方法
@Target(ElementType.METHOD)
//方法参数
@Target(ElementType.PARAMETER)
//构造函数
@Target(ElementType.CONSTRUCTOR)
//局部变量
@Target(ElementType.LOCAL_VARIABLE)
//注解
@Target(ElementType.ANNOTATION_TYPE)
//包
@Target(ElementType.PACKAGE)
```
@Retention(RetentionPolicy.RUNTIME) 用来修饰注解，是注解的注解，称为元注解
```java
/**
 * {@link java.lang.annotation.RetentionPolicy}
 */
public enum RetentionPolicy {
    /**
     * Annotations are to be discarded by the compiler.
     * 编译器处理完Annotation后不存储在class中
     */
    SOURCE,

    /**
     * Annotations are to be recorded in the class file by the compiler
     * but need not be retained by the VM at run time.  This is the default
     * behavior.
     * 编译器把Annotation存储在class中，这是默认值  
     */
    CLASS,

    /**
     * Annotations are to be recorded in the class file by the compiler and
     * retained by the VM at run time, so they may be read reflectively.
     * 
     * @see java.lang.reflect.AnnotatedElement
     * 编译器把Annotation存储在class中，可以由虚拟机读取,反射需要
     */
    RUNTIME
}
```
了解了Java中一个注解类的构成，下面我们定一个AutoLog的注解：
```java
package com.aop.aoplog.annotation;

import com.aop.aoplog.enums.AutoLogEnum;
import com.aop.aoplog.enums.AutoLogOperateEnum;

import java.lang.annotation.*;

/**
 * 自定义注解类
 * @author 等花开
 * @version 1.0
 * @date 2023/6/1
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLog {

    /**
     * 日志内容
     *
     * @return 日志内容
     */
    String value() default "";

    /**
     * 日志类型
     *
     * @return 1.访问日志 2.操作日志 3.异常日志
     */
    AutoLogEnum logType() default AutoLogEnum.LOG_TYPE_1;

    /**
     * 操作日志类型
     *
     * @return 1查询，2添加，3修改，4删除
     */
    AutoLogOperateEnum operateType() default AutoLogOperateEnum.OPERATE_TYPE_1;

}
```
AutoLogEnum枚举类：
```java
package com.aop.aoplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型
 * 
 * @author 等花开
 * @version 1.0
 * @date 2023/6/1
 */
@Getter
@AllArgsConstructor
public enum AutoLogEnum {
    /**
     * 操作日志
     */
    LOG_TYPE_1(1,"操作日志"),
    /**
     * 访问日志
     */
    LOG_TYPE_2(2,"访问日志"),

    /**
     * 异常日志
     */
    LOG_TYPE_3(3,"异常日志");


    final Integer value;

    final String type;
}
```
AutoLogOperateEnum枚举类：
```java
package com.aop.aoplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 等花开
 * @version 1.0
 * @date 2023/6/1
 */
@Getter
@AllArgsConstructor
public enum AutoLogOperateEnum {

    /**
     * 查询
     */
    OPERATE_TYPE_1(1, "查询"),
    /**
     * 添加
     */
    OPERATE_TYPE_2(2, "添加"),
    /**
     * 更新
     */
    OPERATE_TYPE_3(3, "更新"),
    /**
     * 删除
     */
    OPERATE_TYPE_4(4, "删除");

    final Integer value;

    final String type;
}
```
### 定义切面(Aspect)
@Aspect 表示这是一个切面

@Component 告诉Spring这是一个bean

@annotation 获取定义的注解

@Pointcut 切点

@Pointcut("@annotation(xx.AutoLog)") 表示使用了AutoLog注解的，就是切入点

@Around的作用：既可以在目标方法之前织入增强动作，也可以在执行目标方法之后织入增强动作；可以决定目标方法在什么时候执行，如何执行，甚至可以完全阻止目标目标方法的执行；
可以改变执行目标方法的参数值，也可以改变执行目标方法之后的返回值； 当需要改变目标方法的返回值时，只能使用Around方法；

**ProceedingJoinPoint** 环绕通知，主要作用找到程序执行中的可识别的点，当aop的切入点

1.环绕通知 ProceedingJoinPoint 执行proceed方法的作用是让目标方法执行，这也是环绕通知和前置、后置通知方法的一个最大区别。

2.简单理解，环绕通知=前置+目标方法执行+后置通知，proceed方法就是用于启动目标方法执行的.
```java
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
 * @author 等花开
 * @version 1.0
 * @date 2023/6/1
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
        return result.toString().concat(" 这是切面中添加的内容");
    }
}
```
### 使用自定义注解
可以在controller或者实现类上进行注解的加入
```java
package com.aop.aoplog.controller;

import com.aop.aoplog.annotation.AutoLog;
import com.aop.aoplog.enums.AutoLogEnum;
import com.aop.aoplog.enums.AutoLogOperateEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 * 
 * @author 等花开
 * @version 1.0
 * @date 2023/6/1 11:24
 */
@RestController
@RequestMapping("/aop")
public class AopController {

    @AutoLog(value = "测试接口", logType = AutoLogEnum.LOG_TYPE_2, operateType = AutoLogOperateEnum.OPERATE_TYPE_1)
    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }
}
```
运行以上代码，并请求localhost:8080/aop/test接口可看到控制台打印出：
```text
收到请求.....
[环绕通知]:【日志类型】:访问日志, 【操作类型】：查询, 【日志内容】:测试接口
执行完毕.....
[环绕通知]: 执行结果：Hello World
执行用时:3ms
```
因为我在AutoLogAspect#around方法中修改了返回结果，所以最终执行返回结果为：
```text
Hello World 这是切面中添加的内容
```

以上就是使用AOP实现简易日志打印功能，在实际开发中可能需要将日志持久化或其他逻辑需求，只需在对应Aspect类中添加持久化等逻辑即可


