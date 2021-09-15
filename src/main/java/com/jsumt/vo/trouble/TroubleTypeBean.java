/**
 * 
 */
package com.jsumt.vo.trouble;

import java.io.Serializable;
import java.util.Date;

/**
 * 隐患类别Bean
 * ClassName:TroubleTypeBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月9日 上午9:47:09 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
public class TroubleTypeBean implements Serializable
{
    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String pcxm;
    private String pcnr;
    private String yhClyj;
    private String yhLevel;
    private String yhPc;
    private String pid;
    private String yhNo;
    private String yhLayer;
    private String iconCls;
    private String isLeaf;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private String moduleId;
    

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPcxm()
    {
        return pcxm;
    }

    public void setPcxm(String pcxm)
    {
        this.pcxm = pcxm;
    }

    public String getPcnr()
    {
        return pcnr;
    }

    public void setPcnr(String pcnr)
    {
        this.pcnr = pcnr;
    }

    public String getYhClyj()
    {
        return yhClyj;
    }

    public void setYhClyj(String yhClyj)
    {
        this.yhClyj = yhClyj;
    }

    public String getYhLevel()
    {
        return yhLevel;
    }

    public void setYhLevel(String yhLevel)
    {
        this.yhLevel = yhLevel;
    }

    public String getYhPc()
    {
        return yhPc;
    }

    public void setYhPc(String yhPc)
    {
        this.yhPc = yhPc;
    }

    public String getPid()
    {
        return pid;
    }

    public void setPid(String pid)
    {
        this.pid = pid;
    }

    public String getYhNo()
    {
        return yhNo;
    }

    public void setYhNo(String yhNo)
    {
        this.yhNo = yhNo;
    }

    public String getYhLayer()
    {
        return yhLayer;
    }

    public void setYhLayer(String yhLayer)
    {
        this.yhLayer = yhLayer;
    }

    public String getIconCls()
    {
        return iconCls;
    }

    public void setIconCls(String iconCls)
    {
        this.iconCls = iconCls;
    }

    public String getIsLeaf()
    {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf)
    {
        this.isLeaf = isLeaf;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public String getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(String moduleId)
    {
        this.moduleId = moduleId;
    }


}
