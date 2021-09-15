/**
 * Project Name:ECRMS
 * File Name:RoleUserBean.java
 * Package Name:com.jsumt.vo.Role
 * Date:2018年8月13日上午8:51:08
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.io.Serializable;


/**
 * 角色用户实体
 * ClassName:RoleUserBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月13日 上午8:51:08 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public class ProjectUserBean  implements Serializable{
    private String id;
    private String userId;
    private String projectId;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getProjectId()
    {
        return projectId;
    }
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

}
