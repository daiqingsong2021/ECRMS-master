/**
 * Project Name:ECRMS
 * File Name:FileController.java
 * Package Name:com.jsumt.controller.file
 * Date:2018年11月19日下午4:10:20
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.file;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.common.BaseController;
import com.jsumt.service.file.FileService;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.*;
import com.jsumt.vo.file.FileBean;
import com.jsumt.vo.file.FileDowRecordBean;
import com.jsumt.vo.file.FileNewBean;
import com.jsumt.vo.system.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * ClassName:FileController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年11月19日 下午4:10:20 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
@RequestMapping("file")
public class FileController extends BaseController
{

    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private UserManageService userService;

    @Autowired
    private OrganizationService organService;

    // 查询所有文件
    @RequestMapping("queryFileList")
    public @ResponseBody Map<String, Object> queryAllFiles(@RequestParam Map<String, Object> mapWhere)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<FileBean> fileList = Lists.newArrayList();
        try
        {
            Integer page = Integer.valueOf(String.valueOf(mapWhere.get("page")));
            Integer rows = Integer.valueOf(String.valueOf(mapWhere.get("limit")));

            // 设置当前页
            int intPage = page == null || page <= 0 ? 1 : page;
            // 设置每页显示的数量
            int intPageSize = rows == null || rows <= 0 ? 10 : rows;

            PageInfo<FileBean> pageInfo = new PageInfo<FileBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);

            String sessionId = request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);

            // 年份周期为每年的第一天至本年度最后一天
            String year = this.getParameter("year");// 查询年份周期
            if (StringHelper.isNotNullAndEmpty(year))
            {
                mapWhere.put("startDate", year + "-01" + "-01");
                mapWhere.put("endDate", year + "-12" + "-31");
            }
            // 先把status初始化
            if (StringHelper.isNotNullAndEmpty(this.getParameter("status")))
            {
                List<String> statusList = Lists.newArrayList();
                statusList.add(this.getParameter("status"));
                mapWhere.put("status", statusList);
            }
            String isWdtz = this.getParameter("wdtz");
            if ("wdtz".equals(isWdtz) && !"superadmin".equals(userBean.getUser_code()))
            {// 如果是文档台账
                if (StringHelper.isNullAndEmpty(this.getParameter("orgId")))
                {// 如果orgId为空，应该是第一次加载，这加载当前角色所能看到的全部有权限的文档
                    if ("364028873bnull6734117a01673412a5f80002".equals(userBean.getOrgId())
                            || "364028873bnull6734117a01673414009a0003".equals(userBean.getOrgId()))
                    {// 项目部人员
                        List<String> statusList = Lists.newArrayList();
                        statusList.add("1");
                        statusList.add("2");
                        mapWhere.put("status", statusList);
                    }
                    else if (!"364028873bnull6734117a0167341217150001".equals(userBean.getOrgId()))
                    {// 分包单位人员
                        List<String> statusList = Lists.newArrayList();
                        statusList.add("2");
                        mapWhere.put("status", statusList);
                    }
                    mapWhere.put("fileName", this.getParameter("fileName"));
                    mapWhere.put("fileCatalog", this.getParameter("fileCatalog"));// 重新解码
                    mapWhere.put("fileType", this.getParameter("fileType"));// 重新解码
                    // 先查出来其他单位的文件
                    List<FileBean> fileOtherList = fileService.queryAllFiles(mapWhere, null);
                    if (!"364028873bnull6734117a0167341217150001".equals(userBean.getOrgId()))
                    {// 如果不是南轨的
                     // 再查出本单位的所有文件
                        mapWhere.put("status", null);
                        mapWhere.put("orgId", userBean.getOrgId());
                        List<FileBean> fileMyList = fileService.queryAllFiles(mapWhere, null);
                        fileMyList.addAll(fileOtherList);
                        List<FileBean> listBean = Lists.newArrayList();
                        for (FileBean file : fileMyList)
                        {
                            if (!file.getFileCatalog().equals("安全投入台账") && !file.getFileCatalog().equals("隐患管理")
                                    && !file.getFileCatalog().equals("系统管理") && !file.getFileCatalog().equals("事故管理"))
                            {
                                listBean.add(file);
                            }
                        }
                        fileList = new PageInfoUtiil<FileBean>().generatePageList(pageInfo, listBean);
                    }
                    else
                    {// 这段代码执行不到
                        List<FileBean> listBean = Lists.newArrayList();
                        for (FileBean file : fileOtherList)
                        {
                            if (!file.getFileCatalog().equals("安全投入台账") && !file.getFileCatalog().equals("隐患管理")
                                    && !file.getFileCatalog().equals("系统管理") && !file.getFileCatalog().equals("事故管理"))
                            {
                                listBean.add(file);
                            }
                        }
                        fileList = new PageInfoUtiil<FileBean>().generatePageList(pageInfo, listBean);
                    }

                }
                else
                {
                    // 查询当前用户的OrgID是不是传过来的OrgId的上级（包括本身），如果是，则查询传过来的ORGID下的所有文档
                    // 如果不是（肯定不是南轨公司），则判断当前用户角色是否为项目部人员，能看到ORGID下的status为1或者2的所有文档，如果当前用户角色为分包单位人员，能看到ORGID下的status为2的所有文档
                    boolean isParent = organService.judegeOrgParent(userBean.getOrgId(), this.getParameter("orgId"));
                    if (!isParent)
                    {// 不是且不是超级管理员，肯定不是南轨人员
                        if ("364028873bnull6734117a01673412a5f80002".equals(userBean.getOrgId())
                                || "364028873bnull6734117a01673414009a0003".equals(userBean.getOrgId()))
                        {// 项目部人员
                            List<String> statusList = Lists.newArrayList();
                            statusList.add("1");
                            statusList.add("2");
                            mapWhere.put("status", statusList);
                        }
                        else if (!"364028873bnull6734117a0167341217150001".equals(userBean.getOrgId()))
                        {// 分包单位人员
                            List<String> statusList = Lists.newArrayList();
                            statusList.add("2");
                            mapWhere.put("status", statusList);
                        }
                    }
                    mapWhere.put("fileName", this.getParameter("fileName"));
                    mapWhere.put("fileCatalog", this.getParameter("fileCatalog"));// 重新解码
                    mapWhere.put("fileType", this.getParameter("fileType"));// 重新解码
                    fileList = fileService.queryAllFiles(mapWhere, null);
                    List<FileBean> listBean = Lists.newArrayList();
                    for (FileBean file : fileList)
                    {
                        if (!file.getFileCatalog().equals("安全投入台账") && !file.getFileCatalog().equals("隐患管理")
                                && !file.getFileCatalog().equals("系统管理") && !file.getFileCatalog().equals("事故管理"))
                        {
                            listBean.add(file);
                        }
                    }
                    fileList = new PageInfoUtiil<FileBean>().generatePageList(pageInfo, listBean);
                }
            }
            else
            {
                mapWhere.put("fileName", this.getParameter("fileName"));
                mapWhere.put("fileCatalog", this.getParameter("fileCatalog"));// 重新解码
                mapWhere.put("fileType", this.getParameter("fileType"));// 重新解码
                fileList = fileService.queryAllFiles(mapWhere, pageInfo);
            }
            pageInfo = new PageInfo<FileBean>(fileList);

            List<Map<String, Object>> listMap = Lists.newArrayList();
            for (FileBean file : fileList)
            {
                Map<String, Object> map = BeanUtil.toMap(file);
                map.put("updateTime", DateUtil.getDateFormat(file.getUpdateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
                map.put("createTime", DateUtil.getDateFormat(file.getCreateTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
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
     * 根据文件目录查询文件分类
     * queryFileTypes:(这里用一句话描述这个方法的作用). <br/>
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
    @RequestMapping("queryFileTypes")
    public @ResponseBody Map<String, Object> queryFileTypes()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String fileCatalog = this.getParameter("fileCatalog");
            String fileCatalogs = new String(ConfigUtil.getValueByKey("fileCatalogs").getBytes("ISO-8859-1"), "UTF-8");
            List<String> retList = Lists.newArrayList();
            Map<String, List<String>> cataLogMap = JsonHelper.fromJsonWithGson(fileCatalogs, Map.class);
            for (Map.Entry<String, List<String>> entry : cataLogMap.entrySet())
            {
                if (fileCatalog.equals(entry.getKey()))
                {
                    retList = entry.getValue();
                    break;
                }
            }
            retMap = this.generateMsg(retList, true, "查询文件分类成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询文件分类失败!");
            return retMap;
        }

    }

    @RequestMapping("delFilesByBussIds")
    public @ResponseBody Map<String, Object> delFilesByBussIds()
    {

        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String businessId = this.getParameter("bussinessId");
            List<String> buslis = Lists.newArrayList();
            buslis.add(businessId);
            fileService.delFilesByBussIds(buslis);
            retMap = this.generateMsg("", true, "删除图片成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除图片失败!");
            return retMap;
        }
    }

    @RequestMapping("queryFileCatalogs")
    public @ResponseBody Map<String, Object> queryFileCatalogs()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String fileCatalogs = new String(ConfigUtil.getValueByKey("fileCatalogs").getBytes("ISO-8859-1"), "UTF-8");
            Map<String, List<String>> cataLogMap = JsonHelper.fromJsonWithGson(fileCatalogs, Map.class);
            retMap = this.generateMsg(cataLogMap, true, "查询文件分类成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询文件分类失败!");
            return retMap;
        }

    }

    @RequestMapping("delelteFile")
    public @ResponseBody Map<String, Object> delelteFile()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> delelteList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            fileService.delFiles(delelteList);
            retMap = this.generateMsg("", true, "删除文件成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除文件失败!");
            return retMap;
        }

    }

    @RequestMapping("dowloadFile")
    public void dowloadFile()
    {
        try
        {
            String fileName = this.getParameter("fileName");
            String fileLx = this.getParameter("fileLx");
            fileName = fileName + "." + fileLx;
            String fileUrl = this.getParameter("fileUrl");
            String fileId = this.getParameter("fileId");
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            if (StringHelper.isNotNullAndEmpty(fileUrl))
            {
                File file = new File(fileUrl);
                if (file.exists())
                {
                    // 设置响应头和客户端保存文件名
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-disposition",
                            "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
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

                    // 下载记录
                    String sessionId = request.getParameter("sessionId");
                    String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
                    // 判断是否存在改文件这个人的下载记录
                    FileDowRecordBean existBean = fileService.queryFileDowRecoed(userCode, fileId);
                    if (existBean == null)
                    {
                        // 新增一条
                        UserBean userBean = userService.queryUserByCode(userCode);
                        FileDowRecordBean fileDowRecordBean = new FileDowRecordBean();
                        fileDowRecordBean.setId(UUIDHexGenerator.generator());
                        fileDowRecordBean.setCreateTime(new Date());
                        fileDowRecordBean.setUpdateTime(new Date());
                        fileDowRecordBean.setDownNums("1");
                        fileDowRecordBean.setFileId(fileId);
                        fileDowRecordBean.setOrgName(userBean.getOrgName());
                        fileDowRecordBean.setUserCode(userBean.getUser_code());
                        fileDowRecordBean.setUserName(userBean.getUser_name());
                        fileDowRecordBean.setRoleName(userBean.getUserRole());
                        fileService.saveFileDowRecoed(fileDowRecordBean);
                    }
                    else
                    {// 修改
                        existBean.setDownNums(String.valueOf(Integer.valueOf(existBean.getDownNums()) + 1));
                        fileService.updateFileDowRecord(existBean);
                    }

                }
            }

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }

    }

    @RequestMapping("queryFileDowRecords")
    public @ResponseBody Map<String, Object> queryFileDowRecords(@RequestParam Map<String, Object> mapWhere)
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

            PageInfo<FileDowRecordBean> pageInfo = new PageInfo<FileDowRecordBean>();
            pageInfo.setPageNum(intPage);
            pageInfo.setPageSize(intPageSize);
            List<FileDowRecordBean> fileDowRecords = fileService.queryFileDowRecords(mapWhere, pageInfo);
            pageInfo = new PageInfo<FileDowRecordBean>(fileDowRecords);

            retMap = layuiData(pageInfo.getTotal(), fileDowRecords);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询失败!");
        }
        return retMap;

    }

    /**
     * 上传文件
     * updateUserImage:(这里用一句话描述这个方法的作用). <br/>
     * fileCatalog,fileType必传
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("uploadFile")
    public @ResponseBody Map<String, Object> uploadFile()
    {

        Map<String, Object> retMap = new HashMap<String, Object>();
        FileBean fileBean = new FileBean();
        try
        {
            // 处理文件
            // 文件上传的请求
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            // 获取当前登录用户
            String sessionId = mRequest.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            // 新建文件
            String bussinessId = mRequest.getParameter("bussinessId");
            logger.info("==========================upload----------------" + bussinessId);
            String fileCatalog = mRequest.getParameter("fileCatalog");
            fileCatalog = this.formatString(fileCatalog);
            String fileType = mRequest.getParameter("fileType");
            fileType = this.formatString(fileType);
            String isImage = mRequest.getParameter("isImage");
            if (StringHelper.isNullAndEmpty(fileCatalog) || StringHelper.isNullAndEmpty(fileType))
            {
                retMap = this.generateMsg("", false, "上传文件失败!");
                return retMap;
            }
            String orgId = mRequest.getParameter("orgId");
            String status = mRequest.getParameter("status");

            String fileId = UUIDHexGenerator.generator();
            fileBean.setId(fileId);
            fileBean.setScry(userBean.getUser_code());
            fileBean.setScryName(userBean.getUser_name());
            fileBean.setBussinessId(bussinessId);
            fileBean.setCreateTime(new Date());
            fileBean.setUpdateTime(new Date());
            fileBean.setFileCatalog(fileCatalog);
            fileBean.setFileType(fileType);
            if (StringHelper.isNullAndEmpty(orgId))
            {
                orgId = userBean.getOrgId();
            }
            fileBean.setOrgId(orgId);
            fileBean.setOrgName(organService.queryOneById(orgId).getName_cn());
            fileBean.setStatus(StringHelper.isNullAndEmpty(status) ? "0" : status);
            // 查询最大序号
            // String maxNo = fileService.queryMaxNo(fileCatalog, fileType);
            // if (StringUtils.isEmpty(maxNo))
            // fileBean.setNo(1);
            // else
            // fileBean.setNo(Integer.valueOf(maxNo) + 1);

            // 获取请求的参数
            Map<String, MultipartFile> fileMap = mRequest.getFileMap();
            Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator();
            // 用hasNext() 判断是否有值，用next()方法把元素取出。
            while (it.hasNext())
            {
                Map.Entry<String, MultipartFile> entry = it.next();
                MultipartFile mFile = entry.getValue();
                if (mFile.getSize() != 0 && !"".equals(mFile.getName()))
                {
                    // 获取目录
                    String dirUrl = "";
                    if ("1".equals(isImage))
                        dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + "images";
                    else
                        dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + fileCatalog + "/" + fileType;
                    File dir = new File(dirUrl);
                    // 新建目录
                    dir.mkdirs();
                    // 新建文件,物理文件重命名以序列号命名
                    String fileLx = mFile.getOriginalFilename().substring(
                            mFile.getOriginalFilename().lastIndexOf(".") + 1, mFile.getOriginalFilename().length());
                    String fileName =
                            mFile.getOriginalFilename().substring(0, mFile.getOriginalFilename().lastIndexOf("."));
                    String fileUrl = dirUrl + "/" + fileId + "." + fileLx;
                    // 上传文件
                    if ("1".equals(isImage) && ("jpg".equals(fileLx) || "jpeg".equals(fileLx)))
                    {
                        File tempFile = new File(dirUrl + "/" + fileId + "_temp." + fileLx);
                        if (!tempFile.exists())
                            tempFile.createNewFile();
                        mFile.transferTo(tempFile);
                        ExifUtil.writePhoto(tempFile, fileUrl);
                    }
                    else
                    {
                        File file = new File(fileUrl);
                        if (!file.exists())
                            file.createNewFile();
                        mFile.transferTo(file);
                    }

                    fileBean.setFileUrl(fileUrl);
                    fileBean.setFileName(fileName);
                    fileBean.setFileLx(fileLx);
                    fileService.saveFile(fileBean);
                }
            }
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "上传文件失败!");
            return retMap;
        }
        retMap = this.generateMsg(fileBean, true, "上传文件成功!");
        return retMap;
    }

    @RequestMapping("updateFiles")
    public @ResponseBody Map<String, Object> updateFiles()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String checkRecords = this.getParameter("checkRecords");
            List<Map<String, Object>> updateList = JsonHelper.fromJsonWithGson(checkRecords, List.class);
            fileService.updateFilesStatus(updateList);
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

    @RequestMapping("queryImages")
    public @ResponseBody Map<String, Object> queryImages()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String bussinessId = this.getParameter("bussinessId");
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("bussinessId", bussinessId);
            List<FileBean> fileBeans = fileService.queryAllFiles(mapWhere, null);
            retMap = this.generateMsg(fileBeans, true, "查询图片成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询图片失败!");
            return retMap;
        }

    }

    @RequestMapping("queryNewImages")
    public @ResponseBody Map<String, Object> queryNewImages()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String fileIdstr = this.getParameter("fileIds");
            List<String> fileIds = JsonHelper.fromJsonWithGson(fileIdstr, List.class);
            List<FileNewBean> fileBeans = fileService.queryNewFileByIds(fileIds);
            retMap = this.generateMsg(fileBeans, true, "查询图片成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "查询图片失败!");
            return retMap;
        }

    }

    public static void main(String args[])
    {
        System.out.println(UUIDHexGenerator.generator().length());
    }


    /**
     * 上传新文件（不包含bussinessId）
     *
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    @RequestMapping("uploadNewFile")
    public @ResponseBody Map<String, Object> uploadNewFile()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        FileNewBean fileNewBean = new FileNewBean();
        try
        {
            // 处理文件
            // 文件上传的请求
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            // 获取当前登录用户
            String sessionId = mRequest.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = userService.queryUserByCode(userCode);
            // 新建文件
            String fileCatalog = mRequest.getParameter("fileCatalog");
            fileCatalog = this.formatString(fileCatalog);
            String fileType = mRequest.getParameter("fileType");
            fileType = this.formatString(fileType);
            String isImage = mRequest.getParameter("isImage");
            if (StringHelper.isNullAndEmpty(fileCatalog) || StringHelper.isNullAndEmpty(fileType))
            {
                retMap = this.generateMsg("", false, "上传文件失败!");
                return retMap;
            }

            String status = mRequest.getParameter("status");

            String fileId = UUIDHexGenerator.generator();
            fileNewBean.setId(fileId);
            fileNewBean.setScry(userBean.getUser_code());
            fileNewBean.setScryName(userBean.getUser_name());
            fileNewBean.setCreateTime(new Date());
            fileNewBean.setUpdateTime(new Date());
            fileNewBean.setFileCatalog(fileCatalog);
            fileNewBean.setFileType(fileType);
            fileNewBean.setStatus(StringHelper.isNullAndEmpty(status) ? "0" : status);

            // 获取请求的参数
            Map<String, MultipartFile> fileMap = mRequest.getFileMap();
            Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator();
            // 用hasNext() 判断是否有值，用next()方法把元素取出。
            while (it.hasNext())
            {
                Map.Entry<String, MultipartFile> entry = it.next();
                MultipartFile mFile = entry.getValue();
                if (mFile.getSize() != 0 && !"".equals(mFile.getName()))
                {
                    // 获取目录
                    String dirUrl = "";
                    if ("1".equals(isImage))
                        dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + "images";
                    else
                        dirUrl = ConfigUtil.getValueByKey("fileRootUrl") + fileCatalog + "/" + fileType;
                    File dir = new File(dirUrl);
                    // 新建目录
                    dir.mkdirs();
                    // 新建文件,物理文件重命名以序列号命名
                    String fileLx = mFile.getOriginalFilename().substring(
                            mFile.getOriginalFilename().lastIndexOf(".") + 1, mFile.getOriginalFilename().length());
                    String fileName =
                            mFile.getOriginalFilename().substring(0, mFile.getOriginalFilename().lastIndexOf("."));
                    String fileUrl = dirUrl + "/" + fileId + "." + fileLx;
                    // 上传文件
                    if ("1".equals(isImage) && ("jpg".equals(fileLx) || "jpeg".equals(fileLx)))
                    {
                        File tempFile = new File(dirUrl + "/" + fileId + "_temp." + fileLx);
                        if (!tempFile.exists())
                            tempFile.createNewFile();
                        mFile.transferTo(tempFile);
                        ExifUtil.writePhoto(tempFile, fileUrl);
                    }
                    else
                    {
                        File file = new File(fileUrl);
                        if (!file.exists())
                            file.createNewFile();
                        mFile.transferTo(file);
                    }

                    fileNewBean.setFileUrl(fileUrl);
                    fileNewBean.setFileName(fileName);
                    fileNewBean.setFileLx(fileLx);
                    fileService.saveNewFile(fileNewBean);
                }
            }
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "上传文件失败!");
            return retMap;
        }
        retMap = this.generateMsg(fileNewBean, true, "上传文件成功!");
        return retMap;
    }


    @RequestMapping("delNewFile")
    public @ResponseBody Map<String, Object> delNewFile()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String fileIdstr = this.getParameter("fileIds");
            List<String> fileIds = JsonHelper.fromJsonWithGson(fileIdstr, List.class);

            if(!ObjectUtils.isEmpty(fileIds))
            {
                fileService.delNewFilesByIds(fileIds);
            }
            retMap = this.generateMsg("", true, "删除文件成功!");
            return retMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            retMap = this.generateMsg("", false, "删除文件失败!");
            return retMap;
        }

    }
}
