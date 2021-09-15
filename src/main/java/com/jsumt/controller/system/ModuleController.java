/**
 * Project Name:ECRMS
 * File Name:ModuleManage.java
 * Package Name:com.jsumt.controller.module
 * Date:2018年8月7日上午8:56:45
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.system.MenuService;
import com.jsumt.service.system.ModuleService;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.PageInfoUtiil;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.system.ModuleBean;

/**
 * 模块管理控制层
 * ClassName:ModuleManage <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月7日 上午8:56:45 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private MenuService menuService;
    
    private static Logger logger = LoggerFactory.getLogger(ModuleController.class);

    @RequestMapping("queryModules")
    public @ResponseBody Map<String, Object> queryModules() {
        PageInfo<ModuleBean> pageInfo = new PageInfo<ModuleBean>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<ModuleBean> moduleList = new ArrayList<ModuleBean>();
        try {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String moduleName = this.getParameter("module_name");
            String moduleCode = this.getParameter("module_code");
            queryMap.put("moduleName", moduleName);
            queryMap.put("moduleCode", moduleCode);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<ModuleBean> returnPageList = new Page<ModuleBean>();
            // 查询模块对象
            moduleList = moduleService.queryModules(queryMap);

            returnPageList = new PageInfoUtiil<ModuleBean>().generatePageList(pageInfo, moduleList);
            pageInfo = new PageInfo<ModuleBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e) {
            retMap = this.generateMsg("", false, "查询失败!");
            e.printStackTrace();
            return retMap;
        }

        // data:data.msg.userList,data.data.organizlist,html = '';
        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("moduleList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询成功!");
        return retMap;
    }
    @RequestMapping("queryCdModules")
    public @ResponseBody Map<String, Object> queryCdModules() {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<ModuleBean> moduleList = new ArrayList<ModuleBean>();
        try {
            // 接受页面所传参数
            String moduleName = this.getParameter("module_name");
            String moduleCode = this.getParameter("module_code");
            queryMap.put("moduleName", moduleName);
            queryMap.put("moduleCode", moduleCode);
            // 查询模块对象
            moduleList = moduleService.queryModules(queryMap);
        }
        catch (UnsupportedEncodingException e) {
            retMap = this.generateMsg("", false, "查询失败!");
            e.printStackTrace();
            return retMap;
        }

        // data:data.msg.userList,data.data.organizlist,html = '';
        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("moduleList", moduleList);
        retMap = this.generateMsg(dataMap, true, "查询成功!");
        return retMap;
    }
    
    @RequestMapping("addModule")
    public @ResponseBody Map<String, Object> addUser(ModuleBean bean) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        // 封装主primary key
        bean.setModule_id(UUIDHexGenerator.generator());
        try {
            //生成序号
            Integer order_no=moduleService.createOrderno();
            bean.setOrder_no(order_no);
            bean.setIconCls(IconClass.ICON_MODULE.toString());
            moduleService.createModule(bean);
            retMap = this.generateMsg("", true, "增加成功!");
            return retMap;
        }
        catch (Exception e) {
            retMap = this.generateMsg("", false, "增加失败!");
            return retMap;
        }

    }
    
    // 批量删除模块
    @RequestMapping("delModules")
    public @ResponseBody Map<String, Object> delModules(String delList) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> delelteList = JsonHelper.fromJsonWithGson(delList, List.class);
        List< Map<String, Object>> queryList = Lists.newArrayList();
        for(String moduleId:delelteList){
            Map<String, Object> mapWhere = new HashMap<String, Object>();
            mapWhere.put("moduleId", moduleId);
            queryList.add(mapWhere);
        }
        try {
            //删除模块下的所有顶层菜单的muen_id 
            List<String> delMenuIds = menuService.queryMenusInModules(queryList);
            for(String menuId :delMenuIds){
                menuService.deleteByPrimaryKey(menuId);
            }      
            //删除模块角色表
            moduleService.deleteRoleModuleByModuleId(delelteList);
            //删除模块表
            moduleService.delModules(delelteList);
            retMap = this.generateMsg("", true, "删除用户成功!");
            return retMap;
        }
        catch (Exception e) {
            retMap = this.generateMsg("", false, "删除用户失败!");
            return retMap;
        }
    }
    
    // 修改用户信息
    @RequestMapping("editModule")
    public @ResponseBody Map<String, Object> editModule(ModuleBean bean) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try {
            moduleService.editModule(bean);
            retMap = this.generateMsg("", true, "修改用户成功!");
            return retMap;
        }
        catch (Exception e) {
            retMap = this.generateMsg("", false, "修改用户失败!");
            return retMap;
        }
    }
    
    
    /**
     * 流程管理查询流程Model
     * queryCategory:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping(value = "querySystemModel")
    public @ResponseBody Map<String, Object> querySystemModel()
    {
        Map<String, Object> retMap = Maps.newHashMap();
        List<ModuleBean> systemModelueList = Lists.newArrayList();
        boolean rst = false;
        try
        {
            systemModelueList = moduleService.queryModules(null);
            rst = true;
        }
        catch (Exception e)
        {
            logger.error("新建流程查询所属模块失败!", e);
        }
        if (!rst)
            retMap = this.generateMsg("", false, "新建流程查询所属模块失败!");
        else
            retMap = this.generateMsg(systemModelueList, true, "新建流程查询所属模块成功!");
        return retMap;

    }
    
}
