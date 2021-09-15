package com.jsumt.service.system;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsumt.mapper.system.OrganizationMapper;
import com.jsumt.mapper.system.UserManageMapper;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.EnumsUtil.IconClass;
import com.jsumt.util.StringHelper;
import com.jsumt.vo.system.OrganizationBean;
import com.jsumt.vo.system.UserBean;

import net.sf.ehcache.hibernate.management.impl.BeanUtils;

@Service
public class OrganizationService
{
    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private UserManageMapper userManageMapper;
    @Autowired
    private UserManageService umService;

    public List<OrganizationBean> queryorganizations(Map<String, Object> mapWhere)
    {
        List<OrganizationBean> OrganList = organizationMapper.queryorganizations(mapWhere);
        return OrganList;
    }

    public void addorganization(OrganizationBean bean)
    {
        organizationMapper.addorganization(bean);
    }

    public OrganizationBean queryOneById(String org_id)
    {
        OrganizationBean bean = organizationMapper.queryOneById(org_id);
        return bean;
    }

    public void deleteByPrimaryKey(String org_id)
    {
        // (参数org_id可能是父节点，其下有多个子节点)新建集合存贮所有要删除的id
        List<String> deleteIds = Lists.newArrayList();

        OrganizationBean orgBean = organizationMapper.queryOneById(org_id);
        String parentId = orgBean.getOrg_pid();

        // 查询所有组织机构对象，找出参数id以及其下的所以子节点的组织机构对象存贮到集合
        List<OrganizationBean> organList = organizationMapper.queryorganizations(null);
        for (OrganizationBean bean : organList)
        {
            if (org_id.equals(bean.getOrg_id()))
            {
                deleteIds.add(bean.getOrg_id());
                findChildOrgan(deleteIds, organList, bean.getOrg_id());
            }
        }
        // 删除该组织下的用户
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> delUserIDs = Lists.newArrayList();
        for (String orgId : deleteIds)
        {
            queryMap.put("organiz_id", orgId);
            List<UserBean> userList = userManageMapper.queryUsers(queryMap);
            for (UserBean user : userList)
            {
                delUserIDs.add(user.getUser_id());
            }
        }
        umService.deleteUsers(delUserIDs);

        organizationMapper.delorganizations(deleteIds);

        // 更新父节点IS_LEAF 和 iconCls
        if (!"0".equals(parentId))
        {
            OrganizationBean parent = organizationMapper.queryOneById(parentId);
            Map<String, Object> mapWhere = Maps.newHashMap();
            mapWhere.put("org_pid", parent.getOrg_id());
            if (organizationMapper.queryorganizations(mapWhere).isEmpty())
            {// 如果不存在子节点
                parent.setIsLeaf("1");// 更新父节点IS_LEAF
                parent.setIconCls(IconClass.ICON_ORG.toString());
                organizationMapper.updateOrganization(parent);
            }

        }

    }

