/**
 * Project Name:ECRMS
 * File Name:UserManagerBean.java
 * Package Name:com.jsumt.vo.usermanag
 * Date:2018年7月31日下午2:07:59
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:UserManagerBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月31日 下午2:07:59 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class UserBean implements Serializable
{
    private String user_id;
    private String user_name;
    private String user_code;
    private String car_no;
    private Date birthday;
    private String phone;
    private String work_no;
    private String sex;
    private Date work_date;
    private String email;
    private String home_addr;
    private String user_pwd;
    private String orgId;
    private String orgName;
    private byte[] userImage;
    private String userRole;//多个以“,”号关联
    private String userRoleIds;//多个以“,”号关联
    private String userRoleCodes;//多个以“,”号关联
    private String remark;
    private String imageName;
    private String skin;
    private Date loginTime;

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getUser_name()
    {
        return user_name;
    }

    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public String getUser_code()
    {
        return user_code;
    }

    public void setUser_code(String user_code)
    {
        this.user_code = user_code;
    }

    public String getCar_no()
    {
        return car_no;
    }

    public void setCar_no(String car_no)
    {
        this.car_no = car_no;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getWork_no()
    {
        return work_no;
    }

    public void setWork_no(String work_no)
    {
        this.work_no = work_no;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public Date getWork_date()
    {
        return work_date;
    }

    public void setWork_date(Date work_date)
    {
        this.work_date = work_date;
    }

    public String getHome_addr()
    {
        return home_addr;
    }

    public void setHome_addr(String home_addr)
    {
        this.home_addr = home_addr;
    }

    public String getUser_pwd()
    {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd)
    {
        this.user_pwd = user_pwd;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }
    
    /**
     * 
     * getUserRole:(多个以逗号相连). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    public String getUserRole()
    {
        return userRole;
    }

    public void setUserRole(String userRole)
    {
        this.userRole = userRole;
    }
    
    /**
     * 
     * getUserRoleIds:(多个以逗号相连). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    public String getUserRoleIds()
    {
        return userRoleIds;
    }

    public void setUserRoleIds(String userRoleIds)
    {
        this.userRoleIds = userRoleIds;
    }
    
    

    public String getUserRoleCodes()
    {
        return userRoleCodes;
    }

    public void setUserRoleCodes(String userRoleCodes)
    {
        this.userRoleCodes = userRoleCodes;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public byte[] getUserImage()
    {
        return userImage;
    }

    public void setUserImage(byte[] userImage)
    {
        this.userImage = userImage;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    public String getSkin()
    {
        return skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }
    

}
