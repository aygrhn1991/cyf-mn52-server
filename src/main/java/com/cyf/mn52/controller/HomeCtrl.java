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

import java.util.ArrayList;
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

    //region 接口
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

    //栏目列表及每个栏目下4个最新图集
    @RequestMapping("/getIndexData")
    @ResponseBody
    public Result getIndexData() {
        Map map = new HashMap();
        String sql = "select t.* from mn_category t where t.state=1 order by t.sort desc,t.id";
        List<Map<String, Object>> categoryList = this.jdbc.queryForList(sql);
        for (Map m : categoryList) {
            sql = "select * from mn_gallery t where t.state=1 and t.category_id=? order by t.time_publish desc,t.id limit 0,4";
            List<Map<String, Object>> galleryList = this.jdbc.queryForList(sql, m.get("id").toString());
            String galleryIds = "";
            for (Map gallery : galleryList) {
                galleryIds += gallery.get("id").toString() + ",";
            }
            galleryIds = galleryIds.substring(0, galleryIds.length() - 1);
            sql = "select t.*,t1.* from mn_gallery_tag t left join mn_tag t1 on t.tag_id=t1.id where t.gallery_id in (" + galleryIds + ")";
            List<Map<String, Object>> allTag = this.jdbc.queryForList(sql);
            for (Map gallery : galleryList) {
                List<Map<String, Object>> galleryTagList = new ArrayList<>();
                for (Map tag : allTag) {
                    if (tag.get("gallery_id").toString().equals(gallery.get("id").toString())) {
                        galleryTagList.add(tag);
                    }
                }
                gallery.put("tag", galleryTagList);
            }
            m.put("gallery", galleryList);
        }
        map.put("category", categoryList);
        sql = "select t.* from mn_tag t where t.state=1 order by t.time_update desc limit 0,100";
        List<Map<String, Object>> tagList = this.jdbc.queryForList(sql);
        map.put("tag", tagList);
        return R.success("首页数据", map);
    }
    //endregion
}
