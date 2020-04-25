package cn.henry.study.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description: 测试类
 *
 * @author Hlingoes
 * @date 2020/4/8 17:30
 */
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping(value = "hello1")
    public String hello1() {
        return "hello world";
    }

    @GetMapping(value = "hello2")
    public String hello2() {
        return "hello world";
    }
}
