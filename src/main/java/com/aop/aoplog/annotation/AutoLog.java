package com.aop.aoplog.annotation;

import com.aop.aoplog.enums.AutoLogEnum;
import com.aop.aoplog.enums.AutoLogOperateEnum;

import java.lang.annotation.*;

/**
 * @author zhangshuan
 * @version 1.0
 * @date 2023/6/1 11:25
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
