package com.cyf.mn52.controller;

import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class HomeCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @RequestMapping("/test/{message}")
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    //region 页面
    @RequestMapping("/index")
    public String index() {
        return "home/index";
    }

    @RequestMapping("/category")
    public String category() {
        return "home/category";
    }

    @RequestMapping("/gallery")
    public String gallery() {
        return "home/gallery";
    }

    @RequestMapping("/tag")
    public String thumb() {
        return "home/tag";
    }
    //endregion

    //region 页面
    //导航(栏目)/置顶标签
    @RequestMapping("/getLayoutData")
    @ResponseBody
    public Result getLayoutData() {
        Map map = new HashMap();
        String sql = "select t.* from mn_category t where t.state=1 order by t.sort desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql);
        map.put("category", list);
        sql = "select t.* from mn_tag t where t.state=1 and t.top=1";
        list = this.jdbc.queryForList(sql);
        map.put("tag", list);
        return R.success("布局页数据", map);
    }
    //endregion
}
