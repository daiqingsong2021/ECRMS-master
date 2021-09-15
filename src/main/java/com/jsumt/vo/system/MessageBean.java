/**
 * Project Name:ECRMS
 * File Name:MessageBean.java
 * Package Name:com.jsumt.vo.system
 * Date:2018年12月27日下午8:45:48
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.system;

import java.util.Date;

/**
 * ClassName:MessageBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月27日 下午8:45:48 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class MessageBean
{
    private String id;
    private String message;
    private String sender;
    private String senderId;
    private String receiver;
    private String receiverId;
    private String status;// 1已读 0未读
    private String sendOrg;
    private String receiveOrg;
    private Date createTime;
    private Date updateTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    
    public String getSendOrg()
    {
        return sendOrg;
    }

    public void setSendOrg(String sendOrg)
    {
        this.sendOrg = sendOrg;
    }

    public String getReceiveOrg()
    {
        return receiveOrg;
    }

    public void setReceiveOrg(String receiveOrg)
    {
        this.receiveOrg = receiveOrg;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getReceiverId()
    {
        return receiverId;
    }

    public void setReceiverId(String receiverId)
    {
        this.receiverId = receiverId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

}
