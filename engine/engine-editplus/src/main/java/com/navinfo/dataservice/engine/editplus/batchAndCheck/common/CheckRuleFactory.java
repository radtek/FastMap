package com.navinfo.dataservice.engine.editplus.batchAndCheck.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

import com.navinfo.dataservice.commons.database.MultiDataSourceFactory;

public class CheckRuleFactory {

	public CheckRuleFactory() {
		// TODO Auto-generated constructor stub
	}
	
	private Map<String, List<String>> operationMap = new HashMap<String, List<String>>();

	private static class SingletonHolder {
		private static final CheckRuleFactory INSTANCE = new CheckRuleFactory();
	}

	public static final CheckRuleFactory getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public List<String> loadByOperationName(String operationName) throws Exception {
		if (!operationMap.containsKey(operationName)) {
			synchronized (this) {
				if (!operationMap.containsKey(operationName)) {
					try {
						String sql="SELECT OPERATION_CODE, CHECK_ID FROM CHECK_OPERATION_PLUS WHERE OPERATION_CODE=?";
						PreparedStatement pstmt = null;
						ResultSet rs = null;
						Connection conn = null;
						try {
							conn = MultiDataSourceFactory.getInstance().getSysDataSource().getConnection();
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1,operationName);
							rs = pstmt.executeQuery();
							List<String> ruleList=new ArrayList<String>();
							while (rs.next()) {
								ruleList.add(rs.getString("CHECK_ID"));		
							} 
							operationMap.put(operationName, ruleList);
						} catch (Exception e) {
							throw new Exception(e);
						} finally {
							DbUtils.commitAndCloseQuietly(conn);
							DbUtils.closeQuietly(conn, pstmt, rs);
						}
					} catch (Exception e) {
						throw new SQLException("获取检查规则"+operationName+"失败："
								+ e.getMessage(), e);
					}
				}
			}
		}
		return operationMap.get(operationName);
	}

}
