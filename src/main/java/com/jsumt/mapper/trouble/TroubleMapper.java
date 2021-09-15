/**
 * Project Name:ECRMS
 * File Name:TroubleMapper.java
 * Package Name:com.jsumt.mapper.Trouble
 * Date:2018年11月28日上午10:50:04
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.mapper.trouble;

import com.jsumt.vo.trouble.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ClassName:TroubleMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年11月28日 上午10:50:04 <br/>
 * @author   wyf
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
@Repository
public interface TroubleMapper
{

    List<TroubleTypeBean> queryYhTypes(Map<String, Object> mapWhere);

    List<TroubleModuleBean> queryAllYhMb(Map<String, Object> mapWhere);

    void addYhmb(TroubleModuleBean troubleModuleBean);

    void batchInsertTypes(List<TroubleTypeBean> insertTypes);
    
    TroubleModuleBean queryModuleByNameOrId(Map<String, Object> mapWhere);
    
    void delModuleById(String id);

    void updateYhMb(Map<String, Object> map);

    void delYhTypeByModuleId(String moduleId);

    TroubleTypeBean queryThTypeById(String id);

    /**
     * ID集合，批量删除隐患类别
     * delYhType:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param deleteIds
     * @since JDK 1.6
     */
    void delYhType(List<String> deleteIds);
     
    /**
     * 根据ID更新隐患类别
     * updateYhlb:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param parent
     * @since JDK 1.6
     */
    void updateYhlb(TroubleTypeBean parent);

    /**
     * 根据隐患类别PID 查询PID下最大的NO
     * queryMaxNo:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param pid
     * @return
     * @since JDK 1.6
     */
    String queryMaxNo(String pid);
    
    /**
     * 查询所有隐患
     * queryAllTroubleList:(这里用一句话描述这个方法的作用). <br/>
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
    List<TroubleBean> queryAllTroubleList(Map<String, Object> mapWhere);

    void updateYhlbByModuleId(TroubleTypeBean bean);
    
    void addTrouble(TroubleBean troubleBean);

    void addTroubleZz(TroubleZzBean troubleZzBean);

    void addTroubleQuest(TroubleQuestionBean troubleQuestionBean);
    
    void addTroubleQuestBatch(List<TroubleQuestionBean> troubleQuestionBeans);
    
    /**
     * 
     * queryAllQIdsByTrobIds:(根据隐患Ids查出所有问题IDs集合). <br/>
     *
     * @author wyf
     * @param trobIds
     * @return
     * @since JDK 1.6
     */
    List<Map<String,Object>> queryAllQIdsByTrobIds(List<String> trobIds);

    /**
     * 
     * delTrobQuesByIds:(根据quesIds删除所有隐患问题). <br/>
     *
     * @author wyf
     * @param bussinessIds
     * @since JDK 1.6
     */
    void delTrobQuesByIds(List<String> quesIds);

    /**
     * 
     * delTrobbzzByTrobIds:(根据隐患IDS删除所有隐患问题追踪表). <br/>
     *
     * @author wyf
     * @param trobIds
     * @since JDK 1.6
     */
    void delTrobbzzByTrobIds(List<String> trobIds);

    /**
     * 
     * delTrobbzzByTrobIds:(根据隐患IDS删除所有隐患表). <br/>
     *
     * @author wyf
     * @param trobIds
     * @since JDK 1.6
     */
    void delTroubleByIds(List<String> trobIds);

    /**
     * 
     * publishTrouble:(发布隐患). <br/>
     *
     * @author wyf
     * @param publishList
     * @since JDK 1.6
     */
    void publishTrouble(List<Map<String, Object>> publishList);

    /**
     * 根据条件查询隐患追踪表
     * queryTroublezz:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param mapWhere
     * @return
     * @since JDK 1.6
     */
    List<TroubleZzBean> queryTroublezz(Map<String, Object> mapWhere);
    
    /**
     * 根据条件查询隐患问题表
     * queryTroubleQues:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param mapWhere
     * @return
     * @since JDK 1.6
     */
    List<TroubleQuestionBean> queryTroubleQues(Map<String, Object> mapWhere);

    /**
     * 删除TroubleQuest
     * delTroubleQuest:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param mapWhere
     * @since JDK 1.6
     */
    void delTroubleQuest(Map<String, Object> mapWhere);

    void updateQuestBatch(List<TroubleQuestionBean> quesTionBeanList);

    void updateTrouble(TroubleBean troubleBean);

    TroubleBean queryOneTrouble(String troubleId);

    void updateTroubleStatus(Map<String, Object> mapWhere);

    String queryTroubleZzMaxCs(Map<String, Object> mapWhere);

    String queryTroubleZzMaxCs_(Map<String, Object> mapWhere);

    List<Map<String, Object>> queryJcdwQuestNums(Map<String, Object> mapWhere);

    List<TroubleBean> queryDclYhList(Map<String, Object> mapWhere);

    List<TroubleQuestionBean> queryQuestHistory(Map<String, Object> mapWhere);

    void delTrobbzzById(String id);

    void updateYhZzBean(TroubleZzBean zzBean);
}

