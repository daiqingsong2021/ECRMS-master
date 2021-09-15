/**
 * Project Name:ECRMS
 * File Name:ProjectController.java
 * Package Name:com.jsumt.controller.system
 * Date:2018年8月22日上午10:15:37
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.jsumt.common.BaseController;
import com.jsumt.service.system.ProjectService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.PageInfoUtiil;
import com.jsumt.util.PinyinUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.vo.system.OrganizationBean;
import com.jsumt.vo.system.ProjectBean;
import com.jsumt.vo.system.ProjectUserBean;
import com.jsumt.vo.system.RoleUserBean;
import com.jsumt.vo.system.UserBean;

/**
 * 项目控制层
 * ClassName:ProjectController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月22日 上午10:15:37 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
public class ProjectController extends BaseController
{
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserManageService userManageService;

    // 加载所有项目
    @RequestMapping("queryAllProjects")
    public @ResponseBody Map<String, Object> queryProjects(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 根据查询条件加载组织机构的树形结构，若没有条件则加载所以节点
        List<ProjectBean> projectList = projectService.queryProjects(mapWhere);
        resultMap.put("rows", projectList);
        return resultMap;
    }

    // 增加项目
    @RequestMapping("addProject")
    public @ResponseBody Map<String, Object> addProject(ProjectBean bean)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (bean.getProjectPId() == null)
            {
                bean.setProjectPId("0");
                bean.setProjectLayer("1");// 顶层节点1层
                bean.setIsLeaf("1");
                bean.setIconCls(IconClass.ICON_PROJECT.toString());
            }
            else
            {
                // 查询父节点层级
                ProjectBean parent = projectService.queryOneById(bean.getProjectPId());
                parent.setIsLeaf("0");// 更新父节点IS_LEAF
                parent.setIconCls(IconClass.ICON_PROJECT.toString());
                projectService.updateProject(parent);

                bean.setProjectLayer(String.valueOf(Integer.valueOf(parent.getProjectLayer()) + 1));
                bean.setIsLeaf("1");// 默认设为1
                bean.setIconCls(IconClass.ICON_EMPTY.toString());
            }
            // 查询最大序号
            String maxNo = projectService.queryMaxNo(bean.getProjectPId());
            if (StringUtils.isEmpty(maxNo))
                bean.setProjectNo("1");
            else
                bean.setProjectNo(String.valueOf(Integer.valueOf(maxNo) + 1));

            // 封装组织机构的ORG_ID 和 NAME_SPELL
            bean.setProjectId(UUIDHexGenerator.generator());
            projectService.addProject(bean);
            resultMap = generateMsg("", true, "增加成功");
        }
        catch (Exception e)
        {
            resultMap = generateMsg("", false, "增加失败");
        }
        return resultMap;
    }

    // 单个查询项目
    @RequestMapping("queryProjectData")
    public @ResponseBody Map<String, Object> queryOneById(String projectId)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (StringHelper.isNotNullAndEmpty(projectId))
            {
                ProjectBean bean = projectService.queryOneById(projectId);
                resultMap = this.generateMsg(bean, true, "查询成功");

            }
            else
            {
                resultMap = this.generateMsg("", false, "查询失败");
            }
        }
        catch (Exception e)
        {
            resultMap = this.generateMsg("", false, "查询失败");
        }
        return resultMap;
    }

    // 修改项目信息
    @RequestMapping("updateProject")
    public @ResponseBody Map<String, Object> updateProject(ProjectBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            projectService.updateProject(bean);
            retMap = this.generateMsg("", true, "修改成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "修改失败!");
            return retMap;
        }
    }

    // 删除项目
    @RequestMapping("delProjects")
    public @ResponseBody Map<String, Object> delProjects(String projectId)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (StringHelper.isNotNullAndEmpty(projectId))
            {
                projectService.deleteByPrimaryKey(projectId);
                resultMap = this.generateMsg("", true, "删除成功");

            }
        }
        catch (Exception e)
        {
            resultMap = this.generateMsg("", false, "删除失败");
        }
        return resultMap;
    }

    // 查询用户（关联项目）
    @RequestMapping("qUserByProject")
    public @ResponseBody Map<String, Object> qUserByProject()
    {
        PageInfo<UserBean> pageInfo = new PageInfo<UserBean>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<UserBean> userList = new ArrayList<UserBean>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String UserCodePa = this.getParameter("usercode");
            String UserNamePa = this.getParameter("username");
            String projectId = this.getParameter("projectId");
            queryMap.put("userCode", UserCodePa);
            queryMap.put("userName", UserNamePa);
            queryMap.put("projectId", projectId);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<UserBean> returnPageList = new Page<UserBean>();
            // 查询用户对象
            userList = userManageService.queryUserByProject(queryMap);

            returnPageList = new PageInfoUtiil<UserBean>().generatePageList(pageInfo, userList);
            pageInfo = new PageInfo<UserBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }

    // 查询用户（关联项目）
    @RequestMapping("qUserNotInProject")
    public @ResponseBody Map<String, Object> qUserNotInProject()
    {
        PageInfo<UserBean> pageInfo = new PageInfo<UserBean>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<UserBean> userList = new ArrayList<UserBean>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String UserCodePa = this.getParameter("usercode");
            String UserNamePa = this.getParameter("username");
            String projectId = this.getParameter("projectId");
            queryMap.put("userCode", UserCodePa);
            queryMap.put("userName", UserNamePa);
            queryMap.put("projectId", projectId);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<UserBean> returnPageList = new Page<UserBean>();
            // 查询用户对象
            userList = userManageService.qUserNotInProject(queryMap);

            returnPageList = new PageInfoUtiil<UserBean>().generatePageList(pageInfo, userList);
            pageInfo = new PageInfo<UserBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }

    // 为用户分配角色
    @RequestMapping("disUsersInProject")
    public @ResponseBody Map<String, Object> disUsersInProject(String disList, String projectId)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> idList = JsonHelper.fromJsonWithGson(disList, List.class);
        List<ProjectUserBean> distributeList = new ArrayList<ProjectUserBean>();
        try
        {
            for (String userId : idList)
            {
                // 封装项目用户对象
                ProjectUserBean bean = new ProjectUserBean();
                bean.setId(UUIDHexGenerator.generator());
                bean.setUserId(userId);
                bean.setProjectId(projectId);
                distributeList.add(bean);
            }

            userManageService.distributeUserInProject(distributeList);
            retMap = this.generateMsg("", true, "分配成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "分配失败!");
            return retMap;

        }
    }
    
    
    @RequestMapping("delUsersInProject")
    public @ResponseBody Map<String, Object> delUsersInProject(String delList, String projectId)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Map<String, Object> delMap = new HashMap<String, Object>();
        List<String> idList = JsonHelper.fromJsonWithGson(delList, List.class);
        try
        {
            for (String userId : idList)
            { 
                delMap.put("projectId", projectId);
                delMap.put("userId", userId);
                // 解除该用户的角色（删除用户角色表的数据）
                userManageService.delUserInProject(delMap);
            }

            retMap = this.generateMsg("", true, "删除成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "删除失败!");
            return retMap;
        }
        
        
    }
}
