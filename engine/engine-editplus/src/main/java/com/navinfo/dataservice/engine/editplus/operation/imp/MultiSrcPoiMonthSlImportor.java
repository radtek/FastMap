package com.navinfo.dataservice.engine.editplus.operation.imp;

import java.sql.Connection;

import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.operation.AbstractCommand;
import com.navinfo.dataservice.dao.plus.operation.AbstractOperation;
import com.navinfo.dataservice.dao.plus.operation.OperationResult;

import net.sf.json.JSONObject;

/** 
 * @ClassName: MultiSrcPoiMonthSlImportor
 * @author xiaoxiaowen4127
 * @date 2016年11月17日
 * @Description: MultiSrcPoiMonthSlImportor.java
 */
public class MultiSrcPoiMonthSlImportor extends AbstractOperation {

	
	/**
	 * @param conn
	 * @param name
	 * @param preResult
	 */
	public MultiSrcPoiMonthSlImportor(Connection conn, OperationResult preResult) {
		super(conn, preResult);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void operate(AbstractCommand cmd) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MultiSrcPoiMonthSlImportor";
	}

}
