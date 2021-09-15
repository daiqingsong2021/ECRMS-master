/**
 * Project Name:ECRMS
 * File Name:CrossFilter.java
 * Package Name:com.jsumt.filter
 * Date:2018年7月27日下午2:31:52
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:CrossFilter <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年7月27日 下午2:31:52 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class CrossFilter implements Filter{
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
         
    }
 
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        // 设置允许跨域访问的域，*表示支持所有的来源
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 设置允许跨域访问的方法
        response.setHeader("Access-Control-Allow-Methods","POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        chain.doFilter(req, res);
    }
 
    @Override
    public void init(FilterConfig arg0) throws ServletException {
         
    }
}

