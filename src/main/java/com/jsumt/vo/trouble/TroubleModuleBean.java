/**
 * Project Name:ECRMS
 * File Name:TroubleModuleBean.java
 * Package Name:com.jsumt.vo.trouble
 * Date:2018年11月28日下午2:17:53
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.trouble;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:隐患模板Bean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年11月28日 下午2:17:53 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class TroubleModuleBean implements Serializable
{
    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String moduleName;
    private String creater;//创建者Id
    private String orgId;//创建者Id
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
    public String getModuleName()
    {
        return moduleName;
    }
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }
    public String getCreater()
    {
        return creater;
    }
    public void setCreater(String creater)
    {
        this.creater = creater;
    }
    
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
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

