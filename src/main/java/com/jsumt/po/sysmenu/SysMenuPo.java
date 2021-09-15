package com.jsumt.po.sysmenu;

public class SysMenuPo
{
    public static final String TABLE_NAME = "T_SYS_MENU";
    private String menuId;
    private String menuPId;
    private String menuCode;
    private String menuName;
    private Integer menuSpread;
    private Integer menuLayer;
    private Integer menuNo;
    private String menuIcon;
    private String menuUrl;
    private String remark;
    private String  moduleCode;


    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuPId() {
        return menuPId;
    }

    public void setMenuPId(String menuPId) {
        this.menuPId = menuPId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getMenuSpread() {
        return menuSpread;
    }

    public void setMenuSpread(Integer menuSpread) {
        this.menuSpread = menuSpread;
    }

    public Integer getMenuLayer() {
        return menuLayer;
    }

    public void setMenuLayer(Integer menuLayer) {
        this.menuLayer = menuLayer;
    }

    public Integer getMenuNo() {
        return menuNo;
    }

    public void setMenuNo(Integer menuNo) {
        this.menuNo = menuNo;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }
}
