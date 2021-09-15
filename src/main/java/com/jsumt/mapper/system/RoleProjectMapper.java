/**
 * Project Name:ECRMS
 * File Name:RoleProjectMapper.java
 * Package Name:com.jsumt.mapper.system
 * Date:2018年8月22日下午3:33:10
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.RoleProjectBean;

/**
 * ClassName:RoleProjectMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月22日 下午3:33:10 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface RoleProjectMapper
{

    void updateQx(List<Map<String, String>> roleProjectUpdtae);

    void insertQx(List<Map<String, String>> roleProjectInsert);

    Integer roleProjectIsExist(Map<String, Object> roleProjectExist);

    List<RoleProjectBean> queryRoleProjectByRoleId(String roleId);

    void deleteRoleProjectByProjectId(List<String> deleteIds);

    void deleteRoleProjectByRoleId(List<String> delRoleIds);

}

