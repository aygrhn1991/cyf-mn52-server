package com.cyf.mn52.controller;

import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import com.cyf.mn52.suit.util.UtilPage;
import com.cyf.mn52.suit.util.UtilPageOfJava;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
    @ResponseBody
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    //region 页面
    @RequestMapping(value = {"/index"})
    public String index(Model model) {
        //轮播
        String sql = "select t.id,t.title,t.cover " +
                "from mn_gallery t " +
                "where t.state=1 " +
                "and t.carousel=1 " +
                "order by t.time_publish desc";
        List<Map<String, Object>> carouselList = this.jdbc.queryForList(sql);
        model.addAttribute("carousel", carouselList);
        //分类/每个分类下4个图集
        sql = "select t.id,t.name " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc,t.id";
        List<Map<String, Object>> categoryList = this.jdbc.queryForList(sql);
        for (Map category : categoryList) {
            sql = "select t.id,t.title,t.cover,t.scan,t.good,(select count(*) from mn_image tt2 where tt2.state=1 and tt2.gallery_id=t.id) img,t2.id tag_id,t2.name tag_name " +
                    "from (select * from mn_gallery tt1 where tt1.state=1 and tt1.category_id=? order by tt1.time_publish desc limit 0,4) t " +
                    "left join mn_gallery_tag t1 on t.id=t1.gallery_id " +
                    "left join mn_tag t2 on t1.tag_id=t2.id and t2.state=1";
            List<Map<String, Object>> repeatGalleryList = this.jdbc.queryForList(sql, category.get("id").toString());
            List<Map<String, Object>> galleryList = new ArrayList<>();
            Set<String> gallerySet = new HashSet();
            for (Map all : repeatGalleryList) {
                gallerySet.add(all.get("id").toString());
            }
            List<String> galleryIdList = new ArrayList<>(gallerySet);
            for (String galleryId : galleryIdList) {
                Map gallery = new HashMap();
                List<Map<String, Object>> galleryTagList = new ArrayList<>();
                for (Map all : repeatGalleryList) {
                    if (all.get("id").toString().equals(galleryId)) {
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
        sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "order by t.time_update desc limit 0,100";
        List<Map<String, Object>> tagList = this.jdbc.queryForList(sql);
        model.addAttribute("tag", tagList);
        //布局内容
        this.concatLayoutData(model);
        return "home/index";
    }

    @RequestMapping(value = {"/category/{id}/{page}", "/category/{id}"})
    public String category(@PathVariable int id, @PathVariable(required = false) Integer page, Model model) {
        page = page == null ? 1 : page;
        int limit = 20;
        int navigatePages = 10;
        //分类
        String sql = "select * " +
                "from mn_category t " +
                "where t.state=1 " +
                "and t.id=?";
        List<Map<String, Object>> category = this.jdbc.queryForList(sql, id);
        model.addAttribute("category", category.get(0));
        //图集
        sql = "select t.id,t.title,t.cover,t.scan,t.good,(select count(*) from mn_image tt2 where tt2.state=1 and tt2.gallery_id=t.id) img,t2.id tag_id,t2.name tag_name " +
                "from (select * from mn_gallery tt1 where tt1.state=1 and tt1.category_id=? order by tt1.time_publish desc limit " + UtilPage.getPage(page, limit) + ") t " +
                "left join mn_gallery_tag t1 on t.id=t1.gallery_id " +
                "left join mn_tag t2 on t1.tag_id=t2.id and t2.state=1";
        List<Map<String, Object>> repeatGalleryList = this.jdbc.queryForList(sql, id);
        List<Map<String, Object>> galleryList = new ArrayList<>();
        Set<String> gallerySet = new HashSet();
        for (Map all : repeatGalleryList) {
            gallerySet.add(all.get("id").toString());
        }
        List<String> galleryIdList = new ArrayList<>(gallerySet);
        for (String galleryId : galleryIdList) {
            Map gallery = new HashMap();
            List<Map<String, Object>> galleryTagList = new ArrayList<>();
            for (Map all : repeatGalleryList) {
                if (all.get("id").toString().equals(galleryId)) {
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
        model.addAttribute("gallery", galleryList);
        //分页
        sql = "select count(*) " +
                "from mn_gallery t " +
                "where t.state=1 and t.category_id=?";
        int total = this.jdbc.queryForObject(sql, Integer.class, id);
        model.addAttribute("page", new UtilPageOfJava(page, limit, total, navigatePages));
        //布局内容
        this.concatLayoutData(model);
        return "home/category";
    }

    @RequestMapping("/gallery/{id}")
    public String gallery(@PathVariable int id, Model model) {
        String sql = "select * " +
                "from mn_image t " +
                "where t.state=1 " +
                "and t.gallery_id=?";
        List<Map<String, Object>> image = this.jdbc.queryForList(sql, id);
        model.addAttribute("image", image);
        //布局内容
        this.concatLayoutData(model);
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


    private void concatLayoutData(Model model) {
        String sql = "select t.id,t.name " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql);
        model.addAttribute("topCategory", list);
        sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "and t.top=1 " +
                "order by t.time_update desc";
        list = this.jdbc.queryForList(sql);
        model.addAttribute("topTag", list);
        model.addAttribute("ossUrl", this.ossUrl);
        model.addAttribute("date", new Date());
    }
}
