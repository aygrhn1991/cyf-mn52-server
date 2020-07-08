package com.cyf.mn52.suit.middleware;

import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AuthInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isView = (Boolean) request.getAttribute("isView");
        //登录检查
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("admin")) {
                    //已登录
                    return true;
                }
            }
        }
        //未登录
        if (isView) {
            response.sendRedirect("/admin/login");
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            try (PrintWriter out = response.getWriter()) {
                Result r = R.exception("登录信息失效，请重新登录", null);
                out.println(new Gson().toJson(r));
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
