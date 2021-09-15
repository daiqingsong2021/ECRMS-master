/**
 * Project Name:ECRMS
 * File Name:ProjectMapper.java
 * Package Name:com.jsumt.mapper.system
 * Date:2018年8月22日上午10:21:41
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.ProjectBean;

/**
 * ClassName:ProjectMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月22日 上午10:21:41 <br/>
 * @author   txm
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface ProjectMapper
{

    List<ProjectBean> queryProjects(Map<String, Object> mapWhere);

    ProjectBean queryOneById(String projectPId);

    String queryMaxNo(String projectPId);

    void updateProject(ProjectBean parent);

    void addProject(ProjectBean bean);

    void deloProjects(List<String> deleteIds);

}

