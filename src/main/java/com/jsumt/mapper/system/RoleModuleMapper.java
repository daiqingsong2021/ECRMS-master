/**
 * Project Name:ECRMS
 * File Name:RoleModuleMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年8月13日上午10:39:46
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.RoleModuleBean;

/**
 * 角色模块表Mapper
 * ClassName:RoleModuleMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月13日 上午10:39:46 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public interface RoleModuleMapper
{

    List<RoleModuleBean> queryRoleModulesByRoleId(String queryId);

    void deleteByFKeys(Map<String, Object> paraMap);

    void insertByBean(RoleModuleBean bean);

    void deleteRoleModuleByModuleId(List<String> delelteList);

    Integer moduleRoleIsExist(Map<String, Object> roleModuleExist);

    void insertQx(List<Map<String, String>> roleModuleInsert);

    void updateQx(List<Map<String, String>> roleModuleUpdtae);

    void deleteRMByRoleId(List<String> delRoleIds);

    List<Map<String, Object>> queryModulesByRole(List<RoleBean> roleIds);

}
