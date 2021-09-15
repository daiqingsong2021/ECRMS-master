/**
 * Project Name:ECRMS
 * File Name:RoleBean.java
 * Package Name:com.jsumt.vo.Role
 * Date:2018年8月9日上午9:47:09
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.system;

import java.io.Serializable;

/**
 * 角色对象
 * ClassName:RoleBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月9日 上午9:47:09 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class RoleBean implements Serializable{    
    private String role_id;
    private String role_name;
    private String role_no;
    private String role_code;
    private String remark;
    private String role_type; //'1表示为角色分类，0表示为角色'
    private String role_pid;
    private Integer roleLayer;
    private String iconCls;
    public Integer getRoleLayer()
    {
        return roleLayer;
    }
    public void setRoleLayer(Integer roleLayer)
    {
        this.roleLayer = roleLayer;
    }

    public String getIconCls()
    {
        return iconCls;
    }
    public void setIconCls(String iconCls)
    {
        this.iconCls = iconCls;
    }
    public String getRole_id() {
        return role_id;
    }
    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }
    public String getRole_name() {
        return role_name;
    }
    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
    public String getRole_no() {
        return role_no;
    }
    public void setRole_no(String role_no) {
        this.role_no = role_no;
    }
    public String getRole_code() {
        return role_code;
    }
    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRole_type() {
        return role_type;
    }
    public void setRole_type(String role_type) {
        this.role_type = role_type;
    }
    public String getRole_pid() {
        return role_pid;
    }
    public void setRole_pid(String role_pid) {
        this.role_pid = role_pid;
    }
   
}

