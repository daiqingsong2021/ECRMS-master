/**
 * Project Name:ECRMS
 * File Name:SafeService.java
 * Package Name:com.jsumt.service.safe
 * Date:2018年12月20日下午1:59:05
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */
package com.jsumt.service.safe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.safe.PlanMapper;
import com.jsumt.service.system.MessageService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.safe.DetailBean;
import com.jsumt.vo.safe.PlanBean;
import com.jsumt.vo.safe.TypeBean;
import com.jsumt.vo.system.MessageBean;
import com.jsumt.vo.system.UserBean;

/**
 * ClassName:SafeService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月20日 下午1:59:05 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class PlanService
{

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private MessageService messageService;

    // 安全生产投入类别表的相关增删改查*******************************************************************
    public List<TypeBean> queryAllTypes(Map<String, Object> whereMap)
    {
        List<TypeBean> queryAllTypes = planMapper.queryAllTypes(whereMap);
        return queryAllTypes;
    }

    // 安全生产投入计划表的相关增删改查******************************************************************
    public List<PlanBean> queryAllPlans(Map<String, Object> whereMap, PageInfo<PlanBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<PlanBean> queryAllPlans = planMapper.queryAllPlans(whereMap);
        return queryAllPlans;
    }

    // 查询没有被分配的所有子单位计划
    public List<Map<String, Object>> queryNotChildrenPlans(Map<String, Object> mapWhere,
            PageInfo<Map<String, Object>> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 查询组织机构的pid
        String tbdwid = mapWhere.get("tbdwid").toString();
        mapWhere.put("org_pid", tbdwid);
        List<Map<String, Object>> listPlan = planMapper.queryNotChildrenPlans(mapWhere);
        return listPlan;
    }

    // 修改安全生产计划的发布状态
    public void updatePlan(PlanBean bean)
    {
        planMapper.updatePlan(bean);
    }

    // 删除安全生产投入计划及与其相关的内容和修改
    public void delPlans(List<Map<String, Object>> delList)
    {
        List<String> ids = Lists.newArrayList();
        for (Map<String, Object> planMap : delList)
        {
            String planId = String.valueOf(planMap.get("id"));
            ids.add(planId);
        }
        // 设置子级单位的parent_id为0即主表生产计划没了则相应的分配也没了
        planMapper.updatePidToZero(ids);
        // 删除细项表的相关类容
        planMapper.delDetailPlans(ids);
        // 删除主表
        planMapper.delPlans(ids);
    }

    // 根据planId查询表数据
    public PlanBean queryOneById(String planId)
    {
        PlanBean bean = planMapper.queryOneById(planId);
        return bean;
    }

    public DetailBean queryDetailById(String detailId)
    {
        DetailBean bean = planMapper.queryDetailById(detailId);
        return bean;
    }

    // 安全生产投入计划增加与细项表的增加******************************************************************
    public void addPlans(PlanBean bean, List<DetailBean> detailBeanList)
    {

        planMapper.addPlan(bean);

        planMapper.addDetailList(detailBeanList);
    }

    // 计划详情页查询
    public List<Map<String, Object>> queryPlanDetailInfo(String planId)
    {

        // TODO Auto-generated method stub
        return planMapper.queryPlanDetailInfo(planId);
    }

    public void updateDetailBean(DetailBean detailBean)
    {
        // 更新detail表
        planMapper.updateDetailBean(detailBean);

    }

    public String updatePlanDetail(String detailId, String bdwPlan)
    {
        // 更新detail
        DetailBean detailBean = this.queryDetailById(detailId);
        BigDecimal detailBdwPlan = new BigDecimal(detailBean.getBdw_plan());
        BigDecimal newDetailBdwPlan = new BigDecimal(bdwPlan);

        DetailBean updateDetail = new DetailBean();
        updateDetail.setId(detailId);
        updateDetail.setBdw_plan(newDetailBdwPlan.toString());// 本单位计划更新
        BigDecimal plan_sum = new BigDecimal(detailBean.getPlan_sum()).add(newDetailBdwPlan.subtract(detailBdwPlan));
        updateDetail.setPlan_sum(plan_sum.toString());// 总计划更新
        planMapper.updateDetailBean(updateDetail);
        // 更新计划PLAN_TOTAL
        PlanBean planBean = this.queryOneById(detailBean.getPlan_id());
        BigDecimal planTotal = new BigDecimal(planBean.getPlan_total()).add(newDetailBdwPlan.subtract(detailBdwPlan));

        PlanBean updatePlan = new PlanBean();
        updatePlan.setId(planBean.getId());
        updatePlan.setPlan_total(planTotal.toString());
        planMapper.updatePlan(updatePlan);
        
        return plan_sum.toString();
    }

    // 查询单个planBean
    public List<PlanBean> queryOnePlan(PlanBean bean)
    {
        return planMapper.queryOnePlan(bean);
    }

    public void cbChildrenPlan(List<Map<String, Object>> cbLists, UserBean user)
    {

        List<MessageBean> messageBeans = Lists.newArrayList();
        // 生成待办消息
        for (Map<String, Object> map : cbLists)
        {
            String orgId = String.valueOf(map.get("orgId"));
            String orgName = String.valueOf(map.get("orgName"));
            String msg = String.valueOf(map.get("msg"));
            String receiveId = "";
            String receiver = "";
            List<Map<String, Object>> listMap = orgService.queryOrgUsersByOrgId(orgId);
            for (Map<String, Object> userMap : listMap)
            {
                String userRoleCodes = String.valueOf(userMap.get("USERROLE"));
                if(userRoleCodes.indexOf("ngaqglry")> -1 || userRoleCodes.indexOf("xmbzygcs")> -1 || userRoleCodes.indexOf("xmbaqglry")> -1 || userRoleCodes.indexOf("fbaqglry")> -1)
                {
                    receiveId = String.valueOf(userMap.get("USER_ID"));
                    receiver = String.valueOf(userMap.get("USER_NAME"));

                    MessageBean message = new MessageBean();
                    message.setCreateTime(new Date());
                    message.setUpdateTime(new Date());
                    message.setMessage(msg);
                    message.setReceiver(receiver);
                    message.setReceiverId(receiveId);
                    message.setSender(user.getUser_name());
                    message.setSenderId(user.getUser_id());
                    message.setSendOrg(user.getOrgName());
                    message.setReceiveOrg(orgName);
                    message.setStatus("0");
                    message.setId(UUIDHexGenerator.generator());
                    messageBeans.add(message);
                }
            }

        }
        if (!messageBeans.isEmpty())
            messageService.addMessageBatch(messageBeans);

    }

    //根据单位和年查询图标的计划总额与实际总额
    public List<Map<String, Object>> queryAqysctrChart(Map<String, Object> mapWhere)
    {
        List<Map<String, Object>> list=planMapper.queryAqysctrChart(mapWhere);
        return list;
    }

}
