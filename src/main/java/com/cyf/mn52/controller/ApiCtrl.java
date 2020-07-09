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

    //region 图片管理
    //后台-管理员查看图片列表(带tag)
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
        return R.success("图片列表", count, list);
    }

    //后台-管理员查看子图片
    @RequestMapping("/getThumbGallery/{id}")
    @ResponseBody
    public Result getThumbGallery(@PathVariable String id) {
        String sql = "select t.* from mn_gallery t where t.thumb_unique_id=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, id);
        return R.success("子图片列表", list);
    }

    //后台-管理员添加图片
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
}
