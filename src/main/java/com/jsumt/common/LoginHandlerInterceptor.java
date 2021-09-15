/**
 * Project Name:tyfw
 * File Name:LoginHandlerInterceptor.java
 * Package Name:com.jsumt.tyfw.common
 * Date:2017年5月18日上午10:09:26
 * Copyright (c) 2017, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.common;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jsumt.util.ConfigUtil;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.vo.system.UserBean;

/**
 * ClassName:LoginHandlerInterceptor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年5月18日 上午10:09:26 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.9
 * @see
 */
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter
{

    private List<String> allowUrls;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        String requestUrl = request.getRequestURI();
        for (String url : allowUrls)
        {
            if (requestUrl.endsWith(url))
            {
                return true;
            }
        }
        String sessionId = request.getParameter("sessionId");
        if (StringHelper.isNullAndEmpty(sessionId))
        {//一开始登录操作
            request.setCharacterEncoding("UTF-8");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "您已经太长时间没有操作,请刷新页面");
            return false;

        }
        String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
        if (StringHelper.isNullAndEmpty(userCode))
        {//
            request.setCharacterEncoding("UTF-8");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "您已经太长时间没有操作,请刷新页面");
            return false;
        }
        //重新设置超时时间，一旦停止操作就不会被拦截
        RedisUtil.getRedisUtil().setStringValue(sessionId,
                userCode,
                Integer.valueOf(ConfigUtil.getValueByKey("sessionTime")));

        return true;

    }

    public List<String> getAllowUrls()
    {
        return allowUrls;
    }

    public void setAllowUrls(List<String> allowUrls)
    {
        this.allowUrls = allowUrls;
    }

}
