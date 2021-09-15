/**
 * Project Name:ECRMS
 * File Name:MessageMapper.java
 * Package Name:com.jsumt.mapper.system
 * Date:2018年12月27日上午9:43:00
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.system.MessageBean;

/**
 * ClassName:MessageMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月27日 上午9:43:00 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface MessageMapper
{

    List<MessageBean> queryMessage(Map<String, Object> mapWhere);

    void updateMessageStatus(List<Map<String, Object>> updateList);

    void addMessage(MessageBean messageBean);

    void addMessageBatch(List<MessageBean> messageBeans);

    void delMessageShed();
   
}

