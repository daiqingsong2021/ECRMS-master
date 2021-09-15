/**
 * Project Name:ECRMS
 * File Name:TroubleService.java
 * Package Name:com.jsumt.service.trouble
 * Date:2018年11月28日上午10:21:37
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.trouble;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.file.FileMapper;
import com.jsumt.mapper.trouble.TroubleMapper;
import com.jsumt.service.file.FileService;
import com.jsumt.service.system.MessageService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.*;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.vo.file.BizFileBean;
import com.jsumt.vo.file.FileBean;
import com.jsumt.vo.file.FileNewBean;
import com.jsumt.vo.system.MessageBean;
import com.jsumt.vo.system.UserBean;
import com.jsumt.vo.trouble.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * ClassName:TroubleService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 上午10:21:37 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class TroubleService
{

    @Autowired
    private TroubleMapper troubleMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private OrganizationService organService;

    @Autowired
    private UserManageService userService;

    @Autowired
    private MessageService messageService;

    /**
     * 查询隐患类别
     * queryYhTypes:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param mapWhere
     * @return
     * @since JDK 1.6
     */
    public List<TroubleTypeBean> queryYhTypes(Map<String, Object> mapWhere)
    {
        List<TroubleTypeBean> troubleTypeList = troubleMapper.queryYhTypes(mapWhere);
        return troubleTypeList;
    }

    public List<TroubleModuleBean> queryAllYhMb(Map<String, Object> mapWhere)
    {

        List<TroubleModuleBean> troubleModuleList = troubleMapper.queryAllYhMb(mapWhere);
        return troubleModuleList;
    }

    /**
     * 增加隐患模板
     * addYhMb:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param moduleName
     * @param importMbId
     * @param userBean
     * @since JDK 1.6
     */
    public TroubleModuleBean addYhMb(String moduleName, String importMbId, UserBean userBean)
    {
        // 新建module
        String moduleId = UUIDHexGenerator.generator();
        TroubleModuleBean troubleModuleBean = new TroubleModuleBean();
        troubleModuleBean.setCreater(userBean.getUser_id());
        troubleModuleBean.setOrgId(userBean.getOrgId());
        troubleModuleBean.setCreateTime(new Date());
        troubleModuleBean.setId(moduleId);
        troubleModuleBean.setModuleName(moduleName);
        troubleModuleBean.setUpdateTime(new Date());
        troubleMapper.addYhmb(troubleModuleBean);
        
        //模板缓存的存储
       /* RedisUtil.getRedisUtil().*/
       /* RedisUtil.getRedisUtil().setStringValue(moduleId, moduleId);
        RedisUtil.getRedisUtil().setStringValue("CREATER", userBean.getUser_id());
        RedisUtil.getRedisUtil().setStringValue("ORG_ID", userBean.getOrgId());
        RedisUtil.getRedisUtil().setStringValue("CREATE_TIME", DateUtil.getDateFormat(new Date(), DateUtil.DATETIME_DEFAULT_FORMAT));
        RedisUtil.getRedisUtil().setStringValue("MODULE_NAME", moduleName);
        RedisUtil.getRedisUtil().setStringValue("UPDATE_TIME", DateUtil.getDateFormat(new Date(), DateUtil.DATETIME_DEFAULT_FORMAT));*/

        // 定义批量插入List
        List<TroubleTypeBean> insertTypes = Lists.newArrayList();
        if (StringHelper.isNotNullAndEmpty(importMbId))
        {// 如果导入隐患模板ID不为空
         // 查出被导入模板下的所有隐患类别
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("moduleId", importMbId);
            List<TroubleTypeBean> troubleTypeBeans = this.queryYhTypes(mapWhere);
            for (TroubleTypeBean troubleTypeBean : troubleTypeBeans)
            {
                if ("0".equals(troubleTypeBean.getPid()))
                {// 根节点.根节点无实际意义，为模板名称
                    TroubleTypeBean insertBean = new TroubleTypeBean();
                    BeanUtil.copyProperties(insertBean, troubleTypeBean);
                    String id = UUIDHexGenerator.generator();
                    insertBean.setId(id);
                    insertBean.setModuleId(moduleId);
                    insertBean.setPcxm(troubleModuleBean.getModuleName());
                    insertTypes.add(insertBean);
                    insertChildren(troubleTypeBeans, troubleTypeBean.getId(), id, moduleId, insertTypes);
                }
            }
        }
        else
        {// 隐患类别新增一条根节点
            TroubleTypeBean insertBean = new TroubleTypeBean();
            insertBean.setId(UUIDHexGenerator.generator());
            insertBean.setCreateTime(new Date());
            insertBean.setIconCls(IconClass.ICON_EMPTY.toString());
            insertBean.setIsLeaf("1");
            insertBean.setModuleId(moduleId);
            insertBean.setPid("0");
            insertBean.setUpdateTime(new Date());
            insertBean.setYhLayer("1");// 顶层节点第一层
            insertBean.setYhNo("1");
            insertBean.setPcxm(troubleModuleBean.getModuleName());
            insertTypes.add(insertBean);
        }
        // 批量插入隐患类别
        troubleMapper.batchInsertTypes(insertTypes);
        return troubleModuleBean;
    }

    /**
     * insertChildren:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param troubleTypeBeans 所有记录
     * @param pid 父ID
     * @param
     * @param moduleId 模板Id
     * @since JDK 1.6
     */
    private void insertChildren(List<TroubleTypeBean> troubleTypeBeans, String pid, String newParentId, String moduleId,
            List<TroubleTypeBean> insertTypes)
    {

        for (TroubleTypeBean troubleTypeBean : troubleTypeBeans)
        {
            if (pid.equals(troubleTypeBean.getPid()))
            {
                TroubleTypeBean insertBean = new TroubleTypeBean();
                BeanUtil.copyProperties(insertBean, troubleTypeBean);
                String id = UUIDHexGenerator.generator();
                insertBean.setId(id);
                insertBean.setPid(newParentId);
                insertBean.setModuleId(moduleId);
                insertTypes.add(insertBean);
                insertChildren(troubleTypeBeans, troubleTypeBean.getId(), id, moduleId, insertTypes);
            }
        }

    }

    public boolean isExistModule(String moduleName)
    {
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("moduleName", moduleName);
        TroubleModuleBean troubleModuleBean = troubleMapper.queryModuleByNameOrId(mapWhere);
        if (troubleModuleBean != null)
            return true;
        else
            return false;
    }

    public TroubleModuleBean queryModuleByNameOrId(Map<String, Object> mapWhere)
    {
        TroubleModuleBean troubleModuleBean = troubleMapper.queryModuleByNameOrId(mapWhere);
        return troubleModuleBean;
    }

    public void delTroubleModule(String moduleId)
    {
        // 删除隐患模板下的所有隐患类别
        troubleMapper.delYhTypeByModuleId(moduleId);
        // 删除隐患模板
        troubleMapper.delModuleById(moduleId);
    }

    public void updateYhMb(String moduleId, String moduleName)
    {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", moduleId);
        map.put("moduleName", moduleName);
        troubleMapper.updateYhMb(map);
    }

    public void addYhlb(TroubleTypeBean bean)
    {
        // 查询父节点层级
        TroubleTypeBean paraent = troubleMapper.queryThTypeById(bean.getPid());
        paraent.setIsLeaf("0");// 更新父节点IS_LEAF
        paraent.setIconCls(IconClass.ICON_EMPTY.toString());
        troubleMapper.updateYhlb(paraent);

        bean.setYhLayer(String.valueOf(Integer.valueOf(paraent.getYhLayer()) + 1));
        bean.setIsLeaf("1");// 默认设为1
        bean.setIconCls(IconClass.ICON_EMPTY.toString());
        // 查询最大序号
        String maxNo = this.queryMaxNo(bean.getPid());
        if (StringUtils.isEmpty(maxNo))
            bean.setYhNo("1");
        else
            bean.setYhNo(String.valueOf(Integer.valueOf(maxNo) + 1));

        List<TroubleTypeBean> insertTypes = Lists.newArrayList();
        insertTypes.add(bean);
        troubleMapper.batchInsertTypes(insertTypes);
    }

    private String queryMaxNo(String pid)
    {
        String maxNo = "";
        maxNo = troubleMapper.queryMaxNo(pid);
        return maxNo;
    }

    public void delYhlbById(String id)
    {
        List<String> deleteIds = Lists.newArrayList();
        TroubleTypeBean troubleTypeBean = troubleMapper.queryThTypeById(id);
        String parentId = troubleTypeBean.getPid();

        List<TroubleTypeBean> troubleTypeBeans = troubleMapper.queryYhTypes(null);
        deleteIds.add(id);
        findDelTypeChildren(deleteIds, troubleTypeBeans, id);

        troubleMapper.delYhType(deleteIds);

        // 更新父节点IS_LEAF 和 iconCls
        if (!"0".equals(parentId))
        {// 如果所删节点不是顶层节点
            TroubleTypeBean parent = troubleMapper.queryThTypeById(parentId);
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("pid", parent.getId());
            if (troubleMapper.queryYhTypes(mapWhere).isEmpty())
            {// 如果所删节点父节点不存在子节点
                parent.setIsLeaf("1");// 更新父节点IS_LEAF
                parent.setIconCls(IconClass.ICON_EMPTY.toString());
                troubleMapper.updateYhlb(parent);
            }
        }
    }

    private void findDelTypeChildren(List<String> deleteIds, List<TroubleTypeBean> troubleTypeBeans, String pid)
    {
        for (TroubleTypeBean bean : troubleTypeBeans)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getPid()) && pid.equals(bean.getPid()))
            {
                deleteIds.add(bean.getId());
                findDelTypeChildren(deleteIds, troubleTypeBeans, bean.getId());
            }
        }

    }

    public void updateYhlb(TroubleTypeBean bean)
    {
        troubleMapper.updateYhlb(bean);
    }

    /**
     * 查询所有隐患List
     * queryAllTroubleList:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param mapWhere
     * @param pageInfo
     * @return
     * @since JDK 1.6
     */
    public List<TroubleBean> queryAllTroubleList(Map<String, Object> mapWhere, PageInfo<TroubleBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<TroubleBean> troubleList = troubleMapper.queryAllTroubleList(mapWhere);
        return troubleList;
    }

    public List<TroubleBean> queryDclYhList(Map<String, Object> mapWhere, PageInfo<TroubleBean> pageInfo)
    {

        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<TroubleBean> troubleList = troubleMapper.queryDclYhList(mapWhere);
        return troubleList;
    }

    public void updateYhlbByModuleId(TroubleTypeBean bean)
    {
        troubleMapper.updateYhlbByModuleId(bean);
    }

    /**
     * 增加隐患
     * addTrouble:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param troubleBean
     * @since JDK 1.6
     */
    public void addTrouble(TroubleBean troubleBean, TroubleZzBean troubleZzBean,
            List<TroubleQuestionBean> quesTionBeanList)
    {
        // 增加隐患表
        String troubelId = troubleBean.getId();
        troubleMapper.addTrouble(troubleBean);
        // 增加隐患追踪表
        String troubelzzId = UUIDHexGenerator.generator();
        troubleZzBean.setId(troubelzzId);
        troubleZzBean.setYhId(troubelId);
        troubleMapper.addTroubleZz(troubleZzBean);

        List<BizFileBean> bizFileBeanList=Lists.newArrayList();
        for (TroubleQuestionBean troubleQuestionBean : quesTionBeanList)
        {
             troubleQuestionBean.setYhId(troubelId);
             troubleQuestionBean.setYhzzId(troubelzzId);
             List<String> fileIds=troubleQuestionBean.getFileIds();
             for(String fileId:fileIds)
             {
                 BizFileBean bizFileBean=new BizFileBean();
                 bizFileBean.setId(UUIDHexGenerator.generator());
                 bizFileBean.setBizId(troubleQuestionBean.getId());
                 bizFileBean.setFileId(fileId);
                 bizFileBeanList.add(bizFileBean);
             }
        }
        if (!quesTionBeanList.isEmpty())
            troubleMapper.addTroubleQuestBatch(quesTionBeanList);
        if(!bizFileBeanList.isEmpty())//文件绑定
            fileService.saveBindBussFile(bizFileBeanList);
    }

    /**
     * 增加隐患追踪
     * addTroubleZz:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param troubleZzBean
     * @since JDK 1.6
     */
    public void addTroubleZz(TroubleZzBean troubleZzBean)
    {

        troubleMapper.addTroubleZz(troubleZzBean);

    }

    /**
     * 增加问题
     * addTroubleQuest:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param troubleQuestionBean
     * @since JDK 1.6
     */
    public void addTroubleQuest(TroubleQuestionBean troubleQuestionBean)
    {

        troubleMapper.addTroubleQuest(troubleQuestionBean);

    }

    /**
     * 删除隐患
     * delTrouble:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param
     * @since JDK 1.6
     */
    public void delTrouble(List<String> deleIds)
    {
        // 删除隐患问题
        this.delTrleQueByTrobIds(deleIds);
        // 删除隐患追踪表
        troubleMapper.delTrobbzzByTrobIds(deleIds);
        //删除隐患关联 文件
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("bussinessIds", deleIds);
        List<FileBean> files = fileService.queryAllFiles(mapWhere, null);
        for (FileBean fileBean : files)
        {
            // 删除之前的安全检查记录表，如存在
            fileService.delFileById(fileBean);
            // 同时清空PDF文件
            File file = new File(fileBean.getFileUrl().substring(0, fileBean.getFileUrl().indexOf(".")) + ".pdf");
            if (file.exists())
                file.delete();
        }
        // 删除隐患表
        troubleMapper.delTroubleByIds(deleIds);
    }

    public void delUpdateTrouble(List<String> trobIds)
    {
        // 根据bussids删除文件附件以及图片
        // 根据隐患Ids查出所有问题IDs集合
        List<Map<String, Object>> bussinessIds_ = troubleMapper.queryAllQIdsByTrobIds(trobIds);
        List<String> bussinessIds = Lists.newArrayList();
        for (Map<String, Object> map : bussinessIds_)
        {
            bussinessIds.add(String.valueOf(map.get("ID")));
        }
        if (bussinessIds != null && !bussinessIds.isEmpty())
        {
            //此时只删关联表
            fileService.delBizFileByBussIds(bussinessIds);
            // 删除隐患问题表
            troubleMapper.delTrobQuesByIds(bussinessIds);
        }
        // 删除隐患追踪表
        troubleMapper.delTrobbzzByTrobIds(trobIds);
        //删除隐患关联 文件
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("bussinessIds", trobIds);
        List<FileBean> files = fileService.queryAllFiles(mapWhere, null);
        for (FileBean fileBean : files)
        {
            // 删除之前的安全检查记录表，如存在
            fileService.delFileById(fileBean);
            // 同时清空PDF文件
            File file = new File(fileBean.getFileUrl().substring(0, fileBean.getFileUrl().indexOf(".")) + ".pdf");
            if (file.exists())
                file.delete();
        }
        // 删除隐患表
        troubleMapper.delTroubleByIds(trobIds);
    }

    public void delTrobbzzById(String id)
    {
        // 删除隐患问题
        this.delTrleQueByZzId(id);
        troubleMapper.delTrobbzzById(id);
    }

    private void delTrleQueByZzId(String id)
    {
        List<TroubleQuestionBean> questList = this.queryQuesByZzId(id);
        List<String> bussinessIds = Lists.newArrayList();
        for (TroubleQuestionBean ques : questList)
        {
            bussinessIds.add(String.valueOf(ques.getId()));
        }
        if (bussinessIds != null && !bussinessIds.isEmpty())
        {
            fileService.delFilesByBussIds(bussinessIds);
            // 删除隐患问题表
            troubleMapper.delTrobQuesByIds(bussinessIds);
        }
    }

    /**
     * 删除隐患问题
     * delTrleQueByTrobIds:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param trobIds
     * @since JDK 1.6
     */
    private void delTrleQueByTrobIds(List<String> trobIds)
    {
        // 根据bussids删除文件附件以及图片
        // 根据隐患Ids查出所有问题IDs集合
        List<Map<String, Object>> bussinessIds_ = troubleMapper.queryAllQIdsByTrobIds(trobIds);
        List<String> bussinessIds = Lists.newArrayList();
        for (Map<String, Object> map : bussinessIds_)
        {
            bussinessIds.add(String.valueOf(map.get("ID")));
        }
        if (bussinessIds != null && !bussinessIds.isEmpty())
        {
            fileService.delNewFilesByBizIds(bussinessIds);
            // 删除隐患问题表
            troubleMapper.delTrobQuesByIds(bussinessIds);
        }

    }

    public void publishTrouble(List<Map<String, Object>> publishList, UserBean userBean) throws Exception
    {
        // 生成检查记录表 生产整改通知书
        for (Map<String, Object> map : publishList)
        {
            String troubleId = String.valueOf(map.get("id"));
            // this.geneRateJcjlb(troubleId, userBean);
            this.geneRateZgtzs(troubleId, userBean);
        }
        // 修改隐患表状态为待响应"1"
        troubleMapper.publishTrouble(publishList);

        List<MessageBean> messageBeans = Lists.newArrayList();
        // 生成待办消息
        for (Map<String, Object> map : publishList)
        {
            String troubleId = String.valueOf(map.get("id"));
            TroubleBean trouble = troubleMapper.queryOneTrouble(troubleId);
            MessageBean message = new MessageBean();
            message.setCreateTime(new Date());
            message.setUpdateTime(new Date());
            Date zgqx = trouble.getZgsx();
            if (zgqx == null)
            {
                message.setMessage("经检查，发现相关隐患，标题为\"" + trouble.getTitle() + "\"，请及时响应处理！");
            }
            else
            {
                message.setMessage("经检查，发现相关隐患，标题为\"" + trouble.getTitle() + "\"，请在\""
                        + DateUtil.getDateFormat(trouble.getZgsx(), DateUtil.DATETIME_DEFAULT_FORMAT) + "\"之前响应处理！");
            }
            message.setReceiver(trouble.getSjr());
            message.setReceiverId(trouble.getSjrId());
            message.setSender(trouble.getJcr());
            message.setSenderId(trouble.getJcrId());
            message.setSendOrg(trouble.getJcdw());
            message.setReceiveOrg(trouble.getSjdw());
            message.setStatus("0");
            message.setId(UUIDHexGenerator.generator());
            messageBeans.add(message);

            MessageBean message2 = new MessageBean();
            message2.setCreateTime(new Date());
            message2.setUpdateTime(new Date());
            if (zgqx == null)
            {
                message2.setMessage("经检查，发现相关隐患，标题为\"" + trouble.getTitle() + "\"，您为整改责任人，请及时关注！");
            }
            else
            {
                message2.setMessage("经检查，发现相关隐患，标题为\"" + trouble.getTitle() + "\"，整改时限：\""
                        + DateUtil.getDateFormat(trouble.getZgsx(), DateUtil.DATETIME_DEFAULT_FORMAT)
                        + "\"，您为整改责任人，请及时关注！");
            }
            message2.setReceiver(trouble.getZgzrr());
            message2.setReceiverId(trouble.getZgzrrId());
            message2.setSender(trouble.getJcr());
            message2.setSenderId(trouble.getJcrId());
            message2.setSendOrg(trouble.getJcdw());
            message2.setReceiveOrg(trouble.getSjdw());
            message2.setStatus("0");
            message2.setId(UUIDHexGenerator.generator());
            messageBeans.add(message2);
        }
        if (!messageBeans.isEmpty())
            messageService.addMessageBatch(messageBeans);
    }

    /**
     * 根据隐患Id生成安全隐患整改通知单
     * geneRateZgtzs:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param troubleId
     * @throws Exception
     * @since JDK 1.6
     */
    private void geneRateZgtzs(String troubleId, UserBean userBean) throws Exception
    {
        TroubleBean trouble = troubleMapper.queryOneTrouble(troubleId);
        // 删除之前的安全隐患整改通知单，如存在
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("bussinessId", troubleId);
        List<FileBean> files = fileService.queryAllFiles(mapWhere, null);
        for (FileBean fileBean : files)
        {
            if ("安全隐患整改通知单".equals(fileBean.getFileName()))
            {//
             // 删除之前的安全检查记录表，如存在
                fileService.delFileById(fileBean);
                // 同时清空PDF文件
                // String pdfFile = ConfigUtil.getValueByKey("fileRootUrl") +
                // "doc/zgtzd_" + troubleId + ".pdf";
                // File file = new File(pdfFile);
                File file = new File(fileBean.getFileUrl().substring(0, fileBean.getFileUrl().indexOf(".")) + ".pdf");
                if (file.exists())
                    file.delete();
            }
        }
        // 生成事故隐患整改通知书
        FileBean fileBean = new FileBean();
        String bussinessId = troubleId;
        String fileCatalog = "隐患管理";
        String fileType = "安全检查";
        String orgId = userBean.getOrgId();
        String fileId = UUIDHexGenerator.generator();
        fileBean.setId(fileId);
        fileBean.setScry(userBean.getUser_code());
        fileBean.setScryName(userBean.getUser_name());
        fileBean.setBussinessId(bussinessId);
        fileBean.setCreateTime(new Date());
        fileBean.setUpdateTime(new Date());
        fileBean.setFileCatalog(fileCatalog);
        fileBean.setFileType(fileType);
        fileBean.setOrgId(orgId);
        fileBean.setOrgName(organService.queryOneById(orgId).getName_cn());
        fileBean.setStatus("0");
        // String maxNo = fileService.queryMaxNo(fileCatalog, fileType);
        // if (StringUtils.isEmpty(maxNo))
        // fileBean.setNo(1);
        // else
        // fileBean.setNo(Integer.valueOf(maxNo) + 1);
        String dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + fileCatalog + "/" + fileType;
        File dir = new File(dirUrl);
        // 新建目录
        dir.mkdirs();
        // 新建文件,物理文件重命名以序列号命名
        String fileLx = "docx";
        String fileName = "安全隐患整改通知单";
        // 编写文件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("${gcmc}", trouble.getGcmc());
        params.put("${sjdw}", trouble.getSjdw());
        params.put("${jcdw}", trouble.getJcdw());
        params.put("${yhbh}", "编号:" + trouble.getYhbh());
        UserBean jcrBean = userService.queryUserById(trouble.getJcrId());
        params.put("${jcrAndPhone}", trouble.getJcr() + " " + jcrBean.getPhone());
        if (trouble.getZgsx() == null)
        {
            params.put("${zgsx}", "");
        }
        else
        {
            params.put("${zgsx}", DateUtil.getDateFormat(trouble.getZgsx(), DateUtil.DATETIME_CHINA_FORMAT) + "前");
        }

        params.put("${createTime}", DateUtil.getDateFormat(trouble.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
        // 查出隐患所对应的相对应状态的问题
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();// 经检查存在下列隐患和问题
        List<Map<String, Object>> images = new ArrayList<Map<String, Object>>();// 附件图片
        List<TroubleQuestionBean> questionList = this.queryQuesByTroubId(troubleId, "0");// 查出隐患追踪新建状态的所有问题
        for (int i = 0; i < questionList.size(); i++)
        {
            TroubleQuestionBean question = questionList.get(i);

            Map<String, Object> content = Maps.newHashMap();
            String[] contentArray = new String[2];
            contentArray[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if (StringHelper.isNullAndEmpty(question.getContent()))
            {
                contentArray[1] = "意见:无";
            }
            else
            {
                contentArray[1] = "意见:" + question.getContent();
            }
            content.put("content", contentArray);
            contents.add(content);
            // 查出问题所对应的图片
            List<FileNewBean> imageFiles = fileService.queryNewFileByIds(question.getFileIds());
            for (int ii = 0; ii < imageFiles.size(); ii++)
            {
                FileNewBean imageFile = imageFiles.get(ii);
                File tFile = new File(imageFile.getFileUrl());
                if (tFile.exists())
                {
                    Map<String, Object> image = new HashMap<String, Object>();
                    // "100/150" 300/450
                    image.put("width", 600);
                    image.put("imgDesc", "问题" + (i + 1) + " ，图片" + (ii + 1) + ":");
                    image.put("height", 800);
                    image.put("type", imageFile.getFileLx());
                    image.put("content",
                            WordUtils.inputStream2ByteArray(new FileInputStream(imageFile.getFileUrl()), true));
                    image.put("isImage", "1");
                    images.add(image);
                }
            }
        }
        params.put("${contents}", contents);
        params.put("${images}", images);

        WordUtils wordUtil = new WordUtils();
        String sourceFileUrl = this.getClass().getClassLoader().getResource("").getPath() + "templates/newZgtzs.docx"; // 模板文件位置
        // 隐患下的唯一的安事故隐患整改通知书 生成word
        String destFileUrl = dirUrl + "/安全隐患整改通知单_" + troubleId + "." + fileLx;
        wordUtil.generateWord(sourceFileUrl, params, destFileUrl);
        File destFile = new File(destFileUrl);
        if (destFile.exists())
        {
            // 生成PDF
            String pdfFile = dirUrl + "/安全隐患整改通知单_" + troubleId + ".pdf";
            // String pdfFile = ConfigUtil.getValueByKey("fileRootUrl") +
            // "doc/zgtzd_" + troubleId + ".pdf";
            WordToPdfUtil.office2PDF(destFileUrl, pdfFile);
            fileBean.setFileUrl(destFileUrl);
            fileBean.setFileName(fileName);
            fileBean.setFileLx(fileLx);
            fileService.saveFile(fileBean);
        }

    }

    /**
     * 根据隐患ID以及隐患追踪表状态查询问题列表
     * queryQuesByTroubId:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param troubleId 隐患ID
     * @param yhzzType 隐患追踪状态 0新建 1整改 2复查 3闭环
     * @return
     * @since JDK 1.6
     */
    public List<TroubleQuestionBean> queryQuesByTroubId(String troubleId, String yhzzType)
    {

        List<TroubleQuestionBean> quesList = Lists.newArrayList();
        // 根据隐患Id查询隐患追踪表状态为新建的记录
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("yhId", troubleId);
        mapWhere.put("type", yhzzType);
        // 倒序排序
        List<TroubleZzBean> TroubleZzList = troubleMapper.queryTroublezz(mapWhere);
        if (TroubleZzList != null && !TroubleZzList.isEmpty())
        {
            // 查出最新的追踪信息
            TroubleZzBean troubleZzBean = TroubleZzList.get(0);
            // 根据追踪表Id和隐患ID查询所有问题
            mapWhere.clear();
            mapWhere.put("yhId", troubleId);
            mapWhere.put("yhzzId", troubleZzBean.getId());
            quesList = troubleMapper.queryTroubleQues(mapWhere);
        }

        //初始化问题的文件IDS
        List<String> bizIds=Lists.newArrayList();
        for(TroubleQuestionBean troubleQuestionBean:quesList)
        {
            bizIds.add(troubleQuestionBean.getId());
        }
        List<BizFileBean> bizFileBeanList=fileMapper.queryBizFileBeanByBizIds(bizIds);
        Map<String,List<String>> bizFilesMap=Maps.newHashMap();
        for (BizFileBean bizFileBean : bizFileBeanList)
        {
            List<String> valueList = bizFilesMap.get(bizFileBean.getBizId());
            if (ObjectUtils.isEmpty(valueList))
            {
                valueList = Lists.newArrayList();
                valueList.add(bizFileBean.getFileId());
                bizFilesMap.put(bizFileBean.getBizId(), valueList);
            }
            else
            {
                valueList.add(bizFileBean.getFileId());
            }
        }
        for(TroubleQuestionBean troubleQuestionBean:quesList)
        {
            if(!ObjectUtils.isEmpty(bizFilesMap.get(troubleQuestionBean.getId())))
              troubleQuestionBean.setFileIds(bizFilesMap.get(troubleQuestionBean.getId()));
            else  troubleQuestionBean.setFileIds(Lists.<String>newArrayList());
        }
        return quesList;
    }

    public List<TroubleQuestionBean> queryQuesByZzId(String yhzzId)
    {
        List<TroubleQuestionBean> quesList = Lists.newArrayList();
        // 根据隐患Id查询隐患追踪表状态为新建的记录
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("id", yhzzId);
        // 倒序排序
        List<TroubleZzBean> TroubleZzList = troubleMapper.queryTroublezz(mapWhere);
        if (TroubleZzList != null && !TroubleZzList.isEmpty())
        {
            // 查出最新的追踪信息
            TroubleZzBean troubleZzBean = TroubleZzList.get(0);
            // 根据追踪表Id和隐患ID查询所有问题
            mapWhere.clear();
            mapWhere.put("yhId", troubleZzBean.getYhId());
            mapWhere.put("yhzzId", troubleZzBean.getId());
            quesList = troubleMapper.queryTroubleQues(mapWhere);
        }
        //初始化问题的文件IDS
        List<String> bizIds=Lists.newArrayList();
        for(TroubleQuestionBean troubleQuestionBean:quesList)
        {
            bizIds.add(troubleQuestionBean.getId());
        }
        List<BizFileBean> bizFileBeanList=fileMapper.queryBizFileBeanByBizIds(bizIds);
        Map<String,List<String>> bizFilesMap=Maps.newHashMap();
        for (BizFileBean bizFileBean : bizFileBeanList)
        {
            List<String> valueList = bizFilesMap.get(bizFileBean.getBizId());
            if (ObjectUtils.isEmpty(valueList))
            {
                valueList = Lists.newArrayList();
                valueList.add(bizFileBean.getFileId());
                bizFilesMap.put(bizFileBean.getBizId(), valueList);
            }
            else
            {
                valueList.add(bizFileBean.getFileId());
            }
        }
        for(TroubleQuestionBean troubleQuestionBean:quesList)
        {
            if(!ObjectUtils.isEmpty(bizFilesMap.get(troubleQuestionBean.getId())))
                troubleQuestionBean.setFileIds(bizFilesMap.get(troubleQuestionBean.getId()));
            else  troubleQuestionBean.setFileIds(Lists.<String>newArrayList());
        }
        return quesList;
    }

    /**
     * 删除TroubleQuest
     * queryQuesByTroubId:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param
     * @return
     * @since JDK 1.6
     */
    public void delTroubleQuest(Map<String, Object> mapWhere)
    {
        // 删除文件
        List<String> bussinessIds = Lists.newArrayList();
        bussinessIds.add(String.valueOf(mapWhere.get("id")));
        fileService.delNewFilesByBizIds(bussinessIds);
        // 删除问题
        troubleMapper.delTroubleQuest(mapWhere);

    }

    /**
     * 更新Trouble
     * updateTrouble:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param
     * @return
     * @since JDK 1.6
     */
    public void updateTrouble(TroubleBean troubleBean, List<TroubleQuestionBean> quesTionBeanList)
    {
        if (!quesTionBeanList.isEmpty())
        {
            //更新文件信息
            List<BizFileBean> bizFileBeanList=Lists.newArrayList();
            for (TroubleQuestionBean troubleQuestionBean : quesTionBeanList)
            {
                List<String> fileIds=troubleQuestionBean.getFileIds();
                for(String fileId:fileIds)
                {
                    BizFileBean bizFileBean=new BizFileBean();
                    bizFileBean.setId(UUIDHexGenerator.generator());
                    bizFileBean.setBizId(troubleQuestionBean.getId());
                    bizFileBean.setFileId(fileId);
                    bizFileBeanList.add(bizFileBean);
                }
            }
            if(!ObjectUtils.isEmpty(bizFileBeanList))
                fileService.updateBindBussFile(bizFileBeanList);
            // 批量更新问题
            troubleMapper.updateQuestBatch(quesTionBeanList);
        }

        // 更新Trouble
        troubleMapper.updateTrouble(troubleBean);
    }

    /**
     * 更新Trouble
     * updateTrouble:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param
     * @return
     * @since JDK 1.6
     */
    public void updateTrouble(TroubleBean troubleBean)
    {
        // 更新Trouble
        troubleMapper.updateTrouble(troubleBean);
    }

    public void updateTroubleStatus(Map<String, Object> map)
    {

        troubleMapper.updateTroubleStatus(map);

    }

    public TroubleBean queryOneTrouble(String troubleId)
    {

        return troubleMapper.queryOneTrouble(troubleId);

    }

    /**
     * 查询该隐患下yhzzType的次数
     * queryTroubleZzMaxCs:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param troubleId
     * @param
     * @return
     * @since JDK 1.6
     */
    public String queryTroubleZzMaxCs(String troubleId, String yhzzType)
    {
        String maxCs = "";
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("yhId", troubleId);
        mapWhere.put("type", yhzzType);
        maxCs = troubleMapper.queryTroubleZzMaxCs(mapWhere);
        return maxCs;
    }

    public String queryTroubleZzMaxCs_(String troubleId, String yhzzType)
    {
        String maxCs = "";
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("yhId", troubleId);
        mapWhere.put("type", yhzzType);
        maxCs = troubleMapper.queryTroubleZzMaxCs_(mapWhere);
        return maxCs;
    }
    public void addtroubleClFc(TroubleBean troubleBean, TroubleZzBean troubleZzBean,
            List<TroubleQuestionBean> quesTionBeanList, String isyhCl, String isPass, UserBean userBean)
            throws Exception
    {
        // 1 增加隐患追踪表
        troubleZzBean.setYhId(troubleBean.getId());
        troubleMapper.addTroubleZz(troubleZzBean);
        // 3 增加问题List
        for (TroubleQuestionBean troubleQuestionBean : quesTionBeanList)
        {
            if ("1".equals(isPass) && !"1".equals(isyhCl))
            {// 复查都通过的情况下
                troubleQuestionBean.setIsPass("Y");
            }
            troubleQuestionBean.setYhId(troubleBean.getId());
            troubleQuestionBean.setYhzzId(troubleZzBean.getId());
        }
        List<BizFileBean> bizFileBeanList=Lists.newArrayList();
        for (TroubleQuestionBean troubleQuestionBean : quesTionBeanList)
        {
            List<String> fileIds=troubleQuestionBean.getFileIds();
            for(String fileId:fileIds)
            {
                BizFileBean bizFileBean=new BizFileBean();
                bizFileBean.setId(UUIDHexGenerator.generator());
                bizFileBean.setBizId(troubleQuestionBean.getId());
                bizFileBean.setFileId(fileId);
                bizFileBeanList.add(bizFileBean);
            }
        }
        if (!quesTionBeanList.isEmpty())
            troubleMapper.addTroubleQuestBatch(quesTionBeanList);

        if(!bizFileBeanList.isEmpty())//文件绑定
            fileService.saveBindBussFile(bizFileBeanList);
        // 4 更新隐患表
        troubleMapper.updateTrouble(troubleBean);
        if (!"1".equals(isyhCl))
        {
            addRateFkbgResult(troubleBean.getId(), userBean, isPass);
        }
        // 处理OR复查生成待办消息推送
        if ("1".equals(isyhCl))
        {// 隐患处理
            MessageBean message = new MessageBean();
            message.setCreateTime(new Date());
            message.setUpdateTime(new Date());
            message.setMessage("已对标题为\"" + troubleBean.getTitle() + "\"进行了整改，请复查！");
            message.setReceiver(troubleBean.getJcr());
            message.setReceiverId(troubleBean.getJcrId());
            message.setSender(troubleBean.getSjr());
            message.setSenderId(troubleBean.getSjrId());
            message.setSendOrg(troubleBean.getSjdw());
            message.setReceiveOrg(troubleBean.getJcdw());
            message.setStatus("0");
            message.setId(UUIDHexGenerator.generator());
            messageService.addMessage(message);
        }
        else
        {// 复查

            MessageBean message = new MessageBean();
            message.setCreateTime(new Date());
            message.setUpdateTime(new Date());
            if ("1".equals(isPass))
                message.setMessage("经复查，标题为\"" + troubleBean.getTitle() + "\"的隐患已通过！");
            else
                message.setMessage("经复查，标题为\"" + troubleBean.getTitle() + "\"的隐患未通过!请在！"
                        + DateUtil.getDateFormat(troubleBean.getZgsx(), DateUtil.DATETIME_DEFAULT_FORMAT) + "之前响应处理!");
            message.setReceiver(troubleBean.getSjr());
            message.setReceiverId(troubleBean.getSjrId());
            message.setSender(troubleBean.getJcr());
            message.setSenderId(troubleBean.getJcrId());
            message.setSendOrg(troubleBean.getJcdw());
            message.setReceiveOrg(troubleBean.getSjdw());
            message.setStatus("0");
            message.setId(UUIDHexGenerator.generator());
            messageService.addMessage(message);

            MessageBean message2 = new MessageBean();
            message2.setCreateTime(new Date());
            message2.setUpdateTime(new Date());
            if ("1".equals(isPass))
                message2.setMessage("经复查，标题为\"" + troubleBean.getTitle() + "\"的隐患已通过！");
            else
                message2.setMessage("经复查，标题为\"" + troubleBean.getTitle() + "\"的隐患未通过!整改时限："
                        + DateUtil.getDateFormat(troubleBean.getZgsx(), DateUtil.DATETIME_DEFAULT_FORMAT) + "，请及时关注!");
            message2.setReceiver(troubleBean.getZgzrr());
            message2.setReceiverId(troubleBean.getZgzrrId());
            message2.setSender(troubleBean.getJcr());
            message2.setSenderId(troubleBean.getJcrId());
            message2.setSendOrg(troubleBean.getJcdw());
            message2.setReceiveOrg(troubleBean.getSjdw());
            message2.setStatus("0");
            message2.setId(UUIDHexGenerator.generator());
            messageService.addMessage(message2);
        }
    }

    // 安全隐患整改复查单
    public void addRateFkbgResult(String troubleId, UserBean userBean, String isPass) throws Exception
    {
        TroubleBean troubleBean = troubleMapper.queryOneTrouble(troubleId);
        // 查询刚插入的隐患追踪状态为闭环或者复查的追踪bean
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("yhId", troubleBean.getId());
        mapWhere.put("type", "1".equals(isPass) ? "3" : "2");
        List<TroubleZzBean> troubleZzList = troubleMapper.queryTroublezz(mapWhere);
        TroubleZzBean troubleZzBean = troubleZzList.get(0);
        // 查询初始状态为新建的追踪Bean
        mapWhere.clear();
        mapWhere.put("yhId", troubleBean.getId());
        mapWhere.put("type", "0");
        troubleZzList = troubleMapper.queryTroublezz(mapWhere);
        TroubleZzBean newtroubleZzBean = troubleZzList.get(0);
        // 查询最近一次状态为整改的追踪Bean
        mapWhere.clear();
        mapWhere.put("yhId", troubleBean.getId());
        mapWhere.put("type", "1");
        troubleZzList = troubleMapper.queryTroublezz(mapWhere);
        TroubleZzBean zgtroubleZzBean = troubleZzList.get(0);
        BigDecimal zgjfDecimal = new BigDecimal("0");
        for (TroubleZzBean tt : troubleZzList)
        {
            BigDecimal temp = new BigDecimal(tt.getZgjf());
            zgjfDecimal = zgjfDecimal.add(temp);
        }
        // 删除隐患下的安全隐患整改复查单,如有
        mapWhere.clear();
        mapWhere.put("bussinessId", troubleBean.getId());
        List<FileBean> files = fileService.queryAllFiles(mapWhere, null);
        for (FileBean fileBean : files)
        {
            if ("安全隐患整改复查单".equals(fileBean.getFileName()))
            {
                // 删除之前的安全隐患整改复查单，如存在
                fileService.delFileById(fileBean);
                // 同时清空PDF文件
                // String pdfFile = ConfigUtil.getValueByKey("fileRootUrl") +
                // "doc/zgfcd_" + troubleBean.getId() + ".pdf";
                // File file = new File(pdfFile);
                File file = new File(fileBean.getFileUrl().substring(0, fileBean.getFileUrl().indexOf(".")) + ".pdf");
                if (file.exists())
                    file.delete();
            }
        }
        // 新建 安全隐患整改复查单
        FileBean fileBean = new FileBean();
        String bussinessId = troubleBean.getId();
        String fileCatalog = "隐患管理";
        String fileType = "安全检查";
        String orgId = userBean.getOrgId();
        String fileId = UUIDHexGenerator.generator();
        fileBean.setId(fileId);
        fileBean.setScry(userBean.getUser_code());
        fileBean.setScryName(userBean.getUser_name());
        fileBean.setBussinessId(bussinessId);
        fileBean.setCreateTime(new Date());
        fileBean.setUpdateTime(new Date());
        fileBean.setFileCatalog(fileCatalog);
        fileBean.setFileType(fileType);
        fileBean.setOrgId(orgId);
        fileBean.setOrgName(organService.queryOneById(orgId).getName_cn());
        fileBean.setStatus("0");

        String dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + fileCatalog + "/" + fileType;
        File dir = new File(dirUrl);
        // 新建目录
        dir.mkdirs();
        // 新建文件,物理文件重命名以序列号命名
        String fileLx = "docx";
        String fileName = "安全隐患整改复查单";

        // 编写文件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("${sjdw}", troubleBean.getSjdw());
        params.put("${gcmc}", troubleBean.getGcmc());
        params.put("${yhbh}", "编号：" + troubleBean.getYhbh());
        UserBean jcrBean = userService.queryUserById(troubleBean.getJcrId());
        params.put("${jcrAndPhone}", troubleBean.getJcr() + " " + jcrBean.getPhone());
        if (troubleBean.getZgsx() == null)
        {
            params.put("${zgsx}", "");
        }
        else
        {
            params.put("${zgsx}", DateUtil.getDateFormat(troubleBean.getZgsx(), DateUtil.DATETIME_CHINA_FORMAT) + "前");
        }

        params.put("${jcdw}", troubleBean.getJcdw());
        params.put("${createTime}",
                DateUtil.getDateFormat(newtroubleZzBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
        List<TroubleQuestionBean> questionList = this.queryQuesByTroubId(troubleBean.getId(), "0");// 查出隐患新建状态的最新问题
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < questionList.size(); i++)
        {
            Map<String, Object> content = Maps.newHashMap();
            TroubleQuestionBean question = questionList.get(i);
            String[] contentArray = new String[2];
            contentArray[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if (StringHelper.isNullAndEmpty(question.getContent()))
            {
                contentArray[1] = "意见:无";
            }
            else
            {
                contentArray[1] = "意见:" + question.getContent();
            }
            content.put("content", contentArray);
            contents.add(content);
        }
        params.put("${contents}", contents);

        questionList = this.queryQuesByTroubId(troubleBean.getId(), "1");// 查出隐患追踪待整改状态的最新问题
        List<Map<String, Object>> contents2 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> contents3 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < questionList.size(); i++)
        {
            Map<String, Object> content = Maps.newHashMap();
            TroubleQuestionBean question = questionList.get(i);
            List<TroubleQuestionBean> tempQuestlist = Lists.newArrayList();

            String[] contentArray = new String[2];
            contentArray[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if ("Y".equals(question.getIsPass()))
            {
                mapWhere.clear();
                mapWhere.put("yhId", troubleBean.getId());
                mapWhere.put("zzType", "1");
                mapWhere.put("isPass", "N");
                mapWhere.put("title", question.getTitle());
                tempQuestlist = troubleMapper.queryQuestHistory(mapWhere);
                contentArray[1] = "完成情况:" + tempQuestlist.get(0).getContent();
            }
            else
                contentArray[1] = "完成情况:" + question.getContent();
            content.put("content", contentArray);
            contents2.add(content);

            Map<String, Object> content3_ = Maps.newHashMap();
            String[] contentArray3_ = new String[2];
            contentArray3_[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if ("Y".equals(question.getIsPass()))
            {
                contentArray3_[1] = "原因分析:" + tempQuestlist.get(0).getYyfx();
            }
            else
                contentArray3_[1] = "原因分析:" + question.getYyfx();
            content3_.put("content", contentArray3_);
            contents3.add(content3_);
        }
        params.put("${contents2}", contents2);
        params.put("${contents3}", contents3);
        params.put("${createTime2}",
                DateUtil.getDateFormat(zgtroubleZzBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
        params.put("${zgjf}", zgjfDecimal.toString() + "元");
        UserBean sjrBean = userService.queryUserById(troubleBean.getSjrId());
        if (StringHelper.isNullAndEmpty(troubleBean.getZgzrrId()))
        {
            params.put("${sjrAndPhone}", troubleBean.getSjr() + " " + sjrBean.getPhone() + ";");
        }
        else
        {
            UserBean zgzrrBean = userService.queryUserById(troubleBean.getZgzrrId());
            params.put("${sjrAndPhone}",
                    troubleBean.getSjr() + " " + sjrBean.getPhone() + ";" + troubleBean.getZgzrr() + " "
                            + zgzrrBean.getPhone());
        }

        if ("1".equals(isPass))
            params.put("${isPass}", "经复查验收，现场问题已全部完成整改");
        else
        {
            List<Map<String, Object>> isPassContents = new ArrayList<Map<String, Object>>();

            Map<String, Object> content_ = Maps.newHashMap();
            String[] array = new String[2];
            array[0] = "经复查验收，现场问题未完成整改，未通过";
            array[1] = "                          ";
            content_.put("content", array);
            questionList = this.queryQuesByTroubId(troubleBean.getId(), "2");// 查出隐患追踪复查状态的最新问题
            for (int i = 0; i < questionList.size(); i++)
            {
                TroubleQuestionBean question = questionList.get(i);
                if ("N".equals(question.getIsPass()))
                {// 只显示未通过的复查问题
                    Map<String, Object> content = Maps.newHashMap();
                    String[] contentArray = new String[2];
                    contentArray[0] = "问题" + (i + 1) + ":" + question.getTitle();
                    contentArray[1] = "复查:" + question.getContent();
                    content.put("content", contentArray);
                    isPassContents.add(content);
                }

            }
            isPassContents.add(content_);

            params.put("${isPass}", isPassContents);
        }

        params.put("${jcr}", troubleBean.getJcr());
        params.put("${createTime3}", DateUtil.getDateFormat(troubleZzBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));

        WordUtils wordUtil = new WordUtils();
        String sourceFileUrl = this.getClass().getClassLoader().getResource("").getPath() + "templates/newZgfcd.docx"; // 模板文件位置
        // 隐患下的唯一的安全隐患整改复查单 生成word
        String destFileUrl = dirUrl + "/安全隐患整改复查单_" + troubleBean.getId() + "." + fileLx;
        wordUtil.generateWord(sourceFileUrl, params, destFileUrl);
        File destFile = new File(destFileUrl);
        if (destFile.exists())
        {
            // 生成PDF
            String pdfFile = dirUrl + "/安全隐患整改复查单_" + troubleBean.getId() + ".pdf";
            // String pdfFile = ConfigUtil.getValueByKey("fileRootUrl") +
            // "doc/zgfcd_" + troubleBean.getId() + ".pdf";
            WordToPdfUtil.office2PDF(destFileUrl, pdfFile);
            fileBean.setFileUrl(destFileUrl);
            fileBean.setFileName(fileName);
            fileBean.setFileLx(fileLx);
            fileService.saveFile(fileBean);
        }
    }

    //回复单
    public void addRateFkbg(String troubleId, UserBean userBean) throws Exception
    {
        TroubleBean troubleBean = troubleMapper.queryOneTrouble(troubleId);
        // 查询刚插入的隐患追踪状态为整改的追踪bean
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("yhId", troubleBean.getId());
        mapWhere.put("type", "1");
        List<TroubleZzBean> troubleZzList = troubleMapper.queryTroublezz(mapWhere);
        BigDecimal zgjfDecimal = new BigDecimal("0");
        for (TroubleZzBean tt : troubleZzList)
        {
            BigDecimal temp = new BigDecimal(tt.getZgjf());
            zgjfDecimal = zgjfDecimal.add(temp);
        }

        TroubleZzBean troubleZzBean = troubleZzList.get(0);
        // 查询状态为0的追踪表bean
        mapWhere.clear();
        mapWhere.put("yhId", troubleBean.getId());
        mapWhere.put("type", "0");
        troubleZzList = troubleMapper.queryTroublezz(mapWhere);
        TroubleZzBean troubleZzBean1 = troubleZzList.get(0);

        // 删除隐患下的安全隐患整改回复单,如有
        mapWhere.clear();
        mapWhere.put("bussinessId", troubleBean.getId());
        List<FileBean> files = fileService.queryAllFiles(mapWhere, null);
        for (FileBean fileBean : files)
        {
            if ("安全隐患整改回复单".equals(fileBean.getFileName()))
            {
                // 删除之前的事故隐患整改反馈报告单，如存在
                fileService.delFileById(fileBean);
                // 同时清空PDF文件
                // String pdfUrl= ConfigUtil.getValueByKey("fileRootUrl") +
                // "doc/zghfd_" + troubleBean.getId() + ".pdf";
                // File file = new File(pdfUrl);
                File file = new File(fileBean.getFileUrl().substring(0, fileBean.getFileUrl().indexOf(".")) + ".pdf");
                if (file.exists())
                    file.delete();
            }
        }
        // 新建 隐患下安全隐患整改回复单
        FileBean fileBean = new FileBean();
        String bussinessId = troubleBean.getId();
        String fileCatalog = "隐患管理";
        String fileType = "安全检查";
        String orgId = userBean.getOrgId();
        String fileId = UUIDHexGenerator.generator();
        fileBean.setId(fileId);
        fileBean.setScry(userBean.getUser_code());
        fileBean.setScryName(userBean.getUser_name());
        fileBean.setBussinessId(bussinessId);
        fileBean.setCreateTime(new Date());
        fileBean.setUpdateTime(new Date());
        fileBean.setFileCatalog(fileCatalog);
        fileBean.setFileType(fileType);
        fileBean.setOrgId(orgId);
        fileBean.setOrgName(organService.queryOneById(orgId).getName_cn());
        fileBean.setStatus("0");

        String dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + fileCatalog + "/" + fileType;
        File dir = new File(dirUrl);
        // 新建目录
        dir.mkdirs();
        // 新建文件,物理文件重命名以序列号命名
        String fileLx = "docx";
        String fileName = "安全隐患整改回复单";

        // 编写文件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("${jcdw}", troubleBean.getJcdw());
        UserBean jcrBean = userService.queryUserById(troubleBean.getJcrId());
        params.put("${jcrAndPhone}", troubleBean.getJcr() + " " + jcrBean.getPhone());
        UserBean sjrBean = userService.queryUserById(troubleBean.getSjrId());
        if (StringHelper.isNullAndEmpty(troubleBean.getZgzrrId()))
        {
            params.put("${sjrAndPhone}", troubleBean.getSjr() + " " + sjrBean.getPhone() + ";");
        }
        else
        {
            UserBean zgzrrBean = userService.queryUserById(troubleBean.getZgzrrId());
            params.put("${sjrAndPhone}",
                    troubleBean.getSjr() + " " + sjrBean.getPhone() + ";" + troubleBean.getZgzrr() + " "
                            + zgzrrBean.getPhone());
        }

        params.put("${sjdw}", troubleBean.getSjdw());
        params.put("${gcmc}", troubleBean.getGcmc());
        params.put("${yhbh}", "编号：" + troubleBean.getYhbh());
        if (troubleBean.getZgsx() == null)
        {
            params.put("${zgsx}", "");
        }
        else
            params.put("${zgsx}", DateUtil.getDateFormat(troubleBean.getZgsx(), DateUtil.DATETIME_CHINA_FORMAT) + "前");
        List<TroubleQuestionBean> questionList = this.queryQuesByTroubId(troubleBean.getId(), "0");
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < questionList.size(); i++)
        {
            TroubleQuestionBean question = questionList.get(i);
            Map<String, Object> content = Maps.newHashMap();
            String[] contentArray = new String[2];
            contentArray[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if (StringHelper.isNullAndEmpty(question.getContent()))
            {
                contentArray[1] = "意见:无";
            }
            else
            {
                contentArray[1] = "意见:" + question.getContent();
            }
            content.put("content", contentArray);
            contents.add(content);

        }
        params.put("${contents}", contents);

        params.put("${createTime}", DateUtil.getDateFormat(troubleZzBean1.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));
        params.put("${createTime2}", DateUtil.getDateFormat(troubleZzBean.getCreateTime(), DateUtil.DATE_CHINA_FORMAT));

        params.put("${zgjf}", zgjfDecimal.toString() + "元");
        // 查出隐患所对应的相对应状态的问题
        List<Map<String, Object>> contents2 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> contents3 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> images = new ArrayList<Map<String, Object>>();// 附件图片
        questionList = this.queryQuesByTroubId(troubleBean.getId(), "1");// 查出隐患追踪整改状态的最新所有问题
        for (int i = 0; i < questionList.size(); i++)
        {
            TroubleQuestionBean question = questionList.get(i);

            List<TroubleQuestionBean> tempQuestlist = Lists.newArrayList();

            Map<String, Object> content = Maps.newHashMap();
            String[] contentArray = new String[2];
            contentArray[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if ("Y".equals(question.getIsPass()))
            {// 查询隐患追踪状态为 整改 状态的 该问题(未通过)列表（针对单个问题的状态记录）,倒序排序
                mapWhere.clear();
                mapWhere.put("yhId", troubleBean.getId());
                mapWhere.put("zzType", "1");
                mapWhere.put("isPass", "N");
                mapWhere.put("title", question.getTitle());
                tempQuestlist = this.queryQuestHistory(mapWhere);
                contentArray[1] = "完成情况:" + tempQuestlist.get(0).getContent();
            }

            else
                contentArray[1] = "完成情况:" + question.getContent();
            content.put("content", contentArray);
            contents2.add(content);
            Map<String, Object> content3_ = Maps.newHashMap();
            String[] contentArray3_ = new String[2];
            contentArray3_[0] = "问题" + (i + 1) + ":" + question.getTitle();
            if ("Y".equals(question.getIsPass()))
            {
                contentArray3_[1] = "原因分析:" + tempQuestlist.get(0).getYyfx();
            }
            else
                contentArray3_[1] = "原因分析:" + question.getYyfx();
            content3_.put("content", contentArray3_);
            contents3.add(content3_);

            // 查出问题所对应的图片
            List<FileNewBean> imageFiles = Lists.newArrayList();
            if ("Y".equals(question.getIsPass()))
            {
                imageFiles = fileService.queryNewFileByIds(tempQuestlist.get(0).getFileIds());
            }
            else
                imageFiles = fileService.queryNewFileByIds(question.getFileIds());
            for(int ii=0;ii<imageFiles.size();ii++)
            {
                FileNewBean imageFile=imageFiles.get(ii);
                File tFile = new File(imageFile.getFileUrl());
                if (tFile.exists())
                {

                    Map<String, Object> image = new HashMap<String, Object>();
                    // "100/150" 300/450
                    image.put("imgDesc", "问题" + (i + 1) + " ，整改图片" + (ii + 1) + ":");
                    image.put("width", 600);
                    image.put("height", 800);
                    image.put("type", imageFile.getFileLx());
                    image.put("content",
                            WordUtils.inputStream2ByteArray(new FileInputStream(imageFile.getFileUrl()), true));
                    image.put("isImage", "1");
                    images.add(image);
                }
            }
        }
        params.put("${contents2}", contents2);
        params.put("${contents3}", contents3);
        params.put("${images}", images);

        WordUtils wordUtil = new WordUtils();
        String sourceFileUrl = this.getClass().getClassLoader().getResource("").getPath() + "templates/newReply.docx"; // 模板文件位置
        // 隐患下的唯一的事故隐患整改反馈报告单 生成word
        String destFileUrl = dirUrl + "/安全隐患整改回复单_" + troubleBean.getId() + "." + fileLx;
        wordUtil.generateWord(sourceFileUrl, params, destFileUrl);
        File destFile = new File(destFileUrl);
        if (destFile.exists())
        {
            // 生成PDF
            String pdfFile = dirUrl + "/安全隐患整改回复单_" + troubleBean.getId() + ".pdf";
            // String pdfFile = ConfigUtil.getValueByKey("fileRootUrl") +
            // "doc/zghfd_" + troubleBean.getId() + ".pdf";
            WordToPdfUtil.office2PDF(destFileUrl, pdfFile);
            fileBean.setFileUrl(destFileUrl);
            fileBean.setFileName(fileName);
            fileBean.setFileLx(fileLx);
            fileService.saveFile(fileBean);
        }
        troubleBean.setStatus("3");// 设置隐患状态为待复查
        troubleMapper.updateTrouble(troubleBean);
    }

    private List<TroubleQuestionBean> queryQuestHistory(Map<String, Object> mapWhere)
    {
        List<TroubleQuestionBean>  quesList=troubleMapper.queryQuestHistory(mapWhere);

        //初始化问题的文件IDS
        List<String> bizIds=Lists.newArrayList();
        for(TroubleQuestionBean troubleQuestionBean:quesList)
        {
            bizIds.add(troubleQuestionBean.getId());
        }
        List<BizFileBean> bizFileBeanList=fileMapper.queryBizFileBeanByBizIds(bizIds);
        Map<String,List<String>> bizFilesMap=Maps.newHashMap();
        for (BizFileBean bizFileBean : bizFileBeanList)
        {
            List<String> valueList = bizFilesMap.get(bizFileBean.getBizId());
            if (ObjectUtils.isEmpty(valueList))
            {
                valueList = Lists.newArrayList();
                valueList.add(bizFileBean.getFileId());
                bizFilesMap.put(bizFileBean.getBizId(), valueList);
            }
            else
            {
                valueList.add(bizFileBean.getFileId());
            }
        }
        for(TroubleQuestionBean troubleQuestionBean:quesList)
        {
            if(!ObjectUtils.isEmpty(bizFilesMap.get(troubleQuestionBean.getId())))
                troubleQuestionBean.setFileIds(bizFilesMap.get(troubleQuestionBean.getId()));
            else  troubleQuestionBean.setFileIds(Lists.<String>newArrayList());
        }
        return quesList;
    }

    //生成PDF数据
    public static void main(String[] args)
    {
        String sourceFile = "C:/Users/Administrator/Desktop/安全隐患整改回复单.docx";
        File dFile = new File(sourceFile);
        if (dFile.exists()){
            String destFile = "C:/Users/Administrator/Desktop/安全隐患整改回复单.pdf";
            try
            {
                WordToPdfUtil.office2PDF(sourceFile, destFile);
            }
            catch (Exception e)
            {
                
                // TODO Auto-generated catch block
                e.printStackTrace();
                
            }
        }
    }
    
    /**
     * 查询隐患表下的所有隐患追踪消息
     * queryYhzzInfos:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    public List<Map<String, Object>> queryYhzzInfos(String troubleId)
    {
        List<Map<String, Object>> retList = Lists.newArrayList();
        TroubleBean troubleBean = troubleMapper.queryOneTrouble(troubleId);
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("yhId", troubleId);
        List<TroubleZzBean> troubleZzList = troubleMapper.queryTroublezz(mapWhere);// 倒序排序
        for (int i = 0; i < troubleZzList.size(); i++)
        {
            TroubleZzBean troubleZzBean = troubleZzList.get(i);
            Map<String, Object> map = Maps.newHashMap();
            String creatTime = DateUtil.getDateFormat(troubleZzBean.getCreateTime(), DateUtil.DATETIME_CHINA_FORMAT);
            String text = "";
            map.put("show", "1");
            if ("0".equals(troubleZzBean.getType()))
            {// 新建状态
                text = creatTime + "," + troubleBean.getJcdw() + "发出整改";
            }
            if ("5".equals(troubleZzBean.getType()))
            {// 响应状态
                text = creatTime + "," + troubleBean.getSjdw() + "<span>已响应</span>";
                map.put("show", "0");
            }
            if ("1".equals(troubleZzBean.getType()))
            {// 整改
                text = creatTime + "," + troubleBean.getSjdw() + "进行相关第<span>" + troubleZzBean.getCs() + "</span>次整改";
                map.put("isCl", "1");
            }
            if ("2".equals(troubleZzBean.getType()))
            {// 复查不通过

                text = creatTime + "," + "经过" + troubleBean.getJcdw() + "复查，复查结果：<span>不通过</span>";
                map.put("isPass", "0");
            }
            if ("3".equals(troubleZzBean.getType()))
            {// 复查通过
                text = creatTime + "," + "经过" + troubleBean.getJcdw() + "复查，复查结果：<span>通过</span>";
                map.put("show", "0");
            }
            map.put("title", text);
            map.put("yhzzId", troubleZzBean.getId());

            if ("1".equals(troubleZzBean.getType()) && "2".equals(troubleBean.getStatus()) && i == 0)
            {// 如果最新一条隐患追踪为“整改”状态，但是隐患状态为“待整改”，说明此时受检人未回复 改次整改,不加入 list
            }
            else
                retList.add(map);
        }
        for (TroubleZzBean troubleZzBean : troubleZzList)
        {

        }
        return retList;
    }

    /**
     * 隐患响应
     * xyTrouble:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param troubleId
     * @since JDK 1.6
     */
    public void addXyTrouble(String troubleId)
    {
        // 新建一条响应追踪记录表
        TroubleZzBean troubleZzBean = new TroubleZzBean();
        troubleZzBean.setYhId(troubleId);
        troubleZzBean.setCreateTime(new Date());
        troubleZzBean.setUpdateTime(new Date());
        troubleZzBean.setType("5");
        troubleZzBean.setId(UUIDHexGenerator.generator());
        troubleMapper.addTroubleZz(troubleZzBean);

        Map<String, Object> map = Maps.newHashMap();
        map.put("id", troubleId);
        map.put("status", "2");
        this.updateTroubleStatus(map);

        TroubleBean troubleBean = troubleMapper.queryOneTrouble(troubleId);
        MessageBean message = new MessageBean();
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        message.setMessage("已响应标题为\"" + troubleBean.getTitle() + "\"的隐患，将会立即处理！");
        message.setReceiver(troubleBean.getJcr());
        message.setReceiverId(troubleBean.getJcrId());
        message.setSender(troubleBean.getSjr());
        message.setSenderId(troubleBean.getSjrId());
        message.setSendOrg(troubleBean.getSjdw());
        message.setReceiveOrg(troubleBean.getJcdw());
        message.setStatus("0");
        message.setId(UUIDHexGenerator.generator());
        messageService.addMessage(message);
    }

    /**
     * 查询检查单位各隐患等级的个数
     * queryJcdwQuestNums:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    public List<Map<String, Object>> querySjdwQuestNums(Map<String, Object> mapWhere)
    {
        List<Map<String, Object>> retList = Lists.newArrayList();
        List<Map<String, Object>> list = troubleMapper.queryJcdwQuestNums(mapWhere);
        for (Map<String, Object> map : list)
        {
            String sjdw = String.valueOf(map.get("SJDW"));
            String sjdwId = String.valueOf(map.get("SJDW_ID"));
            String yhLevel = String.valueOf(map.get("YH_LEVEL"));
            String NUM = String.valueOf(map.get("NUM"));
            inertJcdwQuestNums(retList, sjdw, yhLevel, NUM, sjdwId);
        }
        return retList;
    }

    private void inertJcdwQuestNums(List<Map<String, Object>> retList, String sjdw, String yhLevel, String num,
            String sjdwId)
    {
        boolean isSert = true;
        for (Map<String, Object> map : retList)
        {
            if (sjdw.equals(map.get("sjdw")))
            {// 说明有值
                isSert = false;
                if ("1".equals(yhLevel))
                {
                    map.put("oneYh", num);
                }
                else if ("2".equals(yhLevel))
                {
                    map.put("twoYh", num);
                }
                else if ("3".equals(yhLevel))
                {
                    map.put("threeYh", num);
                }
            }
        }
        if (isSert)
        {
            Map<String, Object> newMap = Maps.newHashMap();
            newMap.put("sjdw", sjdw);
            newMap.put("sjdwId", sjdwId);
            if ("1".equals(yhLevel))
            {
                newMap.put("oneYh", num);
                newMap.put("twoYh", "0");
                newMap.put("threeYh", "0");
            }
            else if ("2".equals(yhLevel))
            {
                newMap.put("twoYh", num);
                newMap.put("oneYh", "0");
                newMap.put("threeYh", "0");
            }
            else if ("3".equals(yhLevel))
            {
                newMap.put("threeYh", num);
                newMap.put("twoYh", "0");
                newMap.put("oneYh", "0");
            }
            retList.add(newMap);
        }

    }

    public List<TroubleZzBean> queryTroublezz(Map<String, Object> mapWhere)
    {
        return troubleMapper.queryTroublezz(mapWhere);
    }

    public TroubleZzBean queryTroublezzById(String id)
    {
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("id", id);
        List<TroubleZzBean> zzList = troubleMapper.queryTroublezz(mapWhere);
        if (zzList.isEmpty())
            return null;
        else
            return zzList.get(0);
    }

    public TroubleTypeBean queryThTypeById(String id)
    {

        return troubleMapper.queryThTypeById(id);
    }

    public void addTroubleQuestBatch(List<TroubleQuestionBean> quesTionBeanList)
    {

        troubleMapper.addTroubleQuestBatch(quesTionBeanList);

    }

    public void updateQuestBatch(List<TroubleQuestionBean> quesTionBeanList)
    {
        //更新文件信息
        List<BizFileBean> bizFileBeanList=Lists.newArrayList();
        for (TroubleQuestionBean troubleQuestionBean : quesTionBeanList)
        {
            List<String> fileIds=troubleQuestionBean.getFileIds();
            for(String fileId:fileIds)
            {
                BizFileBean bizFileBean=new BizFileBean();
                bizFileBean.setId(UUIDHexGenerator.generator());
                bizFileBean.setBizId(troubleQuestionBean.getId());
                bizFileBean.setFileId(fileId);
                bizFileBeanList.add(bizFileBean);
            }
        }
        if(!ObjectUtils.isEmpty(bizFileBeanList))
            fileService.updateBindBussFile(bizFileBeanList);
        troubleMapper.updateQuestBatch(quesTionBeanList);
    }

    public void updateYhZzBean(TroubleZzBean zzBean)
    {

        troubleMapper.updateYhZzBean(zzBean);

    }

}
