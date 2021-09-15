/**
 * Project Name:ECRMS
 * File Name:QuartzJob.java
 * Package Name:com.jsumt.quartz
 * Date:2018年12月28日下午1:11:32
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.quartz;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jsumt.service.system.MessageService;

/**
 * ClassName:QuartzJob <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月28日 下午1:11:32 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
@Component
public class DelMsgExecutor
{
    private static Logger logger = LoggerFactory.getLogger(DelMsgExecutor.class);
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 删除状态为已读的待办消息，和未读状态时间超过15天的待办消息
     * deleteMessage:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @since JDK 1.6
     */
    public void deleteMessage()
    {
        System.err.println(":" + new Date());
        
        logger.info("*************开始执行定时任务DelMsgExecutor**************");
        
        messageService.delMessageShed();
        
        logger.info("*************定时任务执行完成**************");
    }

}

