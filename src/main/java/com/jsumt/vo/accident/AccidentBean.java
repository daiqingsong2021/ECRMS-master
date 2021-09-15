package com.jsumt.vo.accident;

import java.util.Date;

/**
 * ClassName:AccidentBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 上午11:23:04 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */

public class AccidentBean
{
    
    private String id;
    private String title;
    private String sgdw_id;// 事故单位id
    private String sgdw;// 发生事故单位
    private Date sg_time;// 事故发生时间
    private String sgdd;// 事故发生地点
    private String gjss;// 估计损失
    private String sjxcfzr;// 事故现场负责人
    private String sgxcfzrdh;// 事故现场负责人电话
    private String sgfzr;// 事故单位负责人
    private String sgfzrdh;// 事故单位负责人电话

    private Date createTime;
    private Date updateTime;
    
    private Integer dead_people;
    private Integer hurt_people;
    private Integer light_people;
    private String sgjyjg;// 事故简要经过
    private String sgxcqk;// 事故现场情况
    private String qtqk;// 其他情况
    private String sg_type;// 事故类型0为南轨，1为全国
    private String sglb;/*
                         * '0 触电事故\r\n 1 火灾事故\r\n 2 高处坠落事故\r\n
                         * 3 物体打击事故\r\n 4 中毒事故\r\n 5 起重吊装事故\r\n 6 爆炸事故\r\n
                         * 7 坍塌事故\r\n 8 淹溺事故\r\n
                         */

    private String sgjghyjjyqk;// 事故经过和应急救援情况
    private String sgyyxzzrrdcl;// 事故原因性质责任认定处理
    private String sgffjzgcs;// 事故防范及整改措施
    private String remark;// 备注
    private String org_id;// 组织机构ID
    
    
    public String getSgxcfzrdh()
    {
        return sgxcfzrdh;
    }

    public void setSgxcfzrdh(String sgxcfzrdh)
    {
        this.sgxcfzrdh = sgxcfzrdh;
    }

    public String getSgfzr()
    {
        return sgfzr;
    }

    public void setSgfzr(String sgfzr)
    {
        this.sgfzr = sgfzr;
    }

    public String getSgfzrdh()
    {
        return sgfzrdh;
    }

    public void setSgfzrdh(String sgfzrdh)
    {
        this.sgfzrdh = sgfzrdh;
    }

    public String getSglb()
    {
        return sglb;
    }

    public void setSglb(String sglb)
    {
        this.sglb = sglb;
    }

    public Integer getDead_people()
    {
        return dead_people;
    }

    public void setDead_people(Integer dead_people)
    {
        this.dead_people = dead_people;
    }

    public Integer getHurt_people()
    {
        return hurt_people;
    }

    public void setHurt_people(Integer hurt_people)
    {
        this.hurt_people = hurt_people;
    }

    public Integer getLight_people()
    {
        return light_people;
    }

    public void setLight_people(Integer light_people)
    {
        this.light_people = light_people;
    }

    public String getSgjyjg()
    {
        return sgjyjg;
    }

    public void setSgjyjg(String sgjyjg)
    {
        this.sgjyjg = sgjyjg;
    }

    public String getSgxcqk()
    {
        return sgxcqk;
    }

    public void setSgxcqk(String sgxcqk)
    {
        this.sgxcqk = sgxcqk;
    }

    public String getQtqk()
    {
        return qtqk;
    }

    public void setQtqk(String qtqk)
    {
        this.qtqk = qtqk;
    }

    public String getSgjghyjjyqk()
    {
        return sgjghyjjyqk;
    }

    public void setSgjghyjjyqk(String sgjghyjjyqk)
    {
        this.sgjghyjjyqk = sgjghyjjyqk;
    }

    public String getSgyyxzzrrdcl()
    {
        return sgyyxzzrrdcl;
    }

    public void setSgyyxzzrrdcl(String sgyyxzzrrdcl)
    {
        this.sgyyxzzrrdcl = sgyyxzzrrdcl;
    }

    public String getSgffjzgcs()
    {
        return sgffjzgcs;
    }

    public void setSgffjzgcs(String sgffjzgcs)
    {
        this.sgffjzgcs = sgffjzgcs;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSgdw_id()
    {
        return sgdw_id;
    }

    public void setSgdw_id(String sgdw_id)
    {
        this.sgdw_id = sgdw_id;
    }

    public String getSgdw()
    {
        return sgdw;
    }

    public void setSgdw(String sgdw)
    {
        this.sgdw = sgdw;
    }

    public String getSgdd()
    {
        return sgdd;
    }

    public void setSgdd(String sgdd)
    {
        this.sgdd = sgdd;
    }

    public String getOrg_id()
    {
        return org_id;
    }

    public void setOrg_id(String org_id)
    {
        this.org_id = org_id;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getSg_type()
    {
        return sg_type;
    }

    public void setSg_type(String sg_type)
    {
        this.sg_type = sg_type;
    }

    public String getGjss()
    {
        return gjss;
    }

    public void setGjss(String gjss)
    {
        this.gjss = gjss;
    }

    public Date getSg_time()
    {
        return sg_time;
    }

    public void setSg_time(Date sg_time)
    {
        this.sg_time = sg_time;
    }

    public String getSjxcfzr()
    {
        return sjxcfzr;
    }

    public void setSjxcfzr(String sjxcfzr)
    {
        this.sjxcfzr = sjxcfzr;
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
