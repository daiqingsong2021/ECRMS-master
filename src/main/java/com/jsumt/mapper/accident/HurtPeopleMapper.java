/**
 * Project Name:ECRMS
 * File Name:HurtPeopleMapper.java
 * Package Name:com.jsumt.mapper.accident
 * Date:2018年12月10日上午10:55:09
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.accident;
import java.util.List;
import java.util.Map;

import com.jsumt.vo.accident.HurtPeopleBean;

/**
 * ClassName:HurtPeopleMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月10日 上午10:55:09 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface HurtPeopleMapper
{
    
    List <HurtPeopleBean> queryAllHurtPeoples (Map<String,Object> mapWhere );
    
    void addHurtPeople (HurtPeopleBean bean);
    
    void updateHurtPeople (HurtPeopleBean bean);
    
    HurtPeopleBean queryOneById (String id);
    
    void delHurtPeoples (List<String> hurtPeopleId);
    
    void delHurtPeoplesBySgbid(List<String> sgId);

}

