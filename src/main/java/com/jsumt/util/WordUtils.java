package com.jsumt.util;

import org.apache.poi.xwpf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************
 * 通过word模板生成新的word工具类
 * 
 * @Package com.cccuu.project.myUtils
 * @Author duan
 * @Date 2018/3/29 14:24
 * @Version V1.0
 *******************************************/
public class WordUtils
{

    /**
     * 根据模板生成word
     * 
     * @param path 模板的路径
     * @param params 需要替换的参数
     * @param fileName 生成word文件的文件名
     * @param response
     */
    public void getWord(String path, Map<String, Object> params, String fileName, HttpServletResponse response)
            throws Exception
    {
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        MyXWPFDocument doc = new MyXWPFDocument(is);
        this.replaceInPara(doc, params); // 替换文本里面的变量
        this.replaceInTable(doc, params); // 替换表格里面的变量
        
        String fileName_ = fileName + ".docx";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment; filename=" + URLEncoder.encode(fileName_, "UTF-8"));
        //response.setHeader("Content-Length", String.valueOf(file.length()));
        OutputStream os = response.getOutputStream();
        doc.write(os);
        this.close(os);
        this.close(is);
    }

    public void getWordToPdf(String path, Map<String, Object> params, String fileName, HttpServletResponse response)
            throws Exception
    {
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        MyXWPFDocument doc = new MyXWPFDocument(is);
        this.replaceInPara(doc, params); // 替换文本里面的变量
        this.replaceInTable(doc, params); // 替换表格里面的变量

        // 输出到临时目录生成一个文件
        String tempId = UUIDHexGenerator.generator();
        String tempWordUrl =
                this.getClass().getClassLoader().getResource("").getPath() + "templates/temporary/" + tempId + ".docx";
        File directory=new File(this.getClass().getClassLoader().getResource("").getPath() + "templates/temporary/");
        if(!directory.exists())
           directory.mkdirs();
        FileOutputStream destFileOut = new FileOutputStream(tempWordUrl);
        doc.write(destFileOut);
        // 临时文件转为PDF
        String tempPdfUrl =
                this.getClass().getClassLoader().getResource("").getPath() + "templates/temporary/" + tempId + ".pdf";
        String s= new File(tempWordUrl).toString();
        String y= new File(tempPdfUrl).toString();
        WordToPdfUtil.office2PDF(s,y);

        // response输出PDF
        BufferedInputStream bis = null;
        // 获取输入流
        bis = new BufferedInputStream(new FileInputStream(tempPdfUrl));
        response.setContentType("application/pdf");
        int len = 0;
        byte[] b = new byte[1024];

        while ((len = bis.read(b, 0, 1024)) != -1)
        {
            response.getOutputStream().write(b, 0, len);
        }

        this.close(bis);
        this.close(destFileOut);
        this.close(is);
        response.getOutputStream().flush();

        // 删除两份临时文件
        new File(tempWordUrl).delete();
        new File(tempPdfUrl).delete();
    }

    /**
     * generateWord:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author wyf
     * @param sourceFile 源文件
     * @param params 要替换参数
     * @param destFile 目标文件
     * @throws Exception
     * @since JDK 1.6
     */
    public void generateWord(String sourceFile, Map<String, Object> params, String destFile) throws Exception
    {
        File file = new File(sourceFile);
        InputStream is = new FileInputStream(file);
        MyXWPFDocument doc = new MyXWPFDocument(is);
        this.replaceInPara(doc, params); // 替换文本里面的变量
        this.replaceInTable(doc, params); // 替换表格里面的变量
        FileOutputStream os = new FileOutputStream(destFile);
        doc.write(os);
        this.close(os);
        this.close(is);

    }

