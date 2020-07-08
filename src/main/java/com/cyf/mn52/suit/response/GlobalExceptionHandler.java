package com.cyf.mn52.suit.response;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("异常拦截器", e);
        boolean isView = (Boolean) request.getAttribute("isView");
        if (isView) {
            response.sendRedirect("/error/error");
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            try (PrintWriter out = response.getWriter()) {
                Result r = R.exception("系统错误，请稍后再试", null);
                out.println(new Gson().toJson(r));
            }
        }
        return null;
    }

}
