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
public class FileNewBean
{
    private String id;
    private String fileUrl;
    private String scry;
    private String scryName;
    //文件分类
    private String fileType;
    private Date createTime;
    private Date updateTime;
    private String fileName;

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
