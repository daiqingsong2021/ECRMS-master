package com.jsumt.util;

import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;

public class JsonHelper
{   
    /**
     * 对象转变为JSON
     * toJsonWithGson:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param obj
     * @return
     * @since JDK 1.6
     */
    public static String toJsonWithGson(Object obj)
    {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
    
    public static String toJsonWithGson(Object obj, Type type)
    {
        Gson gson = new Gson();
        return gson.toJson(obj, type);
    }

    /**
     * List转变为JSOn
     * toJsonWithGson:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param list
     * @return
     * @since JDK 1.6
     */
    @SuppressWarnings("unchecked")
    public static String toJsonWithGson(List list)
    {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @SuppressWarnings("unchecked")
    public static String toJsonWithGson(List list, Type type)
    {
        Gson gson = new Gson();
        return gson.toJson(list, type);
    }
 
    /**
     * JSON转变为对象
     * fromJsonWithGson:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param jsonStr
     * @param classOfT 类对象
     * @return
     * @since JDK 1.6
     */
    public static <T> T fromJsonWithGson(String jsonStr, Class<T> classOfT)
    {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, classOfT);
    }

    /**
     * JSON转变为对象
     * fromJsonWithGson:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param jsonStr
     * @param typeofT  反射类型对象
     * @return
     * @since JDK 1.6
     */
    public static <T> T fromJsonWithGson(String jsonStr, Type typeofT)
    {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, typeofT);
    }
}
