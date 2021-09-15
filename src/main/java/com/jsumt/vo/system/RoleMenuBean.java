/**
 * Project Name:ECRMS
 * File Name:RoleMenuBean.java
 * Package Name:com.jsumt.vo.Role
 * Date:2018年8月15日上午9:49:45
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.io.Serializable;

/**
 * 角色菜单表
 * ClassName:RoleMenuBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月15日 上午9:49:45 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class RoleMenuBean implements Serializable {
    private String id;
    private String roleId;
    private String menuId;
    private String canRead;
    private String canWrite;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getMenuId() {
        return menuId;
    }
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    public String getCanRead() {
        return canRead;
    }
    public void setCanRead(String canRead) {
        this.canRead = canRead;
    }
    public String getCanWrite() {
        return canWrite;
    }
    public void setCanWrite(String canWrite) {
        this.canWrite = canWrite;
    }
  
}
