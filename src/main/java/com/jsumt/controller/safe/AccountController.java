/**
 * Project Name:ECRMS
 * File Name:AccountController.java
 * Package Name:com.jsumt.controller.safe
 * Date:2019年1月4日下午4:48:35
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.safe;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.controller.trouble.TroubleController;
import com.jsumt.service.file.FileService;
import com.jsumt.service.safe.AccountService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.file.FileBean;
import com.jsumt.vo.safe.AccountBean;
import com.jsumt.vo.safe.DetailAccountBean;
import com.jsumt.vo.system.UserBean;

import net.sf.jsqlparser.expression.StringValue;

/**
 * 安全生产投入探长
 * ClassName:AccountController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2019年1月4日 下午4:48:35 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("account")
public class AccountController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(TroubleController.class);
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserManageService userService;
    

    @Autowired
    private FileService fileService;

    // 查询出没有被分配的生产投入计划
    @RequestMapping("queryScjhtzNotInBatch")
    public @ResponseBody Map<String, Object> queryScjhtzNotInBatch()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("tbdwid", this.getParameter("tbdwid"));
            List<Map<String, Object>> listMap = accountService.queryScjhtzNotInBatch(res);
            retMap = this.generateMsg(listMap, true, "查询安全投入台账成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询安全投入台账失败!");
            return retMap;
        }
    }

    // 查询生产事故台账
    @RequestMapping("queryScjhtzList")
    public @ResponseBody Map<String, Object> queryScjhtzList(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<AccountBean> pageInfo = new PageInfo<AccountBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            List<AccountBean> tzList = accountService.queryAllTzs(mapWhere, pageInfo);
            pageInfo = new PageInfo<AccountBean>(tzList);

            List<Map<String, Object>> listMap = Lists.newArrayList();

            for (AccountBean bean : tzList)
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                map.put("title", bean.getYear() + "年" + bean.getMonth() + "月" + "安全生产登记台账");
                map.put("updateTime", DateUtil.getDateFormat(bean.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime", DateUtil.getDateFormat(bean.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                listMap.add(map);
            }
            resMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "查询失败!");
        }
        return resMap;
    }

    // 增加生产台账
    @RequestMapping("addSctz")
    public @ResponseBody Map<String, Object> addSctz(AccountBean bean)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            String id = UUIDHexGenerator.generator();
            bean.setId(id);
            //String trjhTotal = bean.getTrjhTotal();
            BigDecimal trjhTotal = new BigDecimal(bean.getTrjhTotal());
            // 初始默认为0
            bean.setTrjhTotalFinish("0");
            bean.setParentId("0");
            bean.setStatus("0");
            // 费用偏差=实际金额-计划总额
            bean.setTrjhFypc((new BigDecimal("0").subtract(trjhTotal)).toString());
            /*bean.setTrjhFypc(String.valueOf(0 - Integer.valueOf(trjhTotal)));*/
            bean.setCreateTime(new Date());
            bean.setUpdateTime(new Date());
            accountService.addSctz(bean);
            return resMap = this.generateMsg("", true, "增加成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "增加失败!");
            return resMap;
        }
    }

    // 修改安全投入台账的发布状态
    @RequestMapping("updateStatus")
    public @ResponseBody Map<String, Object> updateStatus(AccountBean bean)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            // 把上报状态设置为“1”
            bean.setStatus("1");
            accountService.updateAccount(bean);
            resMap = this.generateMsg("", true, "修改成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "修改失败!");
        }
        return resMap;
    }

    // 查询出已经被分配过的子单位的台账信息
    @RequestMapping("queryChildrenTzs")
    public @ResponseBody Map<String, Object> queryChildrenTzs(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;
            mapWhere.put("dwmc", this.getParameter("tbdw"));
            PageInfo<AccountBean> pageInfo = new PageInfo<AccountBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            List<AccountBean> tzList = accountService.queryAllTzs(mapWhere, pageInfo);
            pageInfo = new PageInfo<AccountBean>(tzList);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (AccountBean bean : tzList)
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                map.put("updateTime", DateUtil.getDateFormat(bean.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime", DateUtil.getDateFormat(bean.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                listMap.add(map);
            }
            resMap = layuiData(pageInfo.getTotal(), listMap);

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "查询失败!");
        }
        return resMap;
    }

    // 查询没有被分配的所有子单位（1，表里存在，已发布且没有被分配的 2表里存在未发布且没有被分配的 3计划表里根本就没有数据）
    @RequestMapping("queryNotChildrenTzs")
    public @ResponseBody Map<String, Object> queryNotChildrenTzs(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            mapWhere.put("dwmc", this.getParameter("dwmc"));

            List<Map<String, Object>> listMap = accountService.queryNotChildrenTzs(mapWhere, pageInfo);
            pageInfo = new PageInfo<Map<String, Object>>(listMap);
            // 把String类型换成Date类型
            for (Map<String, Object> map : listMap)
            {
                if (map.get("CREATETIME") != null)
                {
                    map.put("CREATETIME", DateUtil.getDateFormat((Date) map.get("CREATETIME")));
                }
            }
            resMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            resMap = this.generateMsg("", false, "查询失败!");
        }
        return resMap;
    }

    // 催报
    @RequestMapping("cbChildrenTz")
    public @ResponseBody Map<String, Object> cbChildrenTz()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String cbList = this.getParameter("cbList");
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            List<Map<String, Object>> cbLists = JsonHelper.fromJsonWithGson(cbList, List.class);
            accountService.cbChildrenTz(cbLists, userBean);
            retMap = this.generateMsg("", true, "催报成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "催报失败!");
        }
        return retMap;
    }

    // 删除安全安全投入台账及与其相关的内容和修改
    @RequestMapping("delTzList")
    public @ResponseBody Map<String, Object> delTzList()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            accountService.delTzList(delelteList);
            retMap = this.generateMsg("", true, "删除计划成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除计划失败!");
        }
        return retMap;
    }

    // 查询费用清单细项表
    @RequestMapping("queryTzDetailInfo")
    public @ResponseBody Map<String, Object> queryTzDetailInfo(@RequestParam Map<String, Object> mapWhere)
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

            PageInfo<DetailAccountBean> pageInfo = new PageInfo<DetailAccountBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            // 内容
            String content = this.getParameter("content");
            // 入账周期为每月的第一天至最后一天，其中包括30,31天，甚至2月份的最后一天
            String rzzq = this.getParameter("rzzq");// 入账周期查询
                if (StringHelper.isNotNullAndEmpty(rzzq))
                {
                    String startDate =
                            rzzq.substring(0, StringHelper.getFromIndex(rzzq, "-", 3)).trim();
                    String endDate = rzzq
                            .substring(StringHelper.getFromIndex(rzzq, "-", 3) + 2, rzzq.length())
                            .trim();
                    mapWhere.put("startDate", startDate);
                    mapWhere.put("endDate", endDate);
                }
            
            mapWhere.put("content", content);
            List<DetailAccountBean> detailAccountList = accountService.queryTzDetailInfo(mapWhere, pageInfo);
            List<Map<String, Object>> listMap = Lists.newArrayList();
           
            //用于合计数据的计算
            List<DetailAccountBean> detailAccountListHj = accountService.queryTzDetailInfo(mapWhere, null);
            BigDecimal fpsl = new BigDecimal("0");
            BigDecimal xj = new BigDecimal("0");
            BigDecimal key1 = new BigDecimal("0");
            BigDecimal key2 = new BigDecimal("0");
            BigDecimal key3 = new BigDecimal("0");
            BigDecimal key4 = new BigDecimal("0");
            BigDecimal key5 = new BigDecimal("0");
            BigDecimal key6 = new BigDecimal("0");
            BigDecimal key7 = new BigDecimal("0");
            BigDecimal key8 = new BigDecimal("0");
            BigDecimal key9 = new BigDecimal("0");
            for(DetailAccountBean detailBean :detailAccountListHj){
                Map<String, Object> map = BeanUtil.toMap(detailBean);
                BigDecimal sumFpsl = new BigDecimal(map.get("fpsl").toString());
                fpsl = fpsl.add(sumFpsl);
                BigDecimal sumXj = new BigDecimal(map.get("je").toString());
                xj = xj.add(sumXj);
                //9大项的合计计算
                // 对于前端title1的不同金额返回不同
                if ("完善、改造和维护安全防护设施设备支出（不含”三同时“要求初期投入的安全设施）".equals(detailBean.getSctrTypeTitle1()))
                {
                    key1 = key1.add(sumXj);
                }
                else if ("配备、维护、保养应急救援器材、设备支出和应急演练支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key2 = key2.add(sumXj);
                }
                else if ("开展重大危险源和事故隐患评估、监控和整改支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key3 = key3.add(sumXj);
                }
                else if ("安全生产检查、评价（不包括新建、改建、扩建项目安全评价）、咨询和标准化建设支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key4 = key4.add(sumXj);
                }
                else if ("配备和更新现场作业人员安全防护用品支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key5 = key5.add(sumXj);
                }
                else if ("安全生产宣传、教育、培训支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key6 = key6.add(sumXj);
                }
                else if ("安全生产试用的新技术、新标准、新工艺、新装备的推广应用支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key7 = key7.add(sumXj);
                }
                else if ("安全设施及特种设备检测支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key8 = key8.add(sumXj);
                }
                else if ("其他与安全生产直接相关的支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    key9 = key9.add(sumXj);
                }
            }
            
            pageInfo = new PageInfo<DetailAccountBean>(detailAccountList);
            //序号
            Integer numbers = 0; 
            //从页数及每页的个数计数
            Integer num = (intPage-1)*intPageSize;
            numbers = num+numbers;
            for (DetailAccountBean detailBean : detailAccountList)
            {
                Map<String, Object> map = BeanUtil.toMap(detailBean);
                // 判断入账日期，若为null，则不取
                if (StringHelper.isNotNullAndEmpty(String.valueOf(detailBean.getRzrq())))
                {
                    map.put("rzrq", DateUtil.getDateFormat(detailBean.getRzrq(), DateUtil.DATE_DEFAULT_FORMAT));
                }
                // 小计金额
                BigDecimal moneySum = new BigDecimal(detailBean.getJe());
                map.put("xj", moneySum.toString());
                // 对于前端title1的不同金额返回不同
                if ("完善、改造和维护安全防护设施设备支出（不含”三同时“要求初期投入的安全设施）".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key1", moneySum.toString());
                }
                else if ("配备、维护、保养应急救援器材、设备支出和应急演练支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key2", moneySum.toString());
                }
                else if ("开展重大危险源和事故隐患评估、监控和整改支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key3", moneySum.toString());
                }
                else if ("安全生产检查、评价（不包括新建、改建、扩建项目安全评价）、咨询和标准化建设支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key4", moneySum.toString());
                }
                else if ("配备和更新现场作业人员安全防护用品支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key5", moneySum.toString());
                }
                else if ("安全生产宣传、教育、培训支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key6", moneySum.toString());
                }
                else if ("安全生产试用的新技术、新标准、新工艺、新装备的推广应用支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key7", moneySum.toString());
                }
                else if ("安全设施及特种设备检测支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key8", moneySum.toString());
                }
                else if ("其他与安全生产直接相关的支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key9", moneySum.toString());
                }
                //序号排列
                numbers++;
                map.put("numbers", numbers);
                listMap.add(map);
            }
            
            Map<String,Object> tmap=Maps.newHashMap();
            tmap.put("numbers", "合计");
            tmap.put("fpsl", fpsl);
            tmap.put("xj", xj);
            tmap.put("key1", key1);
            tmap.put("key2", key2);
            tmap.put("key3", key3);
            tmap.put("key4", key4);
            tmap.put("key5", key5);
            tmap.put("key6", key6);
            tmap.put("key7", key7);
            tmap.put("key8", key8);
            tmap.put("key9", key9);
            listMap.add(tmap);
            retMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
        }
        return retMap;
    }

    // 费用清单的增加
    @RequestMapping("addTzQd")
    public @ResponseBody Map<String, Object> addTzQd(DetailAccountBean deailBean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            // 新建费用清单
            String id = UUIDHexGenerator.generator();
            deailBean.setId(id);
            deailBean.setUpdateTime(new Date());
            deailBean.setCreateTime(new Date());
            accountService.addTzQd(deailBean);
            retMap = this.generateMsg(id, true, "增加成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加失败!");
        }
        return retMap;
    }

    // 修改清单数据
    @RequestMapping("updateTzQd")
    public @ResponseBody Map<String, Object> updateTzQd(DetailAccountBean deailBean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            accountService.updateTzQd(deailBean);
            retMap = this.generateMsg(deailBean.getId(), true, "修改成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "修改失败!");
        }
        return retMap;
    }

    // 删除安全安全投入台账清单及与其相关的内容和修改
    @RequestMapping("delTzQd")
    public @ResponseBody Map<String, Object> delTzQd()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            String tzId = this.getParameter("tzId");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            accountService.delTzQd(delelteList, tzId);
            retMap = this.generateMsg("", true, "删除台账清单成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除台账清单失败!");
        }
        return retMap;
    }

    // 下载安全生产台账
    @RequestMapping("dowloadAqsctrTz")
    public @ResponseBody Map<String, Object> dowloadAqsctrTz()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            Map<String, Object> semap = Maps.newHashMap();
            // 思路：从前台传来的查询数据作为条件，查出所有的台账清单，然后再存储写进excel文件
            String pzh = this.getParameter("pzh");
            String content = this.getParameter("content");
            // 入账周期
            // 入账周期为每月的第一天至最后一天，其中包括30,31天，甚至2月份的最后一天
            String rzzq = this.getParameter("rzzq");// 入账周期查询
            if (StringHelper.isNotNullAndEmpty(rzzq))
            {
                String startDate =
                        rzzq.substring(0, StringHelper.getFromIndex(rzzq, "-", 3)).trim();
                String endDate = rzzq
                        .substring(StringHelper.getFromIndex(rzzq, "-", 3) + 2, rzzq.length())
                        .trim();
                semap.put("startDate", startDate);
                semap.put("endDate", endDate);
                rzzq = startDate+"--"+endDate;
            }
            String tzId = this.getParameter("tzId");
            semap.put("pzh", pzh);
            semap.put("content", content);
            semap.put("tzId", tzId);
            AccountBean account = accountService.queryTzById(tzId);
            List<DetailAccountBean> detailAccountList = accountService.queryTzDetailInfo(semap, null);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (DetailAccountBean detailBean : detailAccountList)
            {
                Map<String, Object> map = BeanUtil.toMap(detailBean);
                // 判断入账日期，若为null，则不取
                if (StringHelper.isNotNullAndEmpty(String.valueOf(detailBean.getRzrq())))
                {
                    map.put("rzrq", DateUtil.getDateFormat(detailBean.getRzrq(), DateUtil.DATE_DEFAULT_FORMAT));
                }
                // 小计金额
                BigDecimal moneySum = new BigDecimal(detailBean.getJe());
                map.put("xj", moneySum.toString());
                // 对于前端title1的不同金额返回不同
                if ("完善、改造和维护安全防护设施设备支出（不含”三同时“要求初期投入的安全设施）".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key1", moneySum.toString());
                }
                else
                {
                    map.put("key1", "0");
                }

                if ("配备、维护、保养应急救援器材、设备支出和应急演练支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key2", moneySum.toString());
                }
                else
                {
                    map.put("key2", "0");
                }
                if ("开展重大危险源和事故隐患评估、监控和整改支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key3", moneySum.toString());
                }
                else
                {
                    map.put("key3", "0");
                }
                if ("安全生产检查、评价（不包括新建、改建、扩建项目安全评价）、咨询和标准化建设支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key4", moneySum.toString());
                }
                else
                {
                    map.put("key4", "0");
                }
                if ("配备和更新现场作业人员安全防护用品支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key5", moneySum.toString());
                }
                else
                {
                    map.put("key5", "0");
                }
                if ("安全生产宣传、教育、培训支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key6", moneySum.toString());
                }
                else
                {
                    map.put("key6", "0");
                }
                if ("安全生产试用的新技术、新标准、新工艺、新装备的推广应用支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key7", moneySum.toString());
                }
                else
                {
                    map.put("key7", "0");
                }
                if ("安全设施及特种设备检测支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key8", moneySum.toString());
                }
                else
                {
                    map.put("key8", "0");
                }
                if ("其他与安全生产直接相关的支出".equals(detailBean.getSctrTypeTitle1()))
                {
                    map.put("key9", moneySum.toString());
                }
                else
                {
                    map.put("key9", "0");
                }
                listMap.add(map);
            }
            // 用于显示头与尾部
            Map<String, Object> map = Maps.newHashMap();
            map.put("dw", userBean.getOrgName());// 单位
            map.put("zbr", userBean.getUser_name());// 制表人
            map.put("createTime", DateUtil.getDateFormat(new Date(), DateUtil.DATE_CHINA_FORMAT));// 编制日期
            this.writeExcel(rzzq + "安全生产台账清单.xlsx", listMap, map, account, response);
            retMap = this.generateMsg("", true, "下载成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "下载失败!");
        }
        return retMap;
    }

    // 下载成excel文件
    private void writeExcel(String TzName, List<Map<String, Object>> listMap, Map<String, Object> map,
            AccountBean account, HttpServletResponse response) throws Exception
    {
        String fileUrl = this.getClass().getClassLoader().getResource("").getPath() + "templates/sctrtz.xlsx";
        File file = new File(fileUrl);
        if (!file.exists())
        {
            throw new Exception("模板文件不存在!");
        }
        InputStream is = new FileInputStream(file);
        Workbook wb = new XSSFWorkbook(is);
        Sheet sheet = wb.getSheetAt(0);

        // 样式
        CellStyle defaultStyle = wb.createCellStyle();
        defaultStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
        defaultStyle.setAlignment(CellStyle.ALIGN_CENTER);// 水平
        // 字体定义
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体大小
        font.setFontName("宋体"); // 字体
        defaultStyle.setFont(font);
        defaultStyle.setWrapText(true);// 指定当单元格内容显示不下时自动换行

        // 头信息
        CellRangeAddress region_1 = new CellRangeAddress(1, 1, 2, 4);
        sheet.addMergedRegion(region_1);
        sheet.getRow(1).createCell(2).setCellStyle(defaultStyle);
        sheet.getRow(1).getCell(2).setCellValue(String.valueOf(map.get("dw")));
        
        CellRangeAddress region_2 = new CellRangeAddress(1, 1, 8, 9);
        sheet.addMergedRegion(region_2);
        sheet.getRow(1).createCell(8).setCellStyle(defaultStyle);
        sheet.getRow(1).getCell(8).setCellValue(String.valueOf(map.get("createTime")));

        // 从第5行开始依次插入数据
        int rowIndex = 5;
        int x = 0;
        for (Map<String, Object> key : listMap)
        {
            //生成第五行或者++行
            Row row = sheet.createRow(rowIndex);
            for (int mapIndex = 0; mapIndex <= 16; mapIndex++)
            {
                //生成每一个框
                Cell cell = row.createCell(mapIndex);
                //生成样式
                CellStyle style = wb.createCellStyle();
                style.setBorderLeft(CellStyle.BORDER_THIN);
                style.setBorderRight(CellStyle.BORDER_THIN);
                style.setBorderTop(CellStyle.BORDER_THIN);
                style.setBorderBottom(CellStyle.BORDER_THIN);
                style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
                style.setAlignment(CellStyle.ALIGN_CENTER);// 水平
                //生成字体
                Font fontInner = wb.createFont();
                fontInner.setFontHeightInPoints((short) 10.5); // 字体大小
                fontInner.setFontName("宋体"); // 字体
                style.setFont(fontInner);
                style.setWrapText(false);// 指定当单元格内容显示不下时自动换行
                //设置
                cell.setCellStyle(style);
                //生成序号
                if(mapIndex==0){++x;}
                //向第五行每个空格/框中加入数据
                switch (mapIndex)
                {
                case 0:
                    cell.setCellValue(String.valueOf(x));
                    break;
                case 1:
                    cell.setCellValue(String.valueOf(key.get("rzrq")));
                    break;
                case 2:
                    cell.setCellValue(String.valueOf(key.get("pzh")));
                    break;
                case 3:
                    cell.setCellValue(String.valueOf(key.get("useDw")));
                    break;
                case 4:
                    cell.setCellValue(String.valueOf(key.get("content")));
                    break;
                case 5:
                    cell.setCellValue(String.valueOf(key.get("fpsl")));
                    break;
                case 6:
                    cell.setCellValue(String.valueOf(key.get("key1")));
                    break;
                case 7:
                    cell.setCellValue(String.valueOf(key.get("key2")));
                    break;
                case 8:
                    cell.setCellValue(String.valueOf(key.get("key3")));
                    break;
                case 9:
                    cell.setCellValue(String.valueOf(key.get("key4")));
                    break;
                case 10:
                    cell.setCellValue(String.valueOf(key.get("key5")));
                    break;
                case 11:
                    cell.setCellValue(String.valueOf(key.get("key6")));
                    break;
                case 12:
                    cell.setCellValue(String.valueOf(key.get("key7")));
                    break;
                case 13:
                    cell.setCellValue(String.valueOf(key.get("key8")));
                    break;
                case 14:
                    cell.setCellValue(String.valueOf(key.get("key9")));
                    break;
                case 15:
                    cell.setCellValue(String.valueOf(key.get("xj")));
                    break;
                default:
                    cell.setCellValue(String.valueOf(key.get("remark")));
                    break;
                }
            }
            rowIndex++;
        }
        // 开启合计行
        CellRangeAddress regionHj = new CellRangeAddress(rowIndex, rowIndex, 0, 5);//（起始行号，终止行号， 起始列号，终止列号） 下标从0开始
        sheet.addMergedRegion(regionHj);
        CellStyle style = wb.createCellStyle();
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
        style.setAlignment(CellStyle.ALIGN_CENTER);// 水平
        Font fontInner = wb.createFont();
        fontInner.setFontHeightInPoints((short) 10.5); // 字体大小
        fontInner.setFontName("宋体"); // 字体
        style.setFont(fontInner);
        style.setWrapText(true);// 指定当单元格内容显示不下时自动换行
        
        BigDecimal ke1Total = new BigDecimal("0");
        BigDecimal ke2Total = new BigDecimal("0");
        BigDecimal ke3Total = new BigDecimal("0");
        BigDecimal ke4Total = new BigDecimal("0");
        BigDecimal ke5Total = new BigDecimal("0");
        BigDecimal ke6Total = new BigDecimal("0");
        BigDecimal ke7Total = new BigDecimal("0");
        BigDecimal ke8Total = new BigDecimal("0");
        BigDecimal ke9Total = new BigDecimal("0");
        BigDecimal xjTotal = new BigDecimal("0");
        for (Map<String, Object> key : listMap)
        {
            ke1Total = ke1Total.add(new BigDecimal(String.valueOf(key.get("key1"))));
            ke2Total = ke2Total.add(new BigDecimal(String.valueOf(key.get("key2"))));
            ke3Total = ke3Total.add(new BigDecimal(String.valueOf(key.get("key3"))));
            ke4Total = ke4Total.add(new BigDecimal(String.valueOf(key.get("key4"))));
            ke5Total = ke5Total.add(new BigDecimal(String.valueOf(key.get("key5"))));
            ke6Total = ke6Total.add(new BigDecimal(String.valueOf(key.get("key6"))));
            ke7Total = ke7Total.add(new BigDecimal(String.valueOf(key.get("key7"))));
            ke8Total = ke8Total.add(new BigDecimal(String.valueOf(key.get("key8"))));
            ke9Total = ke9Total.add(new BigDecimal(String.valueOf(key.get("key9"))));
            xjTotal = xjTotal.add(new BigDecimal(String.valueOf(key.get("xj"))));
        }
        
        //判断是否存在一行，若不存在则建立，否则报空指针异常
        if(sheet.getRow(rowIndex)==null){
            sheet.createRow(rowIndex);
            sheet.getRow(rowIndex).createCell(0).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(1).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(2).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(3).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(4).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(5).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(0).setCellValue("合计");
            
            sheet.getRow(rowIndex).createCell(6).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(6).setCellValue(String.valueOf(ke1Total));
            sheet.getRow(rowIndex).createCell(7).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(7).setCellValue(String.valueOf(ke2Total));
            sheet.getRow(rowIndex).createCell(8).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(8).setCellValue(String.valueOf(ke3Total));
            sheet.getRow(rowIndex).createCell(9).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(9).setCellValue(String.valueOf(ke4Total));
            sheet.getRow(rowIndex).createCell(10).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(10).setCellValue(String.valueOf(ke5Total));
            sheet.getRow(rowIndex).createCell(11).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(11).setCellValue(String.valueOf(ke6Total));
            sheet.getRow(rowIndex).createCell(12).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(12).setCellValue(String.valueOf(ke7Total));
            sheet.getRow(rowIndex).createCell(13).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(13).setCellValue(String.valueOf(ke8Total));
            sheet.getRow(rowIndex).createCell(14).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(14).setCellValue(String.valueOf(ke9Total));
            sheet.getRow(rowIndex).createCell(15).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(15).setCellValue(String.valueOf(xjTotal));
            sheet.getRow(rowIndex).createCell(16).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(16).setCellValue("");
        }else{
            sheet.getRow(rowIndex).getCell(0).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(1).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(2).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(3).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(4).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(5).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(0).setCellValue("合计");
            
            sheet.getRow(rowIndex).getCell(6).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(6).setCellValue(String.valueOf(ke1Total));
            sheet.getRow(rowIndex).getCell(7).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(7).setCellValue(String.valueOf(ke2Total));
            sheet.getRow(rowIndex).getCell(8).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(8).setCellValue(String.valueOf(ke3Total));
            sheet.getRow(rowIndex).getCell(9).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(9).setCellValue(String.valueOf(ke4Total));
            sheet.getRow(rowIndex).getCell(10).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(10).setCellValue(String.valueOf(ke5Total));
            sheet.getRow(rowIndex).getCell(11).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(11).setCellValue(String.valueOf(ke6Total));
            sheet.getRow(rowIndex).getCell(12).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(12).setCellValue(String.valueOf(ke7Total));
            sheet.getRow(rowIndex).getCell(13).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(13).setCellValue(String.valueOf(ke8Total));
            sheet.getRow(rowIndex).getCell(14).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(14).setCellValue(String.valueOf(ke9Total));
            sheet.getRow(rowIndex).getCell(15).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(15).setCellValue(String.valueOf(xjTotal));
            sheet.getRow(rowIndex).getCell(16).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(16).setCellValue("");
        }
        rowIndex++;

        // 开启累计行
        Map<String, Object> ljMapInfo = this.qryReportLjInfo(account);
        CellRangeAddress regionLj = new CellRangeAddress(rowIndex, rowIndex, 0, 5);// 下标从0开始
        sheet.addMergedRegion(regionLj);
        //判断是否存在一行，若不存在则建立，否则报空指针异常
        if(sheet.getRow(rowIndex)==null){
            sheet.createRow(rowIndex);
            sheet.getRow(rowIndex).createCell(0).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(1).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(2).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(3).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(4).setCellStyle(style);
            sheet.getRow(rowIndex).createCell(5).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(0).setCellValue("累积");
            
            sheet.getRow(rowIndex).createCell(6).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(6).setCellValue(String.valueOf(ljMapInfo.get("type1Num")));
            sheet.getRow(rowIndex).createCell(7).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(7).setCellValue(String.valueOf(ljMapInfo.get("type2Num")));
            sheet.getRow(rowIndex).createCell(8).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(8).setCellValue(String.valueOf(ljMapInfo.get("type3Num")));
            sheet.getRow(rowIndex).createCell(9).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(9).setCellValue(String.valueOf(ljMapInfo.get("type4Num")));
            sheet.getRow(rowIndex).createCell(10).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(10).setCellValue(String.valueOf(ljMapInfo.get("type5Num")));
            sheet.getRow(rowIndex).createCell(11).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(11).setCellValue(String.valueOf(ljMapInfo.get("type6Num")));
            sheet.getRow(rowIndex).createCell(12).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(12).setCellValue(String.valueOf(ljMapInfo.get("type7Num")));
            sheet.getRow(rowIndex).createCell(13).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(13).setCellValue(String.valueOf(ljMapInfo.get("type8Num")));
            sheet.getRow(rowIndex).createCell(14).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(14).setCellValue(String.valueOf(ljMapInfo.get("type9Num")));
            sheet.getRow(rowIndex).createCell(15).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(15).setCellValue(String.valueOf(ljMapInfo.get("allNum")));
            sheet.getRow(rowIndex).createCell(16).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(16).setCellValue("");
        }else{
            sheet.getRow(rowIndex).getCell(0).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(1).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(2).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(3).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(4).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(5).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(0).setCellValue("累积");
            
            sheet.getRow(rowIndex).getCell(6).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(6).setCellValue(String.valueOf(ljMapInfo.get("type1Num")));
            sheet.getRow(rowIndex).getCell(7).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(7).setCellValue(String.valueOf(ljMapInfo.get("type2Num")));
            sheet.getRow(rowIndex).getCell(8).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(8).setCellValue(String.valueOf(ljMapInfo.get("type3Num")));
            sheet.getRow(rowIndex).getCell(9).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(9).setCellValue(String.valueOf(ljMapInfo.get("type4Num")));
            sheet.getRow(rowIndex).getCell(10).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(10).setCellValue(String.valueOf(ljMapInfo.get("type5Num")));
            sheet.getRow(rowIndex).getCell(11).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(11).setCellValue(String.valueOf(ljMapInfo.get("type6Num")));
            sheet.getRow(rowIndex).getCell(12).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(12).setCellValue(String.valueOf(ljMapInfo.get("type7Num")));
            sheet.getRow(rowIndex).getCell(13).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(13).setCellValue(String.valueOf(ljMapInfo.get("type8Num")));
            sheet.getRow(rowIndex).getCell(14).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(14).setCellValue(String.valueOf(ljMapInfo.get("type9Num")));
            sheet.getRow(rowIndex).getCell(15).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(15).setCellValue(String.valueOf(ljMapInfo.get("allNum")));
            sheet.getRow(rowIndex).getCell(16).setCellStyle(style);
            sheet.getRow(rowIndex).getCell(16).setCellValue("");
        }
        rowIndex++;
        
        // 最后一行操作
        //判断是否存在一行，若不存在则建立，否则报空指针异常
        if(sheet.getRow(rowIndex)==null){
            sheet.createRow(rowIndex);
            
            CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0, 1);// 下标从0开始
            sheet.addMergedRegion(region);
            sheet.getRow(rowIndex).createCell(0).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(0).setCellValue("制表人：");
            
            CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 2, 3);
            sheet.addMergedRegion(region1);
            sheet.getRow(rowIndex).createCell(2).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(2).setCellValue(String.valueOf(map.get("zbr")));

            CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
            sheet.addMergedRegion(region2);
            sheet.getRow(rowIndex).createCell(5).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(5).setCellValue("安全质量部审核：");

            CellRangeAddress region3 = new CellRangeAddress(rowIndex, rowIndex, 9, 10);
            sheet.addMergedRegion(region3);
            sheet.getRow(rowIndex).createCell(9).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(9).setCellValue("财务部审核：");

            CellRangeAddress region4 = new CellRangeAddress(rowIndex, rowIndex, 13, 14);
            sheet.addMergedRegion(region4);
            sheet.getRow(rowIndex).createCell(13).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(13).setCellValue("项目经理审核：");
        }else{
            CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0, 1);// 下标从0开始
            sheet.addMergedRegion(region);
            sheet.getRow(rowIndex).createCell(0).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(0).setCellValue("制表人：");
            
            CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 2, 3);
            sheet.addMergedRegion(region1);
            sheet.getRow(rowIndex).createCell(2).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(2).setCellValue(String.valueOf(map.get("zbr")));

            CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
            sheet.addMergedRegion(region2);
            sheet.getRow(rowIndex).getCell(5).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(5).setCellValue("安全质量部审核：");

            CellRangeAddress region3 = new CellRangeAddress(rowIndex, rowIndex, 9, 10);
            sheet.addMergedRegion(region3);
            sheet.getRow(rowIndex).getCell(9).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(9).setCellValue("财务部审核：");

            CellRangeAddress region4 = new CellRangeAddress(rowIndex, rowIndex, 13, 14);
            sheet.addMergedRegion(region4);
            sheet.getRow(rowIndex).getCell(13).setCellStyle(defaultStyle);
            sheet.getRow(rowIndex).getCell(13).setCellValue("项目经理审核："); 
        }
       
        // 设置响应头和客户端保存文件名
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(TzName, "UTF-8"));
        // 获取输入流
        OutputStream os = response.getOutputStream();
        wb.write(os);
        // 关闭流
        os.close();
        // 关闭文件流
        is.close();
    }

    //累计金额
    private Map<String, Object> qryReportLjInfo(AccountBean account)
    {
        Map<String, Object> retMap = Maps.newHashMap();
        // 查询今年之前的所有台账
        Map<String, Object> mapWhere = new HashMap<String, Object>();
        mapWhere.put("year", account.getYear());
        mapWhere.put("tbdwid", account.getTbdwid());
        mapWhere.put("endMonth", account.getMonth());
        List<DetailAccountBean> detailAccountInfo = accountService.qryReportLjInfo(mapWhere);
        BigDecimal type1Num = new BigDecimal("0");
        BigDecimal type2Num = new BigDecimal("0");
        BigDecimal type3Num = new BigDecimal("0");
        BigDecimal type4Num = new BigDecimal("0");
        BigDecimal type5Num = new BigDecimal("0");
        BigDecimal type6Num = new BigDecimal("0");
        BigDecimal type7Num = new BigDecimal("0");
        BigDecimal type8Num = new BigDecimal("0");
        BigDecimal type9Num = new BigDecimal("0");
        BigDecimal allNum = new BigDecimal("0");
        for (DetailAccountBean accountDetail : detailAccountInfo)
        {
            if ("完善、改造和维护安全防护设施设备支出（不含”三同时“要求初期投入的安全设施）".equals(accountDetail.getSctrTypeTitle1()))
            {
                type1Num = type1Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("配备、维护、保养应急救援器材、设备支出和应急演练支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type2Num = type2Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("开展重大危险源和事故隐患评估、监控和整改支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type3Num = type3Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("安全生产检查、评价（不包括新建、改建、扩建项目安全评价）、咨询和标准化建设支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type4Num = type4Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("配备和更新现场作业人员安全防护用品支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type5Num = type5Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("安全生产宣传、教育、培训支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type6Num = type6Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("安全生产试用的新技术、新标准、新工艺、新装备的推广应用支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type7Num = type7Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("安全设施及特种设备检测支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type8Num = type8Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
            if ("其他与安全生产直接相关的支出".equals(accountDetail.getSctrTypeTitle1()))
            {
                type9Num = type9Num.add(new BigDecimal(String.valueOf(accountDetail.getJe())));
            }
        }
        retMap.put("type1Num", type1Num);
        retMap.put("type2Num", type2Num);
        retMap.put("type3Num", type3Num);
        retMap.put("type4Num", type4Num);
        retMap.put("type5Num", type5Num);
        retMap.put("type6Num", type6Num);
        retMap.put("type7Num", type7Num);
        retMap.put("type8Num", type8Num);
        retMap.put("type9Num", type9Num);
        allNum=type1Num.add(type2Num).add(type3Num).add(type4Num).add(type5Num).add(type6Num).add(type7Num).add(type8Num).add(type9Num);
        retMap.put("allNum", allNum);
        return retMap;
    }
    
    // 处理事故台账图片的查询*************************************************************
    @RequestMapping("queryFileList")
    public @ResponseBody Map<String, Object> queryFileList(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            mapWhere.put("bussinessId", this.getParameter("bussinessId"));// 重新解码
            List<FileBean> fileList = fileService.queryAllFiles(mapWhere, null);
            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (FileBean file : fileList)
            {
                Map<String, Object> map = BeanUtil.toMap(file);
                map.put("updateTime", DateUtil.getDateFormat(file.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime", DateUtil.getDateFormat(file.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                listMap.add(map);
            }
            return retMap = this.generateMsg(listMap, true, "查询成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
            return retMap;
        }
    }

}
