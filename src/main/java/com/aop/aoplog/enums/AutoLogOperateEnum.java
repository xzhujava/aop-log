package com.aop.aoplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangshuan
 * @version 1.0
 * @date 2023/6/1 14:14
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
