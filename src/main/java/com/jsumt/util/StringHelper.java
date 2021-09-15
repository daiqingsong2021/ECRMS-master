package com.jsumt.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class  StringHelper
{

    public static Boolean isNullAndEmpty(String str)
    {
        return !(isNotNullAndEmpty(str));
    }

    public static Boolean isNotNullAndEmpty(String str)
    {
        Boolean b = false;
        if (str != null && !"".equals(str) && !"null".equals(str) && !"undefined".equals(str))
        {
            b = true;
        }

        return b;
    }
   
    /**
     * 
     * getFromIndex:(子字符串modelStr在字符串str中第count次出现时的下标). <br/>
     *
     * @author wyf
     * @param str 字符串str
     * @param modelStr 子字符串
     * @param count 第count次出现
     * @return  子字符串modelStr在字符串str中第count次出现时的下标
     * @since JDK 1.6
     */
    public static int getFromIndex(String str, String modelStr, Integer count)
    {
        // 对子字符串进行匹配
        java.util.regex.Matcher slashMatcher = java.util.regex.Pattern.compile(modelStr).matcher(str);
        int index = 0;
        while (slashMatcher.find())
        {
            index++;
            // 当modelStr字符第count次出现的位置
            if (index == count)
            {
                break;
            }
        }
        return slashMatcher.start();

    }

}
