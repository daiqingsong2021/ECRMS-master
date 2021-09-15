/**
 * Project Name:tyfw
 * File Name:MyBatisSqlLoginPlugin.java
 * Package Name:com.jsumt.tyfw.common
 * Date:2017年5月15日上午10:53:56
 * Copyright (c) 2017, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:MyBatisSqlLoginPlugin <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年5月15日 上午10:53:56 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
@Intercepts(
{@Signature(type = Executor.class, method = "query", args =
        {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class,
                method = "update",
                args =
                {MappedStatement.class, Object.class})})
public class MyBatisSqlPlugin implements Interceptor
{

    /**
     * @Fields LOGGER : 日志操作类
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisSqlPlugin.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable
    {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1)
        {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        Object returnValue = null;
        long start = System.currentTimeMillis();
        try
        {
            returnValue = invocation.proceed();
        }
        catch (Exception e)
        {
            String sql = getSql(configuration, boundSql, sqlId);
            LOGGER.info("执行SQL异常：{}", sql);
            LOGGER.info("异常", e);
            throw e;
        }
        long end = System.currentTimeMillis();
        long time = (end - start);
        String sql = getSql(configuration, boundSql, sqlId, time);
        LOGGER.info("执行SQL：" + sql);
        return returnValue;
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId, long... time)
    {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append(":");
        str.append(sql);
        if (time.length > 0)
        {
            str.append(":");
            str.append(time[0]);
            str.append("ms");
        }
        return str.toString();
    }

    public static String showSql(Configuration configuration, BoundSql boundSql)
    {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (!parameterMappings.isEmpty())
        {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass()))
            {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            }
            else
            {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);

                for (ParameterMapping parameterMapping : parameterMappings)
                {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName))
                    {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                    else if (boundSql.hasAdditionalParameter(propertyName))
                    {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;

    }

    private static String getParameterValue(Object obj)
    {
        String value = null;
        if (obj instanceof String)
        {
            value = "'" + obj.toString().replaceAll("'", "''") + "'";
        }
        else if (obj instanceof Date)
        {
            // DateFormat formatter =
            // DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
            // DateFormat.DEFAULT, Locale.CHINA);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            value = "to_date('" + sdf.format((Date) obj) + "','yyyy-mm-dd hh24:mi:ss')";
        }
        else
        {
            if (null != obj)
            {
                value = obj.toString();
            }
            else
            {
                value = "null";
            }
        }
        return value;
    }

    @Override
    public Object plugin(Object target)
    {

        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties)
    {

        LOGGER.info("mybatis properties", properties);
    }
}
