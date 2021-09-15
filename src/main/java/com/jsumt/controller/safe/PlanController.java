
package com.jsumt.controller.safe;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.controller.trouble.TroubleController;
import com.jsumt.service.safe.AccountService;
import com.jsumt.service.safe.PlanService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.util.WordUtils;
import com.jsumt.vo.safe.DetailBean;
import com.jsumt.vo.safe.PlanBean;
import com.jsumt.vo.safe.TypeBean;
import com.jsumt.vo.system.UserBean;

/**
 * 安全生产投入计划
 * ClassName:SafeController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月20日 下午1:56:26 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("plan")
public class PlanController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(TroubleController.class);
    // 安全生产投入类别表的相关增删改查
    @Autowired
    private PlanService planService;
    @Autowired
    private UserManageService userService;
    @Autowired
    private AccountService accountService;

    // 查询所有安全生产投入类别的信息**************************************************************
    @RequestMapping("queryAllJhlb")
    public @ResponseBody Map<String, Object> queryAllJhlb()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> retList = Lists.newArrayList();
            retList = this.queryAllJhlb_();
            retMap = this.generateMsg(retList, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }
    }

    private List<Map<String, Object>> queryAllJhlb_()
    {

        List<Map<String, Object>> retList = Lists.newArrayList();
        // 查询类别表的信息
        List<TypeBean> allTypeList = planService.queryAllTypes(null);
        for (TypeBean typeBean : allTypeList)
        {
            if ("0".equals(typeBean.getId_one()))
            {// 所有类别表中id_one未0说明 了为1级父类
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", typeBean.getId());
                map.put("title", typeBean.getTitle());
                // 第二层
                List<Map<String, Object>> children = this.findSecondChild(allTypeList, typeBean.getId());
                map.put("children", children);
                retList.add(map);
            }
        }
        return retList;
    }

    private List<Map<String, Object>> findSecondChild(List<TypeBean> allTypeList, String id)
    {
        List<Map<String, Object>> retList = Lists.newArrayList();
        for (TypeBean typeBean : allTypeList)
        {
            if (id.equals(typeBean.getId_one()))
            {// 所有类别表中id_one未0说明 了为1级父类
                Map<String, Object> map = Maps.newHashMap();
                map = BeanUtil.toMap(typeBean);
                retList.add(map);
            }
        }
        return retList;
    }

    private List<Map<String, Object>> findSecDetailChild(List<Map<String, Object>> allTypeList, String id)
    {
        List<Map<String, Object>> retList = Lists.newArrayList();
        for (Map<String, Object> rsMap : allTypeList)
        {
            if (id.equals(String.valueOf(rsMap.get("ID_ONE"))))
            {// 所有类别表中id_one为0说明 了为1级父类，否则为2级子数据
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", String.valueOf(rsMap.get("ID_ONE")));
                map.put("title", String.valueOf(rsMap.get("TITLE")));
                String bdwPlan = String.valueOf(rsMap.get("BDW_PLAN"));
                map.put("bdwPlan", StringHelper.isNullAndEmpty(bdwPlan) ? "0.00" : bdwPlan);
                String fbdwPlanSum = String.valueOf(rsMap.get("FBDW_PLAN_SUM"));
                map.put("fbdwPlanSum", StringHelper.isNullAndEmpty(fbdwPlanSum) ? "0.00" : fbdwPlanSum);
                String planSum = String.valueOf(rsMap.get("PLAN_SUM"));
                map.put("planSum", StringHelper.isNullAndEmpty(planSum) ? "0.00" : planSum);
                map.put("detailId", String.valueOf(rsMap.get("DETAILID")));
                retList.add(map);
            }
        }
        return retList;
    }

    @RequestMapping("queryPlanDetailInfo")
    public @ResponseBody Map<String, Object> queryPlanDetailInfo()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> retList = Lists.newArrayList();
            // 查询类别表的信息
            String planId = this.getParameter("planId");
            List<Map<String, Object>> allTypeList = planService.queryPlanDetailInfo(planId);
            for (Map<String, Object> tMap : allTypeList)
            {
                if ("1".equals(String.valueOf(tMap.get("IS_ONE"))))
                {// 所有类别表中id_one未0说明 了为1级父类
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", String.valueOf(tMap.get("ID")));
                    map.put("title", String.valueOf(tMap.get("TITLE")));
                    // 第二层
                    List<Map<String, Object>> children =
                            this.findSecDetailChild(allTypeList, String.valueOf(tMap.get("ID")));
                    map.put("children", children);
                    retList.add(map);
                }
            }
            retMap = this.generateMsg(retList, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    // 安全生产投入计划表的相关增删改查**************************************************************
    @RequestMapping("queryPlanList")
    public @ResponseBody Map<String, Object> queryPlanList(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<PlanBean> pageInfo = new PageInfo<PlanBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            List<PlanBean> planList = planService.queryAllPlans(mapWhere, pageInfo);
            pageInfo = new PageInfo<PlanBean>(planList);

            List<Map<String, Object>> listMap = Lists.newArrayList();

            for (PlanBean bean : planList)
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                map.put("updateTime", DateUtil.getDateFormat(bean.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime", DateUtil.getDateFormat(bean.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                listMap.add(map);
            }
            resMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "查询失败!");
        }
        return resMap;
    }

    // 安全生产投入计划增加与细项表的增加*******************************************************************
    @RequestMapping("addPlans")
    public @ResponseBody Map<String, Object> addPlans()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            String orgId = this.getParameter("orgId");
            String type = this.getParameter("type");
            // 新建计划表主表 有些字段忘记加了，注意看
            PlanBean bean = new PlanBean();
            String planId = UUIDHexGenerator.generator();
            bean.setId(planId);// 主表的id
            bean.setTbr(userBean.getUser_name());
            bean.setTbdw(userBean.getOrgName());
            bean.setTbdwid(userBean.getOrgId());
            bean.setType(type);
            bean.setStatus("0");// 初始化未上報狀態
            bean.setParent_id("0");// 初始化默认
            String date = this.getParameter("Date");// 日期
            if ("M".equals(type))
            { // 截取年份和月份
                String[] strs = date.split("-");
                String year = strs[0].toString();
                String month = strs[1].toString();
                bean.setYear(year);
                bean.setMonth(month);
                bean.setTitle(year + "年" + month + "月安全生产投入计划");
            }
            else
            {
                bean.setYear(date);
                bean.setTitle(date + "年安全生产投入计划");
                
            }

            List<PlanBean> beanList = planService.queryOnePlan(bean);
            if (!beanList.isEmpty())
            {
                return retMap = this.generateMsg("", false, "M".equals(type) ? "该月计划已经存在!" : "该年计划已经存在!");
            }
            bean.setCreateTime(new Date());
            bean.setUpdateTime(new Date());
            bean.setOrg_id(orgId);

            // 增加计划细项表内容list类型存入
            List<DetailBean> detailBeanList = Lists.newArrayList();
            String insertList = this.getParameter("insertList");
            BigDecimal planSum = new BigDecimal("0");// 凡是数值计算用BigDecimal 赵连连注意
            List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(insertList, List.class);
            for (Map<String, Object> map : questionList)
            {
                String sctr_type_id1 = String.valueOf(map.get("id1"));
                String sctr_type_id2 = String.valueOf(map.get("id2"));
                String sctr_type_title1 = String.valueOf(map.get("title1"));
                String sctr_type_title2 = String.valueOf(map.get("title2"));
                String detailId = UUIDHexGenerator.generator();
                String money = String.valueOf(map.get("planMoney"));
                // 新建计划表细则
                DetailBean detailBean = new DetailBean();
                detailBean.setSctr_type_id1(sctr_type_id1);
                detailBean.setSctr_type_id2(sctr_type_id2);
                detailBean.setSctr_type_title1(sctr_type_title1);
                detailBean.setSctr_type_title2(sctr_type_title2);
                detailBean.setCreateTime(new Date());
                detailBean.setUpdateTime(new Date());
                detailBean.setId(detailId);
                detailBean.setBdw_plan(StringHelper.isNullAndEmpty(money) ? "0" : money);
                detailBean.setPlan_sum(StringHelper.isNullAndEmpty(money) ? "0" : money);// 计划总额取默认money
                detailBean.setFbdw_plan_sum("0");
                detailBean.setFbdw_finish_sum("0");
                detailBean.setFinish_sum("0");
                detailBean.setBdw_finish("0");
                // 关联计划ID，写在这里，一次优化遍历，service层不需要了
                detailBean.setPlan_id(planId);
                detailBeanList.add(detailBean);
                BigDecimal planMoney = new BigDecimal(StringHelper.isNullAndEmpty(money) ? "0" : money);
                planSum = planSum.add(planMoney);
            }
            bean.setPlan_total(planSum.toString());
            planService.addPlans(bean, detailBeanList);
            return retMap = this.generateMsg(bean, true, "增加成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加失败!");
            return retMap;
        }
    }
    
    //一键发布addPlansAndPublish
    @RequestMapping("addPlansAndPublish")
    public @ResponseBody Map<String, Object> addPlansAndPublish()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            String orgId = this.getParameter("orgId");
            String type = this.getParameter("type");
            // 新建计划表主表 有些字段忘记加了，注意看
            PlanBean bean = new PlanBean();
            String planId = UUIDHexGenerator.generator();
            bean.setId(planId);// 主表的id
            bean.setTbr(userBean.getUser_name());
            bean.setTbdw(userBean.getOrgName());
            bean.setTbdwid(userBean.getOrgId());
            bean.setType(type);
            bean.setStatus("1");// 初始化未上報狀態
            bean.setParent_id("0");// 初始化默认
            String date = this.getParameter("Date");// 日期
            if ("M".equals(type))
            { // 截取年份和月份
                String[] strs = date.split("-");
                String year = strs[0].toString();
                String month = strs[1].toString();
                bean.setYear(year);
                bean.setMonth(month);
                bean.setTitle(year + "年" + month + "月安全生产投入计划");
            }
            else
            {
                bean.setYear(date);
                bean.setTitle(date + "年安全生产投入计划");
            }

            List<PlanBean> beanList = planService.queryOnePlan(bean);
            if (!beanList.isEmpty())
            {
                return retMap = this.generateMsg("", false, "M".equals(type) ? "该月计划已经存在!" : "该年计划已经存在!");
            }
            bean.setCreateTime(new Date());
            bean.setUpdateTime(new Date());
            bean.setOrg_id(orgId);

            // 增加计划细项表内容list类型存入
            List<DetailBean> detailBeanList = Lists.newArrayList();
            String insertList = this.getParameter("insertList");
            BigDecimal planSum = new BigDecimal("0");// 凡是数值计算用BigDecimal 赵连连注意
            List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(insertList, List.class);
            for (Map<String, Object> map : questionList)
            {
                String sctr_type_id1 = String.valueOf(map.get("id1"));
                String sctr_type_id2 = String.valueOf(map.get("id2"));
                String sctr_type_title1 = String.valueOf(map.get("title1"));
                String sctr_type_title2 = String.valueOf(map.get("title2"));
                String detailId = UUIDHexGenerator.generator();
                String money = String.valueOf(map.get("planMoney"));
                // 新建计划表细则
                DetailBean detailBean = new DetailBean();
                detailBean.setSctr_type_id1(sctr_type_id1);
                detailBean.setSctr_type_id2(sctr_type_id2);
                detailBean.setSctr_type_title1(sctr_type_title1);
                detailBean.setSctr_type_title2(sctr_type_title2);
                detailBean.setCreateTime(new Date());
                detailBean.setUpdateTime(new Date());
                detailBean.setId(detailId);
                detailBean.setBdw_plan(StringHelper.isNullAndEmpty(money) ? "0" : money);
                detailBean.setPlan_sum(StringHelper.isNullAndEmpty(money) ? "0" : money);// 计划总额取默认money
                detailBean.setFbdw_plan_sum("0");
                detailBean.setFbdw_finish_sum("0");
                detailBean.setFinish_sum("0");
                detailBean.setBdw_finish("0");
                // 关联计划ID，写在这里，一次优化遍历，service层不需要了
                detailBean.setPlan_id(planId);
                detailBeanList.add(detailBean);
                BigDecimal planMoney = new BigDecimal(StringHelper.isNullAndEmpty(money) ? "0" : money);
                planSum = planSum.add(planMoney);
            }
            bean.setPlan_total(planSum.toString());
            planService.addPlans(bean, detailBeanList);
            return retMap = this.generateMsg(bean, true, "增加成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加失败!");
            return retMap;
        }
    }

    // 修改安全生产投入表的发布状态
    @RequestMapping("updateStatus")
    public @ResponseBody Map<String, Object> updateStatus(PlanBean bean)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            // 把上报状态设置为“1”
            bean.setStatus("1");
            planService.updatePlan(bean);
            resMap = this.generateMsg("", true, "修改成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "修改失败!");
        }
        return resMap;
    }

    // 删除安全生产投入计划及与其相关的内容和修改
    @RequestMapping("delPlanList")
    public @ResponseBody Map<String, Object> delPlanList()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            planService.delPlans(delelteList);
            retMap = this.generateMsg("", true, "删除计划成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除计划失败!");
        }
        return retMap;
    }

    // 查询没有被分配的所有子单位（1，表里存在，已发布且没有被分配的 2表里存在未发布且没有被分配的 3计划表里根本就没有数据）
    @RequestMapping("queryNotChildrenPlans")
    public @ResponseBody Map<String, Object> queryNotChildrenPlans(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            mapWhere.put("dwmc", this.getParameter("dwmc"));
            List<Map<String, Object>> listMap = planService.queryNotChildrenPlans(mapWhere, pageInfo);
            pageInfo = new PageInfo<Map<String, Object>>(listMap);
            // 把String类型换成Date类型
            for (Map<String, Object> map : listMap)
            {
                if (map.get("CREATETIME") != null)
                {
                    map.put("CREATETIME", DateUtil.getDateFormat((Date) map.get("CREATETIME")));
                }
            }
            resMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "查询失败!");
        }
        return resMap;
    }

    @RequestMapping("queryChildrenPlans")
    public @ResponseBody Map<String, Object> queryChildrenPlans(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;
            mapWhere.put("dwmc", this.getParameter("tbdw"));
            PageInfo<PlanBean> pageInfo = new PageInfo<PlanBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            List<PlanBean> planList = planService.queryAllPlans(mapWhere, pageInfo);
            pageInfo = new PageInfo<PlanBean>(planList);

            List<Map<String, Object>> listMap = Lists.newArrayList();

            for (PlanBean bean : planList)
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                map.put("updateTime", DateUtil.getDateFormat(bean.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime", DateUtil.getDateFormat(bean.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                listMap.add(map);
            }
            resMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "查询失败!");
        }
        return resMap;
    }

    @RequestMapping("updatePlanDetail")
    public @ResponseBody Map<String, Object> updatePlanDetail()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String detailId = this.getParameter("detailId");
            String bdwPlan = this.getParameter("bdwPlan");
            String planSum= planService.updatePlanDetail(detailId, bdwPlan);
            retMap = this.generateMsg(planSum, true, "修改成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改失败!");
        }
        return retMap;

    }

    @RequestMapping("cbChildrenPlan")
    public @ResponseBody Map<String, Object> cbChildrenPlan()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String cbList = this.getParameter("cbList");
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            List<Map<String, Object>> cbLists = JsonHelper.fromJsonWithGson(cbList, List.class);
            planService.cbChildrenPlan(cbLists, userBean);

            retMap = this.generateMsg("", true, "催报成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "催报失败!");
        }
        return retMap;
    }

    @RequestMapping("viewPlan")
    public void viewPlan() throws Exception
    {
        String view = this.getParameter("view");
        WordUtils wordUtil = new WordUtils();
        if ("JHM".equals(view))
        {// 计划月的报表
            String planId = this.getParameter("planId");
            PlanBean planBean = planService.queryOneById(planId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("${tbdwAndCreateTime}",
                    "单位：" + planBean.getTbdw()
                            + "                                                                填报时间："
                            + DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
//            Map<String, Object> mapWhere = Maps.newHashMap();
//            mapWhere.put("year", planBean.getYear());
//            mapWhere.put("month", planBean.getMonth());
//            mapWhere.put("tbdwid", planBean.getTbdwid());
//            List<AccountBean> accounts = accountService.queryAllTzs(mapWhere, null);
//            BigDecimal finishAccounts = new BigDecimal("0");
//            for (AccountBean account : accounts)
//            {
//                BigDecimal finish = new BigDecimal(account.getTrjhTotalFinish());
//                finishAccounts = finishAccounts.add(finish);
//            }
//            params.put("${trjhTotalFinish}", finishAccounts.toString());
            //金额为本单位的金额
            BigDecimal trjhTotal = new BigDecimal("0");
            params.put("${createTime}", DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
            // 查出计划下的所有细项
            List<Map<String, Object>> allTypeList = planService.queryPlanDetailInfo(planId);
            int index = 0;
            for (Map<String, Object> tMap : allTypeList)
            {
                if ("1".equals(String.valueOf(tMap.get("IS_ONE"))))
                {
                    List<Map<String, Object>> children =
                            this.findSecDetailChild(allTypeList, String.valueOf(tMap.get("ID")));
                    for (Map<String, Object> map : children)
                    {
                        index = index + 1;
                        params.put("${planSum" + index + "}", String.valueOf(map.get("bdwPlan")));
                        // 计算出本单位金额合计
                        BigDecimal bdwPlan = new BigDecimal(String.valueOf(map.get("bdwPlan")));
                        trjhTotal=trjhTotal.add(bdwPlan);
                    }
                }
            }
            params.put("${trjhTotal}", trjhTotal.toString());
            String sourceFileUrl =
                    this.getClass().getClassLoader().getResource("").getPath() + "templates/aqscyjh.docx"; // 模板文件位置
            wordUtil.getWordToPdf(sourceFileUrl,
                    params,
                    planBean.getYear() + "年" + planBean.getMonth() + "月生产计划",
                    response);
        }
        if ("TZM".equals(view))
        {// 台账月的报表

        }
        if ("Y".equals(view))
        {// 年的报表
            String planId = this.getParameter("planId");
            PlanBean planBean = planService.queryOneById(planId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("${tbdwAndCreateTime}",
                    "单位：" + planBean.getTbdw()
                            + "                                                                  填报时间："
                            + DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
            // 汇总年下面所有的台账表的TRJH_TOTAL_FINISH
//            Map<String, Object> mapWhere = Maps.newHashMap();
//            mapWhere.put("year", planBean.getYear());
//            mapWhere.put("tbdwid", planBean.getTbdwid());
//            List<AccountBean> accounts = accountService.queryAllTzs(mapWhere, null);
//            BigDecimal finishAccounts = new BigDecimal("0");
//            for (AccountBean account : accounts)
//            {
//                BigDecimal finish = new BigDecimal(account.getTrjhTotalFinish());
//                finishAccounts = finishAccounts.add(finish);
//            }
//            params.put("${trjhTotalFinish}", finishAccounts.toString());
            //金额为本单位的金额
            BigDecimal trjhTotal = new BigDecimal("0");
            params.put("${createTime}", DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
            // 查出计划下的所有细项
            List<Map<String, Object>> allTypeList = planService.queryPlanDetailInfo(planId);
            int index = 0;
            for (Map<String, Object> tMap : allTypeList)
            {
                if ("1".equals(String.valueOf(tMap.get("IS_ONE"))))
                {
                    List<Map<String, Object>> children =
                            this.findSecDetailChild(allTypeList, String.valueOf(tMap.get("ID")));
                    for (Map<String, Object> map : children)
                    {
                        index = index + 1;
                        params.put("${planSum" + index + "}", String.valueOf(map.get("bdwPlan")));
                        // 计算出本单位金额合计
                        BigDecimal bdwPlan = new BigDecimal(String.valueOf(map.get("bdwPlan")));
                        trjhTotal=trjhTotal.add(bdwPlan);
                    }

                }
            }
            params.put("${trjhTotal}", trjhTotal.toString());
            String sourceFileUrl =
                    this.getClass().getClassLoader().getResource("").getPath() + "templates/aqscnjh.docx"; // 模板文件位置
            wordUtil.getWordToPdf(sourceFileUrl, params, planBean.getYear() + "年生产计划", response);
        }
    }

    @RequestMapping("dowloadJhb")
    public void dowloadJhb() throws Exception
    {
        String view = this.getParameter("view");
        WordUtils wordUtil = new WordUtils();
        if ("JHM".equals(view))
        {// 计划月的报表
            String planId = this.getParameter("planId");
            PlanBean planBean = planService.queryOneById(planId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("${tbdwAndCreateTime}",
                    "单位：" + planBean.getTbdw()
                            + "                                                                填报时间："
                            + DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
//            Map<String, Object> mapWhere = Maps.newHashMap();
//            mapWhere.put("year", planBean.getYear());
//            mapWhere.put("month", planBean.getMonth());
//            mapWhere.put("tbdwid", planBean.getTbdwid());
//            List<AccountBean> accounts = accountService.queryAllTzs(mapWhere, null);
//            BigDecimal finishAccounts = new BigDecimal("0");
//            for (AccountBean account : accounts)
//            {
//                BigDecimal finish = new BigDecimal(account.getTrjhTotalFinish());
//                finishAccounts = finishAccounts.add(finish);
//            }
//            params.put("${trjhTotalFinish}", finishAccounts.toString());
            //金额为本单位的金额
            BigDecimal trjhTotal = new BigDecimal("0");
            params.put("${createTime}", DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
            // 查出计划下的所有细项
            List<Map<String, Object>> allTypeList = planService.queryPlanDetailInfo(planId);
            int index = 0;
            for (Map<String, Object> tMap : allTypeList)
            {
                if ("1".equals(String.valueOf(tMap.get("IS_ONE"))))
                {
                    List<Map<String, Object>> children =
                            this.findSecDetailChild(allTypeList, String.valueOf(tMap.get("ID")));
                    for (Map<String, Object> map : children)
                    {
                        index = index + 1;
                        params.put("${planSum" + index + "}", String.valueOf(map.get("bdwPlan")));
                        // 计算出本单位金额合计
                        BigDecimal bdwPlan = new BigDecimal(String.valueOf(map.get("bdwPlan")));
                        trjhTotal=trjhTotal.add(bdwPlan);
                    }

                }
            }
            params.put("${trjhTotal}", trjhTotal.toString());
            String sourceFileUrl =
                    this.getClass().getClassLoader().getResource("").getPath() + "templates/aqscyjh.docx"; // 模板文件位置
            wordUtil.getWord(sourceFileUrl, params, planBean.getYear() + "年" + planBean.getMonth() + "月生产计划", response);
        }
        if ("TZM".equals(view))
        {// 台账月的报表

        }
        if ("Y".equals(view))
        {// 年的报表
            String planId = this.getParameter("planId");
            PlanBean planBean = planService.queryOneById(planId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("${tbdwAndCreateTime}",
                    "单位：" + planBean.getTbdw()
                            + "                                                                  填报时间："
                            + DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
            // 汇总年下面所有的台账表的TRJH_TOTAL_FINISH
//            Map<String, Object> mapWhere = Maps.newHashMap();
//            mapWhere.put("year", planBean.getYear());
//            mapWhere.put("tbdwid", planBean.getTbdwid());
//            List<AccountBean> accounts = accountService.queryAllTzs(mapWhere, null);
//            BigDecimal finishAccounts = new BigDecimal("0");
//            for (AccountBean account : accounts)
//            {
//                BigDecimal finish = new BigDecimal(account.getTrjhTotalFinish());
//                finishAccounts = finishAccounts.add(finish);
//            }
//            params.put("${trjhTotalFinish}", finishAccounts.toString());
            //金额为本单位的金额
            BigDecimal trjhTotal = new BigDecimal("0");
            params.put("${createTime}", DateUtil.getDateFormat(planBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
            List<Map<String, Object>> allTypeList = planService.queryPlanDetailInfo(planId);
            int index = 0;
            for (Map<String, Object> tMap : allTypeList)
            {
                if ("1".equals(String.valueOf(tMap.get("IS_ONE"))))
                {
                    List<Map<String, Object>> children =
                            this.findSecDetailChild(allTypeList, String.valueOf(tMap.get("ID")));
                    for (Map<String, Object> map : children)
                    {
                        index = index + 1;
                        params.put("${planSum" + index + "}", String.valueOf(map.get("bdwPlan")));
                        // 计算出本单位金额合计
                        BigDecimal bdwPlan = new BigDecimal(String.valueOf(map.get("bdwPlan")));
                        trjhTotal=trjhTotal.add(bdwPlan);
                    }

                }
            }
            params.put("${trjhTotal}", trjhTotal.toString());
            String sourceFileUrl =
                    this.getClass().getClassLoader().getResource("").getPath() + "templates/aqscnjh.docx"; // 模板文件位置
            wordUtil.getWord(sourceFileUrl, params, planBean.getYear() + "年生产计划", response);
        }
    }

    public static void main(String[] args)
    {
        String aa = null;
        BigDecimal planSum = new BigDecimal(aa);
        BigDecimal sum = new BigDecimal("2.8");
        planSum = planSum.add(sum);
        planSum = planSum.add(sum);
        System.out.println(planSum.toString());

    }

}
