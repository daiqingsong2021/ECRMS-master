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
 * ClassName:隐患Bean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月30日 下午2:39:28 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class TroubleBean implements Serializable
{

    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     * 
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Date zgsx;// 整改时限
    private String gcmc;//站点标段
    private String yhbh;//隐患编号
    private String jcdw;
    private String jcr;
    private String jcdwId;
    private String jcrId;
    private String sjdw;
    private String sjdwId;
    private String sjr;
    private String sjrId;
    private String jclb;// '0日常安全检查，1月度检查，2季度检查，3专项检查，4领导带班检查'
    private String status;// 0新建 1待响应 2待整改 3待复查 4已闭环
    private String orgId;
    private String zgzrr;
    private String zgzrrId;
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getGcmc()
    {
        return gcmc;
    }

    public void setGcmc(String gcmc)
    {
        this.gcmc = gcmc;
    }
    
    public String getYhbh()
    {
        return yhbh;
    }

    public void setYhbh(String yhbh)
    {
        this.yhbh = yhbh;
    }

    public Date getZgsx()
    {
        return zgsx;
    }

    public void setZgsx(Date zgsx)
    {
        this.zgsx = zgsx;
    }

    public String getJcdw()
    {
        return jcdw;
    }

    public void setJcdw(String jcdw)
    {
        this.jcdw = jcdw;
    }

    public String getJcr()
    {
        return jcr;
    }

    public void setJcr(String jcr)
    {
        this.jcr = jcr;
    }

    public String getJcdwId()
    {
        return jcdwId;
    }

    public void setJcdwId(String jcdwId)
    {
        this.jcdwId = jcdwId;
    }

    public String getJcrId()
    {
        return jcrId;
    }

    public void setJcrId(String jcrId)
    {
        this.jcrId = jcrId;
    }

    public String getSjdw()
    {
        return sjdw;
    }

    public void setSjdw(String sjdw)
    {
        this.sjdw = sjdw;
    }

    public String getSjdwId()
    {
        return sjdwId;
    }

    public void setSjdwId(String sjdwId)
    {
        this.sjdwId = sjdwId;
    }

    public String getSjr()
    {
        return sjr;
    }

    public void setSjr(String sjr)
    {
        this.sjr = sjr;
    }

    public String getSjrId()
    {
        return sjrId;
    }

    public void setSjrId(String sjrId)
    {
        this.sjrId = sjrId;
    }

    public String getJclb()
    {
        return jclb;
    }

    public void setJclb(String jclb)
    {
        this.jclb = jclb;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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

    public String getZgzrr()
    {
        return zgzrr;
    }

    public void setZgzrr(String zgzrr)
    {
        this.zgzrr = zgzrr;
    }

    public String getZgzrrId()
    {
        return zgzrrId;
    }

    public void setZgzrrId(String zgzrrId)
    {
        this.zgzrrId = zgzrrId;
    }
    

}
