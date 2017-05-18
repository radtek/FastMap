package com.navinfo.dataservice.control.row.poiEditStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.navinfo.dataservice.dao.plus.editman.PoiEditStatus;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.control.row.query.PoiQuery;

import net.sf.json.JSONObject;

/** 
 * @ClassName: PoiEditStatusTest
 * @author songdongyan
 * @date 2016年11月24日
 * @Description: PoiEditStatusTest.java
 */
public class PoiEditStatusTest {

	@Before
	public void before() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "dubbo-consumer-datahub-test.xml" });
		context.start();
		new ApplicationContextUtil().setApplicationContext(context);
	}
	
	@Test
	public void test0() throws Exception{
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.4.61:1521/orcl",
				"fm_regiondb_sp6_d_1", "fm_regiondb_sp6_d_1");
		Map<Long,String> map = new HashMap<Long,String>();
		map.put((long) 1, "001000020000");//情报
		map.put((long) 2, "001000030000");//腾讯
		map.put((long) 3, "001000030001");//搜狗
		map.put((long) 4, "001000030002");//百度
		map.put((long) 5, "001000030003");//天地图
		map.put((long) 6, "001000030003");//POI挖掘
		map.put((long) 7, "001000030004");//POI挖掘
		
//		PoiEditStatus.tagMultiSrcPoi(conn, map);
		
	}
	
	@Test
	public void test1() throws Exception{
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.4.61:1521/orcl",
				"fm_regiondb_sp6_d_1", "fm_regiondb_sp6_d_1");
		Map<Integer,Collection<Long>> pids = new HashMap<Integer,Collection<Long>>();
		Collection<Long> insertList = new ArrayList<Long>();
		insertList.add((long) 1);
		insertList.add((long) 2);
		insertList.add((long) 3);

		Collection<Long> pids2 = PoiEditStatus.pidFilterByEditStatus(conn,insertList,3);

		System.out.println("ok");
	}
	@Test
	public void test2() throws Exception{
		String parameter= "{\"dbId\":17,\"subtaskId\":\"30\",\"type\":2,\"pageNum\":1,\"pageSize\":100}";
		PoiQuery aa= new PoiQuery();
		JSONObject result = aa.getPoiList(parameter);
		System.out.println("SIZE:"+result.size());
		System.out.println("result");
	}
}
