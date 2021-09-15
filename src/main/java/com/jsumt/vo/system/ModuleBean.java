/**
 * Project Name:ECRMS
 * File Name:ModuleBean.java
 * Package Name:com.jsumt.vo.module
 * Date:2018年8月7日上午9:01:33
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.system;

import java.io.Serializable;

/**
 * 系统模块对象
 * ClassName:ModuleBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月7日 上午9:01:33 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ModuleBean implements Serializable{
    private String module_id;
    private String module_code;
    private String module_name;
    private Integer order_no;
    private String iconCls;
    private boolean LAY_CHECKED=false;
    private String moduleIcon;
    private String moduleSpread;
    private String moduleUrl;
    public String getModuleSpread()
    {
        return moduleSpread;
    }
    public void setModuleSpread(String moduleSpread)
    {
        this.moduleSpread = moduleSpread;
    }
    public String getModuleUrl()
    {
        return moduleUrl;
    }
    public void setModuleUrl(String moduleUrl)
    {
        this.moduleUrl = moduleUrl;
    }
    public String getModuleIcon()
    {
        return moduleIcon;
    }
    public void setModuleIcon(String moduleIcon)
    {
        this.moduleIcon = moduleIcon;
    }
    public boolean isLAY_CHECKED() {
        return LAY_CHECKED;
    }
    public void setLAY_CHECKED(boolean lAY_CHECKED) {
        LAY_CHECKED = lAY_CHECKED;
    }
    public String getModule_id() {
        return module_id;
    }
    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }
    public String getModule_code() {
        return module_code;
    }
    public void setModule_code(String module_code) {
        this.module_code = module_code;
    }
    public String getModule_name() {
        return module_name;
    }
    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }
    public Integer getOrder_no() {
        return order_no;
    }
    public void setOrder_no(Integer order_no) {
        this.order_no = order_no;
    }
    public String getIconCls()
    {
        return iconCls;
    }
    public void setIconCls(String iconCls)
    {
        this.iconCls = iconCls;
    }
    
   
}

