package com.jsumt.mapper.system;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jsumt.vo.system.OrganizationBean;
@Repository
public interface OrganizationMapper {

    List<OrganizationBean> queryorganizations(Map<String, Object> mapWhere);

    void addorganization(OrganizationBean bean);

    OrganizationBean queryOneById(String org_id);

    void delorganizations(List<String> DeleteId);

    void updateOrganization(OrganizationBean bean);

    String queryMaxNo(String org_pid);

    List<Map<String, Object>> queryOrgUsersByOrgId(String org_id);

    OrganizationBean queryOneByName(String orgName);

}
