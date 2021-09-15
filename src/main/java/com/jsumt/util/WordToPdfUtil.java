/**
 * Project Name:ECRMS
 * File Name:WordToPdfUtil.java
 * Package Name:com.jsumt.util
 * Date:2018年12月14日上午11:22:52
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.util;

import com.aspose.words.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * ClassName:WordToPdfUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年12月14日 上午11:22:52 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class WordToPdfUtil
{
    private static Logger logger = LoggerFactory.getLogger(WordToPdfUtil.class);


    public static void main(String[] args)
    {
        String sourceFile = "E:\\azxtDoc\\隐患管理\\安全检查\\安全隐患整改复查单_3640288377null6e877196016e87890476002b.docx"; // 模板文件位置
        try
        {
            FileInputStream fileInputStream=new FileInputStream(sourceFile);
            WordToPdfUtil wordToPdfUtil=new WordToPdfUtil();
            wordToPdfUtil.docToPDF(fileInputStream,"E:\\azxtDoc\\隐患管理\\安全检查\\安全隐患整改复查单_3640288377null6e877196016e87890476002b.pdf");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * word转化为PDF
     */
    public static void docToPDF(InputStream inputStream, String pdfFilePath) throws Exception
    {
        InputStream wordLicense=WordToPdfUtil.class.getResourceAsStream("/aspose/license.xml");
        com.aspose.words.License license = new com.aspose.words.License();
        license.setLicense(wordLicense);

        Document doc = new Document(inputStream);

        FileOutputStream fileOS = new FileOutputStream(new File(pdfFilePath));
        doc.save(fileOS, com.aspose.words.SaveFormat.PDF);
        if (fileOS != null)
            fileOS.close();
        if (inputStream != null)
            inputStream.close();
        if (wordLicense != null)
            wordLicense.close();
    }

    public static void office2PDF(String fileUrl, String pdfFilePath) throws Exception
    {
        InputStream wordLicense=WordToPdfUtil.class.getResourceAsStream("/aspose/license.xml");
        com.aspose.words.License license = new com.aspose.words.License();
        license.setLicense(wordLicense);

        FileInputStream inputStream=new FileInputStream(fileUrl);
        Document doc = new Document(inputStream);

        FileOutputStream fileOS = new FileOutputStream(new File(pdfFilePath));
        doc.save(fileOS, com.aspose.words.SaveFormat.PDF);
        if (fileOS != null)
            fileOS.close();
        if (inputStream != null)
            inputStream.close();
        if (wordLicense != null)
            wordLicense.close();
    }

}
