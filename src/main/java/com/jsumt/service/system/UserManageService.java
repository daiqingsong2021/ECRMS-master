/**
 * Project Name:ECRMS
 * File Name:UserManagService.java
 * Package Name:com.jsumt.service.usermanagi
 * Date:2018年7月31日下午3:38:17
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jsumt.mapper.system.MessageMapper;
import com.jsumt.mapper.system.UserManageMapper;
import com.jsumt.mapper.system.UserProjectMapper;
import com.jsumt.mapper.system.UserRoleMapper;
import com.jsumt.service.file.FileService;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.system.ProjectUserBean;
import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.RoleUserBean;
import com.jsumt.vo.system.UserBean;

/**
 * 用户管理Service
 * ClassName:UserManagService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月31日 下午3:38:17 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class UserManageService
{
    @Autowired
    private UserManageMapper userManageMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private FileService fileService;

    public List<UserBean> queryUsers(Map<String, Object> queryMap)
    {
        List<UserBean> userList = userManageMapper.queryUsers(queryMap);
        return userList;
    }

    public void createUser(UserBean bean, String org_id)
    {
        Map<String, String> insertMap = new HashMap<String, String>();
        insertMap.put("ID", UUIDHexGenerator.generator());
        insertMap.put("ORG_ID", org_id);
        insertMap.put("USER_ID", bean.getUser_id());
        userManageMapper.insertUserInfo(bean);
        userManageMapper.insertOrgUser(insertMap);

    }

    public void deleteUsers(List<String> list)
    {
        if (!list.isEmpty())
        {
            // 删除用户与组织机构关联表的数据
            userManageMapper.deleteUserOrg(list);
            // 删除用户角色
            userManageMapper.deleteUserRole(list);
            // 删除用户对应的证书
            fileService.delFilesByBussIds(list);
            // 删除用户表的数据
            userManageMapper.deleteUsersInfo(list);
        }

    }

    public void editUser(UserBean bean)
    {
        userManageMapper.updateUser(bean);
    }

    public List<UserBean> queryUserByRole(Map<String, Object> queryMap)
    {
        List<UserBean> userList = userRoleMapper.queryUserByRole(queryMap);
        return userList;
    }

    public List<UserBean> qUserNotInRole(Map<String, Object> queryMap)
    {
        List<UserBean> userList = userRoleMapper.qUserNotInRole(queryMap);
        return userList;
    }

    public void distributeUserInRole(List<RoleUserBean> distributeList)
    {
        userRoleMapper.distributeUserInRole(distributeList);
    }

    public UserBean queryUserByCode(String userCode)
    {
        return userManageMapper.queryUserByCode(userCode);
    }

    public UserBean queryUserById(String userId)
    {
        return userManageMapper.queryUserById(userId);
    }

    public List<UserBean> queryUserByProject(Map<String, Object> queryMap)
    {
        List<UserBean> userList = userProjectMapper.queryUserByProject(queryMap);
        return userList;
    }

    public List<UserBean> qUserNotInProject(Map<String, Object> queryMap)
    {
        List<UserBean> userList = userProjectMapper.qUserNotInProject(queryMap);
        return userList;
    }

    public void distributeUserInProject(List<ProjectUserBean> distributeList)
    {

        userProjectMapper.distributeUserInProject(distributeList);
    }

    public void delUserInProject(Map<String, Object> delMap)
    {
        userProjectMapper.delUserInProject(delMap);
    }

}
