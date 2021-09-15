/**
 * Project Name:ECRMS
 * File Name:UserManageMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年7月31日下午3:55:52
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.MenuBean;

/**
 * 菜单管理mapper
 * ClassName:UserManageMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月31日 下午3:55:52 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public interface MenuMapper
{

    List<MenuBean> queryAllMenus(Map<String, Object> mapWhere);

    void addPMuenu(MenuBean bean);

    MenuBean queryMenuById(String menu_pid);

    String createChiNo(Map<String, Object> queryMap);

    void updateMenu(MenuBean bean);

    void deleteByPrimaryKey(List<String> delIdList);
    
    String queryMaxNo(String menu_pid);

    List<String> queryMenusInModules(List<Map<String, Object>> queryList);

}
