/**
 * Project Name:ECRMS
 * File Name:ProjectBean.java
 * Package Name:com.jsumt.vo.system
 * Date:2018年8月22日上午10:13:39
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.io.Serializable;

/**
 * 项目表
 * ClassName:ProjectBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月22日 上午10:13:39 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public class ProjectBean implements Serializable
{
    private String projectId;
    private String projectPId;
    private String projectCode;
    private String projectName;
    private String projectUnit;
    private String linkMan;
    private String remark;
    private String isLeaf; 
    private String projectNo;
    private String projectLayer;
    private String canRead;
    public String getIconCls()
    {
        return iconCls;
    }
    public void setIconCls(String iconCls)
    {
        this.iconCls = iconCls;
    }
    private String canWrite;
    private String linkTel;
    private String iconCls;
    public String getLinkTel()
    {
        return linkTel;
    }
    public void setLinkTel(String linkTel)
    {
        this.linkTel = linkTel;
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
    public String getProjectId()
    {
        return projectId;
    }
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    public String getProjectPId()
    {
        return projectPId;
    }
    public void setProjectPId(String projectPId)
    {
        this.projectPId = projectPId;
    }
    public String getProjectCode()
    {
        return projectCode;
    }
    public void setProjectCode(String projectCode)
    {
        this.projectCode = projectCode;
    }
    public String getProjectName()
    {
        return projectName;
    }
    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }
    public String getProjectUnit()
    {
        return projectUnit;
    }
    public void setProjectUnit(String projectUnit)
    {
        this.projectUnit = projectUnit;
    }
    public String getLinkMan()
    {
        return linkMan;
    }
    public void setLinkMan(String linkMan)
    {
        this.linkMan = linkMan;
    }
    public String getRemark()
    {
        return remark;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    public String getIsLeaf()
    {
        return isLeaf;
    }
    public void setIsLeaf(String isLeaf)
    {
        this.isLeaf = isLeaf;
    }
    public String getProjectNo()
    {
        return projectNo;
    }
    public void setProjectNo(String projectNo)
    {
        this.projectNo = projectNo;
    }
    public String getProjectLayer()
    {
        return projectLayer;
    }
    public void setProjectLayer(String projectLayer)
    {
        this.projectLayer = projectLayer;
    }
 
   
}
