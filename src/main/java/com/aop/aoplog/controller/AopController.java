package com.aop.aoplog.controller;

import com.aop.aoplog.annotation.AutoLog;
import com.aop.aoplog.enums.AutoLogEnum;
import com.aop.aoplog.enums.AutoLogOperateEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangshuan
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
