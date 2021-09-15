/**
 * Project Name:ECRMS
 * File Name:TroubleQuestionBean.java
 * Package Name:com.jsumt.vo.trouble
 * Date:2018年12月7日下午1:39:10
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.vo.trouble;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName:隐患问题bean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月7日 下午1:39:10 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class TroubleQuestionBean implements Serializable
{
    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     * 
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String yhId;
    private String yhzzId;
    private String title;// 问题标题
    private String content;// 问题内容:意见、整改回复、复查回复
    private String yhLevel;//隐患级别
    private String yyfx;//原因分析
    private String isPass;//是否通过 Y是 N否
    private Date createTime;
    private Date updateTime;
    private List<String> fileIds;//文件IDs

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getYhId()
    {
        return yhId;
    }

    public void setYhId(String yhId)
    {
        this.yhId = yhId;
    }

    public String getYhzzId()
    {
        return yhzzId;
    }

    public void setYhzzId(String yhzzId)
    {
        this.yhzzId = yhzzId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getYhLevel()
    {
        return yhLevel;
    }

    public void setYhLevel(String yhLevel)
    {
        this.yhLevel = yhLevel;
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

    public String getYyfx()
    {
        return yyfx;
    }

    public void setYyfx(String yyfx)
    {
        this.yyfx = yyfx;
    }

    public String getIsPass()
    {
        return isPass;
    }

    public void setIsPass(String isPass)
    {
        this.isPass = isPass;
    }

    public List<String> getFileIds()
    {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds)
    {
        this.fileIds = fileIds;
    }
}
