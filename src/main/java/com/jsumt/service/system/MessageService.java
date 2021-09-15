/**
 * Project Name:ECRMS
 * File Name:MessageService.java
 * Package Name:com.jsumt.service.system
 * Date:2018年12月27日下午8:51:42
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.service.system;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jsumt.mapper.system.MessageMapper;
import com.jsumt.vo.system.MessageBean;

/**
 * ClassName:MessageService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月27日 下午8:51:42 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
@Service
public class MessageService
{
    @Autowired
    private MessageMapper messageMapper;
    
    public List<MessageBean> queryMessage(Map<String, Object> mapWhere,PageInfo<MessageBean>pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List <MessageBean> list = messageMapper.queryMessage(mapWhere);
        return list;
    }


    public void updateMessageStatus(List<Map<String, Object>> updateList)
    {
        
        messageMapper.updateMessageStatus(updateList);
    }
    
    //增加消息
    public void addMessage(MessageBean messageBean)
    {
        messageMapper.addMessage(messageBean);
        
    }
    
    public void addMessageBatch(List<MessageBean> messageBeans)
    {
        messageMapper.addMessageBatch(messageBeans);
        
    }


    public void delMessageShed()
    {
        
        messageMapper.delMessageShed();
    }
}

