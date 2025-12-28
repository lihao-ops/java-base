package com.learning.javabase.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 *
 * 用途：
 * 1. 验证 Spring Boot 环境是否启动成功。
 * 2. 验证日志配置是否生效。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/ping")
    public String ping() {
        log.info("收到健康检查请求|Health_check_request_received");
        return "pong";
    }
}
