/**
 * Project Name:ECRMS
 * File Name:MenuBean.java
 * Package Name:com.jsumt.vo.menuManage
 * Date:2018年8月7日上午11:15:33
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.file;

import java.util.Date;

/**
 * 文件Bean
 * ClassName:MenuBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月7日 上午11:15:33 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class FileBean
{
    private String id;
    private String fileUrl;
    private String scry;
    private String scryName;
    private String bussinessId;
    private Integer no;
    //文件分类
    private String fileType;
    private Date createTime;
    private Date updateTime;
    private String orgId;
    private String orgName;
    private String fileName;
    /**
     * 角色如果是南轨
       0：对内（默认值）
       1：对项目公司
       2：对外（只对自己层级下的）
       ===============
                 角色如果是项目公司
       0：对内（默认）
       2：对外
               角色如果是分包单位
      0：对内
     */
    private String status;
    private String fileCatalog;
    private String fileLx;
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getFileUrl()
    {
        return fileUrl;
    }
    public void setFileUrl(String fileUrl)
    {
        this.fileUrl = fileUrl;
    }
    public String getScry()
    {
        return scry;
    }
    public void setScry(String scry)
    {
        this.scry = scry;
    }
    
    public String getScryName()
    {
        return scryName;
    }
    public void setScryName(String scryName)
    {
        this.scryName = scryName;
    }
    public String getBussinessId()
    {
        return bussinessId;
    }
    public void setBussinessId(String bussinessId)
    {
        this.bussinessId = bussinessId;
    }
    public Integer getNo()
    {
        return no;
    }
    public void setNo(Integer no)
    {
        this.no = no;
    }
    public String getFileType()
    {
        return fileType;
    }
    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }
    public Date getCreateTime()
    {
        return createTime;
    }
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
    public Date getUpdateTime()
    {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }
    
    public String getOrgName()
    {
        return orgName;
    }
    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }
    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getFileCatalog()
    {
        return fileCatalog;
    }
    public void setFileCatalog(String fileCatalog)
    {
        this.fileCatalog = fileCatalog;
    }
    public String getFileLx()
    {
        return fileLx;
    }
    public void setFileLx(String fileLx)
    {
        this.fileLx = fileLx;
    }


    
}
