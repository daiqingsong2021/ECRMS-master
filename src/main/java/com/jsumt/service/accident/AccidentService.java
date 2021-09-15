/**
 * Project Name:ECRMS
 * File Name:AccidentService.java
 * Package Name:com.jsumt.service.accident
 * Date:2018年11月28日下午10:42:55
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.accident;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jsumt.mapper.accident.AccidentMapper;
import com.jsumt.mapper.accident.HurtPeopleMapper;
import com.jsumt.mapper.system.MessageMapper;
import com.jsumt.vo.accident.AccidentBean;

/**
 * ClassName:AccidentService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 下午10:42:55 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class AccidentService
{
    @Autowired
    private AccidentMapper accidentMapper;

    @Autowired
    private HurtPeopleMapper hurtPeopleMapper;

    public List<AccidentBean> queryAllAccidents(Map<String, Object> mapWhere, PageInfo<AccidentBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<AccidentBean> accidentList = accidentMapper.queryAllAccidents(mapWhere);
        return accidentList;
    }

    public void addAccident(AccidentBean bean)
    {
        accidentMapper.addAccident(bean);
    }

    // 删除事故表数据，在删之前首先删除其对应的附表（人员伤亡表）
    public void delAccident(List<Map<String, Object>> delList)
    {
        List<String> ids = Lists.newArrayList();
        for (Map<String, Object> accidentMap : delList)
        {
            String accidentId = String.valueOf(accidentMap.get("id"));
            ids.add(accidentId);
        }
        // 删除附表伤亡表 根据sgbid删除
        hurtPeopleMapper.delHurtPeoplesBySgbid(ids);
        // 删除主表事故表
        accidentMapper.delAccident(ids);
    }

    public void updateAccident(Map<String, Object> accidentUpdate)
    {
        accidentMapper.updateAccident(accidentUpdate);
    }

    public AccidentBean queryOneById(String id)
    {
        return accidentMapper.queryOneById(id);
    }

    public List<Map<String, Object>> querySgReport()
    {
        return accidentMapper.querySgReport();
    }

}
