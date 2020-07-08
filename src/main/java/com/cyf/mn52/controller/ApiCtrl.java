package com.cyf.mn52.controller;

import com.cyf.mn52.model.*;
import com.cyf.mn52.model.em.BoardPlanStepEnum;
import com.cyf.mn52.model.em.PatchPlanStepEnum;
import com.cyf.mn52.model.em.UserStateEnum;
import com.cyf.mn52.suit.request.Search;
import com.cyf.mn52.suit.response.R;
import com.cyf.mn52.suit.response.Result;
import com.cyf.mn52.suit.util.UtilPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

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

    //region 员工
    //后台-管理员查看员工列表
    @RequestMapping("/getUser")
    @ResponseBody
    public Result getUser(@RequestBody Search model) {
        String sql1 = "select t.*,t1.name department_name from t_user t left join t_department t1 on t.department_id=t1.id where 1=1";
        String sql2 = "select count(*) from t_user t where 1=1";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            String and = " and t.name like '%" + model.string1 + "%' ";
            sql1 += and;
            sql2 += and;
        }
        if (model.number1 != -1) {
            String and = " and t.department_id=" + model.number1;
            sql1 += and;
            sql2 += and;
        }
        if (model.number2 != -1) {
            String and = " and t.state=" + model.number2;
            sql1 += and;
            sql2 += and;
        }
        sql1 += " order by t.id limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("员工列表", count, list);
    }

    //后台-管理员拒绝员工的认证申请
    @RequestMapping("/deleteUser/{userid}")
    @ResponseBody
    public Result deleteUser(@PathVariable int userid) {
        String sql = "delete from t_user where id=?";
        int count = this.jdbc.update(sql, userid);
        return R.success("员工申请已拒绝");
    }

    //后台-管理员修改员工账号状态
    @RequestMapping("/updateUserState/{userid}/{state}")
    @ResponseBody
    public Result updateUserState(@PathVariable int userid, @PathVariable int state) {
        String sql = "update t_user t set t.state=? where t.id=?";
        int count = this.jdbc.update(sql, state, userid);
        return R.success("员工状态更新成功");
    }
    //endregion

    //region 管理员账号
    //后台-管理员查看管理员账号
    @RequestMapping("/getAdmin")
    @ResponseBody
    public Result getAdmin(@RequestBody Search model) {
        String sql1 = "select t.*,t1.name name,t2.name department_name from t_admin t left join t_user t1 on t.userid=t1.id left join t_department t2 on t1.department_id=t2.id where 1=1";
        String sql2 = "select count(*) from t_admin t left join t_user t1 on t.userid=t1.id where 1=1";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            String and = " and t1.name like '%" + model.string1 + "%' ";
            sql1 += and;
            sql2 += and;
        }
        if (model.number1 != -1) {
            String and = " and t1.department_id=" + model.number1;
            sql1 += and;
            sql2 += and;
        }
        if (model.number2 != -1) {
            String and = " and t.state=" + model.number2;
            sql1 += and;
            sql2 += and;
        }
        sql1 += " order by t.userid limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("管理员列表", count, list);
    }

//    //后台-管理员添加管理员账号
//    @RequestMapping("/addAdmin")
//    @ResponseBody
//    public Result addAdmin(@RequestBody Admin model) {
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
//        return R.success("管理员权限授权成功");
//    }

    //后台-管理员修改管理员授权页面，获取管理员已授权页面
    @RequestMapping("/getAdminPage/{userid}")
    @ResponseBody
    public Result getAdminPage(@PathVariable String userid) {
        Map map = new HashMap();
        String sql = "select t.* from t_admin_page_app t where t.userid=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, userid);
        map.put("app", list);
        sql = "select t.* from t_admin_page_admin t where t.userid=?";
        list = this.jdbc.queryForList(sql, userid);
        map.put("admin", list);
        return R.success("管理员已授权页面", map);
    }