    /**
     * 替换段落里面的变量
     * 
     * @param doc 要替换的文档
     * @param params 参数
     */
    private void replaceInPara(MyXWPFDocument doc, Map<String, Object> params)
    {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext())
        {
            para = iterator.next();
            this.replaceInPara(para, params, doc);
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param para 要替换的段落
     * @param params 参数
     */
    private void replaceInPara(XWPFParagraph para, Map<String, Object> params, MyXWPFDocument doc)
    {
        List<XWPFRun> runs;
        Matcher matcher;
        if (this.matcher(para.getParagraphText()).find())
        {
            runs = para.getRuns();
            int start = -1;
            int end = -1;
            String str = "";
            for (int i = 0; i < runs.size(); i++)
            {
                XWPFRun run = runs.get(i);
                String runText = run.toString();
                if ('$' == runText.charAt(0) && '{' == runText.charAt(1))
                {
                    start = i;
                }
                if ((start != -1))
                {
                    str += runText;
                }
                if ('}' == runText.charAt(runText.length() - 1))
                {
                    if (start != -1)
                    {
                        end = i;
                        break;
                    }
                }
            }

            for (int i = start; i <= end; i++)
            {
                para.removeRun(i);
                i--;
                end--;
            }

            for (Map.Entry<String, Object> entry : params.entrySet())
            {
                String key = entry.getKey();
                if (str.indexOf(key) != -1)
                {
                    Object value = entry.getValue();
                    if (value instanceof String)
                    {
                        str = str.replace(key, value.toString());
                        para.createRun().setText(str, 0);
                        break;
                    }
                    else if (value instanceof Map)
                    {// 图片
                        str = str.replace(key, "");
                        Map pic = (Map) value;
                        int width = Integer.parseInt(pic.get("width").toString());
                        int height = Integer.parseInt(pic.get("height").toString());
                        int picType = getPictureType(pic.get("type").toString());
                        byte[] byteArray = (byte[]) pic.get("content");
                        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteArray);
                        try
                        {
                            doc.addPictureData(byteInputStream, picType);
                            doc.createPicture(doc.getAllPictures().size() - 1, width, height, para);
                            para.createRun().setText(str, 0);
                            break;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if (value instanceof List)
                    {//
                        List valList = (List) value;
                        str = str.replace(key, "");
                        for (int i = 0; i < valList.size(); i++)
                        {
                            Map map = (Map) valList.get(i);
                            if (map.get("isImage") != null && "1".equals(map.get("isImage").toString()))
                            {// 如果是图片
                                //图片描述读出
                                String imgDesc=String.valueOf(map.get("imgDesc"));
                                if(StringHelper.isNotNullAndEmpty(imgDesc))
                                {
                                    para.createRun().setText(imgDesc, 0);
                                    para.createRun().addBreak();// 换行
                                }
                                int width = Integer.parseInt(map.get("width").toString());
                                int height = Integer.parseInt(map.get("height").toString());
                                int picType = getPictureType(map.get("type").toString());
                                byte[] byteArray = (byte[]) map.get("content");
                                ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteArray);
                                try
                                {
                                    doc.addPictureData(byteInputStream, picType);
                                    doc.createPicture(doc.getAllPictures().size() - 1, width, height, para);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {// 不是图
                                Object contentV = map.get("content");
                                if (contentV.getClass().isArray())
                                {
                                    String[] contentArray = (String[]) contentV;
                                    for (String v : contentArray)
                                    {
                                        para.createRun().setText(v, 0);
                                        para.createRun().addBreak();// 换行
                                    }
                                    para.createRun().addBreak();// 换行
                                }
                                else
                                {
                                    para.createRun().setText(map.get("content").toString(), 0);
                                    para.createRun().addBreak();// 换行
                                }

                            }

                        }
                        para.createRun().setText(str, 0);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 为表格插入数据，行数不够添加新行
     *
     * @param table 需要插入数据的表格
     * @param tableList 插入数据集合
     */
    private static void insertTable(XWPFTable table, List<String[]> tableList)
    {
        // 创建行,根据需要插入的数据添加新行，不处理表头
        for (int i = 0; i < tableList.size(); i++)
        {
            XWPFTableRow row = table.createRow();
        }
        // 遍历表格插入数据
        List<XWPFTableRow> rows = table.getRows();
        int length = rows.size();
        for (int i = 1; i < length - 1; i++)
        {
            XWPFTableRow newRow = table.getRow(i);
            List<XWPFTableCell> cells = newRow.getTableCells();
            for (int j = 0; j < cells.size(); j++)
            {
                XWPFTableCell cell = cells.get(j);
                String s = tableList.get(i - 1)[j];
                cell.setText(s);
            }
        }
    }

    /**
     * 替换表格里面的变量
     * 
     * @param doc 要替换的文档
     * @param params 参数
     */
    private void replaceInTable(MyXWPFDocument doc, Map<String, Object> params)
    {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext())
        {
            table = iterator.next();
            if (table.getRows().size() > 1)
            {
                // 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
                if (this.matcher(table.getText()).find())
                {
                    rows = table.getRows();
                    for (XWPFTableRow row : rows)
                    {
                        cells = row.getTableCells();
                        for (XWPFTableCell cell : cells)
                        {
                            paras = cell.getParagraphs();
                            for (XWPFParagraph para : paras)
                            {
                                this.replaceInPara(para, params, doc);
                            }
                        }
                    }
                }
                // else
                // {
                // insertTable(table, tableList); // 插入数据
                // }
            }
        }
    }

    /**
     * 正则匹配字符串
     *
     * @param str
     * @return
     */
    private Matcher matcher(String str)
    {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 根据图片类型，取得对应的图片类型代码
     *
     * @param picType
     * @return int
     */
    private static int getPictureType(String picType)
    {
        int res = MyXWPFDocument.PICTURE_TYPE_PICT;
        if (picType != null)
        {
            if (picType.equalsIgnoreCase("png"))
            {
                res = MyXWPFDocument.PICTURE_TYPE_PNG;
            }
            else if (picType.equalsIgnoreCase("dib"))
            {
                res = MyXWPFDocument.PICTURE_TYPE_DIB;
            }
            else if (picType.equalsIgnoreCase("emf"))
            {
                res = MyXWPFDocument.PICTURE_TYPE_EMF;
            }
            else if (picType.equalsIgnoreCase("jpg") || picType.equalsIgnoreCase("jpeg"))
            {
                res = MyXWPFDocument.PICTURE_TYPE_JPEG;
            }
            else if (picType.equalsIgnoreCase("wmf"))
            {
                res = MyXWPFDocument.PICTURE_TYPE_WMF;
            }
        }
        return res;
    }

    /**
     * 将输入流中的数据写入字节数组
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static byte[] inputStream2ByteArray(InputStream in, boolean isClose) throws Exception
    {
        byte[] byteArray = null;
        try
        {
            int total = in.available();
            byteArray = new byte[total];
            in.read(byteArray);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (isClose)
            {
                try
                {
                    in.close();
                }
                catch (Exception e2)
                {
                    e2.getStackTrace();
                    throw e2;
                }
            }
        }
        return byteArray;
    }

    /**
     * 关闭输入流
     *
     * @param is
     */
    private void close(InputStream is)
    {
        if (is != null)
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    private void close(OutputStream os)
    {
        if (os != null)
        {
            try
            {
                os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[])
    {

        WordUtils wordUtil = new WordUtils();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("${jcdwt}", "江苏省城市：");
        params.put("${jcr}", "张三");
        params.put("${sjdw}", "bbbb");
        params.put("${sjr}", "ccccc");
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        Map<String, Object> content1 = new HashMap<String, Object>();
        String[] contentArray = new String[2];
        contentArray[0] = "1、问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一问题一";
        contentArray[1] = "答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一答复一";
        content1.put("content", contentArray);
        Map<String, Object> content2 = new HashMap<String, Object>();
        String[] contentArray2 = new String[2];
        contentArray2[0] = "2、问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二问题二";
        contentArray2[1] = "答复二答复二答复二答复二答复二答复二答复二答复二答复二答复二答复二答复二答复二答复二";
        content2.put("content", contentArray2);
        Map<String, Object> content3 = new HashMap<String, Object>();
        String[] contentArray3 = new String[2];
        contentArray3[0] = "3、问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三问题三";
        contentArray3[1] = "答复三答复三答复三答复三答复三答复三答复三答复三答复三答复三答复三答复三答复三答复三";
        content3.put("content", contentArray3);
        contents.add(content1);
        contents.add(content2);
        contents.add(content3);
        params.put("${contents}", contents);
        params.put("${isPass}", "通过");
        params.put("${createTime}", "2018年12月13日");
        params.put("${fcCreate}", "2018年12月13日");
        List<Map<String, Object>> images = new ArrayList<Map<String, Object>>();
        Map<String, Object> image = new HashMap<String, Object>();
        // "100/150" 300/450
        image.put("width", 600);
        image.put("height", 800);
        image.put("type", "JPG");
        try
        {
            image.put("content",
                    WordUtils.inputStream2ByteArray(
                            new FileInputStream("D:/azxtDoc/隐患管理/安全检查/36402894e6null67c53e560167c550980b0016.jpg"),
                            true));
        }
        catch (FileNotFoundException e1)
        {

            // TODO Auto-generated catch block
            e1.printStackTrace();

        }
        catch (Exception e1)
        {

            // TODO Auto-generated catch block
            e1.printStackTrace();

        }
        image.put("isImage", "1");
        images.add(image);

        params.put("${images}", images);
        try
        {
            String sourceFile = "D:/fff/sgyhzgfkbgdResult.docx"; // 模板文件位置
            // String fileName = new String("测试文档.docx".getBytes("UTF-8"),
            // "iso-8859-1"); // 生成word文件的文件名
            wordUtil.generateWord(sourceFile, params, "D:/fff/测试文档.docx");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
