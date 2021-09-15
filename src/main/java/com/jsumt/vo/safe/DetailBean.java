/**
 * Project Name:ECRMS
 * File Name:DetailBean.java
 * Package Name:com.jsumt.vo.safe
 * Date:2018年12月20日上午9:59:18
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.safe;

import java.util.Date;

/**安全生产投入细项表
 * ClassName:DetailBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月20日 上午9:59:18 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class DetailBean
{
    private String id;
    private String sctr_type_id1;//安全生产投入类别表1级ID
    private String sctr_type_title1;
    private String sctr_type_id2;
    private String sctr_type_title2;
    private String plan_sum;//计划合计
    private String bdw_plan;
    private String fbdw_plan_sum;//分包单位合计
    private String finish_sum;//实际合计
    private String bdw_finish;//本单位实际
    private String fbdw_finish_sum;//分包单位实际合计 
    private String fypc;//费用偏差
    private String plan_id;//计划id
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
    public String getSctr_type_id1()
    {
        return sctr_type_id1;
    }
    public void setSctr_type_id1(String sctr_type_id1)
    {
        this.sctr_type_id1 = sctr_type_id1;
    }
    public String getSctr_type_title1()
    {
        return sctr_type_title1;
    }
    public void setSctr_type_title1(String sctr_type_title1)
    {
        this.sctr_type_title1 = sctr_type_title1;
    }
    public String getSctr_type_id2()
    {
        return sctr_type_id2;
    }
    public void setSctr_type_id2(String sctr_type_id2)
    {
        this.sctr_type_id2 = sctr_type_id2;
    }
    public String getSctr_type_title2()
    {
        return sctr_type_title2;
    }
    public void setSctr_type_title2(String sctr_type_title2)
    {
        this.sctr_type_title2 = sctr_type_title2;
    }
    public String getPlan_sum()
    {
        return plan_sum;
    }
    public void setPlan_sum(String plan_sum)
    {
        this.plan_sum = plan_sum;
    }
    public String getBdw_plan()
    {
        return bdw_plan;
    }
    public void setBdw_plan(String bdw_plan)
    {
        this.bdw_plan = bdw_plan;
    }
    public String getFbdw_plan_sum()
    {
        return fbdw_plan_sum;
    }
    public void setFbdw_plan_sum(String fbdw_plan_sum)
    {
        this.fbdw_plan_sum = fbdw_plan_sum;
    }
    public String getFinish_sum()
    {
        return finish_sum;
    }
    public void setFinish_sum(String finish_sum)
    {
        this.finish_sum = finish_sum;
    }
    public String getBdw_finish()
    {
        return bdw_finish;
    }
    public void setBdw_finish(String bdw_finish)
    {
        this.bdw_finish = bdw_finish;
    }
    public String getFbdw_finish_sum()
    {
        return fbdw_finish_sum;
    }
    public void setFbdw_finish_sum(String fbdw_finish_sum)
    {
        this.fbdw_finish_sum = fbdw_finish_sum;
    }
    public String getFypc()
    {
        return fypc;
    }
    public void setFypc(String fypc)
    {
        this.fypc = fypc;
    }
    public String getPlan_id()
    {
        return plan_id;
    }
    public void setPlan_id(String plan_id)
    {
        this.plan_id = plan_id;
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

