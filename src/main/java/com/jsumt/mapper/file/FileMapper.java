/**
 * Project Name:ECRMS
 * File Name:UserManageMapper.java
 * Package Name:com.jsumt.mapper
 * Date:2018年7月31日下午3:55:52
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.mapper.file;

import com.jsumt.vo.file.BizFileBean;
import com.jsumt.vo.file.FileBean;
import com.jsumt.vo.file.FileDowRecordBean;
import com.jsumt.vo.file.FileNewBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理mapper
 * ClassName:UserManageMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月31日 下午3:55:52 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Repository
public interface FileMapper
{

    List<FileBean> queryAllFiles(Map<String, Object> mapWhere);

    String queryMaxNo(Map<String, Object> mapWhere);

    void saveFile(FileBean fileBean);

    void delFiles(List<String> fileIds);

    void updateFiles(List<Map<String, Object>> fileStatusUpdate);

    void updateFile(FileBean file);

    List<FileBean> queryAllFilesByBussinessIds(List<String> bussinessIds);

    FileBean queryFileById(String id);

    FileDowRecordBean queryFileDowRecoed(Map<String, Object> paramMap);

    void updateFileDowRecord(FileDowRecordBean existBean);

    void saveFileDowRecoed(FileDowRecordBean fileDowRecordBean);

    List<FileDowRecordBean> queryFileDowRecords(Map<String, Object> mapWhere);

    void delFileDowRecord(List<String> fileIds);

    void saveNewFile(FileNewBean filenewBean);

    void saveBindBussFile(BizFileBean bizFileBean);

    void delNewFiles(List<String> fileIds);

    void delBussFileByFileIds(List<String> fileIds);

    void delBussFileByBizIds(List<String> bizIds);

    List<FileNewBean> queryNewFileByIds(@Param("fileIds") List<String> ids);

    List<BizFileBean> queryBizFileBeanByBizId(String bizId);

    List<BizFileBean> queryBizFileBeanByBizIds(@Param("bizIds") List<String> bizIds);

    void deleteBizFileBeanByBizIds(@Param("bizIds")  List<String> bizIds);
}
