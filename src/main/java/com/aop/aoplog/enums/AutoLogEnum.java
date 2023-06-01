package com.aop.aoplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangshuan
 * @version 1.0
 * @date 2023/6/1 14:06
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
