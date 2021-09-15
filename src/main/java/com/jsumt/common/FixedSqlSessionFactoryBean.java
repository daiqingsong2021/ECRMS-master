/**
 * Project Name:tyfw
 * File Name:FixedSqlSessionFactoryBean.java
 * Package Name:com.jsumt.tyfw.common
 * Date:2017年6月15日下午4:17:20
 * Copyright (c) 2017, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.common;

import java.io.IOException;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:FixedSqlSessionFactoryBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年6月15日 下午4:17:20 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class FixedSqlSessionFactoryBean extends SqlSessionFactoryBean
{
    /**
     * 捕捉异常，打印日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedSqlSessionFactoryBean.class);
  
    @Override
    protected SqlSessionFactory buildSqlSessionFactory() throws IOException
    {
        try
        {
            return super.buildSqlSessionFactory();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            LOGGER.error(e.getMessage());
        }
        finally
        {
            ErrorContext.instance().reset();
        }
        return null;
    }
}
