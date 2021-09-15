/**
 * Project Name:ECRMS
 * File Name:MenuService.java
 * Package Name:com.jsumt.service.MenuManage
 * Date:2018年8月7日上午10:49:10
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.system;
/**
 * 菜单管理service层
 * ClassName:MenuService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月7日 上午10:49:10 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.jsumt.mapper.system.MenuMapper;
import com.jsumt.mapper.system.RoleMenuMapper;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.vo.system.MenuBean;
import com.jsumt.vo.system.RoleMenuBean;

@Service
public class MenuService
{
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    public List<MenuBean> queryAllMenus(Map<String, Object> mapWhere)
    {
        List<MenuBean> menuList = menuMapper.queryAllMenus(mapWhere);
        return menuList;
    }

    public void addPMuenu(MenuBean bean)
    {
        menuMapper.addPMuenu(bean);
    }

    public MenuBean queryMenuById(String menu_id)
    {
        MenuBean bean = menuMapper.queryMenuById(menu_id);
        return bean;
    }

    public void updateMenu(MenuBean bean)
    {

        menuMapper.updateMenu(bean);
    }

    public void deleteByPrimaryKey(String menu_id)
    {
        List<String> delIdList = new ArrayList<String>();
        delIdList.add(menu_id);
        
        
        MenuBean menuBean = menuMapper.queryMenuById(menu_id);
        String parentId = menuBean.getMenu_pid();
        
        //查询所有子节点
        List<MenuBean> menuList = menuMapper.queryAllMenus(null);
        List<MenuBean> childList = findAllChildMenus(menuList, menu_id);
        for (MenuBean delBean : childList)
        {
            delIdList.add(delBean.getMenu_id());
        }
        
        // 删除T_SYS_ROLEMENU
        roleMenuMapper.deleteRoleMenuByMenuId(delIdList);
        //删除菜单
        menuMapper.deleteByPrimaryKey(delIdList);
        
        
       // 更新父节点IS_LEAF 和 iconCls
        if (!"0".equals(parentId))
        {
            MenuBean parent = menuMapper.queryMenuById(parentId);
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("menu_pid", parent.getMenu_id());
            if (menuMapper.queryAllMenus(mapWhere).isEmpty())
            {//如果不存在子节点
                parent.setIsLeaf("1");// 更新父节点IS_LEAF
                parent.setIconCls(IconClass.ICON_MENU.toString());
                menuMapper.updateMenu(parent);
            }

        }

    }

    // 用于递归获取父节点下的子节点
    public List<MenuBean> findAllChildMenus(List<MenuBean> MenuList, String PMenu_id)
    {
        List<MenuBean> resultlist = new ArrayList<MenuBean>();
        for (MenuBean bean : MenuList)
        {
            if (bean.getMenu_pid().equals(PMenu_id))
            {
                // 是其子节点
                resultlist.add(bean);
                List<MenuBean> findAllChildMenus = findAllChildMenus(MenuList, bean.getMenu_id());
                if (!findAllChildMenus.isEmpty())
                {
                    resultlist.addAll(findAllChildMenus);
                }
            }
        }

        return resultlist;
    }

    public String queryMaxNo(String menu_pid)
    {
        String maxNo = menuMapper.queryMaxNo(menu_pid);
        return maxNo;
    }

    public List<RoleMenuBean> queryRoleMenusByRoleId(String roleId)
    {
        List<RoleMenuBean> resultList = roleMenuMapper.queryRoleMenusByRoleId(roleId);
        return resultList;
    }

    public Integer menuRoleIsExist(Map<String, Object> roleMenuExists)
    {
        Integer ExistCount = roleMenuMapper.menuRoleIsExist(roleMenuExists);

        return ExistCount;
    }

    public void insertQx(List<Map<String, String>> menuRoleInsert)
    {
        roleMenuMapper.insertQx(menuRoleInsert);
    }

    public void updateQx(List<Map<String, String>> menuRoleUpdtae)
    {
        roleMenuMapper.updateQx(menuRoleUpdtae);
    }

    public List<String> queryMenusInModules(List<Map<String, Object>> queryList)
    {
       return menuMapper.queryMenusInModules(queryList);
    }

}
