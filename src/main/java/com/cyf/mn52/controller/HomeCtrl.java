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

import java.util.*;

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
        String sql = "select t.* " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql);
        map.put("category", list);
        sql = "select t.* " +
                "from mn_tag t " +
                "where t.state=1 " +
                "and t.top=1";
        list = this.jdbc.queryForList(sql);
        map.put("tag", list);
        return R.success("布局页数据", map);
    }

    //轮播/栏目列表及每个栏目下4个最新图集/热门标签
    @RequestMapping("/getIndexData")
    @ResponseBody
    public Result getIndexData() {
        Map map = new HashMap();
        String sql = "select t.id,t.name " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc,t.id";
        List<Map<String, Object>> categoryList = this.jdbc.queryForList(sql);
        for (Map category : categoryList) {
            sql = "select t2.id tag_id,t2.name tag_name,t3.*" +
                    "from mn_gallery_tag t1,mn_tag t2,(select t.id,t.title,t.cover,t.scan,t.good,t.time_publish from mn_gallery t where t.category_id=? order by t.time_publish desc limit 0,4) t3 " +
                    "where t1.tag_id=t2.id " +
                    "and t1.gallery_id=t3.id " +
                    "order by t3.time_publish desc";
            List<Map<String, Object>> repeatGalleryList = this.jdbc.queryForList(sql, category.get("id").toString());
            List<Map<String, Object>> galleryList = new ArrayList<>();
            Set<String> gallerySet = new HashSet();
            for (Map all : repeatGalleryList) {
                gallerySet.add(all.get("id").toString());
            }
            List<String> galleryIdList = new ArrayList<>(gallerySet);
            for (String id : galleryIdList) {
                Map gallery = new HashMap();
                List<Map<String, Object>> galleryTagList = new ArrayList<>();
                for (Map all : repeatGalleryList) {
                    if (all.get("id").toString().equals(id)) {
                        gallery = all;
                        Map tag = new HashMap();
                        tag.put("tag_id", all.get("tag_id"));
                        tag.put("tag_name", all.get("tag_name"));
                        galleryTagList.add(tag);
                    }
                }
                gallery.remove("tag_id");
                gallery.remove("tag_name");
                gallery.put("tag", galleryTagList);
                galleryList.add(gallery);
            }
            category.put("gallery", galleryList);
        }
        map.put("category", categoryList);
        sql = "select t.id,t.name from mn_tag t where t.state=1 order by t.time_update desc limit 0,100";
        List<Map<String, Object>> tagList = this.jdbc.queryForList(sql);
        map.put("tag", tagList);
        sql = "select t.id,t.title,t.cover from mn_gallery t where t.state=1 and t.carousel=1 order by t.time_publish desc";
        List<Map<String, Object>> carouselList = this.jdbc.queryForList(sql);
        map.put("carousel", carouselList);
        return R.success("首页数据", map);
    }
    //endregion
}
