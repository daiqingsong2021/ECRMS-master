/**
 * Project Name:ECRMS
 * File Name:TroubleController.java
 * Package Name:com.jsumt.controller.trouble
 * Date:2018年11月28日上午10:20:41
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.trouble;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.file.FileService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.service.trouble.TroubleService;
import com.jsumt.util.DateUtil;
import com.jsumt.util.*;
import com.jsumt.vo.system.UserBean;
import com.jsumt.vo.trouble.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * ClassName:TroubleController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 上午10:20:41 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("trouble")
public class TroubleController extends BaseController
{

    private static Logger logger = LoggerFactory.getLogger(TroubleController.class);

    @Autowired
    private TroubleService troubleService;

    @Autowired
    private UserManageService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private OrganizationService organService;

    // 加载
    @RequestMapping("queryYhTypes")
    public @ResponseBody Map<String, Object> queryYhTypes(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try
        {
            // 根据查询条件加载组织机构的树形结构，若没有条件则加载所以节点
            List<TroubleTypeBean> troubleTypeList = Lists.newArrayList();
            List<Map<String, Object>> listMap = Lists.newArrayList();
            mapWhere.put("pcxm", this.getParameter("pcxm"));
            mapWhere.put("pcnr", this.getParameter("pcnr"));
            if (StringHelper.isNullAndEmpty(this.getParameter("pid")))
            {
                mapWhere.put("pid", "0");
            }
            troubleTypeList = troubleService.queryYhTypes(mapWhere);
            String moduleId = String.valueOf(mapWhere.get("moduleId"));
            if (!StringHelper.isNullAndEmpty(moduleId))
            {
                for (TroubleTypeBean troubleTypeBean : troubleTypeList)
                {
                    Map<String, Object> map = BeanUtil.toMap(troubleTypeBean);
                    if ("1".equals(troubleTypeBean.getIsLeaf()))
                    {// 如果是叶子节点 展开 open表示展开
                        map.put("state", "open");
                    }
                    else
                        map.put("state", "closed");// 其他是关闭
                    listMap.add(map);
                }
                resultMap.put("rows", listMap);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }

        return resultMap;
    }

    @RequestMapping("queryAllYhTypes")
    public @ResponseBody Map<String, Object> queryAllYhTypes(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try
        {
            // 根据查询条件加载组织机构的树形结构，若没有条件则加载所以节点
            List<TroubleTypeBean> troubleTypeList = Lists.newArrayList();
            mapWhere.put("pcxm", this.getParameter("pcxm"));
            mapWhere.put("pcnr", this.getParameter("pcnr"));
            troubleTypeList = troubleService.queryYhTypes(mapWhere);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (TroubleTypeBean troubleTypeBean : troubleTypeList)
            {
                Map<String, Object> map = BeanUtil.toMap(troubleTypeBean);
                if ("2".equals(troubleTypeBean.getYhLayer()))
                {// 第2层关闭
                    map.put("state", "closed");
                }
                else
                {
                    map.put("state", "open");
                }
                listMap.add(map);
            }
            resultMap.put("rows", listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return resultMap;
    }

    @RequestMapping("queryYhmb")
    public @ResponseBody Map<String, Object> queryYhmb()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            List<TroubleModuleBean> moduleList = troubleService.queryAllYhMb(null);
            for (TroubleModuleBean troubleModuleBean : moduleList)
            {
                Map<String, Object> map = Maps.newHashMap();
                map.put("moduleId", troubleModuleBean.getId());
                map.put("moduleName", troubleModuleBean.getModuleName());
                if (userBean.getUser_id().equals(troubleModuleBean.getCreater())
                        || "superadmin".equals(userBean.getUser_code()))
                    // 如果当前用户的角色ID包含隐患模板的创建角色ID
                    map.put("canEdit", "1");
                else
                    map.put("canEdit", "0");
                map.put("createrId", troubleModuleBean.getCreater());
                listMap.add(map);
            }
            retMap = this.generateMsg(listMap, true, "查询隐患模板成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询隐患模板失败!");
            return retMap;
        }

    }

    @RequestMapping("addYhMb")
    public @ResponseBody Map<String, Object> addYhMb()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String moduleName = this.getParameter("moduleName");
            String importMbId = this.getParameter("importMbId");
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            // 判断数据库是否有重名moduleName，如有，则 不允许增加
            boolean res = troubleService.isExistModule(moduleName);
            if (res)
            {
                retMap = this.generateMsg("", false, "隐患模板名称不能重名!");
                return retMap;
            }
            TroubleModuleBean troubleModuleBean = troubleService.addYhMb(moduleName, importMbId, userBean);
            Map<String, Object> map = Maps.newHashMap();
            map.put("moduleId", troubleModuleBean.getId());
            map.put("moduleName", troubleModuleBean.getModuleName());
            map.put("canEdit", "1");
            map.put("createrId", troubleModuleBean.getCreater());
            retMap = this.generateMsg(map, true, "增加隐患模板成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加隐患模板失败!");
            return retMap;

        }

    }

    @RequestMapping("delYhMb")
    public @ResponseBody Map<String, Object> delYhMb()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String moduleId = this.getParameter("moduleId");
            troubleService.delTroubleModule(moduleId);
            retMap = this.generateMsg("", true, "删除模板成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除模板失败!");
            return retMap;

        }
    }

    @RequestMapping("updateYhMb")
    public @ResponseBody Map<String, Object> updateYhMb()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String moduleId = this.getParameter("moduleId");
            String moduleName = this.getParameter("moduleName");
            boolean res = troubleService.isExistModule(moduleName);
            if (res)
            {
                retMap = this.generateMsg("", false, "隐患名称不能重名!");
                return retMap;
            }
            troubleService.updateYhMb(moduleId, moduleName);
            TroubleTypeBean bean = new TroubleTypeBean();
            bean.setModuleId(moduleId);
            bean.setPcxm(moduleName);
            bean.setUpdateTime(new Date());
            troubleService.updateYhlbByModuleId(bean);
            retMap = this.generateMsg("", true, "修改模板成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改模板失败!");
            return retMap;

        }

    }

    /**
     * 增加隐患类别
     * addYhlb:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param bean
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("addYhlb")
    public @ResponseBody Map<String, Object> addYhlb(TroubleTypeBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        // 封装主primary key
        bean.setId(UUIDHexGenerator.generator());
        try
        {
            troubleService.addYhlb(bean);
            retMap = this.generateMsg(bean, true, "增加隐患类别成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加隐患类别失败!");
            return retMap;
        }

    }

    // 删除隐患类别
    @RequestMapping("delYhlb")
    public @ResponseBody Map<String, Object> delYhlb(String id)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            troubleService.delYhlbById(id);
            retMap = this.generateMsg(id, true, "删除成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除失败!");
            return retMap;
        }
    }

    // 修改隐患类别
    @RequestMapping("updateYhlb")
    public @ResponseBody Map<String, Object> updateYhlb(TroubleTypeBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {
            troubleService.updateYhlb(bean);
            TroubleTypeBean troubleTypeBean = troubleService.queryThTypeById(bean.getId());
            retMap = this.generateMsg(troubleTypeBean, true, "修改成功!");// 返回最新的
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改失败!");
            return retMap;
        }
    }

    // 查询所有隐患List
    @RequestMapping("queryYhList")
    public @ResponseBody Map<String, Object> queryYhList(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<TroubleBean> pageInfo = new PageInfo<TroubleBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            // 重新解码
            mapWhere.put("title", this.getParameter("title"));
            // 查询自己的
            mapWhere.put("sjdw", this.getParameter("sjdw"));
            mapWhere.put("jcdw", this.getParameter("jcdw"));
            String ymtype = this.getParameter("ymtype");// 页面类型
            String createTimeData = String.valueOf(mapWhere.get("createTime"));
            if (StringHelper.isNotNullAndEmpty(createTimeData))
            {
                String startDate =
                        createTimeData.substring(0, StringHelper.getFromIndex(createTimeData, "-", 3)).trim();
                String endDate = createTimeData
                        .substring(StringHelper.getFromIndex(createTimeData, "-", 3) + 2, createTimeData.length())
                        .trim();
                mapWhere.put("startDate", startDate);
                mapWhere.put("endDate", endDate);
            }

            List<TroubleBean> troubleList = troubleService.queryAllTroubleList(mapWhere, pageInfo);

            pageInfo = new PageInfo<TroubleBean>(troubleList);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            for (TroubleBean trouble : troubleList)
            {
                Map<String, Object> map = BeanUtil.toMap(trouble);
                map.put("zgsx", DateUtil.getDateFormat(trouble.getZgsx(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("updateTime",
                        DateUtil.getDateFormat(trouble.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime",
                        DateUtil.getDateFormat(trouble.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("ymtype", ymtype);
                map.put("orgId", this.getParameter("orgId"));
                map.put("userId", userBean.getUser_id());
                listMap.add(map);
            }
            retMap = layuiData(pageInfo.getTotal(), listMap);

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return retMap;

    }

    /**
     * 查询当前用户组织机构下的隐患模板以及类别，为增加隐患提供
     * queryAllTroubleType:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("queryAllTroubleType")
    public @ResponseBody Map<String, Object> queryAllTroubleType()
    {

        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            List<Map<String, Object>> retList = Lists.newArrayList();
            // 查询符合条件的隐患模板表
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("orgId", userBean.getOrgId());
            List<TroubleModuleBean> troubleModuleList = troubleService.queryAllYhMb(mapWhere);
            // 查询隐患类别表
            List<TroubleTypeBean> allTypeList = troubleService.queryYhTypes(null);

            for (TroubleModuleBean module : troubleModuleList)
            {
                for (TroubleTypeBean troubleTypeBean : allTypeList)
                {
                    if ("0".equals(troubleTypeBean.getPid()) && troubleTypeBean.getModuleId().equals(module.getId()))
                    {// 所有隐患类别pid为0的且隐患类别moduleId的顶层节点
                        Map<String, Object> topMap = Maps.newHashMap();
                        topMap.put("title", troubleTypeBean.getPcxm());
                        // 二层
                        List<Map<String, Object>> children = this.findSecondChild(allTypeList, troubleTypeBean.getId());
                        topMap.put("children", children);
                        retList.add(topMap);
                    }
                }
            }

            retMap = this.generateMsg(retList, true, "查询成功!");
            return retMap;

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }
    }

    private List<Map<String, Object>> findSecondChild(List<TroubleTypeBean> allTypeList, String pid)
    {
        List<Map<String, Object>> retList = Lists.newArrayList();
        for (TroubleTypeBean troubleTypeBean : allTypeList)
        {
            if (pid.equals(troubleTypeBean.getPid()))
            {
                Map<String, Object> map = Maps.newHashMap();
                map.put("title", troubleTypeBean.getPcxm());
                // 三层
                List<Map<String, Object>> children = this.findThirdChild(allTypeList, troubleTypeBean.getId());
                map.put("children", children);
                retList.add(map);
            }
        }
        return retList;
    }

    private List<Map<String, Object>> findThirdChild(List<TroubleTypeBean> allTypeList, String pid)
    {

        List<Map<String, Object>> retList = Lists.newArrayList();
        for (TroubleTypeBean troubleTypeBean : allTypeList)
        {
            if (pid.equals(troubleTypeBean.getPid()))
            {// 这层忽略
                for (TroubleTypeBean bean : allTypeList)
                {// 直接找下一层
                    if (troubleTypeBean.getId().equals(bean.getPid()))
                    {
                        Map<String, Object> map = Maps.newHashMap();
                        map = BeanUtil.toMap(bean);
                        retList.add(map);
                    }
                }
            }
        }
        return retList;
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
     * @param
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("addTrouble")
    public @ResponseBody Map<String, Object> addTrouble()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            List<Map<String, Object>> questRetList = Lists.newArrayList();
            String title = this.getParameter("title");
            String zgsx = this.getParameter("zgsx");
            String jcdw = this.getParameter("jcdw");
            String jcdwId = this.getParameter("jcdwId");
            String jcr = this.getParameter("jcr");
            String jcrId = this.getParameter("jcrId");
            String sjdw = this.getParameter("sjdw");
            String sjdwId = this.getParameter("sjdwId");
            String sjr = this.getParameter("sjr");
            String sjrId = this.getParameter("sjrId");
            String jclb = this.getParameter("jclb");
            String orgId = this.getParameter("orgId");
            String gcmc = this.getParameter("gcmc");
            String yhbh = this.getParameter("yhbh");
            String zgzrr = this.getParameter("zgzrr");
            String zgzrrId = this.getParameter("zgzrrId");
            // 判断隐患编号是不是存在，如果存在，则跑出异常
            String existTroubleId = this.getParameter("troubleId");
            if (StringHelper.isNotNullAndEmpty(existTroubleId))
            {// 之前已保存过，删除此条隐患（包括隐患的追踪表、问题表、问题图片不删）
                List<String> deleIds = Lists.newArrayList();
                deleIds.add(existTroubleId);
                troubleService.delUpdateTrouble(deleIds);
            }

            // 增加隐患主表
            TroubleBean troubleBean = new TroubleBean();
            // 添加隐患主表的id
            String troubleId = UUIDHexGenerator.generator();

            troubleBean.setId(troubleId);
            troubleBean.setCreateTime(new Date());
            troubleBean.setUpdateTime(new Date());
            troubleBean.setStatus("0");// 待发布状态
            troubleBean.setTitle(title);
            troubleBean.setGcmc(gcmc);
            troubleBean.setYhbh(yhbh);
            if (StringHelper.isNullAndEmpty(zgsx))
                troubleBean.setZgsx(null);
            else
                troubleBean.setZgsx(DateUtil.formatDate(zgsx, DateUtil.DATETIME_DEFAULT_FORMAT));
            troubleBean.setJcdw(jcdw);
            troubleBean.setJcdwId(jcdwId);
            troubleBean.setJcr(jcr);
            troubleBean.setJcrId(jcrId);
            troubleBean.setSjdw(sjdw);
            troubleBean.setSjdwId(sjdwId);
            troubleBean.setSjr(sjr);
            troubleBean.setSjrId(sjrId);
            troubleBean.setZgzrr(zgzrr);
            troubleBean.setZgzrrId(zgzrrId);
            troubleBean.setJclb(jclb);
            troubleBean.setOrgId(orgId);
            // 增加隐患追踪表
            TroubleZzBean troubleZzBean = new TroubleZzBean();
            troubleZzBean.setCreateTime(new Date());
            troubleZzBean.setUpdateTime(new Date());
            troubleZzBean.setCs("1");
            troubleZzBean.setType("0");// 新建类型
            // 增加隐患问题表
            List<TroubleQuestionBean> quesTionBeanList = Lists.newArrayList();
            String questDataList = this.getParameter("questDataList");
            List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(questDataList, List.class);

            Map<String, Object> retturnMap = Maps.newHashMap();
            retturnMap.put("troubleId", troubleId);
            for (Map<String, Object> map : questionList)
            {
                Map<String, Object> questMap = Maps.newHashMap();
                String quesContent = String.valueOf(map.get("content"));
                String questTitle = String.valueOf(map.get("title"));
                String level = String.valueOf(map.get("level"));
                String isPass = String.valueOf(map.get("isPass"));
                com.google.gson.internal.LinkedTreeMap xx=(com.google.gson.internal.LinkedTreeMap)map.get("fileIds");
                List<String> fileIds=Lists.newArrayList();
                if(!ObjectUtils.isEmpty(xx))
                    fileIds=(List<String>)xx.get("value");
                TroubleQuestionBean troubleQuestionBean = new TroubleQuestionBean();
                String questionId = UUIDHexGenerator.generator();
                troubleQuestionBean.setContent(quesContent);
                troubleQuestionBean.setTitle(questTitle);
                troubleQuestionBean.setYhLevel(level.substring(0, level.length() - 2));
                troubleQuestionBean.setIsPass(isPass);
                troubleQuestionBean.setId(questionId);
                troubleQuestionBean.setUpdateTime(new Date());
                troubleQuestionBean.setCreateTime(new Date());
                troubleQuestionBean.setFileIds(fileIds);
                quesTionBeanList.add(troubleQuestionBean);
                questMap.put("bussinessId", questionId);
                questMap.put("questTitle", questTitle);
                questRetList.add(questMap);
            }
            retturnMap.put("questRetList", questRetList);
            troubleService.addTrouble(troubleBean, troubleZzBean, quesTionBeanList);
            // 增加后，返回前端问题List，包含问题Title与问题ID信息，以便前端上传图片
            retMap = this.generateMsg(retturnMap, true, "增加成功!");
            return retMap;
        }
        catch (Exception e)
        {
             e.printStackTrace();
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加失败!");
            return retMap;
        }
    }

    @RequestMapping("addNewQues")
    public @ResponseBody Map<String, Object> addNewQues()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String troubleId = this.getParameter("troubleId");
            // 查询隐患追踪状态
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("yhId", troubleId);
            mapWhere.put("type", "0");
            List<TroubleZzBean> troubleZzList = troubleService.queryTroublezz(mapWhere);
            List<TroubleQuestionBean> quesTionBeanList = Lists.newArrayList();
            String questDataList = this.getParameter("questDataList");
            List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(questDataList, List.class);
            for (Map<String, Object> map : questionList)
            {
                Map<String, Object> questMap = Maps.newHashMap();
                String quesContent = String.valueOf(map.get("content"));
                String questTitle = String.valueOf(map.get("title"));
                String level = String.valueOf(map.get("level"));
                String isPass = String.valueOf(map.get("isPass"));
                TroubleQuestionBean troubleQuestionBean = new TroubleQuestionBean();
                String questionId = UUIDHexGenerator.generator();
                troubleQuestionBean.setYhId(troubleId);
                troubleQuestionBean.setYhzzId(troubleZzList.get(0).getId());
                troubleQuestionBean.setContent(quesContent);
                troubleQuestionBean.setTitle(questTitle);
                troubleQuestionBean.setYhLevel(level.substring(0, level.length() - 2));
                troubleQuestionBean.setIsPass(isPass);
                troubleQuestionBean.setId(questionId);
                troubleQuestionBean.setUpdateTime(new Date());
                troubleQuestionBean.setCreateTime(new Date());
                quesTionBeanList.add(troubleQuestionBean);
            }
            if (!quesTionBeanList.isEmpty())
                troubleService.addTroubleQuestBatch(quesTionBeanList);
            retMap = this.generateMsg(quesTionBeanList, true, "新增成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "新增失败!");
            return retMap;
        }

    }

    @RequestMapping("queryOrgPeople")
    public @ResponseBody Map<String, Object> queryOrgPeople()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {
            String orgId = this.getParameter("orgId");
            Map<String, Object> map = new HashMap<String, Object>();
            List<Map<String, Object>> listMap = organService.queryOrgUsersByOrgId(orgId);
            List<Map<String, Object>> userInfos = Lists.newArrayList();
            List<Map<String, Object>> zgzrrUserInfos = Lists.newArrayList();
            for (Map<String, Object> userMap : listMap)
            {
                String userRoleCodes = String.valueOf(userMap.get("USERROLE"));
                if (userRoleCodes.indexOf("ngaqglry") > -1 || userRoleCodes.indexOf("xmbzygcs") > -1
                        || userRoleCodes.indexOf("xmbaqglry") > -1 || userRoleCodes.indexOf("fbaqglry") > -1)
                {
                    userInfos.add(userMap);
                }
                if (userRoleCodes.indexOf("ngldglc") > -1 || userRoleCodes.indexOf("xmbldglc") > -1
                        || userRoleCodes.indexOf("fbxmjl") > -1)
                {
                    zgzrrUserInfos.add(userMap);
                }
            }
            // 如果当前用户是南轨人员，查询本单位的用户
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            if (userBean.getOrgId().equals("364028873bnull6734117a0167341217150001"))
            {
                listMap = organService.queryOrgUsersByOrgId(userBean.getOrgId());
                for (Map<String, Object> userMap : listMap)
                {
                    String userRoleCodes = String.valueOf(userMap.get("USERROLE"));
                    if (userRoleCodes.indexOf("ngldglc") > -1 || userRoleCodes.indexOf("xmbldglc") > -1
                            || userRoleCodes.indexOf("fbxmjl") > -1)
                    {
                        zgzrrUserInfos.add(userMap);
                    }
                }
            }
            map.put("users", userInfos);
            map.put("zgzrrUserInfos", zgzrrUserInfos);
            retMap = this.generateMsg(map, true, "查询成功!");
            return retMap;

        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    @RequestMapping("updateTrouble")
    public @ResponseBody Map<String, Object> updateTrouble()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String id = this.getParameter("id");
            String title = this.getParameter("title");
            String gcmc = this.getParameter("gcmc");
            String yhbh = this.getParameter("yhbh");
            String zgsx = this.getParameter("zgsx");
            String jcdw = this.getParameter("jcdw");
            String jcdwId = this.getParameter("jcdwId");
            String jcr = this.getParameter("jcr");
            String jcrId = this.getParameter("jcrId");
            String sjdw = this.getParameter("sjdw");
            String sjdwId = this.getParameter("sjdwId");
            String sjr = this.getParameter("sjr");
            String sjrId = this.getParameter("sjrId");
            String jclb = this.getParameter("jclb");
            String zgzrrId = this.getParameter("zgzrrId");
            String zgzrr = this.getParameter("zgzrr");

            TroubleBean troubleBean = new TroubleBean();
            troubleBean.setId(id);
            troubleBean.setUpdateTime(new Date());
            troubleBean.setTitle(title);
            troubleBean.setGcmc(gcmc);
            troubleBean.setYhbh(yhbh);
            if (StringHelper.isNullAndEmpty(zgsx))
                troubleBean.setZgsx(null);
            else
                troubleBean.setZgsx(DateUtil.formatDate(zgsx, DateUtil.DATETIME_DEFAULT_FORMAT));
            troubleBean.setJcdw(jcdw);
            troubleBean.setJcdwId(jcdwId);
            troubleBean.setJcr(jcr);
            troubleBean.setJcrId(jcrId);
            troubleBean.setSjdw(sjdw);
            troubleBean.setSjdwId(sjdwId);
            troubleBean.setSjr(sjr);
            troubleBean.setSjrId(sjrId);
            troubleBean.setJclb(jclb);
            troubleBean.setZgzrr(zgzrr);
            troubleBean.setZgzrrId(zgzrrId);

            List<TroubleQuestionBean> quesTionBeanList = Lists.newArrayList();
            String questDataList = this.getParameter("questDataList");
            List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(questDataList, List.class);
            for (Map<String, Object> map : questionList)
            {
                String quesContent = String.valueOf(map.get("content"));
                String questTitle = String.valueOf(map.get("title"));
                String questionId = String.valueOf(map.get("id"));
                String level = String.valueOf(map.get("level"));

                com.google.gson.internal.LinkedTreeMap xx=(com.google.gson.internal.LinkedTreeMap)map.get("fileIds");
                List<String> fileIds=Lists.newArrayList();
                if(!ObjectUtils.isEmpty(xx))
                    fileIds=(List<String>)xx.get("value");

                TroubleQuestionBean troubleQuestionBean = new TroubleQuestionBean();
                troubleQuestionBean.setYhLevel(level.substring(0, level.length() - 2));
                troubleQuestionBean.setContent(quesContent);
                troubleQuestionBean.setTitle(questTitle);
                troubleQuestionBean.setId(questionId);
                troubleQuestionBean.setUpdateTime(new Date());
                troubleQuestionBean.setFileIds(fileIds);
                quesTionBeanList.add(troubleQuestionBean);
            }

            troubleService.updateTrouble(troubleBean, quesTionBeanList);
            retMap = this.generateMsg("", true, "修改成功!");
            return retMap;
        }
        catch (Exception e)
        {

            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改失败!");
            return retMap;
        }

    }

    @RequestMapping("delTrouble")
    public @ResponseBody Map<String, Object> delTrouble()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            List<String> deleIds = Lists.newArrayList();
            for (Map<String, Object> map : delelteList)
            {
                deleIds.add(String.valueOf(map.get("id")));
            }
            troubleService.delTrouble(deleIds);
            retMap = this.generateMsg("", true, "删除成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除失败!");
            return retMap;
        }

    }

    /**
     * delTroubleQuest:(根据ID删除问题). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("delTroubleQuest")
    public @ResponseBody Map<String, Object> delTroubleQuest()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String questId = this.getParameter("questId");
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("id", questId);
            troubleService.delTroubleQuest(mapWhere);
            retMap = this.generateMsg("", true, "删除成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除失败!");
            return retMap;
        }

    }

    /**
     * publishTrouble:(隐患发布). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("publishTrouble")
    public @ResponseBody Map<String, Object> publishTrouble()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> publishList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            troubleService.publishTrouble(publishList, userBean);
            retMap = this.generateMsg("", true, "发布成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "发布失败!");
            return retMap;
        }

    }

    @RequestMapping("queryQuesByZzId")
    public @ResponseBody Map<String, Object> queryQuesByZzId()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String yhzzId = this.getParameter("yhzzId");
            List<TroubleQuestionBean> questionList = troubleService.queryQuesByZzId(yhzzId);
            retMap = this.generateMsg(questionList, true, "查询成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    /**
     * publishTrouble:(根据隐患Id查询所有问题). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("queryQuesByTroubId")
    public @ResponseBody Map<String, Object> queryQuesByTroubId()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String yhzzType = this.getParameter("yhzzType");
            String troubleId = this.getParameter("troubleId");

            List<TroubleQuestionBean> questionList = troubleService.queryQuesByTroubId(troubleId, yhzzType);
            retMap = this.generateMsg(questionList, true, "查询成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    // 获得图片的base64码
    private static String getImageBase(String src)
    {
        if (src == null || src == "")
        {
            return "";
        }
        File file = new File(src);
        if (!file.exists())
        {
            return "";
        }
        InputStream in = null;
        byte[] data = null;
        try
        {
            in = new FileInputStream(file);
        }
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        try
        {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Base64Helper.encryptBASE64(data);
    }

    /**
     * 预览检查记录表
     * viewJcjlb:(预览检查记录表). <br/>
     *
     * @author wyf
     * @throws Exception
     * @since JDK 1.6
     */
    @RequestMapping("viewJcjlb")
    public void viewJcjlb() throws Exception
    {
        String bussinessId = this.getParameter("bussinessId");
        String pdfName = this.getParameter("pdfName");
        String fileUrl = ConfigUtil.getValueByKey("fileRootUrl") + "隐患管理/安全检查/" + pdfName + "_" + bussinessId + ".pdf";
        BufferedInputStream bis = null;
        File file = new File(fileUrl);
        if (file.exists())
        {
            // 获取输入流
            bis = new BufferedInputStream(new FileInputStream(fileUrl));
            response.setContentType("application/pdf");
            int len = 0;
            byte[] b = new byte[1024];

            while ((len = bis.read(b, 0, 1024)) != -1)
            {
                response.getOutputStream().write(b, 0, len);
            }
            bis.close();
            response.getOutputStream().flush();
        }
    }

    @RequestMapping("dowloadJcjlb")
    public void dowloadJcjlb()
    {
        try
        {
            String fileName = this.getParameter("fileName");
            String bussinessId = this.getParameter("bussinessId");
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            String fileUrl =
                    ConfigUtil.getValueByKey("fileRootUrl") + "隐患管理/安全检查/" + fileName + "_" + bussinessId + ".docx";
            if (StringHelper.isNotNullAndEmpty(fileUrl))
            {
                File file = new File(fileUrl);
                if (file.exists())
                {
                    // 设置响应头和客户端保存文件名
                    String fileName_ = fileName + ".docx";
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-disposition",
                            "attachment; filename=" + URLEncoder.encode(fileName_, "UTF-8"));
                    response.setHeader("Content-Length", String.valueOf(file.length()));
                    // 获取输入流
                    bis = new BufferedInputStream(new FileInputStream(fileUrl));
                    // 输出流
                    bos = new BufferedOutputStream(response.getOutputStream());
                    byte[] buff = new byte[2048];
                    int bytesRead = 0;
                    while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
                    {
                        bos.write(buff, 0, bytesRead);
                    }
                    // 关闭流
                    bis.close();
                    bos.close();
                }
            }

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }

    }

    /**
     * 隐患响应
     * xyTrouble:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @since JDK 1.6
     */
    @RequestMapping("xyTrouble")
    public @ResponseBody Map<String, Object> xyTrouble()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String troubleId = this.getParameter("troubleId");
            troubleService.addXyTrouble(troubleId);
            retMap = this.generateMsg("", true, "响应成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "响应失败!");
            return retMap;
        }

    }

    @RequestMapping("troubleClFc")
    public @ResponseBody Map<String, Object> troubleClFc()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> mapRet = Maps.newHashMap();
            List<Map<String, Object>> retlist = Lists.newArrayList();

            String troubleId = this.getParameter("troubleId");
            String zgsx = this.getParameter("zgsx");
            String isPass = this.getParameter("isPass");
            String isyhCl = this.getParameter("isyhCl");
            String zgjf = this.getParameter("zgjf");
            if (StringHelper.isNullAndEmpty(zgjf))
                zgjf = "0";

            String existTroubelzzId = this.getParameter("troubleZzId");
            if (StringHelper.isNotNullAndEmpty(existTroubelzzId))
            {// 如果原来 追踪表存在，则更新追踪表数据和问题基本信息
                TroubleBean troubleBean = troubleService.queryOneTrouble(troubleId);
                TroubleZzBean zzBean = troubleService.queryTroublezzById(existTroubelzzId);
                zzBean.setZgjf(zgjf);
                List<TroubleQuestionBean> questList = troubleService.queryQuesByZzId(existTroubelzzId);
                String questDataList = this.getParameter("questDataList");
                List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(questDataList, List.class);
                for (Map<String, Object> map : questionList)
                {
                    String quesContent = String.valueOf(map.get("content"));
                    String questTitle = String.valueOf(map.get("title"));
                    String yyfx = String.valueOf(map.get("yyfx"));

                    com.google.gson.internal.LinkedTreeMap xx=(com.google.gson.internal.LinkedTreeMap)map.get("fileIds");
                    List<String> fileIds=Lists.newArrayList();
                    if(!ObjectUtils.isEmpty(xx))
                        fileIds=(List<String>)xx.get("value");
                    for (TroubleQuestionBean ques : questList)
                    {
                        if (questTitle.equals(ques.getTitle()))
                        {
                            ques.setYyfx(yyfx);
                            ques.setContent(quesContent);
                            ques.setFileIds(fileIds);
                        }
                    }
                }
                troubleService.updateYhZzBean(zzBean);
                troubleService.updateQuestBatch(questList);
                for (TroubleQuestionBean ques : questList)
                {
                    Map<String, Object> questMap = Maps.newHashMap();
                    questMap.put("bussinessId", ques.getId());
                    questMap.put("questTitle", ques.getTitle());
                    retlist.add(questMap);
                }
                mapRet.put("yhId", troubleBean.getId());
                mapRet.put("yhzzId", zzBean.getId());
                mapRet.put("quesList", retlist);
            }
            else
            {

                TroubleBean troubleBean = troubleService.queryOneTrouble(troubleId);
                // 初始化追踪表
                TroubleZzBean troubleZzBean = new TroubleZzBean();
                String troubelzzId = UUIDHexGenerator.generator();
                troubleZzBean.setId(troubelzzId);
                troubleZzBean.setYhId(troubleId);
                troubleZzBean.setCreateTime(new Date());
                troubleZzBean.setUpdateTime(new Date());
                if ("1".equals(isyhCl))
                {// 如果是隐患处理整改
                    troubleZzBean.setZgjf(zgjf);
                    //troubleBean.setStatus("3");// 设置隐患状态为待复查
                    // 查询该隐患下整改的最大次数
                    String maxCS = troubleService.queryTroubleZzMaxCs(troubleId, "1");
                    if (StringHelper.isNullAndEmpty(maxCS))
                        troubleZzBean.setCs("1");
                    else
                        troubleZzBean.setCs(String.valueOf(Integer.valueOf(maxCS) + 1));
                    troubleZzBean.setType("1");// 整改类型
                }
                else
                {// 如果是复查隐患
                    if ("1".equals(isPass)) // 通过
                    {
                        troubleBean.setStatus("4");// 状态为闭环
                        // 查询该隐患下复查的最大次数
                        String maxCS = troubleService.queryTroubleZzMaxCs(troubleId, "2");
                        if (StringHelper.isNullAndEmpty(maxCS))
                            troubleZzBean.setCs("1");
                        else
                            troubleZzBean.setCs(String.valueOf(Integer.valueOf(maxCS) + 1));
                        troubleZzBean.setType("3");// 直接跳过复查状态，变为闭环状态，意思为：经过第N次复查，通过
                    }
                    else
                    {// 未通过
                        troubleBean.setZgsx(DateUtil.formatDate(zgsx, DateUtil.DATETIME_DEFAULT_FORMAT));
                        troubleBean.setStatus("2");// 状态为待整改
                        // 查询该隐患下复查的最大次数
                        String maxCS = troubleService.queryTroubleZzMaxCs(troubleId, "2");
                        if (StringHelper.isNullAndEmpty(maxCS))
                            troubleZzBean.setCs("1");
                        else
                            troubleZzBean.setCs(String.valueOf(Integer.valueOf(maxCS) + 1));
                        troubleZzBean.setType("2");// 设为复查状态，意思为：经过第N次复查，未通过
                    }

                }

                // 增加隐患问题表
                List<TroubleQuestionBean> quesTionBeanList = Lists.newArrayList();
                String questDataList = this.getParameter("questDataList");
                List<Map<String, Object>> questionList = JsonHelper.fromJsonWithGson(questDataList, List.class);
                for (Map<String, Object> map : questionList)
                {
                    String quesContent = String.valueOf(map.get("content"));
                    String questTitle = String.valueOf(map.get("title"));
                    String level = String.valueOf(map.get("level"));
                    String yyfx = String.valueOf(map.get("yyfx"));
                    String questIsPass = String.valueOf(map.get("isPass"));
                    com.google.gson.internal.LinkedTreeMap xx=(com.google.gson.internal.LinkedTreeMap)map.get("fileIds");
                    List<String> fileIds=Lists.newArrayList();
                    if(!ObjectUtils.isEmpty(xx))
                        fileIds=(List<String>)xx.get("value");
                    TroubleQuestionBean troubleQuestionBean = new TroubleQuestionBean();
                    String questionId = UUIDHexGenerator.generator();
                    troubleQuestionBean.setContent(quesContent);
                    troubleQuestionBean.setTitle(questTitle);
                    troubleQuestionBean.setYhLevel(level.substring(0, level.length() - 2));
                    troubleQuestionBean.setId(questionId);
                    troubleQuestionBean.setYyfx(yyfx);
                    troubleQuestionBean.setIsPass(questIsPass);
                    troubleQuestionBean.setFileIds(fileIds);
                    troubleQuestionBean.setUpdateTime(new Date());
                    troubleQuestionBean.setCreateTime(new Date());
                    quesTionBeanList.add(troubleQuestionBean);
                    Map<String, Object> questMap = Maps.newHashMap();
                    questMap.put("bussinessId", questionId);
                    questMap.put("questTitle", questTitle);
                    retlist.add(questMap);
                }
                String sessionId = this.getParameter("sessionId");
                String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
                UserBean userBean = userService.queryUserByCode(userCode);
                troubleService.addtroubleClFc(troubleBean, troubleZzBean, quesTionBeanList, isyhCl, isPass, userBean);
                mapRet.put("yhId", troubleBean.getId());
                mapRet.put("yhzzId", troubleZzBean.getId());
                mapRet.put("quesList", retlist);
            }
            retMap = this.generateMsg(mapRet, true, "操作成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "操作失败!");
            return retMap;
        }

    }

    /**
     * 生成安全隐患整改回复单
     * genrateClFc:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("genrateClFc")
    public @ResponseBody Map<String, Object> genrateClFc()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String troubleId = this.getParameter("troubleId");
            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            troubleService.addRateFkbg(troubleId, userBean);
            retMap = this.generateMsg("", true, "生成成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "生成失败!");
            return retMap;
        }
    }

    @RequestMapping("queryYhzzInfos")
    public @ResponseBody Map<String, Object> queryYhzzInfos()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String troubleId = this.getParameter("troubleId");
            // 查询隐患下所有追踪消息列表
            List<Map<String, Object>> retList = troubleService.queryYhzzInfos(troubleId);
            retMap = this.generateMsg(retList, true, "查询成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    /**
     * 查询最新一条隐患追踪记录
     * queryNewYhzz:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author Administrator
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("queryNewYhzz")
    public @ResponseBody Map<String, Object> queryNewYhzz()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String troubleId = this.getParameter("troubleId");// 隐患ID
            String type = this.getParameter("yhzzType");// 追踪类型
            // 查询隐患下所有追踪消息列表
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("yhId", troubleId);
            mapWhere.put("type", type);
            List<TroubleZzBean> zzList = troubleService.queryTroublezz(mapWhere);

            retMap = this.generateMsg((zzList == null || zzList.isEmpty()) ? "" : zzList.get(0), true, "查询成功");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    @RequestMapping("qryClcs")
    public @ResponseBody Map<String, Object> qryClcs()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String troubleId = this.getParameter("troubleId");
            String type = this.getParameter("yhzzType");
            String maxCs = troubleService.queryTroubleZzMaxCs(troubleId, type);
            if (StringHelper.isNotNullAndEmpty(maxCs))
            {
                retMap = this.generateMsg(maxCs, true, "查询成功");
                return retMap;
            }
            else
            {
                retMap = this.generateMsg("0", true, "查询成功");
                return retMap;
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    @RequestMapping("dowTrouble")
    public @ResponseBody Map<String, Object> dowTrouble()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> mapWhere = Maps.newHashMap();

            mapWhere.put("title", this.getParameter("title"));
            mapWhere.put("jclb", this.getParameter("sjdw"));
            mapWhere.put("jcdw", this.getParameter("jcdw"));
            mapWhere.put("sjdw", this.getParameter("jcdw"));
            mapWhere.put("status", this.getParameter("jcdw"));
            mapWhere.put("createTime", this.getParameter("jcdw"));
            mapWhere.put("ymtype", this.getParameter("ymtype"));
            String createTimeData = String.valueOf(mapWhere.get("createTime"));
            if (StringHelper.isNotNullAndEmpty(createTimeData))
            {
                String startDate =
                        createTimeData.substring(0, StringHelper.getFromIndex(createTimeData, "-", 3)).trim();
                String endDate = createTimeData
                        .substring(StringHelper.getFromIndex(createTimeData, "-", 3) + 2, createTimeData.length())
                        .trim();
                mapWhere.put("startDate", startDate);
                mapWhere.put("endDate", endDate);
            }
            Calendar cale = Calendar.getInstance();
            if (mapWhere.get("startDate") == null)
            {// 如果未null,默认当前年份
                mapWhere.put("startDate", cale.get(Calendar.YEAR) + "-01-01");
            }
            if (mapWhere.get("endDate") == null)
            {// 如果未null,默认当前年份
                mapWhere.put("endDate", cale.get(Calendar.YEAR) + "-12-31");
            }
            List<TroubleBean> troubleList = troubleService.queryAllTroubleList(mapWhere, null);

            List<Map<String, Object>> dataList = Lists.newArrayList();
            for (TroubleBean troubleBean : troubleList)
            {// 对符合条件的隐患处理数据
             // 新建的隐患所有问题列表
                Map<String, Object> map = Maps.newHashMap();
                map.put("yhbh", troubleBean.getYhbh());

                map.put("questList", "");
                List<TroubleQuestionBean> questionList = troubleService.queryQuesByTroubId(troubleBean.getId(), "0");
                for (int i = 0; i < questionList.size(); i++)
                {
                    TroubleQuestionBean troubleQuestionBean = questionList.get(i);
                    String questList = String.valueOf(map.get("questList"))
                            + ("问题" + (i + 1) + ":" + troubleQuestionBean.getTitle() + "；");
                    map.put("questList", questList);
                }

                map.put("jcdw", troubleBean.getJcdw());
                map.put("sjdw", troubleBean.getSjdw());
                map.put("jcr", troubleBean.getJcr());

                map.put("zgwcqk", "");
                map.put("yzrq", "");
                map.put("sfyz", "否");
                questionList = troubleService.queryQuesByTroubId(troubleBean.getId(), "3");
                if (!questionList.isEmpty())
                {// 隐患复查通过
                    map.put("zgwcqk", "已全部整改完成");
                    map.put("sfyz", "是");
                    map.put("yzrq",
                            DateUtil.getDateFormat(questionList.get(0).getCreateTime(),
                                    DateUtil.DATETIME_DEFAULT_FORMAT));
                }
                else
                {
                    questionList = troubleService.queryQuesByTroubId(troubleBean.getId(), "2");
                    for (int i = 0; i < questionList.size(); i++)
                    {
                        TroubleQuestionBean troubleQuestionBean = questionList.get(i);
                        String zgwcqk =
                                "问题" + (i + 1) + ("Y".equals(troubleQuestionBean.getIsPass()) ? "整改完成；" : "未整改完成；");
                        map.put("zgwcqk", String.valueOf(map.get("zgwcqk")) + zgwcqk);
                    }
                    if (!questionList.isEmpty())
                    {
                        map.put("sfyz", "是");
                        map.put("yzrq",
                                DateUtil.getDateFormat(questionList.get(0).getCreateTime(),
                                        DateUtil.DATETIME_DEFAULT_FORMAT));
                    }

                }

                map.put("isqftzd", "否");
                mapWhere.clear();
                mapWhere.put("yhId", troubleBean.getId());
                mapWhere.put("type", "5");
                List<TroubleZzBean> troubleZzList = troubleService.queryTroublezz(mapWhere);
                if (!troubleZzList.isEmpty())
                    map.put("isqftzd", "是");

                map.put("jcrq", DateUtil.getDateFormat(troubleBean.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                dataList.add(map);
            }
            this.writeExcel("安全隐患隐患整改通知单台账.xlsx", dataList, response);
            retMap = this.generateMsg("", true, "下载成功!");
            return retMap;

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "下载失败!");
            return retMap;
        }
    }

    private void writeExcel(String fileName, List<Map<String, Object>> dataList, HttpServletResponse response)
            throws Exception
    {

        String fileUrl = this.getClass().getClassLoader().getResource("").getPath() + "templates/yhtz.xlsx";
        File file = new File(fileUrl);
        if (!file.exists())
        {
            throw new Exception("模板文件不存在!");
        }
        InputStream is = new FileInputStream(file);
        Workbook wb = new XSSFWorkbook(is);
        Sheet sheet = wb.getSheetAt(0);
        int rowIndex = 2;
        for (Map<String, Object> map : dataList)
        {
            Row row = sheet.createRow(rowIndex);
            for (int mapIndex = 0; mapIndex < 11; mapIndex++)
            {
                Cell cell = row.createCell(mapIndex);
                CellStyle style = wb.createCellStyle();
                style.setBorderLeft(CellStyle.BORDER_THIN);
                style.setBorderRight(CellStyle.BORDER_THIN);
                style.setBorderTop(CellStyle.BORDER_THIN);
                style.setBorderBottom(CellStyle.BORDER_THIN);
                style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
                style.setAlignment(CellStyle.ALIGN_CENTER);// 水平
                style.setWrapText(true);// 指定当单元格内容显示不下时自动换行
                cell.setCellStyle(style);
                switch (mapIndex)
                {
                case 0:
                    cell.setCellValue(rowIndex - 1);
                    break;
                case 1:
                    cell.setCellValue(String.valueOf(map.get("yhbh")));
                    break;
                case 2:
                    cell.setCellValue(String.valueOf(map.get("questList")));
                    break;
                case 3:
                    cell.setCellValue(String.valueOf(map.get("jcdw")));
                    break;
                case 4:
                    cell.setCellValue(String.valueOf(map.get("sjdw")));
                    break;
                case 5:
                    cell.setCellValue(String.valueOf(map.get("jcr")));
                    break;
                case 6:
                    cell.setCellValue(String.valueOf(map.get("zgwcqk")));
                    break;
                case 7:
                    cell.setCellValue(String.valueOf(map.get("isqftzd")));
                    break;
                case 8:
                    cell.setCellValue(String.valueOf(map.get("jcrq")));
                    break;
                case 9:
                    cell.setCellValue(String.valueOf(map.get("yzrq")));
                    break;
                case 10:
                    cell.setCellValue(String.valueOf(map.get("sfyz")));
                    break;
                }
            }
            rowIndex++;
        }
        // 设置响应头和客户端保存文件名
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 获取输入流
        OutputStream os = response.getOutputStream();
        wb.write(os);
        // 关闭流
        os.close();
        // 关闭文件流
        is.close();
    }

    public static void main(String args[])
    {
        Map<String, Object> map = Maps.newHashMap();
        String v[] = new String[2];
        map.put("a", "xxx");
        v[0] = "22";
        v[1] = "33";
        // if(a instanceof String[]){
        // System.out.println("ss");
        // }

        if (map.get("a").getClass().isArray())
        {
            String[] b = (String[]) map.get("a");
            System.out.println(b.length);
        }
    }

}
