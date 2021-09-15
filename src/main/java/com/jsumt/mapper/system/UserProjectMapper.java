/**
 * Project Name:ECRMS
 * File Name:UserProjectMapper.java
 * Package Name:com.jsumt.mapper.system
 * Date:2018年9月19日下午2:53:54
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.ProjectUserBean;
import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.RoleUserBean;
import com.jsumt.vo.system.UserBean;

/**
 * ClassName:UserProjectMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年9月19日 下午2:53:54 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface UserProjectMapper
{
    List<UserBean> queryUserByProject(Map<String, Object> queryMap);
    
    void delUserInProject(Map<String, Object> delMap);

    List<UserBean> qUserNotInProject(Map<String, Object> queryMap);

    void distributeUserInProject(List<ProjectUserBean> distributeList);

    void deleteURByProjectId(List<String> delRoleIds);

    List<RoleBean> queryProjectByUser(UserBean user);
}

