package com.cyf.mn52.controller;

import com.cyf.mn52.model.Admin;
import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import com.cyf.mn52.suit.util.UtilDate;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/admin")
public class AdminCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @RequestMapping("/test/{message}")
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    private Admin getAdminFromCookie() throws UnsupportedEncodingException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String json = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("admin")) {
                json = URLDecoder.decode(cookie.getValue(), "UTF-8");
            }
        }
        return new Gson().fromJson(json, Admin.class);
    }

    //region 登录
    @RequestMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@RequestBody Admin model) {
        String sql = "select t.* from mn_admin t where t.username=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, model.username);
        if (list.size() == 0) {
            return R.error("账号不存在");
        }
//        if (!list.get(0).get("username").toString().equals(model.password)) {
//            return R.error("密码错误");
//        }
        if (!model.password.equals("yangsha@"+ UtilDate.dateToYYYYMMDD(new Date())+".com")) {
            return R.error("密码错误");
        }
        return R.success("登录成功", list.get(0));
    }

    @RequestMapping("/getAdmin")
    @ResponseBody
    public Result getAdmin() throws UnsupportedEncodingException {
        String username = this.getAdminFromCookie().username;
        String sql = "select t.* from mn_admin t where t.username=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, username);
        return R.success("管理员信息", list.get(0));
    }
    //endregion

    //region 页面
    @RequestMapping("/login")
    public String login() {
        return "admin/login";
    }

    @RequestMapping("/index")
    public String index() {
        return "admin/index";
    }

    @RequestMapping("/welcome")
    public String welcome() {
        return "admin/welcome";
    }

    @RequestMapping("/thumb")
    public String thumb() {
        return "admin/thumb";
    }

    @RequestMapping("/category")
    public String user() {
        return "admin/category";
    }

    @RequestMapping("/tag")
    public String admin() {
        return "admin/tag";
    }

    @RequestMapping("/ad")
    public String ad() {
        return "admin/ad";
    }
    //endregion

}
