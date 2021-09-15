/**
 * Project Name:ECRMS
 * File Name:RoleController.java
 * Package Name:com.jsumt.controller.roleManager
 * Date:2018年8月9日上午9:57:41
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.system.MenuService;
import com.jsumt.service.system.ModuleService;
import com.jsumt.service.system.ProjectService;
import com.jsumt.service.system.RoleService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.EnumsUtil;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.PageInfoUtiil;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.system.MenuBean;
import com.jsumt.vo.system.ModuleBean;
import com.jsumt.vo.system.ProjectBean;
import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.RoleMenuBean;
import com.jsumt.vo.system.RoleModuleBean;
import com.jsumt.vo.system.RoleProjectBean;
import com.jsumt.vo.system.RoleUserBean;
import com.jsumt.vo.system.UserBean;

/**
 * 角色管理控制层
 * ClassName:RoleController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月9日 上午9:57:41 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
public class RoleController extends BaseController
{
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserManageService userManageService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private MenuService menuService;

    @Autowired
    private ProjectService projectService;

    // 加载项目
    @RequestMapping("queryProjectsInRole")
    public @ResponseBody Map<String, Object> queryProjectsInRole(@RequestParam Map<String, Object> mapWhere)
            throws UnsupportedEncodingException
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String roleId = this.getParameter("roleId");
        // 根据条件查询菜单表所有菜单 menu_code
        List<ProjectBean> projectList = projectService.queryProjects(null);
        // 根据角色主键查询角色菜单表
        List<RoleProjectBean> projectInRole = projectService.queryRoleProjectByRoleId(roleId);

        // 封装canRead 和canWrite 属性
        for (RoleProjectBean rpb : projectInRole)
        {
            for (ProjectBean pb : projectList)
            {
                if (rpb.getProjectId().equals(pb.getProjectId()))
                {
                    pb.setCanRead(rpb.getCanRead());
                    pb.setCanWrite(rpb.getCanWrite());
                }
            }
        }

        resultMap.put("rows", projectList);
        return resultMap;
    }

    // 项目权限更改
    @RequestMapping("changeProjectQX")
    public @ResponseBody Map<String, Object> changeProjectQX(String list, String roleId)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, String>> parameter = JsonHelper.fromJsonWithGson(list, List.class);

            // 将合并projectId合并在一起，
            List<Map<String, String>> listMap = Lists.newArrayList();
            for (Map<String, String> paramMap : parameter)
            {
                boolean isInsert = true;
                for (Map<String, String> map : listMap)
                {
                    if (map.get("projectId").equals(paramMap.get("projectId")))
                    {// 如果重复 复写
                        if (StringHelper.isNotNullAndEmpty(paramMap.get("pread")))
                            map.put("canRead", paramMap.get("pread"));
                        if (StringHelper.isNotNullAndEmpty(paramMap.get("pwrite")))
                            map.put("canWrite", paramMap.get("pwrite"));
                        isInsert = false;
                    }

                }
                // 没有找到重复的
                if (isInsert)
                {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("projectId", paramMap.get("projectId"));
                    if (StringHelper.isNotNullAndEmpty(paramMap.get("pread")))
                        map.put("canRead", paramMap.get("pread"));
                    else
                        map.put("canRead", "0");
                    if (StringHelper.isNotNullAndEmpty(paramMap.get("pwrite")))
                        map.put("canWrite", paramMap.get("pwrite"));
                    else
                        map.put("canWrite", "0");
                    listMap.add(map);
                }

            }

            List<Map<String, String>> roleProjectUpdtae = Lists.newArrayList();
            List<Map<String, String>> roleProjectInsert = Lists.newArrayList();
            for (Map<String, String> map : listMap)
            {

                Map<String, Object> roleProjectExist = new HashMap<String, Object>();
                roleProjectExist.put("projectId", map.get("projectId"));
                roleProjectExist.put("roleId", roleId);
                // 判断模块菜单表该数据是否存在
                Integer ExistCount = projectService.roleProjectIsExist(roleProjectExist);
                if (ExistCount > 0)
                {
                    // 菜单模块已经存在，则更新读写属性
                    Map<String, String> updateMap = new HashMap<String, String>();
                    updateMap.put("projectId", map.get("projectId"));
                    updateMap.put("roleId", roleId);
                    updateMap.put("canRead", map.get("canRead"));
                    updateMap.put("canWrite", map.get("canWrite"));
                    roleProjectUpdtae.add(updateMap);
                }
                else
                {
                    // 菜单模块不存在，则插入数据
                    Map<String, String> insertMap = new HashMap<String, String>();
                    insertMap.put("id", UUIDHexGenerator.generator());
                    insertMap.put("projectId", map.get("projectId"));
                    insertMap.put("roleId", roleId);
                    insertMap.put("canRead", map.get("canRead"));
                    insertMap.put("canWrite", map.get("canWrite"));
                    roleProjectInsert.add(insertMap);

                }
            }

            // 更改权限操作
            if (!roleProjectInsert.isEmpty())
            {
                projectService.insertQx(roleProjectInsert);
            }
            if (!roleProjectUpdtae.isEmpty())
            {
                projectService.updateQx(roleProjectUpdtae);
            }

            retMap = this.generateMsg("", true, "分配成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "分配失败!");
            return retMap;
        }

    }

    // 菜单权限更改
    @RequestMapping("changeQx")
    public @ResponseBody Map<String, Object> changeQx(String list, String roleId)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            // 需要更新的MenuID
            List<Map<String, String>> parameter = JsonHelper.fromJsonWithGson(list, List.class);

            // 将合并MenuID合并在一起，
            List<Map<String, String>> listMap = Lists.newArrayList();
            for (Map<String, String> paramMap : parameter)
            {
                boolean isInsert = true;
                for (Map<String, String> map : listMap)
                {
                    if (map.get("menuId").equals(paramMap.get("menu_id")))
                    {// 如果重复 复写
                        if (StringHelper.isNotNullAndEmpty(paramMap.get("read")))
                            map.put("canRead", paramMap.get("read"));
                        if (StringHelper.isNotNullAndEmpty(paramMap.get("write")))
                            map.put("canWrite", paramMap.get("write"));
                        isInsert = false;
                    }

                }
                // 没有找到重复的
                if (isInsert)
                {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("menuId", paramMap.get("menu_id"));
                    if (StringHelper.isNotNullAndEmpty(paramMap.get("read")))
                        map.put("canRead", paramMap.get("read"));
                    else
                        map.put("canRead", "0");
                    if (StringHelper.isNotNullAndEmpty(paramMap.get("write")))
                        map.put("canWrite", paramMap.get("write"));
                    else
                        map.put("canWrite", "0");
                    listMap.add(map);
                }

            }

            List<ModuleBean> allModules = moduleService.queryModules(null);
            List<String> allModulesId = Lists.newArrayList();
            for (ModuleBean bean : allModules)
            {
                allModulesId.add(bean.getModule_id());
            }
            List<Map<String, String>> roleModuleUpdtae = Lists.newArrayList();
            List<Map<String, String>> roleModuleInsert = Lists.newArrayList();
            List<Map<String, String>> menuRoleUpdtae = Lists.newArrayList();
            List<Map<String, String>> menuRoleInsert = Lists.newArrayList();
            for (Map<String, String> map : listMap)
            {
                // 该菜单项是为模块项
                if (allModulesId.contains(map.get("menuId")))
                {
                    Map<String, Object> roleModuleExist = new HashMap<String, Object>();
                    roleModuleExist.put("moduleId", map.get("menuId"));
                    roleModuleExist.put("roleId", roleId);
                    // 判断模块菜单表该数据是否存在
                    Integer ExistCount = moduleService.moduleRoleIsExist(roleModuleExist);
                    if (ExistCount > 0)
                    {
                        // 菜单模块已经存在，则更新读写属性
                        Map<String, String> updateMap = new HashMap<String, String>();
                        updateMap.put("moduleId", map.get("menuId"));
                        updateMap.put("roleId", roleId);
                        updateMap.put("canRead", map.get("canRead"));
                        updateMap.put("canWrite", map.get("canWrite"));
                        roleModuleUpdtae.add(updateMap);
                    }
                    else
                    {
                        // 菜单模块不存在，则插入数据
                        Map<String, String> insertMap = new HashMap<String, String>();
                        insertMap.put("id", UUIDHexGenerator.generator());
                        insertMap.put("moduleId", map.get("menuId"));
                        insertMap.put("roleId", roleId);
                        insertMap.put("canRead", map.get("canRead"));
                        insertMap.put("canWrite", map.get("canWrite"));
                        roleModuleInsert.add(insertMap);

                    }
                }
                else
                {
                    // 为菜单项
                    Map<String, Object> roleMenuExists = new HashMap<String, Object>();
                    roleMenuExists.put("menuId", map.get("menuId"));
                    roleMenuExists.put("roleId", roleId);
                    // 判断模块菜单表该数据是否存在
                    Integer ExistCount = menuService.menuRoleIsExist(roleMenuExists);
                    if (ExistCount > 0)
                    {
                        // 菜单用户已经存在，则更新读写属性
                        Map<String, String> updateMap = new HashMap<String, String>();
                        updateMap.put("menuId", map.get("menuId"));
                        updateMap.put("roleId", roleId);
                        updateMap.put("canRead", map.get("canRead"));
                        updateMap.put("canWrite", map.get("canWrite"));
                        menuRoleUpdtae.add(updateMap);
                    }
                    else
                    {
                        // 菜单用户不存在，则插入数据
                        Map<String, String> insertMap = new HashMap<String, String>();
                        insertMap.put("id", UUIDHexGenerator.generator());
                        insertMap.put("menuId", map.get("menuId"));
                        insertMap.put("roleId", roleId);
                        insertMap.put("canRead", map.get("canRead"));
                        insertMap.put("canWrite", map.get("canWrite"));
                        menuRoleInsert.add(insertMap);
                    }
                }

            }
            // 更改权限操作
            if (!roleModuleInsert.isEmpty())
            {
                moduleService.insertQx(roleModuleInsert);
            }
            if (!menuRoleInsert.isEmpty())
            {
                menuService.insertQx(menuRoleInsert);
            }
            if (!roleModuleUpdtae.isEmpty())
            {
                moduleService.updateQx(roleModuleUpdtae);
            }
            if (!menuRoleUpdtae.isEmpty())
            {
                menuService.updateQx(menuRoleUpdtae);
            }

            retMap = this.generateMsg("", true, "分配成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "分配失败!");
            return retMap;
        }

    }

    // 加载菜单
    @RequestMapping("queryMenusInRole")
    public @ResponseBody Map<String, Object> queryAllMenusInRole(@RequestParam Map<String, Object> mapWhere)
            throws UnsupportedEncodingException
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String roleId = this.getParameter("roleId");
        // 根据条件查询菜单表所有菜单 menu_code
        List<MenuBean> MenuList = menuService.queryAllMenus(mapWhere);
        // 根据角色主键查询角色菜单表
        List<RoleMenuBean> MenusInRole = menuService.queryRoleMenusByRoleId(roleId);
        // 查询module map
        Map<String, Object> queryModuleMap = Maps.newHashMap();
        queryModuleMap.put("moduleName", mapWhere.get("menu_name"));
        queryModuleMap.put("moduleCode", mapWhere.get("menu_code"));
        // 查询所有 模块表的模块
        List<ModuleBean> allModules = moduleService.queryModules(queryModuleMap);
        // 根据模块主键查询角色模块表
        List<RoleModuleBean> modulesInRole = moduleService.queryModulesByRoleId(roleId);
        // 查询所有顶级菜单所属的模块
        for (ModuleBean module : allModules)
        {
            // 将模块对象封装成菜单对象
            MenuBean menuBean = new MenuBean();
            menuBean.setMenu_id(module.getModule_id());
            menuBean.setMenu_pid("0");
            menuBean.setMenu_code(module.getModule_code());
            menuBean.setMenu_name(module.getModule_name());
            menuBean.setIconCls(EnumsUtil.IconClass.ICON_MODULE.toString());
            menuBean.setIsLeaf("1");

            for (MenuBean menu : MenuList)
            { // 设置顶层菜单
                if (StringHelper.isNotNullAndEmpty(menu.getModule_code())
                        && StringHelper.isNotNullAndEmpty(module.getModule_code()) && "0".equals(menu.getMenu_pid())
                        && menu.getModule_code().equals(module.getModule_code()))
                {
                    menu.setMenu_pid(module.getModule_id());
                    menuBean.setIsLeaf("0");
                }
            }

            MenuList.add(menuBean);
        }

        // 封装canRead 和canWrite 属性
        for (RoleMenuBean rmb : MenusInRole)
        {
            for (MenuBean mb : MenuList)
            {
                if (rmb.getMenuId().equals(mb.getMenu_id()))
                {
                    mb.setCanRead(rmb.getCanRead());
                    mb.setCanWrite(rmb.getCanWrite());
                }
            }
        }

        for (RoleModuleBean rmb : modulesInRole)
        {
            for (MenuBean mb : MenuList)
            {
                if (rmb.getModuleId().equals(mb.getMenu_id()))
                {
                    mb.setCanRead(rmb.getCanRead());
                    mb.setCanWrite(rmb.getCanWrite());
                }
            }
        }
        resultMap.put("rows", MenuList);
        return resultMap;
    }

    @RequestMapping("setModulesInRoleOrNot")
    public @ResponseBody Map<String, Object> setModulesInRoleOrNot()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {
            // 接受页面所传参数
            String isCheck = this.getParameter("isCheck");
            String moduleId = this.getParameter("moduleId");
            String roleId = this.getParameter("roleId");
            if ("false".equals(isCheck))
            {
                // 取消选中 即删除模块角色表相对应的数据
                Map<String, Object> ParaMap = new HashMap<String, Object>();
                ParaMap.put("moduleId", moduleId);
                ParaMap.put("roleId", roleId);
                moduleService.notCheck(ParaMap);
            }
            else
            {
                // 选中 即向模块角色表插入一条数据
                RoleModuleBean bean = new RoleModuleBean();
                bean.setId(UUIDHexGenerator.generator());
                bean.setModuleId(moduleId);
                bean.setRoleId(roleId);
                moduleService.isCheck(bean);
            }

            retMap = this.generateMsg("", true, "分配模块成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "分配模块失败!");
            return retMap;
        }
    }

    // @RequestMapping("queryModulesInRole")
    // public @ResponseBody String queryModulesInRole()
    // {
    // PageInfo<ModuleBean> pageInfo = new PageInfo<ModuleBean>();
    // Map<String, Object> queryMap = new HashMap<String, Object>();
    // Map<String, Object> retMap = new HashMap<String, Object>();
    // // List<String> moduleInRoleListId = new ArrayList<>();
    // List<RoleModuleBean> moduleInRoleList = new ArrayList<>();
    // List<ModuleBean> allModules = null;
    // String resultJson = "";
    // try
    // {
    // // 接受页面所传参数
    // String pageNumPa = this.getParameter("pageNum");
    // String rowCountPa = this.getParameter("rowCount");
    // String moduleName = this.getParameter("moduleName");
    // String moduleCode = this.getParameter("moduleCode");
    // String roleId = this.getParameter("roleId");
    // queryMap.put("moduleName", moduleName);
    // queryMap.put("roleId", roleId);
    // queryMap.put("moduleCode", moduleCode);
    // // 设置当前页
    // Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 :
    // Integer.parseInt(pageNumPa);
    // // 设置每页显示的数量
    // Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 :
    // Integer.parseInt(rowCountPa);
    // // 封装分页信息
    // pageInfo.setPageNum(pageNum);
    // pageInfo.setPageSize(rowCount);
    // Page<ModuleBean> returnPageList = new Page<ModuleBean>();
    // // 查询该角色下所能拥有的模块的主键
    // moduleInRoleList = moduleService.queryModulesInRole(roleId);
    // allModules = moduleService.queryModules(queryMap);
    //
    // // LAY_CHECKED 为true
    // for (RoleModuleBean moduleIn : moduleInRoleList)
    // {
    // for (ModuleBean module : allModules)
    // {
    //
    // if (module.getModule_id().equals(moduleIn.getModuleId()))
    // {
    // module.setLAY_CHECKED(true);
    // }
    // }
    // }
    //
    // returnPageList = new
    // PageInfoUtiil<ModuleBean>().generatePageList(pageInfo, allModules);
    // pageInfo = new PageInfo<ModuleBean>(returnPageList);
    //
    // }
    // catch (UnsupportedEncodingException e)
    // {
    // retMap = this.generateMsg("", false, "查询失败!");
    // e.printStackTrace();
    // resultJson = JsonHelper.toJsonWithGson(retMap);
    // return resultJson;
    // }
    // // 封装map结果集
    // Map<String, Object> dataMap = new HashMap<String, Object>();
    // dataMap.put("moduleInRoleList", pageInfo.getList());
    // dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
    // retMap = this.generateMsg(dataMap, true, "查询成功!");
    // resultJson = JsonHelper.toJsonWithGson(retMap);
    // return resultJson;
    // }

    // 为用户分配角色
    @RequestMapping("disUsersInRole")
    public @ResponseBody Map<String, Object> disUsersInRole(String disList, String roleId)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> idList = JsonHelper.fromJsonWithGson(disList, List.class);
        List<RoleUserBean> distributeList = new ArrayList<RoleUserBean>();
        try
        {
            for (String userId : idList)
            {
                // 封装角色用户对象
                RoleUserBean bean = new RoleUserBean();
                bean.setId(UUIDHexGenerator.generator());
                bean.setUserId(userId);
                bean.setRoleId(roleId);
                distributeList.add(bean);
            }

            userManageService.distributeUserInRole(distributeList);
            retMap = this.generateMsg("", true, "分配成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "分配失败!");
            return retMap;
        }
    }

    /**
     * 删除角色用户的关系
     * delUsersInRole:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param disList
     * @param roleId
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("delUsersInRole")
    public @ResponseBody Map<String, Object> delUsersInRole(String delList, String roleId)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Map<String, Object> delMap = new HashMap<String, Object>();
        List<String> idList = JsonHelper.fromJsonWithGson(delList, List.class);
        try
        {
            for (String userId : idList)
            {
                delMap.put("roleId", roleId);
                delMap.put("userId", userId);
                // 解除该用户的角色（删除用户角色表的数据）
                roleService.delUserInRole(delMap);
            }

            retMap = this.generateMsg("", true, "删除成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "删除失败!");
            return retMap;
        }

    }

    // 查询用户（关联角色）
    @RequestMapping("qUserNotInRole")
    public @ResponseBody Map<String, Object> qUserNotInRole()
    {
        PageInfo<UserBean> pageInfo = new PageInfo<UserBean>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<UserBean> userList = new ArrayList<UserBean>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String UserCodePa = this.getParameter("usercode");
            String UserNamePa = this.getParameter("username");
            String roleid = this.getParameter("roleid");
            queryMap.put("userCode", UserCodePa);
            queryMap.put("userName", UserNamePa);
            queryMap.put("roleId", roleid);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<UserBean> returnPageList = new Page<UserBean>();
            // 查询用户对象
            userList = userManageService.qUserNotInRole(queryMap);

            returnPageList = new PageInfoUtiil<UserBean>().generatePageList(pageInfo, userList);
            pageInfo = new PageInfo<UserBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }

    // 删除该角色下的用户
    @RequestMapping("delUserInRole")
    public @ResponseBody Map<String, Object> delUserInRole()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Map<String, Object> delMap = new HashMap<String, Object>();
        try
        {
            String roleId = this.getParameter("roleId");
            String userId = this.getParameter("userId");
            if (StringHelper.isNotNullAndEmpty(roleId) && StringHelper.isNotNullAndEmpty(userId))
            {
                delMap.put("roleId", roleId);
                delMap.put("userId", userId);
                // 解除该用户的角色（删除用户角色表的数据）
                roleService.delUserInRole(delMap);
                retMap = this.generateMsg("", true, "刪除成功!");
                return retMap;
            }
            else
            {
                retMap = this.generateMsg("", false, "刪除失败!");
                return retMap;
            }
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "刪除失败!");
            e.printStackTrace();
            return retMap;
        }

    }

    // 查询用户（关联角色）
    @RequestMapping("qUserByRole")
    public @ResponseBody Map<String, Object> queryUserByRole()
    {
        PageInfo<UserBean> pageInfo = new PageInfo<UserBean>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<UserBean> userList = new ArrayList<UserBean>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String UserCodePa = this.getParameter("usercode");
            String UserNamePa = this.getParameter("username");
            String roleid = this.getParameter("roleid");
            queryMap.put("userCode", UserCodePa);
            queryMap.put("userName", UserNamePa);
            queryMap.put("roleId", roleid);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<UserBean> returnPageList = new Page<UserBean>();
            // 查询用户对象
            userList = userManageService.queryUserByRole(queryMap);

            returnPageList = new PageInfoUtiil<UserBean>().generatePageList(pageInfo, userList);
            pageInfo = new PageInfo<UserBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }

    // 加载所有角色
    @RequestMapping("queryAllRoles")
    public @ResponseBody Map<String, Object> queryAllRoles(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 根据查询条件加载组织机构的树形结构，若没有条件则加载所以节点
        List<RoleBean> roleList = roleService.queryAllRoles(mapWhere);
        resultMap.put("rows", roleList);
        return resultMap;
    }

    @RequestMapping("delRoles")
    public @ResponseBody Map<String, Object> delRoles(String roleId)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (StringHelper.isNotNullAndEmpty(roleId))
            {
                roleService.deleteByPrimaryKey(roleId);
                resultMap = this.generateMsg("", true, "删除成功");

            }
        }
        catch (Exception e)
        {
            resultMap = this.generateMsg("", false, "删除失败");
        }
        return resultMap;
    }

    // 更新角色
    @RequestMapping("updateRoleInfo")
    public @ResponseBody Map<String, Object> updateRoleInfo(RoleBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            roleService.updateRole(bean);;
            retMap = this.generateMsg("", true, "更新角色成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "更新角色失败!");
            return retMap;
        }
    }

    // 添加角色

    @RequestMapping("addRole")
    public @ResponseBody Map<String, Object> addRole(RoleBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        // 封装父级别菜单对象
        bean.setRole_id(UUIDHexGenerator.generator());;
        if (StringHelper.isNullAndEmpty(bean.getRole_pid()))
        {
            // 新增顶层角色
            bean.setRole_pid("0");
            // 生成顶层角色层级
            bean.setRoleLayer(1);
            bean.setIconCls(IconClass.ICON_ROLEGROUP.toString());
        }
        else
        {
            // 生成子菜单层级
            RoleBean parentBean = roleService.queryRoleById(bean.getRole_pid());
            // 该直接点的父节点的IsLeaf更新为0

            bean.setRoleLayer(parentBean.getRoleLayer() + 1);
            if ("1".equals(bean.getRole_type()))
                bean.setIconCls(IconClass.ICON_ROLEGROUP.toString());
            else
                bean.setIconCls(IconClass.ICON_USER.toString());
        }

        // 查询最大序号
        String maxNo = roleService.queryMaxNo(bean.getRole_pid());
        if (StringHelper.isNotNullAndEmpty(maxNo))
        {
            bean.setRole_no(String.valueOf(Integer.valueOf(maxNo) + 1));;
        }
        else
        {
            bean.setRole_no("1");
        }

        try
        {
            roleService.addRole(bean);
            retMap = this.generateMsg("", true, "增加角色成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "增加角色失败!");
            return retMap;
        }
    }

    @RequestMapping("updateRole")
    public @ResponseBody Map<String, Object> updateRole(RoleBean bean)
    {

        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            roleService.updateRole(bean);
            retMap = this.generateMsg("", true, "修改成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "修改失败!");
            return retMap;
        }

    }

    // 根据userCode查询权限
    @RequestMapping("queryAuthByUserCode")
    public @ResponseBody Map<String, Object> queryAuthByUserCode()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            List<Map<String, Object>> data = roleService.queryAuthByUserCode(userCode);
            retMap = this.generateMsg(data, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    /**
     * 根据moduleName 与 menuName 查询权限
     * queryAuthByMenu:(N 没有权限 R 可读权限 W 可写权限). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("queryAuthByMenu")
    public @ResponseBody Map<String, Object> queryAuthByMenu()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            String moduleName = this.getParameter("moduleName");
            String menuName = this.getParameter("menuName");
            List<Map<String, Object>> data = roleService.queryAuthByUserCode(userCode);
            String authCode = "N";// 默认N表示没有权限
            for (Map<String, Object> moduleMap : data)
            {// 第一层模块过滤
                if (moduleName.equals(String.valueOf(moduleMap.get("moduleName"))))
                {
                    List<Map<String, Object>> menus = (List<Map<String, Object>>) moduleMap.get("children");
                    authCode = this.queryChildAuthByMenu(menus, menuName);
                    if (!"N".equals(authCode))// 如果不是N就返回
                        break;
                }
            }
            retMap = this.generateMsg(authCode, true, "查询成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    private String queryChildAuthByMenu(List<Map<String, Object>> data, String menuName)
    {
        String authCode = "N";// 默认N表示没有权限
        for (Map<String, Object> menuMap : data)
        {
            if (menuName.equals(String.valueOf(menuMap.get("name"))))
            {// 遍历菜单如果找到
                String canRead = String.valueOf(menuMap.get("canRead"));
                String canWrite = String.valueOf(menuMap.get("canWrite"));
                if ("1".equals(canRead) && !"1".equals(canWrite))
                {
                    authCode = "R";// 可读
                    return authCode;
                }
                else if ("1".equals(canRead) && "1".equals(canWrite))
                {
                    authCode = "W";// 可写
                    return authCode;
                }
            }
            else
            {// 如果找不到 看看子的遍历有没有
                if (menuMap.get("children") != null)
                {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) menuMap.get("children");
                    authCode = queryChildAuthByMenu(children, menuName);
                    if (!"N".equals(authCode))// 如果不是N就返回
                        return authCode;
                }

            }
        }
        return authCode;
    }

    public static void main(String[] args)
    {
        Map<String, String> map = Maps.newHashMap();
        System.out.println(map.get("222"));
    }
}
