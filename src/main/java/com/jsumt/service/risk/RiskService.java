/**
 * Project Name:ECRMS
 * File Name:RiskService.java
 * Package Name:com.jsumt.service.risk
 * Date:2019年1月9日下午2:11:14
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.risk;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jsumt.mapper.risk.RiskMapper;
import com.jsumt.vo.risk.ZdRiskBean;

/**
 * ClassName:RiskService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2019年1月9日 下午2:11:14 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class RiskService
{

    @Autowired
    private RiskMapper riskMapper;

    public List<ZdRiskBean> queryAllZdRisk(Map<String, Object> mapWhere, PageInfo<ZdRiskBean> pageInfo)
    {

        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<ZdRiskBean> accidentList = riskMapper.queryAllZdRisk(mapWhere);
        return accidentList;
    }

    public void addZdRisk(ZdRiskBean zdRiskBean)
    {
        
        riskMapper.addZdRisk(zdRiskBean);
        
    }

    public void updateZdRisk(ZdRiskBean zdRiskBean)
    {
        
        riskMapper.updateZdRisk(zdRiskBean);
        
    }

    public void delZdRisk(List<Map<String, Object>> delelteList)
    {
        
        List<String> ids = Lists.newArrayList();
        for (Map<String, Object> map : delelteList)
        {
            String id = String.valueOf(map.get("id"));
            ids.add(id);
        }
        riskMapper.delZdRisk(ids);
    }

}
