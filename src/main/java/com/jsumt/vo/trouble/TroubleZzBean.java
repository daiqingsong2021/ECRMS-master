/**
 * Project Name:ECRMS
 * File Name:TroubleBean.java
 * Package Name:com.jsumt.vo.trouble
 * Date:2018年11月30日下午2:39:28
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.trouble;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:隐患追踪Bean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月30日 下午2:39:28 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class TroubleZzBean implements Serializable
{

    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     * 
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String yhId;
    private String type;// 0新建 1整改 2复查 3闭环 5响应
    private String cs;// 整改的次数与复查的次数
    private String zgjf;// 整改经费

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

    public String getYhId()
    {
        return yhId;
    }

    public void setYhId(String yhId)
    {
        this.yhId = yhId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCs()
    {
        return cs;
    }

    public void setCs(String cs)
    {
        this.cs = cs;
    }

    public String getZgjf()
    {
        return zgjf;
    }

    public void setZgjf(String zgjf)
    {
        this.zgjf = zgjf;
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
