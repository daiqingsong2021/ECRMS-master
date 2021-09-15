/**
 * Project Name:ECRMS
 * File Name:ReportController.java
 * Package Name:com.jsumt.controller.report
 * Date:2018年12月21日下午1:47:55
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.report;

import java.util.ArrayList;
import java.util.Collection;
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
import com.jsumt.service.accident.AccidentService;
import com.jsumt.service.risk.RiskService;
import com.jsumt.service.safe.PlanService;
import com.jsumt.service.system.MessageService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.service.trouble.TroubleService;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.vo.accident.AccidentBean;
import com.jsumt.vo.risk.ZdRiskBean;
import com.jsumt.vo.safe.PlanBean;
import com.jsumt.vo.system.MessageBean;
import com.jsumt.vo.system.OrganizationBean;
import com.jsumt.vo.system.UserBean;
import com.jsumt.vo.trouble.TroubleBean;

/**
 * ClassName:ReportController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月21日 下午1:47:55 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("report")
public class ReportController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private TroubleService troubleService;

    @Autowired
    private UserManageService userService;

    @Autowired
    private PlanService planService;

    @Autowired
    private AccidentService accidentService;

    @Autowired
    private OrganizationService organService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RiskService riskService;
    
    //事故统计querySgTjReport
    @RequestMapping("querySgTjReport")
    public @ResponseBody Map<String, Object> querySgTjReport()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> resMap = Maps.newHashMap();
            String orgId = this.getParameter("orgId");
            resMap.put("org_id", orgId);
            List<AccidentBean> accidentList = accidentService.queryAllAccidents(resMap, null);
            Integer deadPeople =0;
            Integer hurtPeople =0;
            Integer lightPeople =0;
            Integer allPeople =0;
            for(AccidentBean accident :accidentList ){
                //计算本单位所有事故伤亡人员
                 deadPeople += accident.getDead_people();
                 hurtPeople += accident.getHurt_people();
                 lightPeople += accident.getLight_people();
            }
            allPeople+=deadPeople+hurtPeople+lightPeople;
            resMap.put("deadPeople", deadPeople);
            resMap.put("hurtPeople", hurtPeople);
            resMap.put("lightPeople", lightPeople);
            resMap.put("allPeople", allPeople);
            retMap = this.generateMsg(resMap, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }
    }
    
    @RequestMapping("queryYhReport")
    public @ResponseBody Map<String, Object> queryYhReport()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> resMap = Maps.newHashMap();
            // 查询各被检查单位的1,2,3级隐患数目（其实就是各等级问题个数）
            List<Map<String, Object>> retList = Lists.newArrayList();
            String createTimeData = String.valueOf(this.getParameter("createTime"));
            Map<String, Object> mapWhere = Maps.newHashMap();
            if (StringHelper.isNotNullAndEmpty(createTimeData))
            {
                String startDate =
                        createTimeData.substring(0, StringHelper.getFromIndex(createTimeData, "-", 3)).trim();
                String endDate = createTimeData
                        .substring(StringHelper.getFromIndex(createTimeData, "-", 3) + 2, createTimeData.length())
                        .trim();
                mapWhere.put("startDate", startDate);
                mapWhere.put("endDate", endDate);
            }
            // 获取当前用户
            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            List<OrganizationBean> orgChild = organService.queryOnlyChildOrg(userBean.getOrgId());
          
            retList = troubleService.querySjdwQuestNums(mapWhere);
            List<Map<String, Object>> list = Lists.newArrayList();
            for (OrganizationBean org : orgChild)
            {
                Map<String, Object> tmap = Maps.newHashMap();
                tmap.put("sjdw", org.getName_cn_simple());
                Integer oneYh = 0;
                Integer twoYh = 0;
                Integer threeYh = 0;
                for (Map<String, Object> map : retList)
                {
                    // 过滤当前用户下所在的组织机构下的组织机构数据
                    boolean isParent = organService.judegeOrgParent(org.getOrg_id(), String.valueOf(map.get("sjdwId")));
                    if (isParent)
                    {
                        oneYh += Integer.valueOf(String.valueOf(map.get("oneYh")));
                        twoYh += Integer.valueOf(String.valueOf(map.get("twoYh")));
                        threeYh += Integer.valueOf(String.valueOf(map.get("threeYh")));
                    }
                }
                tmap.put("oneYh", String.valueOf(oneYh));
                tmap.put("twoYh", String.valueOf(twoYh));
                tmap.put("threeYh", String.valueOf(threeYh));
                list.add(tmap);
            }
            Integer allOneYhs = 0;
            Integer allTwoYhs = 0;
            Integer allThreeYhs = 0;
            for (Map<String, Object> map : list)
            {
                Integer oneYh = Integer.valueOf(String.valueOf(map.get("oneYh")));// 没有默认值就是0
                Integer twoYh = Integer.valueOf(String.valueOf(map.get("twoYh")));
                Integer threeYh = Integer.valueOf(String.valueOf(map.get("threeYh")));
                allOneYhs = allOneYhs + oneYh;
                allTwoYhs = allTwoYhs + twoYh;
                allThreeYhs = allThreeYhs + threeYh;
            }
            resMap.put("infos", list);
            resMap.put("total", allOneYhs + allTwoYhs + allThreeYhs);
            resMap.put("allOneYhs", allOneYhs);
            resMap.put("allTwoYhs", allTwoYhs);
            resMap.put("allThreeYhs", allThreeYhs);
            retMap = this.generateMsg(resMap, true, "查询成功!");
            return retMap;

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    @RequestMapping("querySgReport")
    public @ResponseBody Map<String, Object> querySgReport()
    {
       /* Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> retList = Lists.newArrayList();

            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            
            //查出本单位伤亡情况
            OrganizationBean orgBdw = organService.queryOneById(userBean.getOrgId());
            List<OrganizationBean> orgAll = new ArrayList<>();
            orgAll.add(orgBdw);
            List<OrganizationBean> orgChild = organService.queryOnlyChildOrg(userBean.getOrgId());
            orgAll.addAll(orgChild);
            List<Map<String, Object>> listMap = accidentService.querySgReport();
            
            //查出本单位的所有的事故台账
            Map<String, Object> sgMap = new HashMap<String, Object>();
            sgMap.put("sg_type", "0");
            sgMap.put("org_id", userBean.getOrgId());
            List<AccidentBean> accidentList = accidentService.queryAllAccidents(sgMap, null);
            //把本单位及所有子单位汇总为一个大的List
            List<AccidentBean> allListAccident = new ArrayList<>();
            allListAccident.addAll(accidentList);
            //查出本单位下子单位的所有事故台账
            for(OrganizationBean o :orgChild){
                Map<String, Object> sgChildMap = new HashMap<String, Object>();
                sgChildMap.put("sg_type", "0");
                sgChildMap.put("org_id", o.getOrg_id());
                List<AccidentBean> accidentChildList = accidentService.queryAllAccidents(sgChildMap, null);
                allListAccident.addAll(accidentChildList);
            }
            
            //计算死亡人数
            BigDecimal deadPeople = new BigDecimal("0");
            //计算重伤人数
            BigDecimal hurtPeople = new BigDecimal("0");
            //计算轻伤人数
            BigDecimal lightPeople = new BigDecimal("0");
            //计算伤亡总数
            BigDecimal allPeople = new BigDecimal("0");
            
            //计算本单位及其子单位的伤亡情况
            for(AccidentBean all : allListAccident){
                Map<String, Object> map = Maps.newHashMap();
                map.put("name", all.getSgdw());
                //若是本单位
                if(all.getOrg_id().equals(userBean.getOrgId())){
                    for(){
                        
                    }
                    BigDecimal deadPeoples = new BigDecimal(all.getDead_people().toString());
                    deadPeople.add(deadPeoples);
                }
                
                
            }
            for (OrganizationBean org : orgAll)
            {
                Map<String, Object> tmap = Maps.newHashMap();
                tmap.put("name", org.getName_cn_simple());
                Integer value = 0;
                for (Map<String, Object> map : listMap)
                {
                    if (org.getOrg_id().equals(String.valueOf(map.get("ORG_ID"))))
                    {
                        String NUM = String.valueOf(map.get("NUM"));
                        value += Integer.valueOf(NUM);
                    }
                }
                tmap.put("value", String.valueOf(value));
                retList.add(tmap);
            }
            retMap = this.generateMsg(retList, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }*/
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> retList = Lists.newArrayList();

            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            List<OrganizationBean> orgChild = organService.queryOnlyChildOrg(userBean.getOrgId());
            // 查出
            List<Map<String, Object>> listMap = accidentService.querySgReport();

            for (OrganizationBean org : orgChild)
            {
                Map<String, Object> tmap = Maps.newHashMap();
                tmap.put("name", org.getName_cn_simple());
                Integer value = 0;
                for (Map<String, Object> map : listMap)
                {
                    if (org.getOrg_id().equals(String.valueOf(map.get("ORG_ID"))))
                    {
                        String NUM = String.valueOf(map.get("NUM"));
                        value += Integer.valueOf(NUM);
                    }
                }
                tmap.put("value", String.valueOf(value));
                retList.add(tmap);
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

    @RequestMapping("querySgXw")
    public @ResponseBody Map<String, Object> querySgXw()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> retList = Lists.newArrayList();
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("sg_type", "1");// 重新编码
            List<AccidentBean> accidents = accidentService.queryAllAccidents(mapWhere, null);
            int i = 1;
            for (AccidentBean accidentBean : accidents)
            {
                if (i <= 5)
                {
                    Map<String, Object> tmap = Maps.newHashMap();
                    tmap.put("title", accidentBean.getTitle());
                    tmap.put("sgTime", DateUtil.getDateFormat(accidentBean.getSg_time(), DateUtil.DATE_DEFAULT_FORMAT));
                    tmap.put("sgId", accidentBean.getId());
                    tmap.put("remark", accidentBean.getRemark());
                    retList.add(tmap);
                    i++;
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
    
    @RequestMapping("querySingleXw")
    public @ResponseBody Map<String, Object> querySingleXw(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            mapWhere.put("sg_type", "1");// 重新编码
            List<AccidentBean> accidents = accidentService.queryAllAccidents(mapWhere, null);
            Map<String, Object> tmap = Maps.newHashMap();
            tmap.put("title", accidents.get(0).getTitle());
            tmap.put("sgTime", DateUtil.getDateFormat(accidents.get(0).getSg_time(), DateUtil.DATE_DEFAULT_FORMAT));
            tmap.put("sgId", accidents.get(0).getId());
            tmap.put("remark", accidents.get(0).getRemark());
            tmap.put("sgType", accidents.get(0).getSg_type());
            retMap = this.generateMsg(tmap, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }
    
    @RequestMapping("querySgXwCheck")
    public @ResponseBody Map<String, Object> querySgXwCheck()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> mapWhere = Maps.newHashMap();
            List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
            String sgid = getParameter("sgid");
            mapWhere.put("sg_type", "1");// 重新编码
            List<AccidentBean> accidents = accidentService.queryAllAccidents(mapWhere, null);
            int i = 0;
            for (AccidentBean accidentBean : accidents)
            {
                i++;
                if(sgid.equals(accidentBean.getId())){
                    break;
                }
            }
               //根据i的值返回第几条数据
               //只取本篇上一篇及下一篇，若为第一篇则只返回下一篇，若为最后一篇则只返回上一篇的数据
                Map<String, Object> tmap = Maps.newHashMap();
                if(i==1){//i为1则为第一条数据，则只是返回第二条数据
                    //取第二篇数据
                    AccidentBean accidentBean =accidents.get(1);
                    tmap.put("title", accidentBean.getTitle());
                    tmap.put("sgTime", DateUtil.getDateFormat(accidentBean.getSg_time(), DateUtil.DATE_DEFAULT_FORMAT));
                    tmap.put("sgId", accidentBean.getId());
                    tmap.put("remark", accidentBean.getRemark());
                    tmap.put("djp", "第二篇");
                    retList.add(tmap);
                }else if(i==accidents.size()){//i为最后一个数据则只倒第二个数据
                    //取倒数第二篇数据
                    AccidentBean accidentBean =accidents.get(accidents.size()-2);
                    tmap.put("title", accidentBean.getTitle());
                    tmap.put("sgTime", DateUtil.getDateFormat(accidentBean.getSg_time(), DateUtil.DATE_DEFAULT_FORMAT));
                    tmap.put("sgId", accidentBean.getId());
                    tmap.put("remark", accidentBean.getRemark());
                    tmap.put("djp", "倒数第二篇");
                    retList.add(tmap);
                }else{//其他情况为上一条数据和下一篇数据
                    List<AccidentBean> acis = new ArrayList<AccidentBean>();
                    //取上一篇数据
                    AccidentBean accidentUp =accidents.get(i-2);
                    //取下一篇数据
                    AccidentBean accidentDown =accidents.get(i);
                    acis.add(accidentUp);
                    acis.add(accidentDown);
                    boolean j = true;
                    for(AccidentBean accident :acis ){
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("title", accident.getTitle());
                        map.put("sgTime", DateUtil.getDateFormat(accident.getSg_time(), DateUtil.DATE_DEFAULT_FORMAT));
                        map.put("sgId", accident.getId());
                        map.put("remark", accident.getRemark());
                        if(j){
                            map.put("djp", "上一篇");
                            j=false;
                        }else{
                            map.put("djp", "下一篇");
                        }
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

    @RequestMapping("queryMessageNums")
    public @ResponseBody Map<String, Object> queryMessageNums(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<MessageBean> accidents = messageService.queryMessage(mapWhere, null);
            retMap = this.generateMsg(accidents == null ? "0" : accidents.size(), true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    @RequestMapping("queryMessage")
    public @ResponseBody Map<String, Object> queryMessage(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<MessageBean> pageInfo = new PageInfo<MessageBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            List<MessageBean> accidents = messageService.queryMessage(mapWhere, pageInfo);
            pageInfo = new PageInfo<MessageBean>(accidents);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (MessageBean messageBean : accidents)
            {
                Map<String, Object> map = BeanUtil.toMap(messageBean);
                map.put("createTime",
                        DateUtil.getDateFormat(messageBean.getCreateTime(), DateUtil.DATE_DEFAULT_FORMAT));
                listMap.add(map);
            }
            retMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
        }
        return retMap;

    }

    @RequestMapping("updateMessage")
    public @ResponseBody Map<String, Object> updateMessage()
    {

        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("updatePara");
            List<Map<String, Object>> updateList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            messageService.updateMessageStatus(updateList);
            retMap = this.generateMsg("", true, "更新成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "更新失败!");
            return retMap;
        }

    }

    @RequestMapping("queryDclYhList")
    public @ResponseBody Map<String, Object> queryDclYhList(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<TroubleBean> pageInfo = new PageInfo<TroubleBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            List<Map<String, Object>> listMap = Lists.newArrayList();

            String sessionId = String.valueOf(request.getParameter("sessionId"));
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
//            if(userBean.getUserRoleCodes().indexOf("ngaqglry")== -1 && userBean.getUserRoleCodes().indexOf("xmbzygcs")== -1 && userBean.getUserRoleCodes().indexOf("xmbaqglry")== -1 && userBean.getUserRoleCodes().indexOf("fbaqglry")== -1)
//            {
//                retMap = layuiData(0, listMap);
//                return retMap;
//            }
            
            mapWhere.put("orgId", userBean.getOrgId());
            List<TroubleBean> troubleList = troubleService.queryDclYhList(mapWhere, pageInfo);

            pageInfo = new PageInfo<TroubleBean>(troubleList);

            for (TroubleBean trouble : troubleList)
            {
                if(!trouble.getSjrId().equals(userBean.getUser_id()) && !trouble.getJcrId().equals(userBean.getUser_id()))
                    continue;
                Map<String, Object> map = BeanUtil.toMap(trouble);
                if ("0".equals(trouble.getJclb()))
                {
                    map.put("jclbName", "日常安全检查");
                }
                else if ("1".equals(trouble.getJclb()))
                {
                    map.put("jclbName", "月度检查");
                }
                else if ("2".equals(trouble.getJclb()))
                {
                    map.put("jclbName", "季度检查");

                }
                else if ("3".equals(trouble.getJclb()))
                {
                    map.put("jclbName", "专项检查");
                }
                else if ("4".equals(trouble.getJclb()))
                {
                    map.put("jclbName", "领导带班检查");
                }

                if ("0".equals(trouble.getStatus()))
                {
                    map.put("statusName", "新建");
                }
                else if ("1".equals(trouble.getStatus()))
                {
                    map.put("statusName", "待响应");
                }
                else if ("2".equals(trouble.getStatus()))
                {
                    map.put("statusName", "待整改");

                }
                else if ("3".equals(trouble.getStatus()))
                {
                    map.put("statusName", "待复查");
                }
                else if ("4".equals(trouble.getStatus()))
                {
                    map.put("statusName", "已闭环");
                }
                map.put("zgsx", DateUtil.getDateFormat(trouble.getZgsx(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("updateTime",
                        DateUtil.getDateFormat(trouble.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime",
                        DateUtil.getDateFormat(trouble.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                listMap.add(map);
            }
            retMap = layuiData(pageInfo.getTotal(), listMap);

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return retMap;

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
            List<Map<String, Object>> listMap = Lists.newArrayList();

            String sessionId = String.valueOf(request.getParameter("sessionId"));
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            if(userBean.getUserRoleCodes().indexOf("ngaqglry")== -1 && userBean.getUserRoleCodes().indexOf("xmbzygcs")== -1 && userBean.getUserRoleCodes().indexOf("xmbaqglry")== -1 && userBean.getUserRoleCodes().indexOf("fbaqglry")== -1)
            {
                resMap = layuiData(0, listMap);
                return resMap;
            }
            
            //查询该用户是否有月计划，年计划的的权限
            List<PlanBean> planList = planService.queryAllPlans(mapWhere, pageInfo);
            pageInfo = new PageInfo<PlanBean>(planList);

            for (PlanBean bean : planList)
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                map.put("jhlx", "月计划");
                if (bean.getType().equals("Y"))
                {
                    map.put("jhlx", "年计划");
                }
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

    /**
     * delAccidentList:(这里用一句话描述这个方法的作用). <br/>
     * 参数：projName 项目名称
     * type 类型 0土建 1机电
     * isZd 是否站点 0区间 1站点
     * name 名称 isZd为1时，name为站点名称，如：苏洋站，注意福州要加后缀"站"。isZd为0时，name为区间范围，如：苏洋站-沙提站。
     * qjfw 区间范围编号
     * isZd为0时，qjfw为编号区间，如：苏洋站编号为1，沙提站编号为2，依次往下，点击苏洋-沙提之间，则编号为1-2。isZd为1时，为空字符串
     * 返回参数：title 名称
     * gcjz 工程进展
     * riskInfo 风险
     * ydcs 应对措施
     * 
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("queryZdRiskReport")
    public @ResponseBody Map<String, Object> delAccidentList()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> retParam = new HashMap<String, Object>();

            String projName = this.getParameter("projName");// 项目名称
            String type = this.getParameter("type");// 类型 0土建 1机电
            String isZd = this.getParameter("isZd");// 是否站点 0区间 1站点
            String name = this.getParameter("name");// isZd为1时，name为站点名称，如：苏洋站，注意福州要加后缀"站"。isZd为0时，name为区间范围，如：苏洋站-沙提站。
            String qjfw = this.getParameter("qjfw"); // isZd为0时，qjfw为编号区间，如：苏洋站编号为1，沙提站编号为2，依次往下，点击苏洋-沙提之间，则编号为1-2。isZd为1时，为空字符串
            retParam.put("title", name);
            retParam.put("gcjz", "暂无");
            retParam.put("riskInfo", "暂无");
            retParam.put("ydcs", "暂无");

            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("projName", projName);
            mapWhere.put("type", type);
            mapWhere.put("isZd", isZd);
            List<ZdRiskBean> zdRiskList = riskService.queryAllZdRisk(mapWhere, null);
            for (ZdRiskBean zdRisk : zdRiskList)
            {
                if ("1".equals(isZd))
                {// 站点，判断名称即可
                    if (zdRisk.getZdMc().equals(name))
                    {
                        retParam.clear();
                        retParam.put("title", name);
                        retParam.put("gcjz", zdRisk.getGcjz());
                        retParam.put("riskInfo", zdRisk.getRiskInfo());
                        retParam.put("ydcs", zdRisk.getYdcs());
                        break;
                    }
                }
                else
                {// 区间，判断qjfw是否在内，如在内，则说明找到
                    boolean isJj = this.judegeJj(zdRisk.getQjFw(), qjfw);
                    if (isJj)
                    {
                        retParam.clear();
                        retParam.put("title", name);
                        retParam.put("gcjz", zdRisk.getGcjz());
                        retParam.put("riskInfo", zdRisk.getRiskInfo());
                        retParam.put("ydcs", zdRisk.getYdcs());
                        break;
                    }
                }
            }
            retMap = this.generateMsg(retParam, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }
    }

    private boolean judegeJj(String qjfw, String newQjfw)
    {

        String[] qjfwAr = qjfw.split("-");
        String[] newQjfwAr = newQjfw.split("-");
        Integer newStart = Integer.valueOf(newQjfwAr[0]);
        Integer newEnd = Integer.valueOf(newQjfwAr[1]);
        Integer oldStart = Integer.valueOf(qjfwAr[0]);
        Integer oldEnd = Integer.valueOf(qjfwAr[1]);
        if (newStart >= oldStart && newStart <= oldEnd && newEnd >= oldStart && newEnd <= oldEnd)
            return true;
        return false;
    }

    // 根据单位和年查询图标的计划总额与实际总额
    @RequestMapping("queryAqysctrChart")
    public @ResponseBody Map<String, Object> queryAqysctrChart(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> list = planService.queryAqysctrChart(mapWhere);

            List<Map<String, Object>> retList = Lists.newArrayList();

            for (int month = 1; month <= 12; month++)
            {
                Map<String, Object> monthMap = Maps.newHashMap();
                monthMap.put("MONTH", String.valueOf(month));
                monthMap.put("PLANTOTAL", "0");
                monthMap.put("FINISHTOTAL", "0");
                for (Map<String, Object> money : list)
                {
                    String month_ = String.valueOf(money.get("MONTH"));
                    if (String.valueOf(month).equals(month_))
                    {
                        // 根据返回的金额数值，若其中出现null值，则给前端显示值为0
                        if (money.get("PLANTOTAL") != null)
                        {
                            monthMap.put("PLANTOTAL", money.get("PLANTOTAL"));
                        }

                        if (money.get("FINISHTOTAL") != null)
                        {
                            monthMap.put("FINISHTOTAL", money.get("FINISHTOTAL"));
                        }
                    }

                }
                retList.add(monthMap);
            }
            retMap = this.generateMsg(retList, true, "查询成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
        }
        return retMap;
    }

    // 显示在首页上安全生产投入的动态图表
    @RequestMapping("queryAqysctrDW")
    public @ResponseBody Map<String, Object> queryAqysctrDW()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            // 获取当前用户
            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            // 查询当前项目工程的简称及id
            String simpleName = userBean.getOrgName();
            // 本单位
            List<Map<String, Object>> list = Lists.newArrayList();
            Map<String, Object> map = Maps.newHashMap();
            //判断是否有组织机构，超级管理员是没有组织机构的
            if(StringHelper.isNotNullAndEmpty(simpleName)){
                String dqxmjc = organService.queryOneByName(simpleName).getName_cn_simple();
                String dqdwid = organService.queryOneByName(simpleName).getOrg_id();
               
                map.put("Org_id", dqdwid);
                map.put("sjdw", dqxmjc);
                list.add(map);
                // 子级单位
                List<OrganizationBean> orgChild = organService.queryOnlyChildOrg(userBean.getOrgId());
               
                for (OrganizationBean org : orgChild)
                {
                    Map<String, Object> tmap = Maps.newHashMap();
                    tmap.put("sjdw", org.getName_cn_simple());
                    tmap.put("Org_id", org.getOrg_id());
                    list.add(tmap);
                }
            }else{
                map.put("superadmin", "超级管理员");
                list.add(map);
            }
            retMap = this.generateMsg(list, true, "查询成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
        }
        return retMap;
    }

    public static void main(String args[])
    {
        System.out.println(String.valueOf(StringHelper.isNotNullAndEmpty(null)));
    }
}
