package com.navinfo.dataservice.engine.man;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.navinfo.dataservice.engine.man.produce.ProduceService;

public class ProduceTest extends InitApplication{
	
	@Test
	public void  testGetProduceProgram() throws Exception {
		List<Map<String, Object>> result = ProduceService.getInstance().getProduceProgram();
		System.out.println("end");
	}
	
	@Test
	public void  testDetail() throws Exception {
		Map<String, Object> result = ProduceService.getInstance().queryDetail(41);
		System.out.println(result);
	}

	@Override
	@Before
	public void init() {
		initContext();
	}
}