//    //后台-管理员修改管理员授权页面
//    @RequestMapping("/updateAdminPage")
//    @ResponseBody
//    public Result updateAdminPage(@RequestBody Admin model) {
//        String sql = "delete from t_admin_page_admin where userid=?";
//        int count = this.jdbc.update(sql, model.userid);
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
//        return R.success("管理员授权页面更改成功");
//    }

    //后台-管理员删除管理员账号
    @RequestMapping("/deleteAdmin/{userid}")
    @ResponseBody
    public Result deleteAdmin(@PathVariable int userid) {
        String sql = "delete from t_admin where userid=?";
        int count = this.jdbc.update(sql, userid);
        sql = "delete from t_admin_page_app where userid=?";
        count = this.jdbc.update(sql, userid);
        sql = "delete from t_admin_page_admin where userid=?";
        count = this.jdbc.update(sql, userid);
        return R.success("管理员账号已删除");
    }

    //后台-管理员修改管理员账号状态
    @RequestMapping("/updateAdminState/{userid}/{state}")
    @ResponseBody
    public Result updateAdminState(@PathVariable int userid, @PathVariable int state) {
        String sql = "update t_admin t set t.state=? where t.userid=?";
        int count = this.jdbc.update(sql, state, userid);
        return R.success("管理员账号状态更新成功");
    }

    //后台-管理员重置/更新管理员登录密码
    @RequestMapping({"/updateAdminPassword/{type}/{userid}/{newpassword}", "/updateAdminPassword/{type}/{userid}/{newpassword}/{oldpassword}"})
    @ResponseBody
    public Result updateAdminPassword(@PathVariable int type, @PathVariable int userid, @PathVariable String newpassword, @PathVariable String oldpassword) {
        if (type == 1) {
            String sql = "update t_admin t set t.password=? where t.userid=?";
            int count = this.jdbc.update(sql, newpassword, userid);
            return R.success("管理员登录密码已重置");
        } else if (type == 2) {
            String sql = "select t.* from t_admin t where t.userid=?";
            List<Map<String, Object>> list = this.jdbc.queryForList(sql, userid);
            if (!list.get(0).get("password").toString().equals(oldpassword)) {
                return R.error("旧密码错误");
            }
            sql = "update t_admin t set t.password=? where t.userid=?";
            int count = this.jdbc.update(sql, newpassword, userid);
            return R.success("登录密码修改成功");
        } else {
            return R.error("访问无效");
        }
    }
    //endregion

    //region 加班
    //小程序-员工申报加班
    //后台-管理员添加加班
    @RequestMapping("/addJob")
    public Result addJob(@RequestBody AddJobRecord model) {
        String sql = "select count(*) from t_add_job_record t where t.userid=? and date_format(t.date,'%Y-%m-%d')=?";
        int count = this.jdbc.queryForObject(sql, Integer.class, model.userid, model.date);
        if (count >= 1) {
            return R.error("当天已申报加班");
        }
        sql = "insert into t_add_job_record(userid,`date`,meal,meal_time,bus,bus_time,bus_to,bus_to_station,systime) values(?,?,?,?,?,?,?,?,now())";
        count = this.jdbc.update(sql, model.userid, model.date, model.meal, model.meal_time, model.bus, model.bus_time, model.bus_to, model.bus_to_station);
        return R.success("申报加班成功");
    }

    //小程序-员工查看自己加班记录
    @RequestMapping("/getAddJobRecordOfUser/{userid}")
    public Result getAddJobRecordOfUser(@PathVariable String userid) {
        String sql = "select t.* from t_add_job_record t where t.userid=? order by t.date desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, userid);
        return R.success("加班申报记录", list);
    }

    //小程序-员工删除加班记录
    @RequestMapping("/deleteAddJobRecord/{id}")
    public Result deleteAddJobRecord(@PathVariable String id) {
        String sql = "delete from t_add_job_record where id=?";
        int count = this.jdbc.update(sql, id);
        return R.success("加班申报记录已删除");
    }

    //小程序-管理员按日查看加班记录(不分页)
    //后台-管理员查看加班记录，统计总加班人数、用餐、乘车等
    @RequestMapping("/getAddJobRecordOfDateWithoutPage/{date}")
    @ResponseBody
    public Result getAddJobRecordOfDateWithoutPage(@PathVariable String date) {
        String sql = "select t.*,t1.name user_name,t2.name department_name from t_add_job_record t left join t_user t1 on t.userid=t1.id left join t_department t2 on t1.department_id=t2.id where date_format(t.date,'%Y-%m-%d')=? order by t1.department_id,t.systime";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, date);
        return R.success("一天所有加班申报记录", list);
    }

    //后台-管理员按日查看加班记录(分页)
    @RequestMapping("/getAddJobRecordOfDate")
    @ResponseBody
    public Result getAddJobRecordOfDate(@RequestBody Search model) {
        String sql = "select t.*,t1.name user_name,t2.name department_name from t_add_job_record t left join t_user t1 on t.userid=t1.id left join t_department t2 on t1.department_id=t2.id where date_format(t.date,'%Y-%m-%d')=? order by t.systime limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, model.string1);
        sql = "select count(*) from t_add_job_record t where date_format(t.date,'%Y-%m-%d')=?";
        int count = this.jdbc.queryForObject(sql, Integer.class, model.string1);
        return R.success("加班申报记录(分页)", count, list);
    }

    //后台-管理员查看加班记录，日历标记有加班的日期
    @RequestMapping("/getAddJobRecordAllDate")
    @ResponseBody
    public Result getAddJobRecordAllDate() {
        String sql = "select t.date from t_add_job_record t group by t.date";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql);
        return R.success("所有有加班记录的日期", list);
    }

    //后台-管理员按日期范围查看整合加班记录(分页)
    @RequestMapping("/getAddJobRecordOfDateRange")
    @ResponseBody
    public Result getAddJobRecordOfDateRange(@RequestBody Search model) {
        String sql = "select t.userid,t1.name user_name,t2.name department_name,group_concat(date_format(t.date,'%m-%d') order by t.date separator ' , ') dates from t_add_job_record t left join t_user t1 on t.userid=t1.id left join t_department t2 on t1.department_id=t2.id where t.date>=? and t.date<=? group by t.userid limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, model.string1, model.string2);
        sql = "select count(distinct t.userid) from t_add_job_record t where t.date>=? and t.date<=?";
        int count = this.jdbc.queryForObject(sql, Integer.class, model.string1, model.string2);
        return R.success("加班申报记录整合(分页)", count, list);
    }
    //endregion

    //#region 生产计划(贴片)
    //后台-管理员查看生产计划(贴片)
    @RequestMapping("/getPatchPlan")
    @ResponseBody
    public Result getPatchPlan(@RequestBody Search model) {
        String sql1 = "select t.* from t_plan_patch t where t.del=0";
        String sql2 = "select count(*) from t_plan_patch t where t.del=0";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            String and = " and t.model like '%" + model.string1.toUpperCase() + "%' ";
            sql1 += and;
            sql2 += and;
        }
        sql1 += " order by t.time_start desc,t.time_end desc limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("生产计划(贴片)列表", count, list);
    }

    //后台-管理员添加生产计划(贴片)
    @RequestMapping("/addPatchPlan")
    @ResponseBody
    public Result addPatchPlan(@RequestBody PatchPlan model) {
        String sql = "insert into t_plan_patch(model,`order`,batch,line,card,count_plan,time_start,time_end,extra_hour,speed,step,mark_plan,del,systime) values(?,?,?,?,?,?,?,?,?,?,?,?,0,now())";
        int count = this.jdbc.update(sql, model.model.toUpperCase(), model.order, model.batch, model.line, model.card, model.count_plan, model.time_start, model.time_end, model.extra_hour, model.speed, PatchPlanStepEnum.unpublish.ordinal(), model.mark_plan);
        return R.success("生产计划(贴片)添加成功");
    }

    //后台-管理员修改生产计划(贴片)
    @RequestMapping("/updatePatchPlan")
    @ResponseBody
    public Result updatePatchPlan(@RequestBody PatchPlan model) {
        String sql = "update t_plan_patch t set t.line=?,t.card=?,t.count_plan=?,t.time_start=?,t.time_end=?,t.extra_hour=?,t.speed=?,t.mark_plan=? where t.id=?";
        int count = this.jdbc.update(sql, model.line, model.card, model.count_plan, model.time_start, model.time_end, model.extra_hour, model.speed, model.mark_plan, model.id);
        return R.success("生产计划(贴片)修改成功");
    }

    //后台-管理员删除生产计划(贴片)
    @RequestMapping("/deletePatchPlan/{id}")
    @ResponseBody
    public Result deletePatchPlan(@PathVariable int id) {
        String sql = "update t_plan_patch t set t.del=1 where t.id=?";
        int count = this.jdbc.update(sql, id);
        return R.success("生产计划(贴片)已删除");
    }

    //后台-管理员更新生产计划(贴片)进度
    @RequestMapping("/updatePatchPlanStep")
    @ResponseBody
    public Result updatePatchPlanStep(@RequestBody PlanStep model) {
        String sql = "insert into t_plan_patch_step(plan_id,step,message,systime) values(?,?,?,now())";
        int count = this.jdbc.update(sql, model.plan_id, model.step, model.message);
        sql = "update t_plan_patch t set t.step=? where t.id=?";
        count = this.jdbc.update(sql, model.step, model.plan_id);
        return R.success("生产计划(贴片)状态已更新");
    }

    //后台-管理员结转生产计划(贴片)
    @RequestMapping("/finishPatchPlan")
    @ResponseBody
    public Result finishPatchPlan(@RequestBody PatchPlan model) {
        String sql = "insert into t_plan_patch_step(plan_id,step,message,systime) values(?,?,?,now())";
        int count = this.jdbc.update(sql, model.id, PatchPlanStepEnum.finsih.ordinal(), model.mark_finish);
        sql = "update t_plan_patch t set t.count_finish=?,t.step=?,t.mark_finish=? where t.id=?";
        count = this.jdbc.update(sql, model.count_finish, PatchPlanStepEnum.finsih.ordinal(), model.mark_finish, model.id);
        return R.success("生产计划(贴片)结转完成");
    }

    //后台-管理员查看生产计划(贴片)进度
    @RequestMapping("/getPatchPlanStep/{id}")
    @ResponseBody
    public Result getPatchPlanStep(@PathVariable int id) {
        String sql1 = "select t.* from t_plan_patch_step t where t.plan_id=? order by t.systime desc";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1, id);
        return R.success("生产计划(贴片)进度列表", list);
    }
    //#endregion

    //#region 生产计划(制板)
    //后台-管理员查看生产计划(制板)
    @RequestMapping("/getBoardPlan")
    @ResponseBody
    public Result getBoardPlan(@RequestBody Search model) {
        String sql1 = "select t.* from t_plan_board t where t.del=0";
        String sql2 = "select count(*) from t_plan_board t where t.del=0";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            String and = " and t.model like '%" + model.string1.toUpperCase() + "%' ";
            sql1 += and;
            sql2 += and;
        }
        sql1 += " order by t.time_start desc,t.time_end desc limit " + UtilPage.getPage(model);
        List<Map<String, Object>> list = this.jdbc.queryForList(sql1);
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        return R.success("生产计划(制板)列表", count, list);
    }

    //后台-管理员添加生产计划(制板)
    @RequestMapping("/addBoardPlan")
    @ResponseBody
    public Result addBoardPlan(@RequestBody BoardPlan model) {
        String sql = "insert into t_plan_board(model,`order`,batch,plan_id,team,count_plan,time_start,step,mark_plan,del,systime) values(?,?,?,?,?,?,?,?,?,0,now())";
        int count = this.jdbc.update(sql, model.model.toUpperCase(), model.order, model.batch, model.plan_id, model.team, model.count_plan, model.time_start, BoardPlanStepEnum.unpublish.ordinal(), model.mark_plan);
        return R.success("生产计划(制板)添加成功");
    }

    //后台-管理员修改生产计划(制板)
    @RequestMapping("/updateBoardPlan")
    @ResponseBody
    public Result updateBoardPlan(@RequestBody BoardPlan model) {
        String sql = "update t_plan_board t set t.team=?,t.count_plan=?,t.time_start=?,t.mark_plan=? where t.id=?";
        int count = this.jdbc.update(sql, model.team, model.count_plan, model.time_start, model.mark_plan, model.id);
        return R.success("生产计划(制板)修改成功");
    }

    //后台-管理员删除生产计划(制板)
    @RequestMapping("/deleteBoardPlan/{id}")
    @ResponseBody
    public Result deleteBoardPlan(@PathVariable int id) {
        String sql = "update t_plan_board t set t.del=1 where t.id=?";
        int count = this.jdbc.update(sql, id);
        return R.success("生产计划(制板)已删除");
    }

