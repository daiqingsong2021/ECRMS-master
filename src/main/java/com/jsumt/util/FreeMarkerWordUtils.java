package com.jsumt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerWordUtils
{
    private static Configuration configuration = null;
    private static final String templateFolder =
            FreeMarkerWordUtils.class.getClassLoader().getResource("").getPath() + "templates/";
    /**
     * freemarker 模板路径
     */
    static
    {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        try
        {
            configuration.setDirectoryForTemplateLoading(new File(templateFolder));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    
    /**
     * web导出word
     * exportMillCertificateWord:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param request
     * @param response
     * @param map
     * @param title
     * @param ftlFile
     * @throws IOException
     * @since JDK 1.6
     */
    public static void exportMillCertificateWord(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> map, String title, String ftlFile) throws IOException
    {
        Template freemarkerTemplate = configuration.getTemplate(ftlFile);
        File file = null;
        InputStream fin = null;
        ServletOutputStream out = null;
        try
        {
            // 调用工具类的createDoc方法生成Word文档
            file = createDoc(map, freemarkerTemplate, title);
            fin = new FileInputStream(file);

            response.setContentType("application/msword;charset=utf-8");
            // 设置浏览器以下载的方式处理该文件名
            String fileName = title + DateUtil.getDateTimeFormat(new Date()) + ".doc";
            response.setHeader("Content-Disposition",
                    "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
            out = response.getOutputStream();
            byte[] buffer = new byte[512]; // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = fin.read(buffer)) != -1)
            {
                out.write(buffer, 0, bytesToRead);
            }
        }
        finally
        {
            if (fin != null)
                fin.close();
            if (out != null)
                out.close();
            if (file != null)
                file.delete(); // 删除临时文件
        }
    }
    
    /**
     * 创建word
     * createDoc:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param dataMap
     * @param template
     * @param title
     * @return
     * @since JDK 1.6
     */
    private static File createDoc(Map<?, ?> dataMap, Template template, String title)
    {
        String name = PinyinUtil.getQuanPin(title) + ".doc";
        File f = new File(name);
        Template t = template;
        try
        {
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return f;
    }

    // 获得图片的base64码
    public static String getImageBase(String src)
    {
        if (src == null || src == "")
        {
            return "";
        }
        File file = new File(src);
        if (!file.exists())
        {
            return "";
        }
        InputStream in = null;
        byte[] data = null;
        try
        {
            in = new FileInputStream(file);
        }
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        try
        {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Base64Helper.encryptBASE64(data);
    }

}
