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
        String sql = "update mn_category t set=t.cat_url=?,t.cat_name=?,t.seotitle=?,t.keywords=?,t.description=? where t.id=?";
        int count = this.jdbc.update(sql, model.cat_url, model.cat_name, model.seotitle, model.keywords, model.description, model.id);
        return R.success("类别更新成功");
    }

    //后台-管理员删除类别
    @RequestMapping("/updateCategoryState")
    @ResponseBody
    public Result updateCategoryState(int id, int state) {
        String sql = "update mn_category t set=t.status=? where t.id=?";
        int count = this.jdbc.update(sql, state, id);
        return R.success("类别状态更新成功");
    }
    //endregion
}
