/**
 * Project Name:ECRMS
 * File Name:AccidentMapper.java
 * Package Name:com.jsumt.mapper.accident
 * Date:2018年11月28日下午10:39:55
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.mapper.risk;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.risk.ZdRiskBean;

/**
 * ClassName:AccidentMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 下午10:39:55 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public interface RiskMapper
{


    List<ZdRiskBean> queryAllZdRisk(Map<String, Object> mapWhere);
    
    
    void addZdRisk(ZdRiskBean bean);
    
    void delZdRisk(List<String> delIds);
    
    void updateZdRisk(ZdRiskBean bean);
}
