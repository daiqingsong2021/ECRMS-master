package com.jsumt.mapper.safe;

import java.util.List;
import java.util.Map;
import com.jsumt.vo.safe.DetailBean;
import com.jsumt.vo.safe.PlanBean;
import com.jsumt.vo.safe.TypeBean;

/**
 * ClassName:SafeMapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月20日 上午10:01:07 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
public interface PlanMapper
{
    // 安全生产投入类别表的相关增删改查***********************************
    List<TypeBean> queryAllTypes(Map<String, Object> whereMap);
    // TypeBean queryOneById(String id);

    // 安全生产计划投入计划表的增删改查********************************
    List<PlanBean> queryAllPlans(Map<String, Object> whereMap);

    void addPlan(PlanBean bean);

    // 修改发布状态
    void updatePlan(PlanBean bean);

    // 查询子单位汇总部分
    List<Map<String, Object>> queryNotChildrenPlans(Map<String, Object> listMap);

    // 根据id删除安全生产投入计划
    void delPlans(List<String> planId);

    // 根据id查询安全生产投入计划
    PlanBean queryOneById(String planId);

    // 修改计划表子单位的parent_id为0
    void updatePidToZero(List<String> listId);

    // 生产投入细项表的增删改查****************************************************
    void addDetailList(List<DetailBean> detailBeanList);

    // 根据id删除安全生产投入计划
    void delDetailPlans(List<String> planId);

    List<DetailBean> queryDetails(Map<String, Object> mapWhere);

    List<Map<String, Object>> queryPlanDetailInfo(String planId);

    void updateDetailBean(DetailBean detailBean);

    DetailBean queryDetailById(String detailId);
    
    //查询单个planBean
    List<PlanBean> queryOnePlan(PlanBean bean);
    
    //根据单位和年查询图标的计划总额与实际总额
    List<Map<String, Object>> queryAqysctrChart(Map<String, Object> mapWhere);

}
