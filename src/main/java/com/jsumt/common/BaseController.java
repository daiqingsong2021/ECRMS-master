package com.jsumt.common;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.google.common.collect.Maps;
import com.jsumt.util.StringHelper;

public class BaseController
{
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    /** 成功(success) */
    protected String SUCCESS = "success";
    /** 失败(error) */
    protected String ERROR = "error";

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;

    }

    protected String getParameter(String paraName) throws UnsupportedEncodingException
    {
        String value = "";
        String paramValue=request.getParameter(paraName);
        if (StringHelper.isNotNullAndEmpty(paramValue))
        {
            if(paramValue.equals(new String(paramValue.getBytes("ISO-8859-1"), "ISO-8859-1")))
            {//ISO-8859-1 编码
                value = new String(request.getParameter(paraName).getBytes("ISO-8859-1"), "UTF-8");
            }
            else if(paramValue.equals(new String(paramValue.getBytes("UTF-8"), "UTF-8")))
            {//UTF-8编码
                value=paramValue;
            }
        }
        return value;

    }
    
    protected String formatString(String paramValue) throws UnsupportedEncodingException
    {
        String value = "";
        if (StringHelper.isNotNullAndEmpty(paramValue))
        {
            if(paramValue.equals(new String(paramValue.getBytes("ISO-8859-1"), "ISO-8859-1")))
            {//ISO-8859-1 编码
                value = new String(paramValue.getBytes("ISO-8859-1"), "UTF-8");
            }
            else if(paramValue.equals(new String(paramValue.getBytes("UTF-8"), "UTF-8")))
            {//UTF-8编码
                value=paramValue;
            }
        }
        return value;
    }

    /**
     * 生成报文信息
     * generateMsg:(这里用一句话描述这个方法的作用). <br/>
     * 标准返回报文格式Map
     * map.put("msg","json")
     * map.put("isSuccess","success/error")
     * map.put("info","")
     *
     * @author wyf
     * @return
     * @since JDK 1.6
     */
    protected Map<String, Object> generateMsg(Object msg, boolean isSuccess, String info)
    {
        Map<String, Object> map = Maps.newHashMap();
        map.put("msg", msg);
        if (isSuccess)
            map.put("isSuccess", SUCCESS);
        else
            map.put("isSuccess", ERROR);
        map.put("info", info);
        return map;
    }

    public Map<String, Object> layuiData(long count,List<? extends Object> data)
    {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", "0");
        map.put("msg", "");
        map.put("count", count);
        map.put("data", data);
        return map;
    }

}
