/**
 * Project Name:ECRMS
 * File Name:RoleModuleBean.java
 * Package Name:com.jsumt.vo.Role
 * Date:2018年8月13日下午2:05:47
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.io.Serializable;

/**
 * 角色模块表
 * ClassName:RoleModuleBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月13日 下午2:05:47 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public class RoleModuleBean implements Serializable
{
    private String id;
    private String roleId;
    private String moduleId;
    private String canRead;
    private String canWrite;

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

    public String getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(String moduleId)
    {
        this.moduleId = moduleId;
    }
}
