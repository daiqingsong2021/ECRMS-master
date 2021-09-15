package com.jsumt.tyfw.test;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.jsumt.mapper.trouble.TroubleMapper;
import com.jsumt.service.system.OrganizationService;
import com.jsumt.vo.trouble.TroubleTypeBean;

public class TyfwTest extends BaseTestNG
{

    private static Logger logger=LoggerFactory.getLogger(TyfwTest.class);
    
    @Autowired
    private OrganizationService organService;
    
    @Autowired
    private TroubleMapper troubleMapper;

    /**
     * 根据用户名称测试组织机构
     * selectOrgComanysByUserNameTest:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @since JDK 1.6
     */
    @Test
    public void judegeOrgParentTest()
    {
        List<String> xx=Lists.newArrayList();
        xx.add("364028896dnull6796076b01679620a71a0035");
        List<Map<String, Object>> dsaxx=troubleMapper.queryAllQIdsByTrobIds(xx);
        logger.info(String.valueOf(dsaxx.size()));
    }
   
   
}
