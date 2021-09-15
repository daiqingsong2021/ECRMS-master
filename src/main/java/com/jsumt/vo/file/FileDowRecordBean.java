/**
 * Project Name:ECRMS
 * File Name:FileDowRecordBean.java
 * Package Name:com.jsumt.vo.file
 * Date:2019年2月20日下午7:35:26
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.file;

import java.util.Date;

/**
 * ClassName:FileDowRecordBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2019年2月20日 下午7:35:26 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class FileDowRecordBean
{
    private String id;
    private String fileId;
    private String userCode;
    private String userName;
    private String orgName;
    private String roleName;
    private String downNums;
    private Date createTime;
    private Date updateTime;
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getFileId()
    {
        return fileId;
    }
    public void setFileId(String fileId)
    {
        this.fileId = fileId;
    }
    public String getUserCode()
    {
        return userCode;
    }
    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }
    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public String getOrgName()
    {
        return orgName;
    }
    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }
    public String getRoleName()
    {
        return roleName;
    }
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    public String getDownNums()
    {
        return downNums;
    }
    public void setDownNums(String downNums)
    {
        this.downNums = downNums;
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
    
    
    
}

