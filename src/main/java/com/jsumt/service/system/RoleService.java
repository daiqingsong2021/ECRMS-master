/**
 * Project Name:ECRMS
 * File Name:RoleService.java
 * Package Name:com.jsumt.service.roleService
 * Date:2018年8月9日上午9:58:28
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.system.RoleMapper;
import com.jsumt.mapper.system.RoleMenuMapper;
import com.jsumt.mapper.system.RoleModuleMapper;
import com.jsumt.mapper.system.RoleProjectMapper;
import com.jsumt.mapper.system.UserRoleMapper;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.vo.system.MenuBean;
import com.jsumt.vo.system.ModuleBean;
import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.UserBean;

/**
 * 角色管理Service层
 * ClassName:RoleService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月9日 上午9:58:28 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class RoleService
{
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleModuleMapper roleModuleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private UserManageService userManageService;
    @Autowired
    private RoleProjectMapper roleProjectMapper;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private MenuService menuService;
    

    public List<RoleBean> queryAllRoles(Map<String, Object> mapWhere)
    {
        List<RoleBean> roleList = roleMapper.queryAllRoles(mapWhere);

        return roleList;
    }

    public void delUserInRole(Map<String, Object> delMap)
    {
        userRoleMapper.delUserInRole(delMap);

    }

    public RoleBean queryRoleById(String role_id)
    {
        return roleMapper.queryRoleById(role_id);

    }

    public String queryMaxNo(String role_pid)
    {
        return roleMapper.queryMaxNo(role_pid);

    }

    public void addRole(RoleBean bean)
    {
        roleMapper.addRole(bean);

    }

    public void updateRole(RoleBean parentBean)
    {
        roleMapper.updateRole(parentBean);

    }

    public void deleteByPrimaryKey(String roleId)
    {
        List<RoleBean> allRoles = roleMapper.queryAllRoles(null);

        RoleBean roleBean = roleMapper.queryRoleById(roleId);
        String parentId = roleBean.getRole_pid();

        List<String> delRoleIds = new ArrayList<String>();
        delRoleIds.add(roleId);
        List<String> diGuiGetChild = diGuiGetChild(roleId, allRoles);
        if (!diGuiGetChild.isEmpty())
        {
            delRoleIds.addAll(diGuiGetChild);
        }
         // 删除关联roleProject
        roleProjectMapper.deleteRoleProjectByRoleId(delRoleIds);
        // 删除关联roleModule
        roleModuleMapper.deleteRMByRoleId(delRoleIds);
        // 删除关联roleUser
        userRoleMapper.deleteURByRoleId(delRoleIds);
        // 删除关联roleMenu
        roleMenuMapper.deleteRMByRoleId(delRoleIds);

        roleMapper.deleteByPrimaryKey(delRoleIds);

        // 更新父节点IS_LEAF 和 iconCls
        if (!"0".equals(parentId))
        {
            RoleBean parent = roleMapper.queryRoleById(parentId);
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("role_pid", parent.getRole_id());
            if (roleMapper.queryAllRoles(mapWhere).isEmpty())
            {// 如果不存在子节点
                parent.setRole_type("0");
                parent.setIconCls(IconClass.ICON_USER.toString());
                roleMapper.updateRole(parent);
            }

        }

    }

    public List<String> diGuiGetChild(String rolePId, List<RoleBean> allRoles)
    {
        List<String> delRoleIds = new ArrayList<String>();
        for (RoleBean bean : allRoles)
        {
            if (bean.getRole_pid().equals(rolePId))
            {
                delRoleIds.add(bean.getRole_id());
                List<String> child = diGuiGetChild(bean.getRole_id(), allRoles);
                if (!child.isEmpty())
                {
                    delRoleIds.addAll(child);
                }
            }

        }
        return delRoleIds;
    }

    public List<RoleBean> queryRoleByUser(UserBean user)
    {
        List<RoleBean> roleList=Lists.newArrayList();
        if(user!=null)
        {
            roleList=userRoleMapper.queryRoleByUser(user);
        }
        return roleList;
    }

    public List<Map<String, Object>> queryAuthByUserCode(String userCode)
    {
        // 1 查询用户的角色List
        UserBean user = userManageService.queryUserByCode(userCode);
        if(userCode.equals("superadmin"))
        {//如果是超级管理员，直接无视权限，全部查询出来显示，并且可以编辑
            List<Map<String, Object>> supList=this.queryAllMenus();
            return supList;
        }
        List<RoleBean> roleList = queryRoleByUser(user);
        if(roleList.isEmpty()) return Lists.newArrayList();
        // 2 查询角色能看到的所有模块
        List<Map<String, Object>> modules = Lists.newArrayList();
        List<Map<String, Object>> modules_ = roleModuleMapper.queryModulesByRole(roleList);
        // 3 根据canRead 与 canWirte对模块去重，mapper已经disticnt一重过滤过，现将canRead 与
        // canWrite合并
        for (Map<String, Object> newMod : modules_)
        {
            boolean isInsert = true;
            for (Map<String, Object> map : modules)
            {
                if (String.valueOf(newMod.get("MODULE_CODE")).equals(String.valueOf(map.get("code"))))
                {// 如果存在
                    String canRead = String.valueOf(map.get("canRead"));
                    String canWrite = String.valueOf(map.get("canWrite"));
                    String newCanRead = String.valueOf(newMod.get("CAN_READ"));
                    String newCanWrite = String.valueOf(newMod.get("CAN_WRITE"));
                    if ("1".equals(newCanRead) && "0".equals(canRead))
                        map.put("canRead", newCanRead);
                    if ("1".equals(newCanWrite) && "0".equals(canWrite))
                        map.put("canWrite", newCanWrite);
                    isInsert = false;
                }
            }
            if (isInsert)
            {
                Map<String, Object> insetMap = Maps.newHashMap();
                insetMap.put("id", String.valueOf(newMod.get("MODULE_ID")));
                insetMap.put("code", String.valueOf(newMod.get("MODULE_CODE")));
                insetMap.put("name", String.valueOf(newMod.get("MODULE_NAME")));
                insetMap.put("icon", String.valueOf(newMod.get("MODULE_ICON")));
                insetMap.put("url", String.valueOf(newMod.get("MODULE_URL")));
                insetMap.put("isSpread", String.valueOf(newMod.get("MODULE_SPREAD")));
                insetMap.put("canRead", String.valueOf(newMod.get("CAN_READ")));
                insetMap.put("canWrite", String.valueOf(newMod.get("CAN_WRITE")));
                insetMap.put("pid", "0");
                insetMap.put("isModule", "1");
                insetMap.put("moduleCode", String.valueOf(newMod.get("MODULE_CODE")));
                insetMap.put("moduleName", String.valueOf(newMod.get("MODULE_NAME")));
                modules.add(insetMap);
            }

        }
        // 4 查询角色能看到的所有菜单，第一层去重distinct,然后 再合并canRead,canWrite
        List<Map<String, Object>> menus = Lists.newArrayList();
        List<Map<String, Object>> menus_ = roleMenuMapper.queryMenusByRole(roleList);
        for (Map<String, Object> newMenu : menus_)
        {
            boolean isInsert = true;
            for (Map<String, Object> map : menus)
            {
                if (String.valueOf(newMenu.get("MENU_CODE")).equals(String.valueOf(map.get("code"))))
                {// 如果存在
                    String canRead = String.valueOf(map.get("canRead"));
                    String canWrite = String.valueOf(map.get("canWrite"));
                    String newCanRead = String.valueOf(newMenu.get("CAN_READ"));
                    String newCanWrite = String.valueOf(newMenu.get("CAN_WRITE"));
                    if ("1".equals(newCanRead) && "0".equals(canRead))
                        map.put("canRead", newCanRead);
                    if ("1".equals(newCanWrite) && "0".equals(canWrite))
                        map.put("canWrite", newCanWrite);
                    isInsert = false;
                }
            }
            if (isInsert)
            {
                Map<String, Object> insetMap = Maps.newHashMap();
                insetMap.put("id", String.valueOf(newMenu.get("MENU_ID")));
                insetMap.put("code", String.valueOf(newMenu.get("MENU_CODE")));
                insetMap.put("name", String.valueOf(newMenu.get("MENU_NAME")));
                insetMap.put("icon", String.valueOf(newMenu.get("MENU_ICON")));
                insetMap.put("url", String.valueOf(newMenu.get("MENU_URL")));
                insetMap.put("isSpread", String.valueOf(newMenu.get("MENU_SPREAD")));
                insetMap.put("canRead", String.valueOf(newMenu.get("CAN_READ")));
                insetMap.put("canWrite", String.valueOf(newMenu.get("CAN_WRITE")));
                insetMap.put("moduleCode", String.valueOf(newMenu.get("MODULE_CODE")));
                String parentId = String.valueOf(newMenu.get("MENU_PID"));
                if ("0".equals(parentId))
                {// 顶层节点。查询所属MODULEID
                    for (Map<String, Object> map : modules)
                    {
                        if (String.valueOf(map.get("code")).equals(String.valueOf(newMenu.get("MODULE_CODE"))))
                        {
                            insetMap.put("pid", map.get("id"));
                            insetMap.put("moduleName", map.get("name"));
                            break;
                        }
                    }
                }
                else
                {
                    insetMap.put("pid", String.valueOf(newMenu.get("MENU_PID")));
                }
                insetMap.put("isModule", "0");
                menus.add(insetMap);

            }
        }
        //模块，菜单拼成树
        for (Map<String, Object> module : modules)
        {
            //查询module所有的子节点
            List<Map<String,Object>> children=getMenusChildren(String.valueOf(module.get("id")),menus);
            if(!children.isEmpty())
                module.put("children", children);
        }
        return modules;
    }

    private List<Map<String, Object>> queryAllMenus()
    {
        List<Map<String, Object>> menuListMap=Lists.newArrayList();
        // 根据条件查询菜单表所有菜单 menu_code
        List<MenuBean> MenuList = menuService.queryAllMenus(null);
        // 查询所有 模块表的模块
        List<Map<String, Object>> moduleListMap=Lists.newArrayList();
        List<ModuleBean> allModules = moduleService.queryModules(null);
        // 查询所有顶级菜单所属的模块
        for (ModuleBean module : allModules)
        {
            // 将模块对象封装成菜单对象
            Map<String, Object> insetMap = Maps.newHashMap();
            insetMap.put("id", module.getModule_id());
            insetMap.put("code", module.getModule_code());
            insetMap.put("name", module.getModule_name());
            insetMap.put("icon", module.getModuleIcon());
            insetMap.put("url", module.getModuleUrl());
            insetMap.put("isSpread", module.getModuleSpread());
            insetMap.put("canRead", "1");
            insetMap.put("canWrite", "1");
            insetMap.put("pid", "0");
            insetMap.put("isModule", "1");
            insetMap.put("moduleCode", module.getModule_code());
            insetMap.put("moduleName", module.getModule_name());
            moduleListMap.add(insetMap);
        }
        for (MenuBean menu : MenuList)
        {
            Map<String, Object> insetMap = Maps.newHashMap();
            insetMap.put("id", menu.getMenu_id());
            insetMap.put("code", menu.getMenu_code());
            insetMap.put("name", menu.getMenu_name());
            insetMap.put("icon", menu.getMenuIcon());
            insetMap.put("url", menu.getMenu_url());
            insetMap.put("isSpread", menu.getMenu_spread());
            insetMap.put("canRead", "1");
            insetMap.put("canWrite", "1");
            insetMap.put("moduleCode", menu.getModule_code());
            String parentId = menu.getMenu_pid();
            if ("0".equals(parentId))
            {// 顶层节点。查询所属MODULEID
                for (ModuleBean module : allModules)
                {
                    if (module.getModule_code().equals(menu.getModule_code()))
                    {
                        insetMap.put("pid", module.getModule_id());
                        insetMap.put("moduleName",module.getModule_name() );
                        break;
                    }
                }
            }
            else
            {
                insetMap.put("pid", parentId);
            }
            insetMap.put("isModule", "0");
            menuListMap.add(insetMap);
        }
        //模块，菜单拼成树
        for (Map<String, Object> module : moduleListMap)
        {
            //查询module所有的子节点
            List<Map<String,Object>> children=getMenusChildren(String.valueOf(module.get("id")),menuListMap);
            if(!children.isEmpty())
                module.put("children", children);
        }
        return moduleListMap;
    }

    public List<Map<String, Object>> getMenusChildren(String menuPid, List<Map<String, Object>> menus)
    {
        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : menus)
        {
            if (String.valueOf(map.get("pid")).equals(menuPid))
            {
                List<Map<String, Object>> children = getMenusChildren(String.valueOf(map.get("id")), menus);
                if(!children.isEmpty())
                   map.put("children", children);
                childList.add(map);
            }

        }
        return childList;
    }

    //用来查询已经分配给用户分配的角色
    public List<RoleBean> queryRoleInUser(Map<String, Object> queryMap)
    {
        List<RoleBean> roleList = roleMapper.queryRoleInUser(queryMap);
        return roleList;
    }
    
    //用来查询没有被分配的角色
    public List<RoleBean> queryRoleNotInUser(Map<String, Object> queryMap)
    {
        List<RoleBean> roleList = roleMapper.queryRoleNotInUser(queryMap);
        return roleList;
    }

}
