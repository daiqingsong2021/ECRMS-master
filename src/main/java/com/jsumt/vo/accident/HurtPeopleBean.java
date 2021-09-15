/**
 * Project Name:ECRMS
 * File Name:HurtPeopleBean.java
 * Package Name:com.jsumt.vo.accident
 * Date:2018年12月10日上午9:05:47
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.accident;

import java.util.Date;

/**
 * ClassName:HurtPeopleBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月10日 上午9:05:47 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class HurtPeopleBean
{
    private String id;
    private String user_name;//姓名
    private String work_type;//工种
    private Date birthday;//从业时间
    private String cbxs;//承包形式
    private String sex;
    private Integer age;
    private String is_draf;//是否职工
    private String status;//伤亡情况
    private String sgbid;//事故表id
    private String carNo;//身份证号码
    public String getCarNo()
    {
        return carNo;
    }
    public void setCarNo(String carNo)
    {
        this.carNo = carNo;
    }
    private Date createTime;
    private Date updateTime;
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
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getUser_name()
    {
        return user_name;
    }
    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }
    public String getWork_type()
    {
        return work_type;
    }
    public void setWork_type(String work_type)
    {
        this.work_type = work_type;
    }
    public Date getBirthday()
    {
        return birthday;
    }
    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }
    public String getCbxs()
    {
        return cbxs;
    }
    public void setCbxs(String cbxs)
    {
        this.cbxs = cbxs;
    }
    public String getSex()
    {
        return sex;
    }
    public void setSex(String sex)
    {
        this.sex = sex;
    }
    public Integer getAge()
    {
        return age;
    }
    public void setAge(Integer age)
    {
        this.age = age;
    }
    public String getIs_draf()
    {
        return is_draf;
    }
    public void setIs_draf(String is_draf)
    {
        this.is_draf = is_draf;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getSgbid()
    {
        return sgbid;
    }
    public void setSgbid(String sgbid)
    {
        this.sgbid = sgbid;
    }
}

