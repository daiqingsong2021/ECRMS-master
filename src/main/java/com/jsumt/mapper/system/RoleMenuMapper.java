/**
 * Project Name:ECRMS
 * File Name:RoleMenuMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年8月15日上午9:15:38
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.RoleMenuBean;

/**角色菜单mapper
 * ClassName:RoleMenuMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月15日 上午9:15:38 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface RoleMenuMapper {

    List<RoleMenuBean> queryRoleMenusByRoleId(String roleId);

    Integer menuRoleIsExist(Map<String, Object> roleMenuExists);

    void insertQx(List<Map<String, String>> menuRoleInsert);

    void updateQx(List<Map<String, String>> menuRoleUpdtae);
    
    void deleteRoleMenuByMenuId(List<String> delIdList);

    void deleteRMByRoleId(List<String> delRoleIds);

    List<Map<String, Object>> queryMenusByRole(List<RoleBean> roleList);
}

