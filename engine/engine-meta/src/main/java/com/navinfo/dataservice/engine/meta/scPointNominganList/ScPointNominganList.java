package com.navinfo.dataservice.engine.meta.scPointNominganList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

import com.navinfo.dataservice.api.metadata.model.ScSensitiveWordsObj;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;

public class ScPointNominganList {
	
	private List<String> pidNameList= new ArrayList<String>();

	private static class SingletonHolder {
		private static final ScPointNominganList INSTANCE = new ScPointNominganList();
	}

	public static final ScPointNominganList getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/**
	 * select pid,name from sc_point_nomingan_list
	 * @return List<String>: pid|name 所拼字符串列表
	 * @throws Exception
	 */
	public List<String> scPointNominganListPidNameList() throws Exception{
		if (pidNameList==null||pidNameList.isEmpty()) {
				synchronized (this) {
					if (pidNameList==null||pidNameList.isEmpty()) {
						try {
							String sql = "select pid,name from sc_point_nomingan_list";
								
							PreparedStatement pstmt = null;
							ResultSet rs = null;
							Connection conn = null;
							try {
								conn = DBConnector.getInstance().getMetaConnection();
								pstmt = conn.prepareStatement(sql);
								rs = pstmt.executeQuery();
								while (rs.next()) {
									String pid=rs.getString("pid");
									String name=rs.getString("name");
									pidNameList.add(pid+"|"+name);} 
							} catch (Exception e) {
								throw new Exception(e);
							} finally {
								DbUtils.closeQuietly(conn, pstmt, rs);
							}
						} catch (Exception e) {
							throw new SQLException("加载pidNameList失败："+ e.getMessage(), e);
						}
					}
				}
			}
			return pidNameList;
	}
}
