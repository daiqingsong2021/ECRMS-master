/**
 * Project Name:ECRMS
 * File Name:MenuService.java
 * Package Name:com.jsumt.service.MenuManage
 * Date:2018年8月7日上午10:49:10
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.file;
/**
 * 菜单管理service层
 * ClassName:MenuService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月7日 上午10:49:10 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.file.FileMapper;
import com.jsumt.util.StringHelper;
import com.jsumt.vo.file.BizFileBean;
import com.jsumt.vo.file.FileBean;
import com.jsumt.vo.file.FileDowRecordBean;
import com.jsumt.vo.file.FileNewBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class FileService
{
    @Autowired
    private FileMapper fileMapper;

    public List<FileBean> queryAllFiles(Map<String, Object> mapWhere, PageInfo<FileBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<FileBean> fileList = fileMapper.queryAllFiles(mapWhere);
        return fileList;
    }

    // public String queryMaxNo(String fileCatalog, String fileType)
    // {
    // String maxNo = "";
    // Map<String, Object> mapWhere = Maps.newHashMap();
    // mapWhere.put("fileCatalog", fileCatalog);
    // mapWhere.put("fileType", fileType);
    // maxNo = fileMapper.queryMaxNo(mapWhere);
    // return maxNo;
    // }

    public void saveFile(FileBean fileBean)
    {
        fileMapper.saveFile(fileBean);

    }

    /**
     * 保存新文件
     * @param filenewBean
     */
    public void saveNewFile(FileNewBean filenewBean)
    {
        fileMapper.saveNewFile(filenewBean);
    }

    /**
     * 删除新文件
     * @param delelteList
     */
    public void delNewFiles(List<Map<String, Object>> delelteList)
    {
        if(ObjectUtils.isEmpty(delelteList))
            return;
        List<String> fileIds = Lists.newArrayList();
        for (Map<String, Object> fileMap : delelteList)
        {
            String fileId = String.valueOf(fileMap.get("id"));
            String fileUrl = String.valueOf(fileMap.get("fileUrl"));
            // 清空物理文件
            if (StringHelper.isNotNullAndEmpty(fileUrl))
            {
                File file = new File(fileUrl);
                if (file.exists())
                    file.delete();
            }
            fileIds.add(fileId);
        }
        // 删除文件业务关联表
        fileMapper.delBussFileByFileIds(fileIds);
        // 删除文件表
        fileMapper.delNewFiles(fileIds);
    }

    /**
     * 新文件与业务关联
     * @param bizFileBeans
     */
    public void saveBindBussFile(List<BizFileBean> bizFileBeans)
    {
        for(BizFileBean bizFileBean:bizFileBeans)
          fileMapper.saveBindBussFile(bizFileBean);
    }

    /**
     * 更新业务文件关联
     * @param bizFileBeans
     */
    public void updateBindBussFile(List<BizFileBean> bizFileBeans)
    {
        //清空原先的关联关系表
        List<String> bizIds=Lists.newArrayList();
        for(BizFileBean bizFileBean:bizFileBeans)
            bizIds.add(bizFileBean.getBizId());
        fileMapper.deleteBizFileBeanByBizIds(bizIds);
        //更新新业务关联
        this.saveBindBussFile(bizFileBeans);
    }

    public void delFiles(List<Map<String, Object>> delelteList)
    {
        List<String> fileIds = Lists.newArrayList();
        for (Map<String, Object> fileMap : delelteList)
        {
            String fileId = String.valueOf(fileMap.get("id"));
            String fileUrl = String.valueOf(fileMap.get("fileUrl"));
            // 清空物理文件
            if (StringHelper.isNotNullAndEmpty(fileUrl))
            {
                File file = new File(fileUrl);
                if (file.exists())
                    file.delete();
            }
            fileIds.add(fileId);
        }
        //删除文件关联的下载记录
        fileMapper.delFileDowRecord(fileIds);
        // 删除文件表
        fileMapper.delFiles(fileIds);
    }

    public void updateFilesStatus(List<Map<String, Object>> fileStatusUpdate)
    {
        // 修改文件状态
        fileMapper.updateFiles(fileStatusUpdate);
    }

    public void updateFile(FileBean file)
    {
        // 修改文件
        fileMapper.updateFile(file);
    }

    /**
     * 根据bussinessId删除文件
     * delFilesByBussIds:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param bussinessIds
     * @since JDK 1.6
     */
    public void delFilesByBussIds(List<String> bussinessIds)
    {
        // 根据bussinessId查出所有文件List
        List<FileBean> fileList = fileMapper.queryAllFilesByBussinessIds(bussinessIds);
        // 开始删除文件
        List<String> fileIds = Lists.newArrayList();
        for (FileBean fileBean : fileList)
        {
            // 清空物理文件
            if (StringHelper.isNotNullAndEmpty(fileBean.getFileUrl()))
            {
                File file = new File(fileBean.getFileUrl());
                if (file.exists())
                    file.delete();
            }
            fileIds.add(fileBean.getId());
        }
        // 删除文件表
        if (!fileIds.isEmpty())
        {
            //删除文件关联的下载记录
            fileMapper.delFileDowRecord(fileIds);
            fileMapper.delFiles(fileIds);
        }
            
    }

    public void delBizFileByBussIds(List<String> bussinessIds)
    {
        if(ObjectUtils.isEmpty(bussinessIds))
            return;
        fileMapper.delBussFileByBizIds(bussinessIds);
    }

    public void delNewFilesByBizIds(List<String> bussinessIds)
    {
        if(ObjectUtils.isEmpty(bussinessIds))
            return;
        List<BizFileBean> bizFileBeanList=fileMapper.queryBizFileBeanByBizIds(bussinessIds);
        List<String> fileIds=Lists.newArrayList();
        for(BizFileBean bizFileBean:bizFileBeanList)
        {
            fileIds.add(bizFileBean.getFileId());
        }
        if(!ObjectUtils.isEmpty(fileIds))
        {
            List<FileNewBean> fileNewBeans=fileMapper.queryNewFileByIds(fileIds);
            List<Map<String, Object>> delelteList =Lists.newArrayList();
            for(FileNewBean fileNewBean:fileNewBeans)
            {
                Map<String, Object> delMap=Maps.newHashMap();
                delMap.put("id",fileNewBean.getId());
                delMap.put("fileUrl",fileNewBean.getFileUrl());
                delelteList.add(delMap);
            }
            this.delNewFiles(delelteList);
        }
    }

    public FileBean queryFileById(String fileId)
    {

        return fileMapper.queryFileById(fileId);
    }

    public List<FileBean> queryFileByBussineId(String bussinessId)
    {
        Map<String, Object> mapWhere = Maps.newHashMap();
        mapWhere.put("bussinessId", bussinessId);
        List<FileBean> fileBeans = this.queryAllFiles(mapWhere, null);
        return fileBeans;
    }


    public void delFileById(FileBean fileBean)
    {
        List<String> fileIds = Lists.newArrayList();
        // 清空物理文件
        if (StringHelper.isNotNullAndEmpty(fileBean.getFileUrl()))
        {
            File file = new File(fileBean.getFileUrl());
            if (file.exists())
                file.delete();
        }
        fileIds.add(fileBean.getId());
        //删除文件关联的下载记录
        fileMapper.delFileDowRecord(fileIds);
        fileMapper.delFiles(fileIds);
    }

    public FileDowRecordBean queryFileDowRecoed(String userCode, String fileId)
    {
        
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("userCode", userCode);
        paramMap.put("fileId", fileId);
        return fileMapper.queryFileDowRecoed(paramMap);
    }

    public void updateFileDowRecord(FileDowRecordBean existBean)
    {
        fileMapper.updateFileDowRecord(existBean);
    }

    public void saveFileDowRecoed(FileDowRecordBean fileDowRecordBean)
    {
        
        fileMapper.saveFileDowRecoed(fileDowRecordBean);
        
    }

    public List<FileDowRecordBean> queryFileDowRecords(Map<String, Object> mapWhere,
            PageInfo<FileDowRecordBean> pageInfo)
    {
        
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List <FileDowRecordBean> list = fileMapper.queryFileDowRecords(mapWhere);
        return list;
    }

    public void delNewFilesByIds(List<String> fileIds)
    {
        if(ObjectUtils.isEmpty(fileIds))
            return;
        List<FileNewBean> fileNewBeans=fileMapper.queryNewFileByIds(fileIds);
        List<Map<String, Object>> delelteList =Lists.newArrayList();
        for(FileNewBean fileNewBean:fileNewBeans)
        {
            Map<String, Object> delMap=Maps.newHashMap();
            delMap.put("id",fileNewBean.getId());
            delMap.put("fileUrl",fileNewBean.getFileUrl());
            delelteList.add(delMap);
        }
        this.delNewFiles(delelteList);
    }

    public List<FileNewBean> queryNewFileByIds(List<String> fileIds)
    {
        if(ObjectUtils.isEmpty(fileIds))
            return Lists.newArrayList();
        return fileMapper.queryNewFileByIds(fileIds);
    }
}
