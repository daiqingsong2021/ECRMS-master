/**
 * Project Name:ECRMS
 * File Name:MenuController.java
 * Package Name:com.jsumt.controller.MenuManage
 * Date:2018年8月7日上午10:47:01
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.system.MenuService;
import com.jsumt.service.system.ModuleService;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.system.MenuBean;
import com.jsumt.vo.system.ModuleBean;

/**
 * 菜单管理控制层
 * ClassName:MenuController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月7日 上午10:47:01 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
public class MenuManagController extends BaseController
{
    @Autowired
    private MenuService menuService;

    @Autowired
    private ModuleService moduleService;
    
    private static Logger logger = LoggerFactory.getLogger(MenuManagController.class);

    // 加载菜单所有节点
    @RequestMapping("queryMenus")
    public @ResponseBody Map<String, Object> queryMenus(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<ModuleBean> moduleList = new ArrayList<ModuleBean>();
        // 根据查询条件加载菜单管理的树形结构，若没有条件则加载所有节点
        List<MenuBean> MenuList = menuService.queryAllMenus(mapWhere);
        moduleList = moduleService.queryModules(mapWhere);
        resultMap.put("rows", MenuList);
        resultMap.put("moduleList", moduleList);
        return resultMap;
    }

    // 新增菜单
    @RequestMapping("addMuenu")
    public @ResponseBody Map<String, Object> addMuenu(MenuBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        // 封装父级别菜单对象
        bean.setMenu_id(UUIDHexGenerator.generator());
        if (StringHelper.isNullAndEmpty(bean.getMenu_pid()))
        {
            // 新增顶层菜单
            bean.setMenu_pid("0");
            // 生成顶层菜单层级
            bean.setMenu_layer(1);
            bean.setIconCls(IconClass.ICON_MENU.toString());

        }
        else
        {
            // 生成子菜单层级
            MenuBean parentBean = menuService.queryMenuById(bean.getMenu_pid());
            // 该直接点的父节点的IsLeaf更新为0
            parentBean.setIsLeaf("0");
            parentBean.setIconCls(IconClass.ICON_MENU.toString());
            menuService.updateMenu(parentBean);
            
            bean.setMenu_layer(parentBean.getMenu_layer() + 1);
            bean.setModule_code(parentBean.getModule_code());
            bean.setIconCls(IconClass.ICON_MENU.toString());
        }
        // 查询最大序号
        String maxNo = menuService.queryMaxNo(bean.getMenu_pid());
        if (StringHelper.isNotNullAndEmpty(maxNo))
        {
            bean.setMenu_no(String.valueOf(Integer.valueOf(maxNo) + 1));
        }
        else
        {
            bean.setMenu_no("1");
        }
        // 新增的节点一定是子节点
        bean.setIsLeaf("1");
        try
        {
            menuService.addPMuenu(bean);
            retMap = this.generateMsg("", true, "增加用户成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "增加用户失败!");
            return retMap;
        }

    }

    // 根据主键查询菜单对象数据
    @RequestMapping("queryMenu")
    public @ResponseBody Map<String, Object> queryMenu(String menu_id)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        MenuBean parentMenu = menuService.queryMenuById(menu_id);
        retMap = this.generateMsg(parentMenu, true, "查询成功!");
        return retMap;
    }

    // 根据主键更新菜单对象数据
    @RequestMapping("editMenu")
    public @ResponseBody Map<String, Object> editMenu(MenuBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            menuService.updateMenu(bean);
            retMap = this.generateMsg("", true, "更新成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "更新失败!");
            return retMap;
        }

    }

    // 根据主键删除 菜单对象 ： 子节点删除/父节点删除
    @RequestMapping("delMenus")
    public @ResponseBody Map<String, Object> delMenus(String menu_id)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (StringHelper.isNotNullAndEmpty(menu_id))
            {
                menuService.deleteByPrimaryKey(menu_id);
                resultMap = this.generateMsg("", true, "删除成功");

            }
        }
        catch (Exception e)
        {
            resultMap = this.generateMsg("", false, "删除失败");
        }
        return resultMap;
    }
    
    
    /**
     * 流程管理查询流程业务编码与名称
     * queryCategory:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping(value = "querySystemYw")
    @ResponseBody
    public Map<String, Object> querySystemYw()
    {
        Map<String, Object> retMap = Maps.newHashMap();
        List<MenuBean> systemYwList = Lists.newArrayList();
        boolean rst = false;
        try
        {   
            String moduleCode=this.getParameter("moduleCode");
            Map<String, Object> paramWhere=Maps.newHashMap();
            paramWhere.put("isLeaf", "1");
            paramWhere.put("module_code", moduleCode);
            systemYwList = menuService.queryAllMenus(paramWhere);
            rst = true;
        }
        catch (Exception e)
        {
            logger.error("新建流程查询所属模块失败!", e);
        }
        if (!rst)
            retMap = this.generateMsg("", false, "新建流程查询业务名称失败!");
        else
            retMap = this.generateMsg(systemYwList, true, "新建流程查询业务名称成功!");
        return retMap;

    }
    

}
