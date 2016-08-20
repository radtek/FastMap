package com.navinfo.dataservice.dao.glm.selector.poi.deep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;

public class IxPoiDeepStatusSelector extends AbstractSelector {
	
	private Connection conn;

	public IxPoiDeepStatusSelector(Connection conn) {
		super(conn);
		this.conn = conn;
	}
	
	/**
	 * 查询可申请的rowId
	 * @param taskId
	 * @param first_work_item
	 * @return
	 * @throws Exception
	 */
	public List<String> getRowIdByTaskId(int taskId,String firstWorkItem) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT s.row_id");
		sb.append(" FROM poi_deep_status s,poi_deep_work_item_conf w");
		sb.append(" WHERE s.work_item_id=w.work_item_id");
		sb.append(" AND s.handler=0");
		sb.append(" AND s.task_id=:1");
		sb.append(" AND w.first_work_item=:2");
		
		if (firstWorkItem.equals("poi_englishname")) {
			sb.append(" AND s.work_item_id != 'FM-YW-20-017'");
			sb.append(" AND s.row_id not in (SELECT d.row_id FROM poi_deep_status d,poi_deep_work_item_conf c WHERE");
			sb.append(" d.work_item_id=c.work_item_id AND c.first_work_item='poi_name'");
			sb.append(" AND d.first_work_status != 3)");
		} else if (firstWorkItem.equals("poi_englishaddress")) {
			sb.append(" AND s.row_id not in (SELECT d.row_id FROM poi_deep_status d,poi_deep_work_item_conf c WHERE");
			sb.append(" d.work_item_id=c.work_item_id AND c.first_work_item='poi_address'");
			sb.append(" AND d.first_work_status != 3)");
		}
		
		
		PreparedStatement pstmt = null;

		ResultSet resultSet = null;
		
		try {
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setInt(1, taskId);
			
			pstmt.setString(2, firstWorkItem);

			resultSet = pstmt.executeQuery();
			
			int count = 0;
			
			List<String> rowIdList = new ArrayList<String>();
			
			while (resultSet.next()) {
				rowIdList.add(resultSet.getString("row_id"));
				count ++;
				if (count == 100) {
					break;
				}
			}
			
			return rowIdList;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt); 
		}
	}
	
	/**
	 * 查询作业员名下已申请未提交的数据量
	 * @param firstWorkItem
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public int queryHandlerCount(String firstWorkItem,long userId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(1) num");
		sb.append(" FROM poi_deep_status s,poi_deep_work_item_conf w");
		sb.append(" WHERE s.work_item_id=w.work_item_id");
		sb.append(" AND s.handler=:1");
		sb.append(" AND w.first_work_item=:2");
		sb.append(" AND s.first_work_status != 3");
		
		PreparedStatement pstmt = null;

		ResultSet resultSet = null;
		
		try {
			
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setLong(1, userId);
			
			pstmt.setString(2, firstWorkItem);

			resultSet = pstmt.executeQuery();
			
			int count = 0;
			
			if (resultSet.next()) {
				count= resultSet.getInt("num");
			}
			
			return count;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt); 
		}
	}
	
	/**
	 * 查詢当前poi已打作业标记
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public List<String> queryClassifyByRowid(Object rowId,Object taskId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT work_item_id,handler FROM poi_deep_status s where s.row_id=:1 and s.first_work_status=1 and s.task_id=:2 ");
		
		PreparedStatement pstmt = null;

		ResultSet resultSet = null;
		
		List<String> workItemList=new ArrayList<String>();
		
		try {
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setString(1, (String) rowId);
			pstmt.setInt(2, (int) taskId);
			
			resultSet = pstmt.executeQuery();
			
			if (resultSet.next()) {
				workItemList.add(resultSet.getString("work_item_id"));
			}
			
			return workItemList;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt); 
		}
		
	}
	
	

}
