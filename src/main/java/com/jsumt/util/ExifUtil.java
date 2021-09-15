/**
 * Project Name:ECRMS
 * File Name:ExifUtil.java
 * Package Name:com.jsumt.util
 * Date:2019年3月18日下午3:38:12
 * Copyright (c) 2019, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;

import net.coobird.thumbnailator.Thumbnails;

/**
 * ClassName:ExifUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2019年3月18日 下午3:38:12 <br/>
 * 
 * @author wyf
 * @version
 * @since JDK 1.6
 * @see
 */
public class ExifUtil
{
    /**
     * 获取图片正确显示需要旋转的角度（顺时针）
     * 
     * @return
     */
    public static int writePhoto(File tempFile, String destUrl)
    {
        int angle = 0;
        Metadata metadata;
        try
        {
            metadata = JpegMetadataReader.readMetadata(tempFile);
            Directory directory = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            int orientation = 0;
            if (directory != null && directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION))
            { // Exif信息中有保存方向,把信息复制到缩略图
                orientation = directory.getInt(ExifDirectoryBase.TAG_ORIENTATION); // 原图片的方向信息
                // 原图片的方向信息
                if (6 == orientation)
                {
                    // 6旋转90
                    angle = 90;
                }
                else if (3 == orientation)
                {
                    // 3旋转180
                    angle = 180;
                }
                else if (8 == orientation)
                {
                    // 8旋转90
                    angle = 270;
                }
                // 先构建
                BufferedImage src = ImageIO.read(tempFile);
                BufferedImage des = RotateImage.Rotate(src, angle);
                ImageIO.write(des, "jpg", new File(destUrl));
                tempFile.delete();
                // 进行压缩
                Thumbnails.of(destUrl).scale(1f).outputQuality(0.5f).toFile(destUrl);
            }
            else
            {// 没有旋转，原样信息
                BufferedImage src = ImageIO.read(tempFile);
                ImageIO.write(src, "jpg", new File(destUrl));
                tempFile.delete();
                // 进行压缩
                Thumbnails.of(destUrl).scale(1f).outputQuality(0.5f).toFile(destUrl);
            }
        }
        catch (JpegProcessingException e)
        {
            e.printStackTrace();
        }
        catch (MetadataException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return angle;
    }

}
