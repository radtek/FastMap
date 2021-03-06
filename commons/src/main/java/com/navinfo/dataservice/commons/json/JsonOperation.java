package com.navinfo.dataservice.commons.json;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;


public class JsonOperation {

	public JsonOperation() {
		// TODO Auto-generated constructor stub
	}
	/*
	 * JSONObject转类对象，调用方式jsonOperation.jsonToBean(taskJson,Task.class)
	 */
	public static Object jsonToBean(JSONObject json,Class classObj){
		String[] formats={"yyyyMMdd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"}; 
		//String[] formats={"yyyyMMdd"};
		
		JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats));  
		JSONObject taskJson=JSONObject.fromObject(json); 
		
		Object bean = JSONObject.toBean(taskJson, classObj);
		return bean;
		}
	/*
	 * 类对象转JSONObject，调用方式jsonOperation.beanToJson(taskObj,Task.class)
	 */
	public static JSONObject beanToJson(Object bean){  
	    JsonConfig config=new JsonConfig();  
	    //格式要求只返回年月日
	    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyyMMdd"));  
	    config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor2("yyyyMMdd"));  
	    JSONObject json=JSONObject.fromObject(bean,config);  
	    return json;
	    } 
	
	public static JSONArray beanToJsonList(List beanList){
		JSONArray resultList=new JSONArray();
		for(int i=0;i<beanList.size();i++){
			resultList.add(beanToJson(beanList.get(i)));
		}
		return resultList;
	}
	
}
