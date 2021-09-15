/**
 * Project Name:ECRMS
 * File Name:ProjectService.java
 * Package Name:com.jsumt.service.system
 * Date:2018年8月22日上午10:18:13
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.system;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.system.ProjectMapper;
import com.jsumt.mapper.system.RoleProjectMapper;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.util.StringHelper;
import com.jsumt.vo.system.ProjectBean;
import com.jsumt.vo.system.RoleProjectBean;

/**
 * 项目Service层
 * ClassName:ProjectService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月22日 上午10:18:13 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class ProjectService
{
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private RoleProjectMapper roleProjectMapper;
    
    public List<ProjectBean> queryProjects(Map<String, Object> mapWhere)
    {
        List<ProjectBean> projectList = projectMapper.queryProjects(mapWhere);
        return projectList;
    }

    public ProjectBean queryOneById(String projectPId)
    {

        return projectMapper.queryOneById(projectPId);
    }

    public void updateProject(ProjectBean parent)
    {
        projectMapper.updateProject(parent);

    }

    public String queryMaxNo(String projectPId)
    {
        String maxNo = "";
        maxNo = projectMapper.queryMaxNo(projectPId);
        return maxNo;

    }

    public void addProject(ProjectBean bean)
    {

        projectMapper.addProject(bean);
    }

    public void deleteByPrimaryKey(String projectId)
    {

        // (参数id可能是父节点，其下有多个子节点)新建集合存贮所有要删除的id
        List<String> deleteIds = Lists.newArrayList();

        ProjectBean projectBean = projectMapper.queryOneById(projectId);
        String parentId = projectBean.getProjectPId();

        // 查询所有组织机构对象，找出参数id以及其下的所以子节点的组织机构对象存贮到集合
        List<ProjectBean> projectList = projectMapper.queryProjects(null);
        for (ProjectBean bean : projectList)
        {
            if (projectId.equals(bean.getProjectId()))
            {
                deleteIds.add(bean.getProjectId());
                findChildOrgan(deleteIds, projectList, bean.getProjectId());
            }
        }
        
      //删除角色工程关联表数据
        roleProjectMapper.deleteRoleProjectByProjectId(deleteIds);
        //删除工程
        projectMapper.deloProjects(deleteIds);

        // 更新父节点IS_LEAF 
        if (!"0".equals(parentId))
        {
            ProjectBean parent = projectMapper.queryOneById(parentId);
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("projectPId", parent.getProjectId());
            if (projectMapper.queryProjects(mapWhere).isEmpty())
            {// 如果不存在子节点
                parent.setIsLeaf("1");// 更新父节点IS_LEAF
               parent.setIconCls(IconClass.ICON_EMPTY.toString());
                projectMapper.updateProject(parent);
            }

        }

    }

    public void findChildOrgan(List<String> deleteIds, List<ProjectBean> projectList, String ProjectPid)
    {
        for (ProjectBean bean : projectList)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getProjectPId()) && ProjectPid.equals(bean.getProjectPId()))
            {
                deleteIds.add(bean.getProjectId());
                findChildOrgan(deleteIds, projectList, bean.getProjectId());
            }
        }
    }

    public Integer roleProjectIsExist(Map<String, Object> roleProjectExist)
    {
        
        return roleProjectMapper.roleProjectIsExist(roleProjectExist);
    }

    public void insertQx(List<Map<String, String>> roleProjectInsert)
    {
        
        roleProjectMapper.insertQx(roleProjectInsert);
        
    }

    public void updateQx(List<Map<String, String>> roleProjectUpdtae)
    {
        
        roleProjectMapper.updateQx(roleProjectUpdtae);
        
    }

    public List<RoleProjectBean> queryRoleProjectByRoleId(String roleId)
    {
        
        return roleProjectMapper.queryRoleProjectByRoleId(roleId);

    }

}
