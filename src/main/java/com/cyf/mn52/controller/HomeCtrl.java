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
import org.thymeleaf.util.StringUtils;

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
            category.put("gallery", this.makeGallery(repeatGalleryList));
        }
        model.addAttribute("category", categoryList);
        //布局内容
        this.concatLayoutData(model);
        return "home/index";
    }

    @RequestMapping(value = {"/category/{id}/{page}", "/category/{id}"})
    public String category(@PathVariable int id, @PathVariable(required = false) Integer page, Model model) {
        page = page == null ? 1 : page;
        int limit = 20;
        int navigatePages = 10;
        //分类（页面路径）
        String sql = "select t.id,t.name " +
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
        model.addAttribute("gallery", this.makeGallery(repeatGalleryList));
        //分页
        sql = "select count(*) " +
                "from mn_gallery t " +
                "where t.state=1 " +
                "and t.category_id=?";
        int total = this.jdbc.queryForObject(sql, Integer.class, id);
        model.addAttribute("page", new UtilPageOfJava(page, limit, total, navigatePages));
        //布局内容
        this.concatLayoutData(model);
        return "home/category";
    }

    @RequestMapping("/gallery/{id}")
    public String gallery(@PathVariable int id, Model model) {
        //图集信息（页面路径）
        String sql = "select t.id,t.title,t.category_id,t1.name category_name " +
                "from mn_gallery t " +
                "left join mn_category t1 on t.category_id=t1.id " +
                "where t.id=?";
        List<Map<String, Object>> gallery = this.jdbc.queryForList(sql, id);
        model.addAttribute("gallery", gallery.get(0));
        //图片
        sql = "select t.id,t.path " +
                "from mn_image t " +
                "where t.state=1 " +
                "and t.gallery_id=? " +
                "order by t.id desc";
        List<Map<String, Object>> image = this.jdbc.queryForList(sql, id);
        model.addAttribute("image", image);
        //标签
        sql = "select t1.id,t1.name " +
                "from mn_gallery_tag t " +
                "left join mn_tag t1 on t.tag_id=t1.id " +
                "where t.gallery_id=? " +
                "order by t1.time_update";
        List<Map<String, Object>> tag = this.jdbc.queryForList(sql, id);
        model.addAttribute("tag", tag);
        //前后图集
        sql = "select t.id,t.cover " +
                "from mn_gallery t " +
                "where t.state=1 " +
                "and t.category_id=? " +
                "and t.id<? " +
                "order by t.id desc limit 0,1";
        List<Map<String, Object>> perGallery = this.jdbc.queryForList(sql, gallery.get(0).get("category_id"), id);
        if (perGallery.size() == 0) {
            sql = "select t.id,t.cover " +
                    "from mn_gallery t " +
                    "where t.state=1 " +
                    "order by t.id limit 0,1";
            perGallery = this.jdbc.queryForList(sql);
        }
        model.addAttribute("perGallery", perGallery.get(0));
        sql = "select t.id,t.cover " +
                "from mn_gallery t " +
                "where t.state=1 " +
                "and t.category_id=? " +
                "and t.id>? " +
                "order by t.id limit 0,1";
        List<Map<String, Object>> nextGallery = this.jdbc.queryForList(sql, gallery.get(0).get("category_id"), id);
        if (nextGallery.size() == 0) {
            sql = "select t.id,t.cover " +
                    "from mn_gallery t " +
                    "where t.state=1 " +
                    "order by t.id desc limit 0,1";
            nextGallery = this.jdbc.queryForList(sql);
        }
        model.addAttribute("nextGallery", nextGallery.get(0));
        //相关图集
        List<String> tagIds = new ArrayList<>();
        for (Map t : tag) {
            tagIds.add(t.get("id").toString());
        }
        String inTag = StringUtils.join(tagIds, ",");
        sql = "select distinct t.id,t.title,t.cover,t.scan,t.good,t.time_publish,(select count(*) from mn_image tt2 where tt2.state=1 and tt2.gallery_id=t.id) img,t2.id tag_id,t2.name tag_name " +
                "from (select tt12.* from mn_gallery_tag tt1 left join mn_tag tt11 on tt1.tag_id=tt11.id left join mn_gallery tt12 on tt1.gallery_id=tt12.id where tt12.state=1 and tt12.category_id=? and tt1.tag_id in (" + inTag + ") order by tt12.time_publish limit 0,8) t " +
                "left join mn_gallery_tag t1 on t.id=t1.gallery_id " +
                "left join mn_tag t2 on t1.tag_id=t2.id and t2.state=1";
        List<Map<String, Object>> repeatGalleryList = this.jdbc.queryForList(sql, gallery.get(0).get("category_id").toString());
        model.addAttribute("relativeGallery", this.makeGallery(repeatGalleryList));
        //热门图集
        sql = "select t.id,t.title,t.cover,t.scan,t.good,(select count(*) from mn_image tt2 where tt2.state=1 and tt2.gallery_id=t.id) img,t2.id tag_id,t2.name tag_name " +
                "from (select * from mn_gallery tt1 where tt1.state=1 and tt1.category_id=? order by tt1.scan desc limit 0,8) t " +
                "left join mn_gallery_tag t1 on t.id=t1.gallery_id " +
                "left join mn_tag t2 on t1.tag_id=t2.id and t2.state=1";
        repeatGalleryList = this.jdbc.queryForList(sql, gallery.get(0).get("category_id").toString());
        model.addAttribute("scanGallery", this.makeGallery(repeatGalleryList));
        //最新图集
        sql = "select t.id,t.title,t.cover,t.scan,t.good,(select count(*) from mn_image tt2 where tt2.state=1 and tt2.gallery_id=t.id) img,t2.id tag_id,t2.name tag_name " +
                "from (select * from mn_gallery tt1 where tt1.state=1 and tt1.category_id=? order by tt1.time_publish desc limit 0,8) t " +
                "left join mn_gallery_tag t1 on t.id=t1.gallery_id " +
                "left join mn_tag t2 on t1.tag_id=t2.id and t2.state=1";
        repeatGalleryList = this.jdbc.queryForList(sql, gallery.get(0).get("category_id").toString());
        model.addAttribute("newGallery", this.makeGallery(repeatGalleryList));
        //布局内容
        this.concatLayoutData(model);
        //浏览量+1
        try {
            sql = "update mn_gallery t set t.scan=t.scan+1 where t.id=?";
            int count = this.jdbc.update(sql, id);
        } catch (Exception e) {

        }
        return "home/gallery";
    }

    @RequestMapping("/tags")
    public String tags(Model model) {
        //随机标签
        String sql = "select t.id,t.name " +
                "from mn_tag t " +
                "order by rand() limit 0,100";
        List<Map<String, Object>> randomTag = this.jdbc.queryForList(sql);
        model.addAttribute("randomTag", randomTag);
        //热门标签
        sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "order by t.scan desc limit 0,100";
        List<Map<String, Object>> scanTag = this.jdbc.queryForList(sql);
        model.addAttribute("scanTag", scanTag);
        //布局内容
        this.concatLayoutData(model);
        return "home/tags";
    }

    @RequestMapping(value = {"/tag/{id}/{page}", "/tag/{id}"})
    public String tag(@PathVariable int id, @PathVariable(required = false) Integer page, Model model) {
        page = page == null ? 1 : page;
        int limit = 20;
        int navigatePages = 10;
        //标签（页面路径）
        String sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "and t.id=?";
        List<Map<String, Object>> tag = this.jdbc.queryForList(sql, id);
        model.addAttribute("tag", tag.get(0));
        //图集
        sql = "select t.id,t.title,t.cover,t.scan,t.good,(select count(*) from mn_image tt2 where tt2.state=1 and tt2.gallery_id=t.id) img,t2.id tag_id,t2.name tag_name " +
                "from (select tt11.* from mn_gallery_tag tt1 left join mn_gallery tt11 on tt1.gallery_id=tt11.id where tt1.tag_id=? and tt11.id is not null order by tt1.gallery_id desc limit " + UtilPage.getPage(page, limit) + ") t " +
                "left join mn_gallery_tag t1 on t.id=t1.gallery_id " +
                "left join mn_tag t2 on t1.tag_id=t2.id and t2.state=1";
        List<Map<String, Object>> repeatGalleryList = this.jdbc.queryForList(sql, id);
        model.addAttribute("gallery", this.makeGallery(repeatGalleryList));
        //分页
        sql = "select count(*) " +
                "from mn_gallery_tag t left join mn_gallery t1 on t.gallery_id=t1.id " +
                "where t1.state=1 " +
                "and t.tag_id=? " +
                "and t1.id is not null";
        int total = this.jdbc.queryForObject(sql, Integer.class, id);
        model.addAttribute("page", new UtilPageOfJava(page, limit, total, navigatePages));
        //布局内容
        this.concatLayoutData(model);
        return "home/tag";
    }

    @RequestMapping("/search")
    public String search(@RequestParam int id, @RequestParam String key) {
        return "home/search";
    }
    //endregion


    private void concatLayoutData(Model model) {
        //顶部header（包括底部footer）导航
        String sql = "select t.id,t.name " +
                "from mn_category t " +
                "where t.state=1 " +
                "order by t.sort desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql);
        model.addAttribute("layoutCategory", list);
        //顶部置顶标签
        sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "and t.top=1 " +
                "order by t.time_update desc";
        list = this.jdbc.queryForList(sql);
        model.addAttribute("layoutTopTag", list);
        //最新标签
        sql = "select t.id,t.name " +
                "from mn_tag t " +
                "where t.state=1 " +
                "order by t.time_update desc limit 0,100";
        list = this.jdbc.queryForList(sql);
        model.addAttribute("layoutTag", list);
        //全局变量
        model.addAttribute("ossUrl", this.ossUrl);
        model.addAttribute("date", new Date());
    }

    private List<Map<String, Object>> makeGallery(List<Map<String, Object>> repeatGalleryList) {
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
        return galleryList;
    }

    @RequestMapping("/good/{id}")
    @ResponseBody
    public Result good(@PathVariable String id) {
        String sql = "update mn_gallery t set t.good=t.good+1 where t.id=?";
        int count = this.jdbc.update(sql, id);
        return R.success("已点赞");
    }

}
