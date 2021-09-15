/**
 * Project Name:ECRMS
 * File Name:AccountMapper.java
 * Package Name:com.jsumt.mapper.safe
 * Date:2019年1月4日上午10:04:41
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.safe;

import java.util.List;
import java.util.Map;

import com.jsumt.vo.safe.AccountBean;
import com.jsumt.vo.safe.DetailAccountBean;
/**
 * ClassName:AccountMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2019年1月4日 上午10:04:41 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface AccountMapper
{
    //查询出没有被分配过的计划台账
    List<Map<String, Object>> queryScjhtzNotInBatch (Map<String, Object> mapWhere);
    //增加安全生产计划台账数据
    void addSctz (AccountBean bean);
    //查询所有台账
    List<AccountBean> queryAllTzs(Map<String, Object> mapWhere);
    //修改发布状态
    void updateAccount (AccountBean bean);
    //查询未分配子单位汇总
    List<Map<String, Object>> queryNotChildrenTzs(Map<String, Object> mapWhere);
    //查询出清单表的数据
    List<DetailAccountBean> queryTzDetailInfo(Map<String, Object> mapWhere);
    //根据id删除安全生产台账细项表
    void delDetailTzs(List<String> delIds);
    // 修改计划表子单位的parent_id为0
    void updatePidToZero(List<String> listId);
    // 根据id删除安全生产投入计划
    void delTzs(List<String> ids);
    //插入台账细项表的数据
    void addTzQd(DetailAccountBean deailBean);
    //修改台账细项表的数据
    void updateTzQd(DetailAccountBean deailBean);
    //导出excel的累计行，其逻辑稍微注意：是本年度的到目前为止月份的金额数
    List<DetailAccountBean> qryReportLjInfo(Map<String, Object> mapWhere);
    
   
    
}

