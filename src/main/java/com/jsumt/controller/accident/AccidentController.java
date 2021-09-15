/**
 * Project Name:ECRMS
 * File Name:AccidentController.java
 * Package Name:com.jsumt.controller.accident
 * Date:2018年11月28日下午10:36:46
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.accident;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.accident.AccidentService;
import com.jsumt.service.accident.HurtPeopleService;
import com.jsumt.service.file.FileService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.Base64Helper;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.ConfigUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.PageInfoUtiil;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.accident.AccidentBean;
import com.jsumt.vo.accident.HurtPeopleBean;
import com.jsumt.vo.file.FileBean;
import com.jsumt.vo.system.OrganizationBean;
import com.jsumt.vo.system.UserBean;

/**
 * ClassName:AccidentController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月28日 下午10:36:46 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("accident")
public class AccidentController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(AccidentController.class);

    @Autowired
    private AccidentService accidentService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserManageService userService;

    @Autowired
    private OrganizationService organService;

    @Autowired
    private HurtPeopleService hurtPeopleService;

    // 查询所有事故
    @RequestMapping("queryAccidentList")
    public @ResponseBody Map<String, Object> queryAllAccidents(@RequestParam Map<String, Object> mapWhere)
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

            PageInfo<AccidentBean> pageInfo = new PageInfo<AccidentBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            // 获取事故
            String sg_type = this.getParameter("sg_type");//事故类型（全国or本单位相关）
            String sgjb = this.getParameter("sgjb");//重大特重大等
            mapWhere.put("sgdw", this.getParameter("sgdw"));// 重新编码
            mapWhere.put("title", this.getParameter("title"));// 重新编码
            String sgTimeData = String.valueOf(mapWhere.get("sg_time"));
            if (StringHelper.isNotNullAndEmpty(sgTimeData))
            {
                String startDate =
                        sgTimeData.substring(0, StringHelper.getFromIndex(sgTimeData, "-", 3)).trim();
                String endDate = sgTimeData
                        .substring(StringHelper.getFromIndex(sgTimeData, "-", 3) + 2, sgTimeData.length())
                        .trim();
                mapWhere.put("startDate", startDate);
                mapWhere.put("endDate", endDate);
            }
            
            List<AccidentBean> accidentList = accidentService.queryAllAccidents(mapWhere, pageInfo);
            pageInfo = new PageInfo<AccidentBean>(accidentList);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (AccidentBean accident : accidentList)
            {
                Map<String, Object> map = BeanUtil.toMap(accident);
                // 判断若时间为null，则不存储时间数据
                if (StringHelper.isNotNullAndEmpty(String.valueOf(accident.getSg_time())))
                {
                    map.put("sg_time", DateUtil.getDateFormat(accident.getSg_time(), DateUtil.DATE_DEFAULT_FORMAT));
                }
                // 添加伤亡人数
                if ("0".equals(sg_type))
                {
                    Integer deadPeople = accident.getDead_people();
                    Integer hurtPeople = accident.getHurt_people();
                    Integer lightPeople = accident.getLight_people();
                    BigDecimal gjss = new BigDecimal(accident.getGjss() == null ? "0" : accident.getGjss());
                    if (deadPeople > 30 || hurtPeople > 100 || gjss.compareTo(new BigDecimal(10000.00)) == 1)
                    {
                        map.put("sgjb", "1");
                    }
                    else if ((deadPeople >= 10 && deadPeople < 30) || (hurtPeople >= 50 && hurtPeople < 100)
                            || (gjss.compareTo(new BigDecimal(5000.00)) == 1
                                    && gjss.compareTo(new BigDecimal(10000.00)) == -1
                                    || gjss.compareTo(new BigDecimal(5000.00)) == 0))
                    {
                        map.put("sgjb", "2");
                    }
                    else if ((deadPeople >= 3 && deadPeople < 10) || (hurtPeople >= 10 && hurtPeople < 50)
                            || (gjss.compareTo(new BigDecimal(1000.00)) == 1
                                    && gjss.compareTo(new BigDecimal(5000.00)) == -1
                                    || gjss.compareTo(new BigDecimal(1000.00)) == 0))
                    {
                        map.put("sgjb", "3");
                    }
                    else if ((deadPeople < 3) || (hurtPeople < 10) || (gjss.compareTo(new BigDecimal(1000.00)) == -1))
                    {
                        map.put("sgjb", "4");
                    }
                    // 显示前端页面数据
                    map.put("deadPeople", deadPeople);
                    map.put("hurtPeople", hurtPeople);
                    map.put("lightPeople", lightPeople);
                }
                //判断事故级别,若前端选择的级别是相同的则只选择确定的事故级别，若sgjb为空则不变
                if(StringHelper.isNullAndEmpty(sgjb)){
                    listMap.add(map);
                }else if(sgjb.equals(map.get("sgjb"))){
                    listMap.add(map);
                }
                
            }
            retMap = layuiData(pageInfo.getTotal(), listMap);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return retMap;

    }

    // 增加事故信息
    @RequestMapping("addAccident")
    public @ResponseBody Map<String, Object> addAccident(AccidentBean accidentBean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            // 新建事故数据
            String id = UUIDHexGenerator.generator();
            accidentBean.setId(id);
            String sg_type = this.getParameter("sg_type");
            accidentBean.setCreateTime(new Date());
            accidentBean.setUpdateTime(new Date());
            // 判断类型为1还是0,根据此不同判断添加事故文件数据的值，其中0为南轨公司及其下属单位事故数据；1为全国性事故数据
            if (sg_type.equals("1"))
            {
                // 全国性事故
                accidentBean.setSg_type(sg_type);
                String remark = this.getParameter("remark");
                accidentBean.setRemark(remark);
            }
            else if (sg_type.equals("0"))
            {
                String dead_people = this.getParameter("dead_people");
                String hurt_people = this.getParameter("hurt_people");
                String light_people = this.getParameter("light_people");
                String sg_time = this.getParameter("sg_time");
                String sgjghyjjyqk = this.getParameter("sgjghyjjyqk");
                String sgyyxzzrrdcl = this.getParameter("sgyyxzzrrdcl");
                String sgffjzgcs = this.getParameter("sgffjzgcs");
                String remark = this.getParameter("remark");
                // String sessionId = this.getParameter("sessionId");
                // String userCode =
                // RedisUtil.getRedisUtil().getStringValue(sessionId);
                // UserBean userBean = userService.queryUserByCode(userCode);
                // accidentBean.setSgdw_id(userBean.getOrgId());
                accidentBean.setSg_type(sg_type);

                if (StringHelper.isNotNullAndEmpty(sg_time))
                {
                    accidentBean.setSg_time(DateUtil.getDateFormat(sg_time));
                }

                if (StringHelper.isNullAndEmpty(hurt_people))
                {
                    accidentBean.setHurt_people(Integer.valueOf(0));
                }
                else
                {
                    accidentBean.setHurt_people(Integer.valueOf(hurt_people));
                }

                if (StringHelper.isNullAndEmpty(dead_people))
                {
                    accidentBean.setDead_people(Integer.valueOf(0));
                }
                else
                {
                    accidentBean.setDead_people(Integer.valueOf(dead_people));
                }

                if (StringHelper.isNullAndEmpty(light_people))
                {
                    accidentBean.setLight_people(Integer.valueOf(0));
                }
                else
                {
                    accidentBean.setLight_people(Integer.valueOf(light_people));
                }

                if (StringHelper.isNullAndEmpty(sgjghyjjyqk))
                {
                    accidentBean.setSgjghyjjyqk("");
                }
                else
                {
                    accidentBean.setSgjghyjjyqk(sgjghyjjyqk);
                }

                if (StringHelper.isNullAndEmpty(sgyyxzzrrdcl))
                {
                    accidentBean.setSgyyxzzrrdcl("");
                }
                else
                {
                    accidentBean.setSgyyxzzrrdcl(sgyyxzzrrdcl);
                }

                if (StringHelper.isNullAndEmpty(sgffjzgcs))
                {
                    accidentBean.setSgffjzgcs("");
                }
                else
                {
                    accidentBean.setSgffjzgcs(sgffjzgcs);
                }

                if (StringHelper.isNullAndEmpty(remark))
                {
                    accidentBean.setRemark("");
                }
                else
                {
                    accidentBean.setRemark(remark);
                }

            }
            accidentService.addAccident(accidentBean);
            return retMap = this.generateMsg("", true, "增加成功!");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "增加失败!");
            return retMap;
        }
    }

    // 删除事故信息
    @RequestMapping("delAccidentList")
    public @ResponseBody Map<String, Object> delAccidentList()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            accidentService.delAccident(delelteList);
            retMap = this.generateMsg("", true, "删除事件成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除事故数据失败!");
            return retMap;
        }
    }

    // 编辑or修改事故信息
    @RequestMapping("updateAccident")
    public @ResponseBody Map<String, Object> updateAccident()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            Map<String, Object> updateMap = JsonHelper.fromJsonWithGson(checkRecords, Map.class);
            accidentService.updateAccident(updateMap);
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


    // base64码用于传量大的图片，不容易出现部分图片没传递
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


    @RequestMapping("queryAccidentReport")
    public @ResponseBody Map<String, Object> queryAccidentReport(@RequestParam Map<String, Object> mapWhere)
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

            PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            Map<String, Object> semap = Maps.newHashMap();
            semap.put("sg_type", "0");
            String fwzq = this.getParameter("fwzq");// 范围周期查询
            if (StringHelper.isNotNullAndEmpty(fwzq))
            {
                //2019-01 - 2019-02
                String startMonth = fwzq.substring(0, StringHelper.getFromIndex(fwzq, "-", 2)).trim();
                String endMonth = fwzq.substring(StringHelper.getFromIndex(fwzq, "-", 2) + 2, fwzq.length()).trim();
                // 计算startMonth的上个月的
                int lastMonth = Integer.valueOf(startMonth.split("-")[1]) - 1;
                int lastyear = Integer.valueOf(startMonth.split("-")[0]);
                if (lastMonth == 0)
                {
                    lastMonth = 12;
                    lastyear = Integer.valueOf(startMonth.split("-")[0]) - 1;
                }
                String lsMonth = String.valueOf(lastMonth);
                lsMonth = lsMonth.length() == 1 ? "0" + lsMonth : lsMonth;

                semap.put("startDate", lastyear + "-" + lsMonth + "-20");
                semap.put("endDate", endMonth + "-19");
            }
            else
            {// 默认是上月20日至本月19日
                int lastMonth = DateUtil.getNowMonth() - 1;
                int lastyear = DateUtil.getNowYear();
                if (lastMonth == 0)
                {
                    lastMonth = 12;
                    lastyear = DateUtil.getNowYear() - 1;
                }

                String lsMonth = String.valueOf(lastMonth);
                lsMonth = lsMonth.length() == 1 ? "0" + lsMonth : lsMonth;

                String byMonth = String.valueOf(DateUtil.getNowMonth());
                byMonth = byMonth.length() == 1 ? "0" + byMonth : byMonth;
                semap.put("startDate", lastyear + "-" + lsMonth + "-20");
                semap.put("endDate", DateUtil.getNowYear() + "-" + byMonth + "-19");

            }
            List<AccidentBean> accidentList = accidentService.queryAllAccidents(semap, null);
            semap.clear();
            List<HurtPeopleBean> hurtPeopleList = hurtPeopleService.queryAllHurtPeoples(semap, null);
            
            List<OrganizationBean> orgChild =Lists.newArrayList();
            //判断当前用户所在组织机构是不是南轨公司，如果不是，仅显示本单位
            if("南京轨道交通系统工程有限公司".equals(userBean.getOrgName()))
            {
                 orgChild = organService.queryOnlyChildOrg(userBean.getOrgId());//当前用户下的组织机构
            }
            else
            {
                orgChild.add(organService.queryOneById(userBean.getOrgId()));
            }

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (OrganizationBean organization : orgChild)
            {
                Map<String, Object> tmap = Maps.newHashMap();
                // 查询当前用户组织机构下的子机构的所有事故
                List<AccidentBean> accidents = this.getAccidentInOrg(accidentList, organization.getOrg_id());
                Integer deadsg = 0;// 死亡事故数
                Integer zssg = 0;// 重伤事故数
                Integer qssg = 0;// 轻伤事故数
                Integer qtsg = 0;// 其他事故数
                Integer zgdead = 0;// 职工死亡人数
                Integer zghurt = 0;// 职工重伤人数
                Integer zglight = 0;// 职工轻伤人数
                Integer nozgdead = 0;// 非职工死亡人数
                Integer nozghurt = 0;// 非职工重伤人数
                Integer nozglight = 0;// 非职工轻伤人数
                Integer swhj = 0;// 伤亡合计
                for (AccidentBean accident : accidents)
                {
                    if (accident.getDead_people() > 0)
                        deadsg++;
                    else if (accident.getHurt_people() > 0)
                        zssg++;
                    else if (accident.getLight_people() > 0)
                        qssg++;
                    else
                        qtsg++;
                    // 查询当前事故下的所有伤亡人员表
                    List<HurtPeopleBean> hurtPeoples = this.getHurtPeoples(hurtPeopleList, accident.getId());
                    swhj = swhj + hurtPeoples.size();
                    for (HurtPeopleBean hurtPeople : hurtPeoples)
                    {
                        if ("1".equals(hurtPeople.getIs_draf()))
                        {// 职工
                            if ("0".equals(hurtPeople.getStatus()))
                            {// 死亡
                                zgdead++;
                            }
                            else if ("1".equals(hurtPeople.getStatus()))
                            {// 重伤
                                zghurt++;
                            }
                            else if ("2".equals(hurtPeople.getStatus()))
                            {// 轻伤
                                zglight++;
                            }
                        }
                        else
                        {// 非职工
                            if ("0".equals(hurtPeople.getStatus()))
                            {// 死亡
                                nozgdead++;
                            }
                            else if ("1".equals(hurtPeople.getStatus()))
                            {// 重伤
                                nozghurt++;
                            }
                            else if ("2".equals(hurtPeople.getStatus()))
                            {// 轻伤
                                nozglight++;
                            }
                        }

                    }
                }

                tmap.put("xmmc", organization.getName_cn());// 项目名称
                tmap.put("deadsg", String.valueOf(deadsg));
                tmap.put("zssg", String.valueOf(zssg));
                tmap.put("qssg", String.valueOf(qssg));
                tmap.put("qtsg", String.valueOf(qtsg));
                tmap.put("sghj", String.valueOf(accidents.size()));// 事故合计

                tmap.put("swhj", String.valueOf(swhj));// 伤亡合计
                tmap.put("zgdead", String.valueOf(zgdead));// 伤亡合计
                tmap.put("zghurt", String.valueOf(zghurt));
                tmap.put("zglight", String.valueOf(zglight));
                tmap.put("nozgdead", String.valueOf(nozgdead));
                tmap.put("nozghurt", String.valueOf(nozghurt));
                tmap.put("nozglight", String.valueOf(nozglight));
                listMap.add(tmap);
            }
            // 过滤
            String xmmc = this.getParameter("xmmc");
            List<Map<String, Object>> retList = Lists.newArrayList();
            for (Map<String, Object> map : listMap)
            {
                boolean pzb = false;
                if (StringHelper.isNotNullAndEmpty(xmmc))
                {// 如果不为空
                    if (String.valueOf(map.get("xmmc")).indexOf(xmmc) > -1)
                        pzb = true;
                    // 不匹配的忽略
                }
                else
                    pzb = true;
                if (pzb)
                    retList.add(map);

            }

            retList = new PageInfoUtiil<Map<String, Object>>().generatePageList(pageInfo, retList);
            pageInfo = new PageInfo<Map<String, Object>>(retList);

            retMap = layuiData(pageInfo.getTotal(), retList);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return retMap;
    }

    private List<HurtPeopleBean> getHurtPeoples(List<HurtPeopleBean> hurtPeopleList, String sgId)
    {

        List<HurtPeopleBean> retList = Lists.newArrayList();
        for (HurtPeopleBean hurtPeople : hurtPeopleList)
        {
            if (sgId.equals(hurtPeople.getSgbid()))
                retList.add(hurtPeople);
        }

        return retList;
    }

    private List<AccidentBean> getAccidentInOrg(List<AccidentBean> accidentList, String org_id)
    {
        List<AccidentBean> retList = Lists.newArrayList();
        for (AccidentBean accident : accidentList)
        {
            if (org_id.equals(accident.getOrg_id()))
                retList.add(accident);
        }

        return retList;
    }

    public void writeExcel(String fileName, List<Map<String, Object>> dataList, Map<String, Object> headInfo,
            HttpServletResponse response) throws Exception
    {
        String fileUrl = this.getClass().getClassLoader().getResource("").getPath() + "templates/aqsgtj.xlsx";
        File file = new File(fileUrl);
        if (!file.exists())
        {
            throw new Exception("模板文件不存在!");
        }
        InputStream is = new FileInputStream(file);
        Workbook wb = new XSSFWorkbook(is);
        Sheet sheet = wb.getSheetAt(0);
        // 头信息输出
        sheet.getRow(1).getCell(1).setCellValue(String.valueOf(headInfo.get("tbdw")));
        sheet.getRow(1).getCell(5).setCellValue(String.valueOf(headInfo.get("dwfzr")));
        sheet.getRow(1).getCell(9).setCellValue(String.valueOf(headInfo.get("tbr")));
        sheet.getRow(1).getCell(12).setCellValue(String.valueOf(headInfo.get("createTime")));
        // 从第6行开始依次插入数据
        int rowIndex = 6;
        int[] totalNum=new int[12];
        for (Map<String, Object> map : dataList)
        {
            Row row = sheet.createRow(rowIndex);
            for (int mapIndex = 0; mapIndex <= 12; mapIndex++)
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
                        cell.setCellValue(String.valueOf(map.get("xmmc")));
                        break;
                    case 1:
                        cell.setCellValue(String.valueOf(map.get("sghj")));
                        totalNum[0]=totalNum[0]+Integer.valueOf(String.valueOf(map.get("sghj")));
                        break;
                    case 2:
                        cell.setCellValue(String.valueOf(map.get("deadsg")));
                        totalNum[1]=totalNum[1]+Integer.valueOf(String.valueOf(map.get("deadsg")));
                        break;
                    case 3:
                        cell.setCellValue(String.valueOf(map.get("zssg")));
                        totalNum[2]=totalNum[2]+Integer.valueOf(String.valueOf(map.get("zssg")));
                        break;
                    case 4:
                        cell.setCellValue(String.valueOf(map.get("qssg")));
                        totalNum[3]=totalNum[3]+Integer.valueOf(String.valueOf(map.get("qssg")));
                        break;
                    case 5:
                        cell.setCellValue(String.valueOf(map.get("qtsg")));
                        totalNum[4]=totalNum[4]+Integer.valueOf(String.valueOf(map.get("qtsg")));
                        break;
                    case 6:
                        cell.setCellValue(String.valueOf(map.get("swhj")));
                        totalNum[5]=totalNum[5]+Integer.valueOf(String.valueOf(map.get("swhj")));
                        break;
                    case 7:
                        cell.setCellValue(String.valueOf(map.get("zgdead")));
                        totalNum[6]=totalNum[6]+Integer.valueOf(String.valueOf(map.get("zgdead")));
                        break;
                    case 8:
                        cell.setCellValue(String.valueOf(map.get("zghurt")));
                        totalNum[7]=totalNum[6]+Integer.valueOf(String.valueOf(map.get("zghurt")));
                        break;
                    case 9:
                        cell.setCellValue(String.valueOf(map.get("zglight")));
                        totalNum[8]=totalNum[8]+Integer.valueOf(String.valueOf(map.get("zglight")));
                        break;
                    case 10:
                        cell.setCellValue(String.valueOf(map.get("nozgdead")));
                        totalNum[9]=totalNum[9]+Integer.valueOf(String.valueOf(map.get("nozgdead")));
                        break;
                    case 11:
                        cell.setCellValue(String.valueOf(map.get("nozghurt")));
                        totalNum[10]=totalNum[10]+Integer.valueOf(String.valueOf(map.get("nozghurt")));
                        break;
                    default:
                        cell.setCellValue(String.valueOf(map.get("nozglight")));
                        totalNum[11]=totalNum[11]+Integer.valueOf(String.valueOf(map.get("nozglight")));
                        break;
                }
            }
            rowIndex++;
        }
        
        Row row = sheet.createRow(rowIndex);
        for (int mapIndex = 0; mapIndex <= 12; mapIndex++)
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
                    cell.setCellValue("合计");
                    break;
                case 1:
                    cell.setCellValue(totalNum[0]);
                    break;
                case 2:
                    cell.setCellValue(totalNum[1]);
                    break;
                case 3:
                    cell.setCellValue(totalNum[2]);
                    break;
                case 4:
                    cell.setCellValue(totalNum[3]);
                    break;
                case 5:
                    cell.setCellValue(totalNum[4]);
                    break;
                case 6:
                    cell.setCellValue(totalNum[5]);
                    break;
                case 7:
                    cell.setCellValue(totalNum[6]);
                    break;
                case 8:
                    cell.setCellValue(totalNum[7]);
                    break;
                case 9:
                    cell.setCellValue(totalNum[8]);
                    break;
                case 10:
                    cell.setCellValue(totalNum[9]);
                    break;
                case 11:
                    cell.setCellValue(totalNum[10]);
                    break;
                default:
                    cell.setCellValue(totalNum[11]);
                    break;
            }
        }
        // 设置响应头和客户端保存文件名
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 获取输入流
        OutputStream os = response.getOutputStream();
        wb.write(os);
        // 关闭流
        os.close();
        // 关闭文件流
        is.close();
    }

    @RequestMapping("downAqsgtj")
    public @ResponseBody Map<String, Object> downAqsgtj()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            Map<String, Object> semap = Maps.newHashMap();
            semap.put("sg_type", "0");
            String fwzq = this.getParameter("fwzq");// 范围周期查询
            if (StringHelper.isNotNullAndEmpty(fwzq))
            {
                String startMonth = fwzq.substring(0, StringHelper.getFromIndex(fwzq, "-", 2)).trim();
                String endMonth = fwzq.substring(StringHelper.getFromIndex(fwzq, "-", 2) + 2, fwzq.length()).trim();
                // 计算startMonth的上个月的
                int lastMonth = Integer.valueOf(startMonth.split("-")[1]) - 1;
                int lastyear = Integer.valueOf(startMonth.split("-")[0]);
                if (lastMonth == 0)
                {
                    lastMonth = 12;
                    lastyear = Integer.valueOf(startMonth.split("-")[0]) - 1;
                }
                String lsMonth = String.valueOf(lastMonth);
                lsMonth = lsMonth.length() == 1 ? "0" + lsMonth : lsMonth;

                semap.put("startDate", lastyear + "-" + lsMonth + "-20");
                semap.put("endDate", endMonth + "-19");
            }
            else
            {// 默认是上月20日至本月19日
                int lastMonth = DateUtil.getNowMonth() - 1;
                int lastyear = DateUtil.getNowYear();
                if (lastMonth == 0)
                {
                    lastMonth = 12;
                    lastyear = DateUtil.getNowYear() - 1;
                }
                String lsMonth = String.valueOf(lastMonth);
                lsMonth = lsMonth.length() == 1 ? "0" + lsMonth : lsMonth;
                String byMonth = String.valueOf(DateUtil.getNowMonth());
                byMonth = byMonth.length() == 1 ? "0" + byMonth : byMonth;
                semap.put("startDate", lastyear + "-" + lsMonth + "-20");
                semap.put("endDate", DateUtil.getNowYear() + "-" + byMonth + "-19");
                fwzq = lastyear + "-" + lsMonth + "-20--" + DateUtil.getNowYear() + "-" + byMonth + "-19";
            }
            List<AccidentBean> accidentList = accidentService.queryAllAccidents(semap, null);
            semap.clear();
            List<HurtPeopleBean> hurtPeopleList = hurtPeopleService.queryAllHurtPeoples(semap, null);
            List<OrganizationBean> orgChild = Lists.newArrayList();
            if("南京轨道交通系统工程有限公司".equals(userBean.getOrgName()))
            {
                 orgChild = organService.queryOnlyChildOrg(userBean.getOrgId());//当前用户下的组织机构
            }
            else
            {
                orgChild.add(organService.queryOneById(userBean.getOrgId()));
            }


            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (OrganizationBean organization : orgChild)
            {
                Map<String, Object> tmap = Maps.newHashMap();
                // 查询当前用户组织机构下的子机构的所有事故
                List<AccidentBean> accidents = this.getAccidentInOrg(accidentList, organization.getOrg_id());
                Integer deadsg = 0;// 死亡事故数
                Integer zssg = 0;// 重伤事故数
                Integer qssg = 0;// 轻伤事故数
                Integer qtsg = 0;// 其他事故数
                Integer zgdead = 0;// 职工死亡人数
                Integer zghurt = 0;// 职工重伤人数
                Integer zglight = 0;// 职工轻伤人数
                Integer nozgdead = 0;// 非职工死亡人数
                Integer nozghurt = 0;// 非职工重伤人数
                Integer nozglight = 0;// 非职工轻伤人数
                Integer swhj = 0;// 伤亡合计
                for (AccidentBean accident : accidents)
                {
                    if (accident.getDead_people() > 0)
                        deadsg++;
                    else if (accident.getHurt_people() > 0)
                        zssg++;
                    else if (accident.getLight_people() > 0)
                        qssg++;
                    else
                        qtsg++;
                    // 查询当前事故下的所有伤亡人员表
                    List<HurtPeopleBean> hurtPeoples = this.getHurtPeoples(hurtPeopleList, accident.getId());
                    swhj = swhj + hurtPeoples.size();
                    for (HurtPeopleBean hurtPeople : hurtPeoples)
                    {
                        if ("1".equals(hurtPeople.getIs_draf()))
                        {// 职工
                            if ("0".equals(hurtPeople.getStatus()))
                            {// 死亡
                                zgdead++;
                            }
                            else if ("1".equals(hurtPeople.getStatus()))
                            {// 重伤
                                zghurt++;
                            }
                            else if ("2".equals(hurtPeople.getStatus()))
                            {// 轻伤
                                zglight++;
                            }
                        }
                        else
                        {// 非职工
                            if ("0".equals(hurtPeople.getStatus()))
                            {// 死亡
                                nozgdead++;
                            }
                            else if ("1".equals(hurtPeople.getStatus()))
                            {// 重伤
                                nozghurt++;
                            }
                            else if ("2".equals(hurtPeople.getStatus()))
                            {// 轻伤
                                nozglight++;
                            }
                        }

                    }
                }

                tmap.put("xmmc", organization.getName_cn());// 项目名称
                tmap.put("deadsg", String.valueOf(deadsg));
                tmap.put("zssg", String.valueOf(zssg));
                tmap.put("qssg", String.valueOf(qssg));
                tmap.put("qtsg", String.valueOf(qtsg));
                tmap.put("sghj", String.valueOf(accidents.size()));// 事故合计

                tmap.put("swhj", String.valueOf(swhj));// 伤亡合计
                tmap.put("zgdead", String.valueOf(zgdead));// 伤亡合计
                tmap.put("zghurt", String.valueOf(zghurt));
                tmap.put("zglight", String.valueOf(zglight));
                tmap.put("nozgdead", String.valueOf(nozgdead));
                tmap.put("nozghurt", String.valueOf(nozghurt));
                tmap.put("nozglight", String.valueOf(nozglight));
                listMap.add(tmap);
            }
            // 过滤
            String xmmc = this.getParameter("xmmc");
            List<Map<String, Object>> retList = Lists.newArrayList();
            for (Map<String, Object> map : listMap)
            {
                boolean pzb = false;
                if (StringHelper.isNotNullAndEmpty(xmmc))
                {// 如果不为空
                    if (String.valueOf(map.get("xmmc")).indexOf(xmmc) > -1)
                        pzb = true;
                    // 不匹配的忽略
                }
                else
                    pzb = true;
                if (pzb)
                    retList.add(map);

            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("tbdw", userBean.getOrgName());
            map.put("dwfzr", "");
            map.put("tbr", userBean.getUser_name());
            map.put("createTime", DateUtil.getDateFormat(new Date(), DateUtil.DATE_CHINA_FORMAT));
            this.writeExcel(fwzq + "生产安全事故统计表.xlsx", retList, map, response);
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

    public static void main(String[] args)
    {
        BigDecimal a = new BigDecimal(2.01);
        BigDecimal b = new BigDecimal(2.00);
        System.out.println(b.compareTo(a));
    }
}