    public void findChildOrgan(List<String> deleteIds, List<OrganizationBean> organList, String org_pid)
    {
        for (OrganizationBean bean : organList)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getOrg_pid()) && org_pid.equals(bean.getOrg_pid()))
            {
                deleteIds.add(bean.getOrg_id());
                findChildOrgan(deleteIds, organList, bean.getOrg_id());
            }
        }
    }

    public void updateOrganization(OrganizationBean bean)
    {
        organizationMapper.updateOrganization(bean);

    }

    /**
     * 查询最大层级与序号
     * queryMaxLayerAndNo:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param org_pid
     * @since JDK 1.6
     */
    public String queryMaxNo(String org_pid)
    {
        String maxNo = "";
        maxNo = organizationMapper.queryMaxNo(org_pid);
        return maxNo;
    }

    /**
     * 根据用户code查询用户有权限看到的所有组织机构
     * queryAuthOrgInfoByUserCode:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param userCode
     * @param leftAll
     * @return
     * @since JDK 1.6
     */
    public List<Map<String, Object>> queryAuthOrgInfoByUserCode(String userCode, String leftAll)
    {

        UserBean userBean = umService.queryUserByCode(userCode);
        if (userCode.equals("superadmin"))
        {// 如果是超级管理员，直接无视权限，全部查询出来显示，并且可以编辑
            List<Map<String, Object>> supList = this.queryAllOrgainzations();
            return supList;
        }
        if ("true".equals(leftAll))
        {// 如果显示全部则查询全部
            List<Map<String, Object>> supList = this.queryAllOrgainzations(userBean.getOrgId());
            return supList;
        }
        // 查询该用户所属的组织机构
        List<OrganizationBean> organList = organizationMapper.queryorganizations(null);
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> orgMap = BeanUtil.toMap(this.queryOneById(userBean.getOrgId()));
        orgMap.put("active", "true");
        List<Map<String, Object>> childrenList = queryChildOrgan(organList, userBean.getOrgId());
        if (!childrenList.isEmpty())
            orgMap.put("children", childrenList);
        list.add(orgMap);

        return list;
    }

    private List<Map<String, Object>> queryAllOrgainzations(String orgId)
    {

        List<Map<String, Object>> listMap = Lists.newArrayList();
        List<OrganizationBean> organList = organizationMapper.queryorganizations(null);
        for (int i = 0; i < organList.size(); i++)
        {
            OrganizationBean bean = organList.get(i);
            if ("0".equals(bean.getOrg_pid()))
            {
                Map<String, Object> parent = BeanUtil.toMap(bean);
                if (orgId.equals(bean.getOrg_id()))
                    parent.put("active", "true");
                else
                    parent.put("active", "false");
                List<Map<String, Object>> childrenList = queryChildOrgan(organList, bean.getOrg_id(), orgId);
                if (!childrenList.isEmpty())
                    parent.put("children", childrenList);
                listMap.add(parent);
            }
        }
        return listMap;
    }

    private List<Map<String, Object>> queryChildOrgan(List<OrganizationBean> organList, String parentId, String orgId)
    {

        List<Map<String, Object>> listMap = Lists.newArrayList();
        for (OrganizationBean bean : organList)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getOrg_pid()) && parentId.equals(bean.getOrg_pid()))
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                if (orgId.equals(bean.getOrg_id()))
                    map.put("active", "true");
                else
                    map.put("active", "false");
                List<Map<String, Object>> childrenList = queryChildOrgan(organList, bean.getOrg_id(),orgId);
                if (!childrenList.isEmpty())
                    map.put("children", childrenList);
                listMap.add(map);
            }
        }
        return listMap;
    }

    /**
     * 递归查询所有组织机构信息
     * queryAllOrgainzations:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    private List<Map<String, Object>> queryAllOrgainzations()
    {
        List<Map<String, Object>> listMap = Lists.newArrayList();
        List<OrganizationBean> organList = organizationMapper.queryorganizations(null);
        for (int i = 0; i < organList.size(); i++)
        {
            OrganizationBean bean = organList.get(i);
            if ("0".equals(bean.getOrg_pid()))
            {
                Map<String, Object> parent = BeanUtil.toMap(bean);

                if (i == 0)
                    parent.put("active", "true");// 第一个默认是选中
                else
                    parent.put("active", "false");
                List<Map<String, Object>> childrenList = queryChildOrgan(organList, bean.getOrg_id());
                if (!childrenList.isEmpty())
                    parent.put("children", childrenList);
                listMap.add(parent);
            }

        }
        return listMap;
    }

    private List<Map<String, Object>> queryChildOrgan(List<OrganizationBean> organList, String parentId)
    {
        List<Map<String, Object>> listMap = Lists.newArrayList();
        for (OrganizationBean bean : organList)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getOrg_pid()) && bean.getOrg_pid().equals(parentId))
            {
                Map<String, Object> map = BeanUtil.toMap(bean);
                map.put("active", "false");
                List<Map<String, Object>> childrenList = queryChildOrgan(organList, bean.getOrg_id());
                if (!childrenList.isEmpty())
                    map.put("children", childrenList);
                listMap.add(map);
            }
        }
        return listMap;
    }

    /**
     * 判断parentOrgId是不是orgId的上级
     * judegeOrgParent:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param orgId
     * @param parameter
     * @return
     * @since JDK 1.6
     */
    public boolean judegeOrgParent(String parentOrgId, String orgId)
    {
        if (orgId.equals(parentOrgId))
            return true;
        List<OrganizationBean> organList = organizationMapper.queryorganizations(null);
        //查询所有parent下的结构节点
        List<Map<String, Object>> childrenList = queryChildOrgan(organList, parentOrgId);
        return judegeOrgParent_(childrenList, orgId);
    }
    
    /**
     * 判断节点下是否有与parentOrgId相等的节点
     * judegeOrgParent_:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param organList
     * @param parentOrgId
     * @return
     * @since JDK 1.6
     */
    private boolean judegeOrgParent_(List<Map<String, Object>> organList, String parentOrgId)
    {

        for (Map<String, Object> map : organList)
        {
            String childrenOrgId = String.valueOf(map.get("org_id"));
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("children");
            if (childrenOrgId.equals(parentOrgId))
                return true;
            else if (list!=null && !list.isEmpty())
            {
                boolean si = judegeOrgParent_(list, parentOrgId);
                if (si)
                    return true;
            }

        }
        return false;
    }
    
    /**
     * 根据PID查询子节点
     * queryChildOrg:(这里用一句话描述这个方法的作用). <br/>
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
    public List<OrganizationBean> queryOnlyChildOrg(String pid)
    {
        
        List<OrganizationBean> listMap = Lists.newArrayList();
        List<OrganizationBean> organList = organizationMapper.queryorganizations(null);
        for (OrganizationBean bean : organList)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getOrg_pid()) && bean.getOrg_pid().equals(pid))
            {
                listMap.add(bean);
            }
        }
        return listMap;
    }

    public List<Map<String, Object>> queryOrgUsersByOrgId(String org_id)
    {
        List<Map<String, Object>> listMap=Lists.newArrayList();
        listMap= organizationMapper.queryOrgUsersByOrgId(org_id);
        return listMap;
    }

    public List<Map<String, Object>> quyAuthTreeOrg(String userCode, List<OrganizationBean> organList)
    {
        UserBean userBean = umService.queryUserByCode(userCode);
        if (userCode.equals("superadmin"))
        {// 如果是超级管理员，直接无视权限，全部查询出来显示，并且可以编辑
            List<Map<String, Object>> supList = this.quyAllAuthTreeChild(organList);
            return supList;
        }
        // 查询该用户所属的组织机构
        List<Map<String, Object>> list = Lists.newArrayList();
        OrganizationBean bean= this.queryOneById(userBean.getOrgId());
        Map<String, Object> map = Maps.newHashMap();
        map.put("text", bean.getName_cn());
        map.put("id", bean.getOrg_id());
        map.put("org_pid", bean.getOrg_pid());
        map.put("iconCls", bean.getIconCls());
        map.put("state", "open");
        List<Map<String, Object>> childrenList = quyAuthTreeChild(organList, userBean.getOrgId());
        if (!childrenList.isEmpty())
            map.put("children", childrenList);
        list.add(map);

        return list;
    }

    private List<Map<String, Object>> quyAllAuthTreeChild(List<OrganizationBean> organList)
    {
        List<Map<String, Object>> listMap = Lists.newArrayList();
        for (int i = 0; i < organList.size(); i++)
        {
            OrganizationBean bean = organList.get(i);
            if ("0".equals(bean.getOrg_pid()))
            {
                Map<String, Object> map = Maps.newHashMap();
                map.put("text", bean.getName_cn());
                map.put("id", bean.getOrg_id());
                map.put("org_pid", bean.getOrg_pid());
                map.put("iconCls", bean.getIconCls());
                map.put("state", "open");
                List<Map<String, Object>> childrenList = quyAuthTreeChild(organList, bean.getOrg_id());
                if (!childrenList.isEmpty())
                    map.put("children", childrenList);
                listMap.add(map);
            }

        }
        return listMap;
    }

    private List<Map<String, Object>> quyAuthTreeChild(List<OrganizationBean> organList, String parentId)
    {
        List<Map<String, Object>> listMap = Lists.newArrayList();
        for (OrganizationBean bean : organList)
        {
            if (StringHelper.isNotNullAndEmpty(bean.getOrg_pid()) && bean.getOrg_pid().equals(parentId))
            {
                Map<String, Object> map = Maps.newHashMap();
                map.put("text", bean.getName_cn());
                map.put("id", bean.getOrg_id());
                map.put("org_pid", bean.getOrg_pid());
                map.put("iconCls", bean.getIconCls());
                map.put("state", "open");
                List<Map<String, Object>> childrenList = quyAuthTreeChild(organList, bean.getOrg_id());
                if (!childrenList.isEmpty())
                    map.put("children", childrenList);
                listMap.add(map);
            }
        }
        return listMap;
    }

    public OrganizationBean queryOneByName(String orgName)
    {
        
        OrganizationBean bean = organizationMapper.queryOneByName(orgName);
        return bean;
    }
    
    public static void main(String args[])
    {
        String xx="2018-01 - 2019-06";
        String startDate = xx.substring(0, StringHelper.getFromIndex(xx, "-", 2)).trim();
        String endDate = xx.substring(StringHelper.getFromIndex(xx, "-", 2) + 2, xx.length()).trim();
        System.out.println(startDate+"   "+endDate);
    }

}
