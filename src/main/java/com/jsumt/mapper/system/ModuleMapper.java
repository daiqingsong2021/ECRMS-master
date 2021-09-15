/**
 * Project Name:ECRMS
 * File Name:ModuleMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年8月7日上午8:59:05
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.ModuleBean;

/**
 * ClassName:ModuleMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月7日 上午8:59:05 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface ModuleMapper {

    List<ModuleBean> queryModules(Map<String, Object> queryMap);

    void addModule(ModuleBean bean);

    Integer createOrderno();

    void delModules(List<String> delelteList);

    void updateModule(ModuleBean bean);

}

