/**
 * Project Name:ECRMS
 * File Name:HurtPeopleService.java
 * Package Name:com.jsumt.service.accident
 * Date:2018年12月10日上午10:55:40
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.service.accident;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jsumt.mapper.accident.AccidentMapper;
import com.jsumt.mapper.accident.HurtPeopleMapper;
import com.jsumt.vo.accident.AccidentBean;
import com.jsumt.vo.accident.HurtPeopleBean;

/**
 * ClassName:HurtPeopleService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月10日 上午10:55:40 <br/>
 * 
 * @author zll
 * @version
 * @since JDK 1.6
 * @see
 */
@Service
public class HurtPeopleService
{
    @Autowired
    private HurtPeopleMapper hurtPeopleMapper;

    @Autowired
    private AccidentMapper accidentMapper;

    public List<HurtPeopleBean> queryAllHurtPeoples(Map<String, Object> mapWhere, PageInfo<HurtPeopleBean> pageInfo)
    {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        // 紧跟着PageHelper.startPage的第一个select方法会被分页
        List<HurtPeopleBean> listMap = hurtPeopleMapper.queryAllHurtPeoples(mapWhere);
        return listMap;
    }

    public void addHurtPeople(HurtPeopleBean hurtPeopleBean)
    {
        // 添加数据
        hurtPeopleMapper.addHurtPeople(hurtPeopleBean);

        // 对于伤亡情况，若伤亡表情况发生改变，同样事故表对应的三个数据各有相应的变化！
        String status = hurtPeopleBean.getStatus();
        String sgbid = hurtPeopleBean.getSgbid();
        // 查询单个事故信息
        AccidentBean accidentBean = accidentMapper.queryOneById(sgbid);
        Map<String, Object> updateMap = new HashMap<String, Object>();
        if ("0".equals(status))
        {
            // 死亡（失踪人数增加1人）
            Integer dead_people = accidentBean.getDead_people() + 1;
            updateMap.put("dead_people", String.valueOf(dead_people));
        }
        else if ("1".equals(status))
        {
            // 重伤人数增加1人
            Integer hurt_people = accidentBean.getHurt_people() + 1;
            updateMap.put("hurt_people", String.valueOf(hurt_people));
        }
        else if ("2".equals(status))
        {
            // 轻伤人数增加一人
            Integer light_people = accidentBean.getLight_people() + 1;
            updateMap.put("light_people", String.valueOf(light_people));
        }
        updateMap.put("id", sgbid);
        accidentMapper.updateAccident(updateMap);
    }
    /*
     * //此方法用于判断是否对于事故主表修改参数
     * public boolean table(String hurtPeopleStatus,String Status,HurtPeopleBean
     * hurtPeopleBean){
     * String sgbid = hurtPeopleBean.getSgbid();
     * Map<String,Object> updateMap = new HashMap<String,Object>();
     * //查询单个事故信息
     * AccidentBean accidentBean = accidentMapper.queryOneById(sgbid);
     * //事故主表相应的增加
     * if("0".equals(hurtPeopleStatus) && !hurtPeopleStatus.equals(Status)){
     * //死亡（失踪人数增加1人）
     * Integer dead_people=accidentBean.getDead_people() + 1;
     * updateMap.put("dead_people", String.valueOf(dead_people));
     * }else if("1".equals(hurtPeopleStatus) && !hurtPeopleStatus.equals(Status)
     * ){
     * //重伤人数增加1人
     * Integer hurt_people=accidentBean.getHurt_people() + 1;
     * updateMap.put("hurt_people", String.valueOf(hurt_people));
     * }else if("2".equals(hurtPeopleStatus) &&
     * !hurtPeopleStatus.equals(Status)){
     * //轻伤人数增加一人
     * Integer light_people=accidentBean.getLight_people() + 1;
     * updateMap.put("light_people", String.valueOf(light_people));
     * }
     * //事故主表相应的减少
     * if( "0".equals(Status) && !hurtPeopleStatus.equals(Status)){
     * //死亡（失踪人数减少1人）
     * Integer dead_people=accidentBean.getDead_people() - 1;
     * updateMap.put("dead_people", String.valueOf(dead_people));
     * }else if("1".equals(Status) && !hurtPeopleStatus.equals(Status)) {
     * //重伤人数减少1人
     * Integer hurt_people=accidentBean.getHurt_people() - 1;
     * updateMap.put("hurt_people", String.valueOf(hurt_people));
     * }else if("2".equals(Status) && !hurtPeopleStatus.equals(Status)){
     * //轻伤人数减少1人
     * Integer light_people=accidentBean.getLight_people() - 1;
     * updateMap.put("light_people", String.valueOf(light_people));
     * }
     * return false;
     * }
     */

