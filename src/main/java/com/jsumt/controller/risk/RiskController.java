/**
 * Project Name:ECRMS
 * File Name:Risk.java
 * Package Name:com.jsumt.controller.risk
 * Date:2019年1月9日下午1:58:59
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.risk;

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
import com.jsumt.service.risk.RiskService;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.risk.ZdRiskBean;

/**
 * ClassName:Risk <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2019年1月9日 下午1:58:59 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("risk")
public class RiskController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(RiskController.class);

    @Autowired
    private RiskService riskService;

    // 查询所有事故
    @RequestMapping("queryRiskList")
    public @ResponseBody Map<String, Object> queryRiskList(@RequestParam Map<String, Object> mapWhere)
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

            PageInfo<ZdRiskBean> pageInfo = new PageInfo<ZdRiskBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            // 获取事故
            mapWhere.put("projName", this.getParameter("projName"));// 重新编码

            List<ZdRiskBean> zdRiskList = riskService.queryAllZdRisk(mapWhere, pageInfo);
            pageInfo = new PageInfo<ZdRiskBean>(zdRiskList);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (ZdRiskBean zdRisk : zdRiskList)
            {
                Map<String, Object> map = BeanUtil.toMap(zdRisk);
                map.put("createTime", DateUtil.getDateFormat(zdRisk.getCreateTime(), DateUtil.DATE_DEFAULT_FORMAT));
                map.put("typeText", "机电");
                if ("0".equals(zdRisk.getType()))
                    map.put("typeText", "土建");
                map.put("isZdText", "站点");
                if ("0".equals(zdRisk.getIsZd()))
                    map.put("isZdText", "区间");

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

    @RequestMapping("addZdRisk")
    public @ResponseBody Map<String, Object> addZdRisk(ZdRiskBean zdRiskBean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String isZd = zdRiskBean.getIsZd();
            String beginZd = this.getParameter("beginZd");
            String endZd = this.getParameter("endZd");
            String beginZdName = this.getParameter("beginZdName");
            String endZdName = this.getParameter("endZdName");
            if ("0".equals(isZd))
            {// 区间
                zdRiskBean.setZdMc(beginZdName + "-" + endZdName);
                Integer beginZdIndex = Integer.valueOf(beginZd);
                Integer endZdIndex = Integer.valueOf(endZd);
                zdRiskBean.setQjFw(beginZd + "-" + endZd);
                zdRiskBean.setZdMcNum(beginZd + "-" + endZd);
                if (beginZdIndex > endZdIndex)
                {
                    zdRiskBean.setQjFw(endZd + "-" + beginZd);
                }

                // 判断当前projName,type,isZd下的区间判断是否有交集，如果有交集，则增加失败
                Map<String, Object> mapWhere = Maps.newHashMap();
                mapWhere.put("projName", zdRiskBean.getProjName());
                mapWhere.put("type", zdRiskBean.getType());
                mapWhere.put("isZd", zdRiskBean.getIsZd());

                List<ZdRiskBean> zdRiskList = riskService.queryAllZdRisk(mapWhere, null);
                for (ZdRiskBean bean : zdRiskList)
                {
                    String qjfw = bean.getQjFw();
                    String newQjfw = zdRiskBean.getQjFw();

                    boolean isJj = this.judegeJj(qjfw, newQjfw);
                    if (isJj)
                    {
                        retMap = this.generateMsg("不能与原有区间冲突!", false, "新增失败!");
                        return retMap;
                    }
                }
            }
            else
            {
                // 判断当前projName,type,isZd下是否存在相同的站点，如存在，则增加失败
                Map<String, Object> mapWhere = Maps.newHashMap();
                mapWhere.put("projName", zdRiskBean.getProjName());
                mapWhere.put("type", zdRiskBean.getType());
                mapWhere.put("isZd", zdRiskBean.getIsZd());
                mapWhere.put("zdMc", zdRiskBean.getZdMc());
                List<ZdRiskBean> zdRiskList = riskService.queryAllZdRisk(mapWhere, null);
                if (!zdRiskList.isEmpty())
                {
                    retMap = this.generateMsg("已存在相同的站点!", false, "新增失败!");
                    return retMap;
                }
            }
            zdRiskBean.setId(UUIDHexGenerator.generator());
            zdRiskBean.setCreateTime(new Date());
            zdRiskBean.setUpdateTime(new Date());
            riskService.addZdRisk(zdRiskBean);
            retMap = this.generateMsg("", true, "新增成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "新增失败!");
        }
        return retMap;
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

    @RequestMapping("updateZdRisk")
    public @ResponseBody Map<String, Object> updateZdRisk(ZdRiskBean zdRiskBean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String isZd = zdRiskBean.getIsZd();
            String beginZd = this.getParameter("beginZd");
            String endZd = this.getParameter("endZd");
            String beginZdName = this.getParameter("beginZdName");
            String endZdName = this.getParameter("endZdName");
            if ("0".equals(isZd))
            {// 区间
                zdRiskBean.setZdMc(beginZdName + "-" + endZdName);
                Integer beginZdIndex = Integer.valueOf(beginZd);
                Integer endZdIndex = Integer.valueOf(endZd);
                zdRiskBean.setQjFw(beginZd + "-" + endZd);
                zdRiskBean.setZdMcNum(beginZd + "-" + endZd);
                if (beginZdIndex > endZdIndex)
                {
                    zdRiskBean.setQjFw(endZd + "-" + beginZd);
                }

                // 判断当前projName,type,isZd下的区间判断是否有交集，如果有交集，则增加失败
                Map<String, Object> mapWhere = Maps.newHashMap();
                mapWhere.put("projName", zdRiskBean.getProjName());
                mapWhere.put("type", zdRiskBean.getType());
                mapWhere.put("isZd", zdRiskBean.getIsZd());

                List<ZdRiskBean> zdRiskList = riskService.queryAllZdRisk(mapWhere, null);
                for (ZdRiskBean bean : zdRiskList)
                {
                    String qjfw = bean.getQjFw();
                    String newQjfw = zdRiskBean.getQjFw();

                    boolean isJj = this.judegeJj(qjfw, newQjfw);
                    if (isJj && !bean.getId().equals(zdRiskBean.getId()))
                    {// 不为自己的是否存在

                        retMap = this.generateMsg("不能与原有区间冲突!", false, "新增失败!");
                        return retMap;
                    }
                }

            }
            else
            {// 站点
                zdRiskBean.setQjFw("");
                zdRiskBean.setZdMcNum("");

                // 判断当前projName,type,isZd下是否存在相同的站点，如存在，则增加失败
                Map<String, Object> mapWhere = Maps.newHashMap();
                mapWhere.put("projName", zdRiskBean.getProjName());
                mapWhere.put("type", zdRiskBean.getType());
                mapWhere.put("isZd", zdRiskBean.getIsZd());
                mapWhere.put("zdMc", zdRiskBean.getZdMc());
                List<ZdRiskBean> zdRiskList = riskService.queryAllZdRisk(mapWhere, null);
                if (!zdRiskList.isEmpty())
                {
                    // 是否有非自己的
                    for (ZdRiskBean bean : zdRiskList)
                    {
                        if (!bean.getId().equals(zdRiskBean.getId()))
                        {
                            retMap = this.generateMsg("已存在相同的站点!", false, "新增失败!");
                            return retMap;
                        }
                    }

                }
            }
            riskService.updateZdRisk(zdRiskBean);
            retMap = this.generateMsg("", true, "修改成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改失败!");
        }
        return retMap;
    }

    @RequestMapping("delZdRisk")
    public @ResponseBody Map<String, Object> delAccidentList()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            riskService.delZdRisk(delelteList);
            retMap = this.generateMsg("", true, "删除成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除失败!");
            return retMap;
        }
    }

    public static void main(String args[])
    {

        try
        {
            DateUtil.formatDate(null, "sadasdsadas");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error(e.getStackTrace()[0].toString());
        }

    }
}
