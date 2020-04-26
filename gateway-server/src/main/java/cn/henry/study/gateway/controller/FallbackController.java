package cn.henry.study.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/17 0:44
 */

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public String fallback() {
        return "I'm Spring Cloud Gateway fallback.";
    }
}
