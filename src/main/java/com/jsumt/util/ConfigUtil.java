package com.jsumt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil
{
    static String fileUrl = "config.properties";

    public static String getValueByKey(String key)
    {

        String value = "";
        try
        {
            Properties props = new Properties();
            InputStream ips = ConfigUtil.class.getResourceAsStream("/" + fileUrl);
            props.load(ips);
            value = props.getProperty(key);
            return value;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public static void main(String[] args)
    {
        System.out.println(ConfigUtil.getValueByKey("sessionTime"));
    }
}
