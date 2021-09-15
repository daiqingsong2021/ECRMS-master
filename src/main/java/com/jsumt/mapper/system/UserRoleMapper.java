/**
 * Project Name:ECRMS
 * File Name:UserRoleMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年8月10日下午2:04:35
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.RoleUserBean;
import com.jsumt.vo.system.UserBean;

/**
 * 角色与用户关联表  Mapper
 * ClassName:UserRoleMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月10日 下午2:04:35 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface UserRoleMapper {

    List<UserBean> queryUserByRole(Map<String, Object> queryMap);

    void delUserInRole(Map<String, Object> delMap);

    List<UserBean> qUserNotInRole(Map<String, Object> queryMap);

    void distributeUserInRole(List<RoleUserBean> distributeList);

    void deleteURByRoleId(List<String> delRoleIds);

    List<RoleBean> queryRoleByUser(UserBean user);


}

