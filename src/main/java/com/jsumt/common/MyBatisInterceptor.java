package com.jsumt.common;

import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.jsumt.util.Consts;

@Intercepts({@Signature(
	method = "update", 
	type = Executor.class,
	args = { MappedStatement.class,Object.class })})
public class MyBatisInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation arg0) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement)arg0.getArgs()[0];
        // 根据ID生成相应类型的sql语句（id需剔除namespace信息）  
        String id = mappedStatement.getId();
        id = id.substring(id.lastIndexOf(".") + 1);
        Object obj = arg0.getArgs()[1];
		if (!id.contains("update") || !obj.getClass().getName().contains(Consts.ECRMS_NAMESPACE))
			return arg0.proceed();
		
		Boolean isAllEmpty = true;
		
		Field[] fields = null;
		if (obj.getClass().getSuperclass().getSuperclass() == null)
			fields = obj.getClass().getDeclaredFields();
		else
			fields = obj.getClass().getSuperclass().getDeclaredFields();
		
		for (Field f : fields) {
			//if (f.getClass().getSimpleName() != "String")
			//	continue;
		    f.setAccessible(true);
		    if ("id" != f.getName() && "TABLE_NAME" != f.getName()) {
			    if (f.get(obj) != null) { //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
			        isAllEmpty = false;
			        break;
			    }
		    }
		} 
		if (isAllEmpty)
			return 1;
		else
			return arg0.proceed();  
		
		//MappedStatement mappedStatement = (MappedStatement)arg0.getArgs()[0]; 
		//mappedStatement.
        //String methodName = arg0.getMethod().getName();  
        //if(methodName.equals("update")){  
        //    Object parameter = invocation.getArgs()[1];  
        //    if(parameter instanceof User)  
        //        ((User)parameter).setGender(saveValueToDb(((User)parameter).getGender()));  
        //}
		//Object object = arg0.proceed();
	}

	@Override
	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
		//return null;
	}

	@Override
	public void setProperties(Properties arg0) {

	}

}
