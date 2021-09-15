/**
 * Project Name:ECRMS
 * File Name:HurtPeopleController.java
 * Package Name:com.jsumt.controller.accident
 * Date:2018年12月10日上午10:54:31
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.accident;

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
import com.jsumt.common.BaseController;
import com.jsumt.service.accident.HurtPeopleService;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.accident.HurtPeopleBean;

/**
 * ClassName:HurtPeopleController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月10日 上午10:54:31 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */

@Controller
@RequestMapping("accident")
public class HurtPeopleController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(HurtPeopleController.class);

    @Autowired
    private HurtPeopleService hurtPeopleService;

    // 查询所有伤亡人员信息
    @RequestMapping("queryAllHurtPeoples")
    public @ResponseBody Map<String, Object> queryAllHurtPeoples(@RequestParam Map<String, Object> mapWhere)
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

            PageInfo<HurtPeopleBean> pageInfo = new PageInfo<HurtPeopleBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            // 获取人员伤亡查询信息
            // String sg_type=this.getParameter("sg_type");
            mapWhere.put("user_name", this.getParameter("user_name"));
            mapWhere.put("status", this.getParameter("status"));
            mapWhere.put("sgbid", this.getParameter("sgbid"));

            List<HurtPeopleBean> HurtPeopleList = hurtPeopleService.queryAllHurtPeoples(mapWhere, pageInfo);
            pageInfo = new PageInfo<HurtPeopleBean>(HurtPeopleList);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (HurtPeopleBean hurtPeople : HurtPeopleList)
            {
                Map<String, Object> map = BeanUtil.toMap(hurtPeople);
                if (StringHelper.isNotNullAndEmpty(String.valueOf(hurtPeople.getBirthday())))
                {
                    map.put("birthday", DateUtil.getDateFormat(hurtPeople.getBirthday(), DateUtil.DATE_DEFAULT_FORMAT));
                }

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

    // 增加伤亡人员信息
    @RequestMapping("addHurtPeoples")
    public @ResponseBody Map<String, Object> addHurtPeople(HurtPeopleBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            // 新建事故数据
            String id = UUIDHexGenerator.generator();
            String sgbid = this.getParameter("sgbid");
            // System.out.println(sgbid);
            bean.setId(id);
            bean.setSgbid(sgbid);
            bean.setCreateTime(new Date());
            bean.setUpdateTime(new Date());
            hurtPeopleService.addHurtPeople(bean);
            return retMap = this.generateMsg("", true, "增加成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加失败!");
            return retMap;
        }
    }

    // 修改伤亡人员信息
    @RequestMapping("editHurtPeoples")
    public @ResponseBody Map<String, Object> updateHurtPeople(HurtPeopleBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            hurtPeopleService.updateHurtPeople(bean);
            return retMap = this.generateMsg("", true, "修改成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改失败!");
            return retMap;
        }

    }

    // 删除伤亡人员信息
    @RequestMapping("delHurtPeoples")
    public @ResponseBody Map<String, Object> delHurtPeoples()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            hurtPeopleService.delHurtPeoples(delelteList);
            retMap = this.generateMsg("", true, "删除人员信息成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除人员信息失败!");
            return retMap;
        }
    }

}
