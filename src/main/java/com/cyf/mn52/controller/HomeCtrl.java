package com.cyf.mn52.controller;

import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping(value = {"/home"})
public class HomeCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @Value("${oss.url}")
    private String ossUrl;

    @RequestMapping("/test/{message}")
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    //region 页面
    @RequestMapping(value = {"/index"})
    public String index(Model model) {
        //轮播
        String sql = "select t.id,t.title,t.cover from mn_gallery t where t.state=1 and t.carousel=1 order by t.time_publish desc";
        List<Map<String, Object>> carouselList = this.jdbc.queryForList(sql);
        model.addAttribute("carousel", carouselList);
        //分类/每个分类下4个图集
        sql = "select t.id,t.name " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc,t.id";
        List<Map<String, Object>> categoryList = this.jdbc.queryForList(sql);
        for (Map category : categoryList) {
            sql = "select t1.id,t1.title,t1.cover,t1.scan,t1.good,(select count(*) from mn_image tt2 where tt2.gallery_unique_id=t1.unique_id) img,t3.id tag_id,t3.name tag_name " +
                    "from (select * from mn_gallery tt1 where tt1.category_id=? order by tt1.time_publish desc limit 0,4) t1 " +
                    "left join mn_gallery_tag t2 on t1.id=t2.gallery_id " +
                    "left join mn_tag t3 on t2.tag_id=t3.id";
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
        model.addAttribute("category", categoryList);
        //最新标签
        sql = "select t.id,t.name from mn_tag t where t.state=1 order by t.time_update desc limit 0,100";
        List<Map<String, Object>> tagList = this.jdbc.queryForList(sql);
        model.addAttribute("tag", tagList);
        //布局内容
        Map layout = this.getLayoutData();
        model.addAttribute("topCategory", layout.get("topCategory"));
        model.addAttribute("topTag", layout.get("topTag"));
        model.addAttribute("ossUrl", this.ossUrl);
        model.addAttribute("date", new Date());
        return "home/index";
    }

    @RequestMapping("/category/{id}")
    public String category(@PathVariable int id) {
        return "home/category";
    }

    @RequestMapping("/gallery/{id}")
    public String gallery(@PathVariable int id) {
        return "home/gallery";
    }

    @RequestMapping("/tag/{id}")
    public String tag(@PathVariable int id) {
        return "home/tag";
    }

    @RequestMapping("/search")
    public String search(@RequestParam int id, @RequestParam String key) {
        return "home/search";
    }
    //endregion


    private Map getLayoutData() {
        Map map = new HashMap();
        String sql = "select t.id,t.name " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql);
        map.put("topCategory", list);
        sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "and t.top=1 " +
                "order by t.time_update desc";
        list = this.jdbc.queryForList(sql);
        map.put("topTag", list);
        return map;
    }
}
