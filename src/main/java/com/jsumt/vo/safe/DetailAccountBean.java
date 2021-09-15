/**
 * Project Name:ECRMS
 * File Name:DetailAccountBean.java
 * Package Name:com.jsumt.vo.safe
 * Date:2019年1月4日上午9:20:57
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.safe;

import java.util.Date;

/**安全生产投入台账明细
 * ClassName:DetailAccountBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2019年1月4日 上午9:20:57 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class DetailAccountBean
{
    private String id;
    private String useDw;//部门
    private String content;//内容
    private String fpsl;//发票数量
    private String je;//金额
    private String sctrTypeId1;//安全生产投入类别表1级ID
    private String sctrTypeTitle1;//安全生产投入类别1级title
    private Date rzrq;//入账日期
    private String pzh;//凭证号
    private String remark;//备注
    private String tzId;//台账id
    private Date createTime;
    private Date updateTime;
    public String getTzId()
    {
        return tzId;
    }
    public void setTzId(String tzId)
    {
        this.tzId = tzId;
    }
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getUseDw()
    {
        return useDw;
    }
    public void setUseDw(String useDw)
    {
        this.useDw = useDw;
    }
    public String getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
    public String getFpsl()
    {
        return fpsl;
    }
    public void setFpsl(String fpsl)
    {
        this.fpsl = fpsl;
    }
    public String getJe()
    {
        return je;
    }
    public void setJe(String je)
    {
        this.je = je;
    }
    public String getSctrTypeId1()
    {
        return sctrTypeId1;
    }
    public void setSctrTypeId1(String sctrTypeId1)
    {
        this.sctrTypeId1 = sctrTypeId1;
    }
    public String getSctrTypeTitle1()
    {
        return sctrTypeTitle1;
    }
    public void setSctrTypeTitle1(String sctrTypeTitle1)
    {
        this.sctrTypeTitle1 = sctrTypeTitle1;
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
    public Date getRzrq()
    {
        return rzrq;
    }
    public void setRzrq(Date rzrq)
    {
        this.rzrq = rzrq;
    }
    public String getPzh()
    {
        return pzh;
    }
    public void setPzh(String pzh)
    {
        this.pzh = pzh;
    }
    public String getRemark()
    {
        return remark;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    
}

