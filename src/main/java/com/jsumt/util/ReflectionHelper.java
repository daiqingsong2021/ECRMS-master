package com.jsumt.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class ReflectionHelper
{

    public static Object InvokeObjectMethod(String methodName, Object obj, Object[] args)
    {
        Object retObj = null;

        if (null != obj)
        {
            try
            {
                Method[] methods = obj.getClass().getDeclaredMethods();
                for (Method m : methods)
                {
                    if (m.getName().indexOf(methodName) > -1)
                    {
                        retObj = (String) m.invoke(obj, args);
                        break;
                    }
                }
                return retObj;
            }
            catch (Exception e)
            {
                return null;
            }
        }
        else
            return null;
    }

    /**
     * 将map转为对象，注意：如果类中属性是int或Integer类型，并且map中此属性的值是null，此方法会把null值强制转化为0。
     * 
     * @param map
     * @param obj
     */
    public static void TransMap2Bean(Map<String, Object> map, Object obj)
    {
        if (map == null || obj == null)
        {
            return;
        }
        try
        {
            BeanUtils.populate(obj, map);
        }
        catch (Exception e)
        {
            System.out.println("transMap2Bean Error " + e);
            obj = null;
        }
    }

    /**
     * 将map转化为对象，注意不能将int或Integer转化为BigDecimal
     * 
     * @param map
     * @param obj
     */
    public static void TransMap2Bean2(Map<String, Object> map, Object obj)
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors)
            {
                String key = property.getName();

                if (map.containsKey(key))
                {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("transMap2Bean Error " + e);
            obj = null;
            try
            {
                throw new Exception("Map转化成类时错误！");
            }
            catch (Exception e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
