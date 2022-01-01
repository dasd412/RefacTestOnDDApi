package com.dasd412.remake.api.controller.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/loginForm")
    public String loginForm() {
        logger.info("loginForm view resolve");
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        logger.info("joinForm view resolve");
        return "joinForm";
    }

    @GetMapping("api/diary/diabetes-diary")
    public String diary() {
        logger.info("diary view resolve");
        return "diary";
    }

}
