package com.jsumt.vo.risk;

import java.util.Date;

/**
 * ClassName:AccidentBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 上午11:23:04 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */

public class ZdRiskBean
{

    private String id;
    private String gcjz;
    private String riskInfo;
    private String ydcs;
    private String projName;
    private String type;
    private String isZd;
    private String zdMc;
    private String qjFw;
    private String zdMcNum;

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

    public String getGcjz()
    {
        return gcjz;
    }

    public void setGcjz(String gcjz)
    {
        this.gcjz = gcjz;
    }

    public String getRiskInfo()
    {
        return riskInfo;
    }

    public void setRiskInfo(String riskInfo)
    {
        this.riskInfo = riskInfo;
    }

    public String getYdcs()
    {
        return ydcs;
    }

    public void setYdcs(String ydcs)
    {
        this.ydcs = ydcs;
    }

    public String getProjName()
    {
        return projName;
    }
    

    public String getZdMcNum()
    {
        return zdMcNum;
    }

    public void setZdMcNum(String zdMcNum)
    {
        this.zdMcNum = zdMcNum;
    }

    public void setProjName(String projName)
    {
        this.projName = projName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getIsZd()
    {
        return isZd;
    }

    public void setIsZd(String isZd)
    {
        this.isZd = isZd;
    }

    public String getZdMc()
    {
        return zdMc;
    }

    public void setZdMc(String zdMc)
    {
        this.zdMc = zdMc;
    }

    public String getQjFw()
    {
        return qjFw;
    }

    public void setQjFw(String qjFw)
    {
        this.qjFw = qjFw;
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
