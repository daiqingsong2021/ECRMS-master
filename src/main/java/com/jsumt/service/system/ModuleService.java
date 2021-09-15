/**
 * Project Name:ECRMS
 * File Name:ModuleService.java
 * Package Name:com.jsumt.service.module
 * Date:2018年8月7日上午8:58:17
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.system;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jsumt.mapper.system.ModuleMapper;
import com.jsumt.mapper.system.RoleModuleMapper;
import com.jsumt.vo.system.ModuleBean;
import com.jsumt.vo.system.RoleModuleBean;

/**
 * 模块service层
 * ClassName:ModuleService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月7日 上午8:58:17 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class ModuleService {
    @Autowired
    private ModuleMapper moduleMapper;
    @Autowired
    private RoleModuleMapper roleModuleMapper;

    public List<ModuleBean> queryModules(Map<String, Object> queryMap) {
        List<ModuleBean> moduleList = moduleMapper.queryModules(queryMap);
        return moduleList;

    }

    public void createModule(ModuleBean bean) {
        moduleMapper.addModule(bean);
    }

    public Integer createOrderno() {
        return moduleMapper.createOrderno();
    }

    public void delModules(List<String> delelteList) {
        moduleMapper.delModules(delelteList);
    }

    public void editModule(ModuleBean bean) {
        moduleMapper.updateModule(bean);
    }

    public List<RoleModuleBean> queryModulesByRoleId(String queryId) {
        List<RoleModuleBean> resultList = roleModuleMapper.queryRoleModulesByRoleId(queryId);
        return resultList;
    
    }

    //根据2个外键删除 角色模块表的数据
    public void notCheck(Map<String, Object> paraMap) {
        
        roleModuleMapper.deleteByFKeys(paraMap);
        
    }

    //向角色模块表插入数据
    public void isCheck(RoleModuleBean bean) {
        
        roleModuleMapper.insertByBean(bean);
        
    }

    public void deleteRoleModuleByModuleId(List<String> delelteList) {
        roleModuleMapper.deleteRoleModuleByModuleId(delelteList);
        
    }

    public Integer moduleRoleIsExist(Map<String, Object> roleModuleExist) {
        Integer ExistCount=roleModuleMapper.moduleRoleIsExist(roleModuleExist);
        return ExistCount;
    }

    public void insertQx(List<Map<String, String>> roleModuleInsert) {
        roleModuleMapper.insertQx(roleModuleInsert);
               
    }

    public void updateQx(List<Map<String, String>> roleModuleUpdtae) {
        roleModuleMapper.updateQx(roleModuleUpdtae);
      
        
    }


 

   

}