//    //后台-管理员更新生产计划(制板)进度
//    @RequestMapping("/updateBoardPlanStep")
//    @ResponseBody
//    public Result updateBoardPlanStep(@RequestBody PlanStep model) {
//        String sql = "insert into t_plan_board_step(plan_id,step,message,systime) values(?,?,?,now())";
//        int count = this.jdbc.update(sql, model.plan_id, model.step, model.message);
//        sql = "update t_plan_board t set t.step=? where t.id=?";
//        count = this.jdbc.update(sql, model.step, model.plan_id);
//        return R.success("生产计划(制板)状态已更新");
//    }
//
//    //后台-管理员结转生产计划(制板)
//    @RequestMapping("/finishBoardPlan")
//    @ResponseBody
//    public Result finishBoardPlan(@RequestBody BoardPlan model) {
//        String sql = "insert into t_plan_board_step(plan_id,step,message,systime) values(?,?,?,now())";
//        int count = this.jdbc.update(sql, model.id, BoardPlanStepEnum.finsih.ordinal(), model.mark_finish);
//        sql = "update t_plan_board t set t.count_finish=?,t.step=?,t.mark_finish=? where t.id=?";
//        count = this.jdbc.update(sql, model.count_finish, BoardPlanStepEnum.finsih.ordinal(), model.mark_finish, model.id);
//        return R.success("生产计划(制板)结转完成");
//    }
//
//    //后台-管理员查看生产计划(制板)进度
//    @RequestMapping("/getBoardPlanStep/{id}")
//    @ResponseBody
//    public Result getBoardPlanStep(@PathVariable int id) {
//        String sql1 = "select t.* from t_plan_board_step t where t.plan_id=? order by t.systime desc";
//        List<Map<String, Object>> list = this.jdbc.queryForList(sql1, id);
//        return R.success("生产计划(制板)进度列表", list);
//    }
    //#endregion
}
