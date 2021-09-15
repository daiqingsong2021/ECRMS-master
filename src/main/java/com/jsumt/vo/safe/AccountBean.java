/**
 * Project Name:ECRMS
 * File Name:AccountBean.java
 * Package Name:com.jsumt.vo.safe
 * Date:2019年1月4日上午9:20:05
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.safe;

import java.util.Date;

/**安全生产投入台账
 * ClassName:AccountBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2019年1月4日 上午9:20:05 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class AccountBean
{
   private String id;
   private String tbdw;//填报单位
   private String tbdwid;//填报单位id
   private String tbr;//填报人
   private String year;
   private String month;
   private String status;//状态0为未发布，1为已发布
   private String trjhTotal;//计划总额
   private String trjhTotalFinish;//实际总额
   private String trjhFypc;//费用偏差
   private String parentId;//父台账ID
   private String planId;//计划ID
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
    public String getTbdw()
    {
        return tbdw;
    }
    public void setTbdw(String tbdw)
    {
        this.tbdw = tbdw;
    }
    public String getTbdwid()
    {
        return tbdwid;
    }
    public void setTbdwid(String tbdwid)
    {
        this.tbdwid = tbdwid;
    }
    public String getTbr()
    {
        return tbr;
    }
    public void setTbr(String tbr)
    {
        this.tbr = tbr;
    }
    public String getYear()
    {
        return year;
    }
    public void setYear(String year)
    {
        this.year = year;
    }
    public String getMonth()
    {
        return month;
    }
    public void setMonth(String month)
    {
        this.month = month;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getTrjhTotal()
    {
        return trjhTotal;
    }
    public void setTrjhTotal(String trjhTotal)
    {
        this.trjhTotal = trjhTotal;
    }
    public String getTrjhTotalFinish()
    {
        return trjhTotalFinish;
    }
    public void setTrjhTotalFinish(String trjhTotalFinish)
    {
        this.trjhTotalFinish = trjhTotalFinish;
    }
    public String getTrjhFypc()
    {
        return trjhFypc;
    }
    public void setTrjhFypc(String trjhFypc)
    {
        this.trjhFypc = trjhFypc;
    }
    public String getParentId()
    {
        return parentId;
    }
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }
    public String getPlanId()
    {
        return planId;
    }
    public void setPlanId(String planId)
    {
        this.planId = planId;
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

