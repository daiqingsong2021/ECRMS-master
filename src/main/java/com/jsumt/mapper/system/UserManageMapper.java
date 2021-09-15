/**
 * Project Name:ECRMS
 * File Name:UserManageMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年7月31日下午3:55:52
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.UserBean;

/**
 * 用户管理mapper
 * ClassName:UserManageMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月31日 下午3:55:52 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public interface UserManageMapper
{

    List<UserBean> queryUsers(Map<String, Object> queryMap);

    void insertUserInfo(UserBean bean);

    void insertOrgUser(Map<String, String> insertMap);

    void deleteUsersInfo(List<String> list);

    void deleteUserOrg(List<String> list);

    void updateUser(UserBean bean);

    boolean deleteOrgUserByOrgIds(List<String> orgIds);

    void deleteUserRole(List<String> list);

    UserBean queryUserByCode(String userCode);

    UserBean queryUserById(String userId);

}
