package com.navinfo.dataservice.engine.edit.zhangyuntao.rd;

import org.junit.Test;

import com.navinfo.dataservice.engine.edit.InitApplication;
import com.navinfo.dataservice.engine.edit.zhangyuntao.eleceye.TestUtil;

/**
 * @Title: RdLinkTest.java
 * @Description: TODO
 * @author zhangyt
 * @date: 2016年8月3日 上午10:32:29
 * @version: v1.0
 */
public class RdLinkTest extends InitApplication {

	public RdLinkTest() {
	}

	@Override
	public void init() {
		super.initContext();
	}

	@Test
	public void testDelete() {
		String parameter = "{\"command\":\"DELETE\",\"dbId\":42,\"type\":\"RDLINK\",\"objId\":100008436}";
		TestUtil.run(parameter);
	}

	@Test
	public void update() {
		String parameter = "{\"command\":\"BREAK\",\"dbId\":42,\"objId\":100008435,\"data\":{\"longitude\":116.4702206995429,\"latitude\":40.08258242178863},\"type\":\"RDLINK\"}";
		TestUtil.run(parameter);
	}

	@Test
	public void repair() {
		String parameter = "{\"command\":\"REPAIR\",\"dbId\":42,\"objId\":100008849,\"data\":{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46721,40.083],[116.46730363368988,40.082890151613405],[116.46738,40.08272]]},\"interLinks\":[],\"interNodes\":[]},\"type\":\"RDLINK\"}";
//		parameter = "{\"command\":\"REPAIR\",\"dbId\":42,\"objId\":100008881,\"data\":{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46375,40.08197],[116.46422982215881,40.08203232873719],[116.46456,40.08214]]},\"interLinks\":[],\"interNodes\":[]},\"type\":\"RDLINK\"}";
		parameter = "{\"command\":\"REPAIR\",\"dbId\":42,\"objId\":100008888,\"data\":{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46471,40.08145],[116.46497547626494,40.081500801483934],[116.46513,40.0816]]},\"interLinks\":[],\"interNodes\":[]},\"type\":\"RDLINK\"}";
		TestUtil.run(parameter);
	}

	@Test
	public void create() {
		String parameter = "{\"command\":\"CREATE\",\"dbId\":42,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46699,40.08309],[116.46714,40.08249]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
		parameter = "{\"command\":\"CREATE\",\"dbId\":42,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46726,40.08326],[116.46621,40.0831]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
		parameter = "{\"command\":\"CREATE\",\"dbId\":42,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46726,40.08326],[116.46661,40.083]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
		parameter = "{\"command\":\"CREATE\",\"dbId\":42,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46377652883528,40.08195639652649],[116.46438270807268,40.082114417518376]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
		parameter = "{\"command\":\"CREATE\",\"dbId\":42,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46462410688399,40.08142281644134],[116.4649111032486,40.08155210738431]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
		TestUtil.run(parameter);
	}
}
