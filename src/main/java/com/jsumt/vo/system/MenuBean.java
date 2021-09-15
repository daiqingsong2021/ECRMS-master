/**
 * Project Name:ECRMS
 * File Name:MenuBean.java
 * Package Name:com.jsumt.vo.menuManage
 * Date:2018年8月7日上午11:15:33
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.system;
/**菜单 类
 * ClassName:MenuBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月7日 上午11:15:33 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class MenuBean {
    private String menu_id;
    private String menu_pid;
    private String menu_code;
    private String menu_name;
    private char menu_spread ='0';//1表示默认展开，0表示默认不展开，默认值0
    private Integer menu_layer;
    private String menu_no;
    private String iconCls;
    private String menu_url;
    private String remark;
    private String module_code;
    private String canRead;
    private String canWrite;
    private String isLeaf;
    private String menuIcon;
    public String getMenuIcon()
    {
        return menuIcon;
    }
    public void setMenuIcon(String menuIcon)
    {
        this.menuIcon = menuIcon;
    }
    public String getIsLeaf() {
        return isLeaf;
    }
    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
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
    public String getMenu_id() {
        return menu_id;
    }
    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }
    public String getMenu_pid() {
        return menu_pid;
    }
    public void setMenu_pid(String menu_pid) {
        this.menu_pid = menu_pid;
    }
    public String getMenu_code() {
        return menu_code;
    }
    public void setMenu_code(String menu_code) {
        this.menu_code = menu_code;
    }
    public String getMenu_name() {
        return menu_name;
    }
    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }
    public char getMenu_spread() {
        return menu_spread;
    }
    public void setMenu_spread(char menu_spread) {
        this.menu_spread = menu_spread;
    }
    public Integer getMenu_layer() {
        return menu_layer;
    }
    public void setMenu_layer(Integer menu_layer) {
        this.menu_layer = menu_layer;
    }

 
    public String getMenu_no() {
        return menu_no;
    }
    public void setMenu_no(String menu_no) {
        this.menu_no = menu_no;
    }
    
    public String getIconCls()
    {
        return iconCls;
    }
    public void setIconCls(String iconCls)
    {
        this.iconCls = iconCls;
    }
    public String getMenu_url() {
        return menu_url;
    }
    public void setMenu_url(String menu_url) {
        this.menu_url = menu_url;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getModule_code() {
        return module_code;
    }
    public void setModule_code(String module_code) {
        this.module_code = module_code;
    }

}

