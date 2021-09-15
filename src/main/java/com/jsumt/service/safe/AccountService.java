/**
 * Project Name:ECRMS
 * File Name:AccountService.java
 * Package Name:com.jsumt.service.safe
 * Date:2019年1月4日下午4:45:54
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.safe;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.safe.AccountMapper;
import com.jsumt.service.file.FileService;
import com.jsumt.service.system.MessageService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.safe.AccountBean;
import com.jsumt.vo.safe.DetailAccountBean;
import com.jsumt.vo.system.MessageBean;
import com.jsumt.vo.system.UserBean;

/**
 * ClassName:AccountService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2019年1月4日 下午4:45:54 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class AccountService
{
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private OrganizationService orgService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private FileService fileService;   

    // 查询出没有被分配过的计划台账
    public List<Map<String, Object>> queryScjhtzNotInBatch(Map<String, Object> mapWhere)
    {
        return accountMapper.queryScjhtzNotInBatch(mapWhere);
    }

    // 添加生产计划台账
    public void addSctz(AccountBean bean)
    {
        accountMapper.addSctz(bean);
    }

    // 查询所有台账
    public List<AccountBean> queryAllTzs(Map<String, Object> mapWhere, PageInfo<AccountBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<AccountBean> queryAllTzs = accountMapper.queryAllTzs(mapWhere);
        return queryAllTzs;
    }

    // 发布台账状态
    public void updateAccount(AccountBean bean)
    {
        accountMapper.updateAccount(bean);
    }

    // 查询没有被分配的所有子单位计划
    public List<Map<String, Object>> queryNotChildrenTzs(Map<String, Object> mapWhere,
            PageInfo<Map<String, Object>> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 查询生产计划的pid
        String tbdwid = mapWhere.get("tbdwid").toString();
        mapWhere.put("org_pid", tbdwid);
        List<Map<String, Object>> listTz = accountMapper.queryNotChildrenTzs(mapWhere);
        return listTz;
    }

    // 催报
    public void cbChildrenTz(List<Map<String, Object>> cbLists, UserBean userBean)
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
                    message.setSender(userBean.getUser_name());
                    message.setSenderId(userBean.getUser_id());
                    message.setSendOrg(userBean.getOrgName());
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

    // 删除安全安全投入台账及与其相关的内容和修改
    public void delTzList(List<Map<String, Object>> delelteList)
    {
        List<String> ids = Lists.newArrayList();
        for (Map<String, Object> tzMap : delelteList)
        {
            String tzId = String.valueOf(tzMap.get("id"));
            ids.add(tzId);
        }
        // 设置子级单位的parent_id为0即主表生产计划没了则相应的分配也没了
        accountMapper.updatePidToZero(ids);
        // 删除细项表的相关类容
        accountMapper.delDetailTzs(ids);
        // 删除主表
        accountMapper.delTzs(ids);
    }

    public List<DetailAccountBean> queryTzDetailInfo(Map<String, Object> mapWhere, PageInfo<DetailAccountBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<DetailAccountBean> detailList = accountMapper.queryTzDetailInfo(mapWhere);
        return detailList;
    }

    // 生产台账清单
    public void addTzQd(DetailAccountBean deailBean)
    {
        // 查询出台账表目前实际金额数目queryAllTzs
        Map<String, Object> map = new HashMap<String, Object>();
        String tzId = deailBean.getTzId();
        map.put("id", tzId);
        List<AccountBean> queryAllTzs = accountMapper.queryAllTzs(map);
        // 获取台账表的计划金额、实际金额、费用偏差
        AccountBean accountBean = queryAllTzs.get(0);
        String finish = accountBean.getTrjhTotalFinish();
        String pc = accountBean.getTrjhFypc();
        BigDecimal finishMoney = new BigDecimal(finish);
        BigDecimal fypc = new BigDecimal(pc);
        // 对台账表的实际金额进行计算
        String je = deailBean.getJe();
        finishMoney = finishMoney.add(new BigDecimal(je));
        // 对台账表进行费用偏差进行计算
        fypc = fypc.add(new BigDecimal(je));
        AccountBean bean = new AccountBean();
        bean.setId(tzId);
        bean.setTrjhTotalFinish(finishMoney.toString());
        bean.setTrjhFypc(fypc.toString());
        // 更新台账表
        accountMapper.updateAccount(bean);
        accountMapper.addTzQd(deailBean);
    }

    // 修改清单数据
    public void updateTzQd(DetailAccountBean deailBean)
    {
        String tzQdId = deailBean.getId();
        deailBean.setId(tzQdId);
        // 查询出台账表目前实际金额数目queryAllTzs
        Map<String, Object> map = new HashMap<String, Object>();
        String tzId = deailBean.getTzId();
        map.put("id", tzId);
        List<AccountBean> queryAllTzs = accountMapper.queryAllTzs(map);
        AccountBean accountBean = queryAllTzs.get(0);
        // 获取台账表的计划金额、实际金额、费用偏差
        String finish = accountBean.getTrjhTotalFinish();
        String pc = accountBean.getTrjhFypc();
        BigDecimal finishMoney = new BigDecimal(finish);
        BigDecimal fypc = new BigDecimal(pc);
        // 对台账表的实际金额进行计算 和 对清单表的数据金额计算
        // 对清单表的数据金额计算
        // 获取前端的金额数
        String je = deailBean.getJe();
        BigDecimal JEQ = new BigDecimal(je);
        // 获取后端这个台账清单的金额数
        Map<String, Object> detaiMap = new HashMap<String, Object>();
        String id = deailBean.getId();
        detaiMap.put("id", id);
        DetailAccountBean bean = accountMapper.queryTzDetailInfo(detaiMap).get(0);
        BigDecimal JEH = new BigDecimal(bean.getJe());

        // 对台账表的实际金额进行计算
        finishMoney = finishMoney.add(JEQ.subtract(JEH));
        // 对台账表进行费用偏差进行计算
        fypc = fypc.add(JEQ.subtract(JEH));

        // 创建台账表
        AccountBean Tzbean = new AccountBean();
        Tzbean.setId(tzId);
        Tzbean.setTrjhTotalFinish(finishMoney.toString());
        Tzbean.setTrjhFypc(fypc.toString());
        // 更新台账表
        accountMapper.updateAccount(Tzbean);
        accountMapper.updateTzQd(deailBean);
    }

    // 删除安全安全投入台账清单及与其相关的内容和修改
    public void delTzQd(List<Map<String, Object>> delelteList, String tzId)
    {
        List<String> ids = Lists.newArrayList();
        // 删除台账表的逻辑：修改台账表的费用偏差及实际金额，然后才是删除台账清单表
        BigDecimal je = new BigDecimal("0");
        for (Map<String, Object> tzMap : delelteList)
        {
            String id = String.valueOf(tzMap.get("id"));
            ids.add(id);
            // 从前端获取金额数量，然后对台账表进行相应的修改
            je = je.add(new BigDecimal(String.valueOf(tzMap.get("je"))));
        }
        // 删除台账表后相应的金额
        // 获取台账表的相关信息
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", tzId);
        AccountBean accountBean = accountMapper.queryAllTzs(map).get(0);
        // 台账表的计划金额、实际金额、费用偏差
        String finish = accountBean.getTrjhTotalFinish();
        String pc = accountBean.getTrjhFypc();
        BigDecimal finishMoney = new BigDecimal(finish);
        BigDecimal fypc = new BigDecimal(pc);
        // 对台账表的实际金额进行计算
        finishMoney = finishMoney.add(new BigDecimal("0").subtract(je));
        // 对台账表的费用偏差进行计算
        fypc = fypc.add(new BigDecimal("0").subtract(je));
        // 创建台账表
        AccountBean Tzbean = new AccountBean();
        Tzbean.setId(tzId);
        Tzbean.setTrjhTotalFinish(finishMoney.toString());
        Tzbean.setTrjhFypc(fypc.toString());
        //删除文件
        fileService.delFilesByBussIds(ids);
        // 更新台账表
        accountMapper.updateAccount(Tzbean);
        // 删除细项表的相关类容
        accountMapper.delDetailTzs(ids);
    }

    public AccountBean queryTzById(String tzId)
    {
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("id", tzId);
        List<AccountBean> list = accountMapper.queryAllTzs(mapWhere);
        if (!list.isEmpty())
            return list.get(0);
        else
            return null;

    }

    public List<DetailAccountBean> qryReportLjInfo(Map<String, Object> mapWhere)
    {
        List<DetailAccountBean> detailBeans=Lists.newArrayList();
        detailBeans=accountMapper.qryReportLjInfo(mapWhere);
        return detailBeans;
    }

}
