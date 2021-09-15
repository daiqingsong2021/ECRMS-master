/**
 * Project Name:ECRMS
 * File Name:RoleProjectBean.java
 * Package Name:com.jsumt.vo.system
 * Date:2018年8月22日下午3:30:03
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.io.Serializable;

/**
 * 角色项目表
 * ClassName:RoleProjectBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月22日 下午3:30:03 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public class RoleProjectBean implements Serializable
{
    private String id;
    private String roleId;
    private String projectId;
    private String canRead;
    private String canWrite;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getCanRead()
    {
        return canRead;
    }

    public void setCanRead(String canRead)
    {
        this.canRead = canRead;
    }

    public String getCanWrite()
    {
        return canWrite;
    }

    public void setCanWrite(String canWrite)
    {
        this.canWrite = canWrite;
    }
}
