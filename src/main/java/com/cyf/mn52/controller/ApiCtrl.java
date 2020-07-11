package com.cyf.mn52.controller;

import com.cyf.mn52.model.*;
import com.cyf.mn52.suit.request.Search;
import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import com.cyf.mn52.suit.util.UtilPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ApiCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @RequestMapping("/test/{message}")
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    //region 图集管理
    //后台-管理员查看图集列表(带tag)
    @RequestMapping("/getThumb")
    @ResponseBody
    public Result getThumb(@RequestBody Search model) {
        String sql1 = "select t1.*,group_concat(t.tag) tags from mn_thumb_tag_id t right join mn_thumbs t1 on t.thumb_id=t1.id where 1=1";
        String sql2 = "select count(*) from mn_thumbs t where 1=1";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            sql1 += " and t1.title like '%" + model.string1 + "%' ";
            sql2 += " and t.title like '%" + model.string1 + "%' ";
        }
        if (model.number1 != -1) {
            sql1 += " and t1.cat_id=" + model.number1;
            sql2 += " and t.cat_id=" + model.number1;
        }
        sql1 += " group by t1.id order by t1.created_at desc limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("图集列表", count, list);
    }

    //后台-管理员查看图集内容
    @RequestMapping("/getThumbGallery/{id}")
    @ResponseBody
    public Result getThumbGallery(@PathVariable String id) {
        String sql = "select t.* from mn_gallery t where t.thumb_unique_id=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, id);
        return R.success("图集内容列表", list);
    }

    //后台-管理员添加图集
    @RequestMapping("/addThumb")
    @ResponseBody
    public Result addThumb(@RequestBody Thumb model, @RequestParam("file") MultipartFile[] files) {
//        String sql = "select count(*) from t_admin t where t.userid=?";
//        int count = this.jdbc.queryForObject(sql, Integer.class, model.userid);
//        if (count >= 1) {
//            return R.error("该员工已授权过管理员权限");
//        }
//        sql = "insert into t_admin(userid,password,state,systime) values(?,'123456',?,now())";
//        count = this.jdbc.update(sql, model.userid, UserStateEnum.active.ordinal());
//        sql = "delete from t_admin_page_admin where userid=?";
//        count = this.jdbc.update(sql, model.userid);
//        for (int id : model.adminIds) {
//            sql = "insert into t_admin_page_admin(userid,page_id) values(?,?)";
//            count = this.jdbc.update(sql, model.userid, id);
//        }
//        sql = "delete from t_admin_page_app where userid=?";
//        count = this.jdbc.update(sql, model.userid);
//        for (int id : model.appIds) {
//            sql = "insert into t_admin_page_app(userid,page_id) values(?,?)";
//            count = this.jdbc.update(sql, model.userid, id);
//        }
        return R.success("管理员权限授权成功");
    }
    //endregion

    //region 类别管理
    //后台-管理员查看类别列表
    @RequestMapping("/getCategory")
    @ResponseBody
    public Result getCategory(@RequestBody Search model) {
        String sql1 = "select t.* from mn_category t where t.status<>-2 order by t.id desc limit " + UtilPage.getPage(model);
        String sql2 = "select count(*) from mn_category t where t.status<>-2";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("类别列表", count, list);
    }

    //后台-管理员添加类别
    @RequestMapping("/addCategory")
    @ResponseBody
    public Result addCategory(@RequestBody Category model) {
        String sql = "select count(*) from mn_category t where t.status<>-2 and t.cat_name=?";
        int count = this.jdbc.queryForObject(sql, Integer.class, model.cat_name);
        if (count >= 1) {
            return R.error("该类别已存在");
        }
        sql = "insert into mn_category(old_id,cat_url,parent_cat_url,cat_name,parent_name,seotitle,keywords,description,status,order_sn,thumb_ids,created_at,updated_at) values(?,?,?,?,?,?,?,?,?,?,?,now(),now())";
        count = this.jdbc.update(sql, 0, model.cat_url, "", model.cat_name, "", model.seotitle, model.keywords, model.description, 1, 0, "");
        return R.success("类别添加成功");
    }

    //后台-管理员更新类别
    @RequestMapping("/updateCategory")
    @ResponseBody
    public Result updateCategory(@RequestBody Category model) {
        String sql = "update mn_category t set t.cat_url=?,t.cat_name=?,t.seotitle=?,t.keywords=?,t.description=? where t.id=?";
        int count = this.jdbc.update(sql, model.cat_url, model.cat_name, model.seotitle, model.keywords, model.description, model.id);
        return R.success("类别更新成功");
    }

    //后台-管理员删除类别
    @RequestMapping("/updateCategoryState/{id}/{state}")
    @ResponseBody
    public Result updateCategoryState(@PathVariable int id, @PathVariable int state) {
        String sql = "update mn_category t set t.status=? where t.id=?";
        int count = this.jdbc.update(sql, state, id);
        return R.success("类别状态更新成功");
    }
    //endregion

    //region 标签管理
    //后台-管理员查看标签列表
    @RequestMapping("/getTag")
    @ResponseBody
    public Result getTag(@RequestBody Search model) {
        String sql1 = "select t1.*,count(*) thumb_count from mn_thumb_tag_id t right join mn_tags t1 on t.tag_id=t1.id where t1.status<>-2";
        String sql2 = "select count(*) from mn_tags t where t.status<>-2";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            sql1 += " and t1.tag like '%" + model.string1 + "%' ";
            sql2 += " and t.tag like '%" + model.string1 + "%' ";
        }
        sql1 += " group by t1.id order by t1.id desc limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("标签列表", count, list);
    }

    //后台-管理员添加标签
    @RequestMapping("/addTag")
    @ResponseBody
    public Result addTag(@RequestBody Tag model) {
        String sql = "select count(*) from mn_tags t where t.status<>-2 and t.tag=?";
        int count = this.jdbc.queryForObject(sql, Integer.class, model.tag);
        if (count >= 1) {
            return R.error("该标签已存在");
        }
        sql = "insert into mn_tags(unique_id,tag,order_sn,parent_id,seotitle,keywords,description,click,status,tpath,writer,editor,created_at,updated_at) values(?,?,?,?,?,?,?,?,?,?,?,?,now(),now())";
        count = this.jdbc.update(sql, UUID.randomUUID().toString(), model.tag, 0, 0, model.seotitle, model.keywords, model.description, 0, 1, "", "", "");
        return R.success("标签添加成功");
    }

    //后台-管理员更新标签
    @RequestMapping("/updateTag")
    @ResponseBody
    public Result updateTag(@RequestBody Tag model) {
        String sql = "update mn_tags t set t.tag=?,t.seotitle=?,t.keywords=?,t.description=? where t.id=?";
        int count = this.jdbc.update(sql, model.tag, model.seotitle, model.keywords, model.description, model.id);
        return R.success("标签更新成功");
    }

    //后台-管理员删除标签
    @RequestMapping("/updateTagState/{id}/{state}")
    @ResponseBody
    public Result updateTagState(@PathVariable int id, @PathVariable int state) {
        String sql = "update mn_tags t set t.status=? where t.id=?";
        int count = this.jdbc.update(sql, state, id);
        return R.success("标签状态更新成功");
    }
    //endregion

    //region 广告管理
    //后台-管理员查看广告列表
    @RequestMapping("/getAd")
    @ResponseBody
    public Result getAd(@RequestBody Search model) {
        String sql1 = "select t.* from mn_ads t where t.status<>-2";
        String sql2 = "select count(*) from mn_ads t where t.status<>-2";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            String and = " and t.code like '%" + model.string1 + "%' ";
            sql1 += and;
            sql2 += and;
        }
        sql1 += " order by t.id desc limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("广告列表", count, list);
    }

    //后台-管理员添加广告
    @RequestMapping("/addAd")
    @ResponseBody
    public Result addAd(@RequestBody Ad model) {
        String sql = "select count(*) from mn_ads t where t.status<>-2 and t.code=?";
        int count = this.jdbc.queryForObject(sql, Integer.class, model.code);
        if (count >= 1) {
            return R.error("该广告已存在");
        }
        sql = "insert into mn_ads(unique_id,status,position,code,position_desc,writer,editor,created_at,updated_at,ip) values(?,?,?,?,?,?,?,now(),now(),?)";
        count = this.jdbc.update(sql, UUID.randomUUID().toString(), 1, "", model.code, model.position_desc, "", "", "");
        return R.success("广告添加成功");
    }

    //后台-管理员更新广告
    @RequestMapping("/updateAd")
    @ResponseBody
    public Result updateAd(@RequestBody Ad model) {
        String sql = "update mn_ads t set t.code=?,t.position_desc=? where t.id=?";
        int count = this.jdbc.update(sql, model.code, model.position_desc, model.id);
        return R.success("广告更新成功");
    }

    //后台-管理员删除广告
    @RequestMapping("/updateAdState/{id}/{state}")
    @ResponseBody
    public Result updateAdState(@PathVariable int id, @PathVariable int state) {
        String sql = "update mn_ads t set t.status=? where t.id=?";
        int count = this.jdbc.update(sql, state, id);
        return R.success("广告状态更新成功");
    }
    //endregion
}
