package com.jsumt.controller.system;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.PinyinUtil;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.util.FreeMarkerWordUtils;
import com.jsumt.vo.system.OrganizationBean;

@Controller
public class OrganizationController extends BaseController
{
    @Autowired
    private OrganizationService organService;

    // 加载组织机构所有节点
    @RequestMapping("queryorganizations")
    public @ResponseBody Map<String, Object> queryorganizations(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 根据查询条件加载组织机构的树形结构，若没有条件则加载所以节点
        List<OrganizationBean> organList = organService.queryorganizations(mapWhere);
        resultMap.put("rows", organList);
        return resultMap;
    }

    @RequestMapping("queryorganizationsTree")
    public @ResponseBody String queryorganizationsTree(@RequestParam Map<String, Object> mapWhere)
            throws UnsupportedEncodingException
    {
        List<Map<String, Object>> listMap = Lists.newArrayList();// 返回结果List
        List<OrganizationBean> organList = organService.queryorganizations(null);// 查出所有符合条件的记录，按父子排序
        String sessionId = request.getParameter("sessionId");
        String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
        listMap = organService.quyAuthTreeOrg(userCode, organList);
        return JsonHelper.toJsonWithGson(listMap);
    }

    // 增加组织机构
    @RequestMapping("addorganization")
    public @ResponseBody Map<String, Object> addorganization(OrganizationBean bean)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (bean.getOrg_pid() == null)
            {
                bean.setOrg_pid("0");
                bean.setOrgLayer("1");// 顶层节点1层
                bean.setIsLeaf("1");
                bean.setIconCls(IconClass.ICON_ORG.toString());
            }
            else
            {
                // 查询父节点层级
                OrganizationBean paraent = organService.queryOneById(bean.getOrg_pid());
                paraent.setIsLeaf("0");// 更新父节点IS_LEAF
                paraent.setIconCls(IconClass.ICON_ORG.toString());
                organService.updateOrganization(paraent);

                bean.setOrgLayer(String.valueOf(Integer.valueOf(paraent.getOrgLayer()) + 1));
                bean.setIsLeaf("1");// 默认设为1
                bean.setIconCls(IconClass.ICON_ORG.toString());
            }
            // 查询最大序号
            String maxNo = organService.queryMaxNo(bean.getOrg_pid());
            if (StringUtils.isEmpty(maxNo))
                bean.setOrgNo("1");
            else
                bean.setOrgNo(String.valueOf(Integer.valueOf(maxNo) + 1));

            // 封装组织机构的ORG_ID 和 NAME_SPELL
            bean.setOrg_id(UUIDHexGenerator.generator());
            bean.setName_spell(String.valueOf(PinyinUtil.getQuanPin(bean.getName_cn())));
            organService.addorganization(bean);
            resultMap = generateMsg("", true, "增加成功");
        }
        catch (Exception e)
        {
            resultMap = generateMsg("", false, "增加失败");
        }
        return resultMap;
    }

    // 单个查询组织机构
    @RequestMapping("queryorganizationData")
    public @ResponseBody Map<String, Object> queryOneById(String org_id)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (StringHelper.isNotNullAndEmpty(org_id))
            {
                OrganizationBean bean = organService.queryOneById(org_id);
                resultMap = this.generateMsg(bean, true, "查询成功");

            }
            else
            {
                resultMap = this.generateMsg("", false, "查询失败");
            }
        }
        catch (Exception e)
        {
            resultMap = this.generateMsg("", false, "查询失败");
        }
        return resultMap;
    }

    // 删除组织机构
    @RequestMapping("delorganizations")
    public @ResponseBody Map<String, Object> delorganizations(String org_id)
    {
        Map<String, Object> resultMap = null;
        try
        {
            if (StringHelper.isNotNullAndEmpty(org_id))
            {
                organService.deleteByPrimaryKey(org_id);
                resultMap = this.generateMsg("", true, "删除成功");

            }
        }
        catch (Exception e)
        {
            resultMap = this.generateMsg("", false, "删除失败");
        }
        return resultMap;
    }

    // 修改组织机构信息
    @RequestMapping("editorganization")
    public @ResponseBody Map<String, Object> editorganization(OrganizationBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            organService.updateOrganization(bean);
            retMap = this.generateMsg("", true, "修改成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "修改失败!");
            return retMap;
        }
    }

    /**
     * 整改通知书
     * exportSellPlan:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param id
     * @since JDK 1.6
     */
    @RequestMapping("/exports2")
    public @ResponseBody void exportSellPlan2(Long id)
    {
        // 获得数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sjdw", "sjdw");// 受检单位
        map.put("sjr", "sjr");
        map.put("gcmc", "gcmc");
        map.put("jcdw", "jcdw");
        map.put("jcr", "jcr");
        map.put("year", "2018");
        map.put("month", "12");
        map.put("day", "13");

        String file = FreeMarkerWordUtils.getImageBase(
                this.getClass().getClassLoader().getResource("").getPath() + "templates/20180817162358.jpg");
        List<String> images = Lists.newArrayList();
        images.add(file);
        images.add(file);
        images.add(file);

        map.put("images", images);

        List<String> contents = Lists.newArrayList();
        contents.add("1,首先第一首先第一首先第一首先第一首先第一首先第一首先第一首先第一首先第一");
        contents.add("2,首先第二首先第二首先第2首先第2首先第2首先第2首先第2首先第2首先第2");
        contents.add("3,首先第3首先第3首先第3首先第3首先第3首先第3首先第3首先第3首先第3");
        contents.add("4,首先第4首先第4首先第4首先第4首先第4首先第4首先第4首先第4首先第4");
        map.put("contents", contents);

        List<String> clyjs = Lists.newArrayList();
        clyjs.add("处理意见1");
        clyjs.add("处理意见2");
        clyjs.add("处理意见3");
        clyjs.add("处理意见4");
        map.put("clyjs", clyjs);
        // zgsxY zgsxM zsgxD 整改时限年月日
        map.put("zgsxY", "2018");
        map.put("zgsxM", "12");
        map.put("zsgxD", "14");

        // 发出日期
        map.put("fcY", "2018");
        map.put("fcM", "12");
        map.put("fcD", "15");
        try
        {
            FreeMarkerWordUtils.exportMillCertificateWord(this.request, this.response, map, "事故隐患整改通知书", "zgtzs.ftl");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查询有权限看到的组织机构信息
     * queryAuthOrgInfo:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("queryAuthOrgInfo")
    public @ResponseBody Map<String, Object> queryAuthOrgInfo()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {

            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            String leftAll = this.getParameter("leftAll");// 是否显示全部
            List<Map<String, Object>> data = organService.queryAuthOrgInfoByUserCode(userCode, leftAll);
            retMap = this.generateMsg(data, true, "查询成功!");
            return retMap;

        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    @RequestMapping("queryChildOrg")
    public @ResponseBody Map<String, Object> queryChildOrg()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {
            String pid = this.getParameter("pid");
            List<OrganizationBean> data = organService.queryOnlyChildOrg(pid);
            // 把自己也加进去
            OrganizationBean organizationBean = organService.queryOneById(pid);
            data.add(organizationBean);
            retMap = this.generateMsg(data, true, "查询成功!");
            return retMap;

        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }

    }

    public static void main(String[] args)
    {
        String month = "01".substring(1);// 01
        System.out.println(month);
    }

}
