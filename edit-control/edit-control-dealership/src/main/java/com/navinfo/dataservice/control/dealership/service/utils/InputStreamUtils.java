package com.navinfo.dataservice.control.dealership.service.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/** 
 * @ClassName: InputStream
 * @author songdongyan
 * @date 2017年6月2日
 * @Description: InputStream.java
 */
public class InputStreamUtils {

	public static String request2File(HttpServletRequest request,String filePath) throws Exception{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);
		Iterator<FileItem> it = items.iterator();
		
		FileItem uploadItem = null;
		
		while(it.hasNext()){
			FileItem item = it.next();
			if (item.getName()!= null && !item.getName().equals("")){
				uploadItem = item;
			}
		}

		InputStream fileStream = uploadItem.getInputStream();

		File tempFile = new File(uploadItem.getName());
		File file = new File(filePath,tempFile.getName());
		File fileParent = file.getParentFile();
		if(!fileParent.exists()){
			fileParent.mkdirs();
		}
		if(!file.exists()){
			file.createNewFile(); 
		}
		uploadItem.write(file);
		return file.getAbsolutePath();
	}
	
	
	 public static void transMap2Bean(Map<String, Object> map, Object obj) throws Exception {  
		  
	        try {  
	            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
	            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
	  
	            for (PropertyDescriptor property : propertyDescriptors) {  
	                String key = property.getName();  
	  
	                if (map.containsKey(key)) {  
	                    Object value = map.get(key);  
//	                    System.out.println(key+" : "+value);
	                    // 得到property对应的setter方法  
	                    Method setter = property.getWriteMethod();  
	                    setter.invoke(obj, value);  
	                }  
	  
	            }  
	  
	        } catch (Exception e) {  
	            System.out.println("transMap2Bean Error " + e);  
	            throw e;
	        }  
	  
	        return;  
	  
	    } 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
