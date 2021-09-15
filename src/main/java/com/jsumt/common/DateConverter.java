package com.jsumt.common;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.jsumt.util.DateUtil;
import com.jsumt.util.StringHelper;

public class DateConverter implements Converter<String, Date>
{
    @Override
    public Date convert(String source)
    {
        if(StringHelper.isNotNullAndEmpty(source)){
            return DateUtil.formatDate(source, DateUtil.DATE_DEFAULT_FORMAT);
        }else{
            return null;
        }
       
    }
}
