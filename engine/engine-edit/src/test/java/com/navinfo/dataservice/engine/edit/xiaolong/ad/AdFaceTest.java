package com.navinfo.dataservice.engine.edit.xiaolong.ad;


import org.apache.log4j.Logger;
import org.junit.Before;

import com.navinfo.dataservice.engine.edit.edit.operation.Transaction;
import com.navinfo.dataservice.engine.edit.xiaolong.InitApplication;

public class AdFaceTest extends InitApplication{
	
	@Override
	@Before
	public void init() {
		initContext();
	}
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	public  void createFaceTest() {
		String parameter = "{\"command\":\"CREATE\",\"type\":\"ADFACE\",\"projectId\":11," +
				"\"data\":{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.30551,39.96853],[ 116.29007,39.95719],[116.30732,39.95042],[116.30551,39.96853]]}}}";
		log.info(parameter);
		Transaction t = new Transaction(parameter);;
		try {
			String msg = t.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) throws Exception{
		new AdFaceTest().createFaceTest();
		
	}
}