    public void updateHurtPeople(HurtPeopleBean hurtPeopleBean)
    {
        // 对于伤亡情况，若伤亡表情况发生改变，同样事故表对应的三个数据各有相应的变化！（从页面传过来的值）
        String hurtPeopleStatus = hurtPeopleBean.getStatus();
        String sgbid = hurtPeopleBean.getSgbid();
        // 查询单个事故信息
        AccidentBean accidentBean = accidentMapper.queryOneById(sgbid);

        // 从人员伤亡表中查询status，根据此状态再修改事故表的伤亡人员信息，同样事故表对应的三个数据各有相应的变化！
        // 查询原来的单个伤亡信息
        String id = hurtPeopleBean.getId();
        HurtPeopleBean bean = hurtPeopleMapper.queryOneById(id);
        String Status = bean.getStatus();
        Map<String, Object> updateMap = new HashMap<String, Object>();

        // 事故主表相应的增加
        if ("0".equals(hurtPeopleStatus) && !hurtPeopleStatus.equals(Status))
        {
            // 死亡（失踪人数增加1人）
            Integer dead_people = accidentBean.getDead_people() + 1;
            updateMap.put("dead_people", String.valueOf(dead_people));
        }
        else if ("1".equals(hurtPeopleStatus) && !hurtPeopleStatus.equals(Status))
        {
            // 重伤人数增加1人
            Integer hurt_people = accidentBean.getHurt_people() + 1;
            updateMap.put("hurt_people", String.valueOf(hurt_people));
        }
        else if ("2".equals(hurtPeopleStatus) && !hurtPeopleStatus.equals(Status))
        {
            // 轻伤人数增加一人
            Integer light_people = accidentBean.getLight_people() + 1;
            updateMap.put("light_people", String.valueOf(light_people));
        }

        // 事故主表相应的减少
        if ("0".equals(Status) && !hurtPeopleStatus.equals(Status))
        {
            // 死亡（失踪人数减少1人）
            Integer dead_people = accidentBean.getDead_people() - 1;
            updateMap.put("dead_people", String.valueOf(dead_people));
        }
        else if ("1".equals(Status) && !hurtPeopleStatus.equals(Status))
        {
            // 重伤人数减少1人
            Integer hurt_people = accidentBean.getHurt_people() - 1;
            updateMap.put("hurt_people", String.valueOf(hurt_people));
        }
        else if ("2".equals(Status) && !hurtPeopleStatus.equals(Status))
        {
            // 轻伤人数减少1人
            Integer light_people = accidentBean.getLight_people() - 1;
            updateMap.put("light_people", String.valueOf(light_people));
        }
        // 修改数据
        // 判断map集合是否为空，若为空则主表不改数据,但附表（人员伤亡表）所改数据必须可以修改
        if (!updateMap.isEmpty())
        {
            updateMap.put("id", sgbid);
            accidentMapper.updateAccident(updateMap);
        }
        hurtPeopleMapper.updateHurtPeople(hurtPeopleBean);
    }

    public void delHurtPeoples(List<Map<String, Object>> hurtPeopleList)
    {
        List<String> hurtPeopleLists = Lists.newArrayList();
        // 查出主表的数据
        HurtPeopleBean hurtPeopleBean_ = hurtPeopleMapper.queryOneById(String.valueOf(hurtPeopleList.get(0).get("id")));
        String sgbid = hurtPeopleBean_.getSgbid();
        // 根据sgbid查询事故表的数据
        AccidentBean accidentBean = accidentMapper.queryOneById(sgbid);
        Integer deadPeopleNums = accidentBean.getDead_people();
        Integer hurtPeopleNums = accidentBean.getHurt_people();
        Integer lightPeopleNums = accidentBean.getLight_people();

        for (Map<String, Object> hurtPeopleMap : hurtPeopleList)
        {
            String hurtPeopleId = String.valueOf(hurtPeopleMap.get("id"));
            // 根据伤亡人员信息表的id，查询伤亡人员信息
            HurtPeopleBean hurtPeopleBean = hurtPeopleMapper.queryOneById(hurtPeopleId);
            String Status = hurtPeopleBean.getStatus();
            if ("0".equals(Status))
            {
                // 死亡（失踪人数减少1人）
                deadPeopleNums = deadPeopleNums - 1;
            }
            else if ("1".equals(Status))
            {
                // 重伤人数减少1人
                hurtPeopleNums = hurtPeopleNums - 1;
            }
            else if ("2".equals(Status))
            {
                // 轻伤人数减少1人
                lightPeopleNums = lightPeopleNums - 1;
            }
            // 需要删除人员伤亡表的id
            hurtPeopleLists.add(hurtPeopleId);
        }
        // 修改事故表数据
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("dead_people", String.valueOf(deadPeopleNums));
        updateMap.put("hurt_people", String.valueOf(hurtPeopleNums));
        updateMap.put("light_people", String.valueOf(lightPeopleNums));
        updateMap.put("id", sgbid);
        accidentMapper.updateAccident(updateMap);
        // 存储需要更新的主表的数据
        hurtPeopleMapper.delHurtPeoples(hurtPeopleLists);
    }

}
