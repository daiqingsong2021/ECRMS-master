/**
 * Project Name:ECRMS
 * File Name:AccidentMapper.java
 * Package Name:com.jsumt.mapper.accident
 * Date:2018年11月28日下午10:39:55
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.accident;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.accident.AccidentBean;

/**
 * ClassName:AccidentMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年11月28日 下午10:39:55 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface AccidentMapper
{
    
    List <AccidentBean> queryAllAccidents(Map<String, Object> mapWhere);
    
    void addAccident(AccidentBean bean);
    
    void delAccident(List<String>accidentId);
    
    void updateAccident(Map<String,Object> accidentUpdate);
    
    AccidentBean queryOneById(String id);

    List<Map<String, Object>> querySgReport();
    
}

