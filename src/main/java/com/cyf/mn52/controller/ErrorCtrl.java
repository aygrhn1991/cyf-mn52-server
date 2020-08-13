package com.cyf.mn52.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/error")
    public String error() {
        return "error/error";
    }

    @RequestMapping("/unauthorized")
    public String unauthorized() {
        return "error/unauthorized";
    }

}
