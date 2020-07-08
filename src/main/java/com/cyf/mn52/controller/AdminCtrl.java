package com.cyf.mn52.controller;

import com.cyf.mn52.model.Admin;
import com.cyf.mn52.model.em.UserStateEnum;
import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
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
        String sql = "select t.* from t_admin t where t.userid=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, model.userid);
        if (list.size() == 0) {
            return R.error("账号未授权");
        }
        if (!list.get(0).get("password").toString().equals(model.password)) {
            return R.error("密码错误");
        }
        if (Integer.parseInt(list.get(0).get("state").toString()) == UserStateEnum.disabled.ordinal()) {
            return R.error("管理员账号已禁用");
        }
        return R.success("登录成功", list.get(0));
    }

    @RequestMapping("/getAdminAndPage")
    @ResponseBody
    public Result getAdminAndPage() throws UnsupportedEncodingException {
        int userid = this.getAdminFromCookie().userid;
        Map map = new HashMap();
        String sql = "select t.*,t1.name user_name,t2.name department_name from t_admin t left join t_user t1 on t.userid=t1.id left join t_department t2 on t1.department_id=t2.id where t.userid=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, userid);
        map.put("admin", list.get(0));
        sql = "select t1.* from t_admin_page_admin t left join t_page_admin t1 on t.page_id=t1.id where t.userid=? order by t1.group_sort,t1.sort";
        list = this.jdbc.queryForList(sql, userid);
        map.put("page", list);
        return R.success("管理员信息与授权页面", map);
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

    @RequestMapping("/user/user")
    public String user() {
        return "admin/user/user";
    }

    @RequestMapping("/user/admin")
    public String admin() {
        return "admin/user/admin";
    }

    @RequestMapping("/addjob/addjobrecord")
    public String addjobrecord() {
        return "admin/addjob/addjobrecord";
    }

    @RequestMapping("/addjob/addjobstatistic")
    public String addjobstatistic() {
        return "admin/addjob/addjobstatistic";
    }

    @RequestMapping("/produce/patchplan")
    public String patchplan() {
        return "admin/produce/patchplan";
    }

    @RequestMapping("/produce/boardplan")
    public String boardplan() {
        return "admin/produce/boardplan";
    }
    //endregion

}
