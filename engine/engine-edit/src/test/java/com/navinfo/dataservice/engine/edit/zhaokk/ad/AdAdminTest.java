package com.navinfo.dataservice.engine.edit.zhaokk.ad;

import org.junit.Before;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.util.ResponseUtils;
import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.ObjLevel;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.engine.edit.InitApplication;
import com.navinfo.dataservice.engine.edit.operation.Transaction;
import com.navinfo.dataservice.engine.edit.search.SearchProcess;

import net.sf.json.JSONObject;

public class AdAdminTest extends InitApplication{
	
	@Override
	@Before
	public void init() {
		initContext();
	}
	
	public void testAdd() {
		String parameter = "{\"command\":\"CREATE\",\"type\":\"ADADMIN\",\"projectId\":11,\"data\":{\"longitude\":116.39552235603331,\"latitude\":39.90676527744907,\"linkPid\":625962}}";
		Transaction t = new Transaction(parameter);
		try {
			String msg = t.run();
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testDelete()
	{
		String parameter = "{\"command\":\"DELETE\",\"type\":\"ADADMIN\",\"projectId\":11,\"objId\":100000138}";
		Transaction t = new Transaction(parameter);
		try {
			String msg = t.run();
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testMove()
	{
		String parameter = "{\"command\":\"MOVE\",\"type\":\"ADADMIN\",\"projectId\":11,\"objId\":100000136,\"data\":{\"longitude\":116.39932036399843,\"latitude\":39.9071109355894,\"linkPid\":19609778}}";
		Transaction t = new Transaction(parameter);
		try {
			String msg = t.run();
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testUpdateAttr()
	{
		String paratmeter = "{\"command\":\"UPDATE\",\"type\":\"ADADMIN\",\"projectId\":11,\"data\":{\"names\":[{\"regionId\":100000136,\"nameGroupId\":1,\"langCode\":\"CHI\",\"nameClass\":1,\"name\":\"测试\",\"phonetic\":\"Ce Shi\",\"srcFlag\":0,\"pid\":100000136,\"objStatus\":\"INSERT\"}],\"pid\":100000136}}";
		Transaction t = new Transaction(paratmeter);
		try {
			String msg = t.run();
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testUpdateAdadmin() {
		String parameter = "{\"command\":\"UPDATE\",\"type\":\"ADADMIN\",\"projectId\":11,\"data\":{\"population\":2,\"pid\":3538,\"objStatus\":\"UPDATE\"}}";
		try {
			Transaction t = new Transaction(parameter);

			String msg = t.run();

			String log = t.getLogs();

			JSONObject json = new JSONObject();

			json.put("result", msg);

			json.put("log", log);

			json.put("check", t.getCheckLog());

			json.put("pid", t.getPid());

			System.out.println(ResponseUtils.assembleRegularResult(json));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testSearch()
	{
		String paratmeter = "{\"projectId\":11,\"type\":\"ADADMIN\",\"pid\":100000137}";
		try {
			
			JSONObject jsonReq = JSONObject.fromObject(paratmeter);

			String objType = jsonReq.getString("type");

			int projectId = jsonReq.getInt("projectId");
			
			int pid = jsonReq.getInt("pid");
			
			SearchProcess p = new SearchProcess(DBConnector.getInstance().getConnectionById(projectId));
			
			IObj obj = p.searchDataByPid(ObjType.valueOf(objType), pid);
			
			System.out.println(ResponseUtils.assembleRegularResult(obj
									.Serialize(ObjLevel.FULL)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testUpdatTree()
	{
		String parameter = "{\"command\":\"UPDATE\",\"type\":\"ADADMINGROUP\",\"projectId\":11,\"data\":{\"groupTree\":{\"regionId\":1273,\"name\":\"中国大陆\",\"group\":{\"groupId\":248,\"regionIdUp\":1273,\"rowId\":\"2D71EFCB1966DCE7E050A8C083040693\"},\"children\":[{\"regionId\":163,\"name\":\"北京市\",\"group\":{\"groupId\":40,\"regionIdUp\":163,\"rowId\":\"2D71EFCB16D7DCE7E050A8C083040693\"},\"part\":{\"groupId\":248,\"regionIdDown\":163,\"rowId\":\"2D71EFCB56BEDCE7E050A8C083040693\"},\"children\":[{\"regionId\":580,\"name\":\"北京市\",\"group\":{\"groupId\":114,\"regionIdUp\":580,\"rowId\":\"2D71EFCB1711DCE7E050A8C083040693\"},\"part\":{\"groupId\":40,\"regionIdDown\":580,\"rowId\":\"2D71EFCB642CDCE7E050A8C083040693\"},\"children\":[{\"regionId\":387274,\"name\":\"东直门\",\"group\":null,\"part\":{\"groupId\":114,\"rowId\":null,\"objType\":\"insert\"},\"children\":[]}]}]}]}}}";
		Transaction t = new Transaction(parameter);
		try {
			String msg = t.run();
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//new AdAdminTest().testAdd();
		testUpdatTree();
	}
}
