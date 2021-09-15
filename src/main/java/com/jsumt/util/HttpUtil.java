package com.jsumt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jsumt.vo.system.UserBean;

public class HttpUtil
{
    public static UserBean getSeesionUserInfo()
    {
        UserBean userInfo = null;
        WebServiceContext wsContext = new org.apache.cxf.jaxws.context.WebServiceContextImpl();
        HttpServletRequest request;
        MessageContext mc;
        HttpSession session;
        mc = wsContext.getMessageContext();

        if (mc != null)
        {
            request = (HttpServletRequest) mc.get("HTTP.REQUEST");
        }
        else
        {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }

        session = request.getSession();
        if (session != null)
        {
            userInfo = (UserBean) request.getSession().getAttribute("userinfo");
        }

        return userInfo;
    }

    public static Object getSessionObject(String key)
    {
        WebServiceContext wsContext = new org.apache.cxf.jaxws.context.WebServiceContextImpl();
        HttpServletRequest request;
        MessageContext mc;
        HttpSession session;
        mc = wsContext.getMessageContext();

        if (mc != null)
        {
            request = (HttpServletRequest) mc.get("HTTP.REQUEST");
        }
        else
        {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }

        session = request.getSession();
        Object obj = null;
        if (session != null)
        {
            obj = session.getAttribute(key);
        }
        return obj;
    }

    public static void setSessionObject(String key, Object obj)
    {
        WebServiceContext wsContext = new org.apache.cxf.jaxws.context.WebServiceContextImpl();
        HttpServletRequest request;
        MessageContext mc;
        HttpSession session;
        mc = wsContext.getMessageContext();

        if (mc != null)
        {
            request = (HttpServletRequest) mc.get("HTTP.REQUEST");
        }
        else
        {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }

        session = request.getSession();
        if (null != session)
            session.setAttribute(key, obj);
    }

    public static List<String> getSessionKeys()
    {
        WebServiceContext wsContext = new org.apache.cxf.jaxws.context.WebServiceContextImpl();
        HttpServletRequest request;
        MessageContext mc;
        HttpSession session;
        mc = wsContext.getMessageContext();

        if (mc != null)
        {
            request = (HttpServletRequest) mc.get("HTTP.REQUEST");
        }
        else
        {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }

        session = request.getSession();
        if (null != session)
        {
            Enumeration names = session.getAttributeNames();
            while (names.hasMoreElements())
            {
                String name = (String) names.nextElement();
            }
            return (List<String>) session.getAttributeNames();
        }
        else
            return null;
    }
    
    /**
     * 发送http请求
     */
    public static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            System.setProperty("http.keepAlive", "false");
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "close");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            System.out.println("HTTP 发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return result;
    }
    
    public static void main(String []args)
    {
        //String reqeustUrl="http://192.168.1.112/transfer_data/flow.php";
        String reqeustUrl="http://192.168.1.112/transfer_data/notice.php";
        String strParam = "{user_id:'wangshuo'}";
        String response=HttpUtil.sendPost(reqeustUrl, strParam);
        if(!"null".equalsIgnoreCase(response))
           System.out.println(response.replaceAll("\\\\", ""));
        
    }
}
