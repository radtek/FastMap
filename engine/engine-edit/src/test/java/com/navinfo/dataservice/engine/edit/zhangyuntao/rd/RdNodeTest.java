package com.navinfo.dataservice.engine.edit.zhangyuntao.rd;

import org.junit.Test;

import com.navinfo.dataservice.engine.edit.InitApplication;
import com.navinfo.dataservice.engine.edit.zhangyuntao.eleceye.TestUtil;

/**
 * @Title: RdNodeTest.java
 * @Description: TODO
 * @author zhangyt
 * @date: 2016年8月3日 上午9:43:51
 * @version: v1.0
 */
public class RdNodeTest extends InitApplication {

	public RdNodeTest() {
	}

	@Override
	public void init() {
		super.initContext();
	}

	@Test
	public void testBreak() {
		String parameter = "{\"command\":\"BREAK\",\"dbId\":42,\"objId\":100008261,\"data\":{\"longitude\":116.50592938748717,\"latitude\":40.14143148683055},\"type\":\"RDLINK\"}";
		TestUtil.run(parameter);
	}
	
	@Test
	public void testDelete(){
		String parameter = "{\"command\":\"DELETE\",\"dbId\":42,\"type\":\"RDNODE\",\"objId\":100025259}";
		TestUtil.run(parameter);
	}
	
	@Test
	public void testMove(){
		String parameter = "{\"command\":\"MOVE\",\"dbId\":42,\"objId\":100025302,\"data\":{\"longitude\":116.50697350502014,\"latitude\":40.141557623890094},\"type\":\"RDNODE\"}";
		parameter = "{\"command\":\"MOVE\",\"dbId\":42,\"objId\":100025378,\"data\":{\"longitude\":116.50605275367737,\"latitude\":40.142110160100965},\"type\":\"RDNODE\"}";
		TestUtil.run(parameter);
	}
}
