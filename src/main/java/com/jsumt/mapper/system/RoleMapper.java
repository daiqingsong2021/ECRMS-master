/**
 * Project Name:ECRMS
 * File Name:RoleMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年8月9日上午9:58:38
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.RoleBean;

/**
 * 角色管理Mapper层
 * ClassName:RoleMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月9日 上午9:58:38 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface RoleMapper {

    List<RoleBean> queryAllRoles(Map<String, Object> mapWhere);

    RoleBean queryRoleById(String role_id);

    String queryMaxNo(String role_pid);

    void addRole(RoleBean bean);

    void updateRole(RoleBean parentBean);

    void deleteByPrimaryKey(List<String> delRoleIds);

    //用来查询已经分配给用户分配的角色
    List<RoleBean> queryRoleInUser(Map<String, Object> queryMap);
    
    //用来查询没有被分配的角色
    List<RoleBean> queryRoleNotInUser(Map<String, Object> queryMap);

}

