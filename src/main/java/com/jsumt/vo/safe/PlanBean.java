/**
 * Project Name:ECRMS
 * File Name:PlanBean.java
 * Package Name:com.jsumt.vo.safe
 * Date:2018年12月19日下午6:20:11
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.safe;

import java.math.BigDecimal;
import java.util.Date;

/**安全生产投入计划表
 * ClassName:PlanBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月19日 下午6:20:11 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class PlanBean
{
   private String id;
   private String tbdw;//填报单位
   private String tbdwid;//填报单位编码
   private String tbr;//填报人
   private String year;
   private String month;
   private String status;//状态
   
   private String plan_total;//计划总额
   private Date createTime;
   private Date updateTime;
    
   private String parent_id;//父类id
   private String org_id;
   private String title;//标题
   private String type;//类别
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
public String getPlan_total()
{
    return plan_total;
}
public void setPlan_total(String plan_total)
{
    this.plan_total = plan_total;
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
public String getParent_id()
{
    return parent_id;
}
public void setParent_id(String parent_id)
{
    this.parent_id = parent_id;
}
public String getOrg_id()
{
    return org_id;
}
public void setOrg_id(String org_id)
{
    this.org_id = org_id;
}
public String getTitle()
{
    return title;
}
public void setTitle(String title)
{
    this.title = title;
}
public String getType()
{
    return type;
}
public void setType(String type)
{
    this.type = type;
}
   
   
}

