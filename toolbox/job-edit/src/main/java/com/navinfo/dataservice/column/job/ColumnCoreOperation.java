package com.navinfo.dataservice.column.job;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.api.man.model.Subtask;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.dao.check.NiValExceptionSelector;
import com.navinfo.dataservice.dao.glm.selector.poi.deep.IxPoiColumnStatusSelector;
import com.navinfo.navicommons.database.sql.DBUtils;

import net.sf.json.JSONObject;

public class ColumnCoreOperation {

	protected static Logger log = LoggerRepos.getLogger(ColumnCoreOperation.class);

	/**
	 * 实现逻辑 1) 取数据检查结果 2) 检查结果和poi_column_status里已记录的work_item_id做差分比较 3)
	 * 若同一一级项或者二级项下，①已记录了该检查项，则不做处理；②没记录则新增一条记录；③若检查结果没有该规则号，classifyRules中有，
	 * poi_column_status表中记录了该规则号，则从poi_column_status表删除该记录
	 * 
	 * @param mapParams
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void runClassify(HashMap mapParams, Connection conn, int taskId) throws Exception {
		try {
			String[] strCkRules = ((String) mapParams.get("ckRules")).split(",");
			String[] strClassifyRules = ((String) mapParams.get("classifyRules")).split(",");
			int userId = (Integer) mapParams.get("userId");
			List pidList = (List) mapParams.get("pids"); // 每条数据需包含pid
			Map<Integer,JSONObject> qcFlag =(Map<Integer,JSONObject>) mapParams.get("qcFlag");
			List ckRules = new ArrayList();
			for(int i=0;i<strCkRules.length;i++){
				ckRules.add(strCkRules[i]);
			}
			List classifyRules = new ArrayList();
			for(int i=0;i<strClassifyRules.length;i++){
				classifyRules.add(strClassifyRules[i]);
			}
			for (int i = 0; i < pidList.size(); i++) {
				int pid = (Integer) pidList.get(i);
				// 根据数据取检查结果
				NiValExceptionSelector checkSelector = new NiValExceptionSelector(conn);
				List checkResultList = checkSelector.loadByPid(pid, ckRules);
				IxPoiColumnStatusSelector columnStatusSelector = new IxPoiColumnStatusSelector(conn);

				// 取poi_column_status中打标记结果
				List existClassifyList = columnStatusSelector.queryClassifyByPid(pid,classifyRules);
				
				int existQcFlag =0;
				int existComHandler = 0;
				if(qcFlag.containsKey(pid)){
					JSONObject data=qcFlag.get(pid);
					existQcFlag = data.getInt("qc_flag");
					existComHandler = data.getInt("common_handler");
				}
				
				// poi_deep_status不存在的作业项插入,存在的更新
				checkResultList.retainAll(classifyRules);
				if (checkResultList.size()>0) {
					insertWorkItem(checkResultList, conn, pid, userId, taskId,existQcFlag,existComHandler);
				}

				// 重分类回退，本次要重分类classifyRules,检查结果中没有，若poi_deep_status存在,需从poi_deep_status中删掉
				existClassifyList.removeAll(checkResultList);
				deleteWorkItem(existClassifyList, conn, pid);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 往poi_column_status表插入作业标记信息
	 * 
	 * @param checkResultList
	 * @param conn
	 * @param pid
	 * @throws Exception
	 */
	public void insertWorkItem(List<String> checkResultList, Connection conn, int pid,int userId,int taskId,int existQcFlag,int existComHandler) throws Exception {
		PreparedStatement pstmt = null;

		try {
			
			for (String workItem : checkResultList) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("MERGE INTO poi_column_status T1 ");
				sb.append(" USING (SELECT "+userId+" as b," + taskId + " as c,'" + workItem
						+ "' as d," + " sysdate as e," + pid + " as f " + "  FROM dual) T2 ");
				sb.append(" ON ( T1.pid=T2.f and T1.work_item_id = T2.d) ");
				sb.append(" WHEN MATCHED THEN ");
				sb.append(" UPDATE SET T1.handler = T2.b,T1.task_id= T2.c,T1.first_work_status = 1,T1.second_work_status = 1,T1.apply_date = T2.e,T1.qc_flag="+existQcFlag+",T1.common_handler="+existComHandler+" ");
				sb.append(" WHEN NOT MATCHED THEN ");
				sb.append(" INSERT (T1.pid,T1.work_item_id,T1.first_work_status,T1.second_work_status,T1.handler,T1.task_id,T1.apply_date,T1.qc_flag,T1.common_handler) VALUES"
						+ " (T2.f,T2.d,1,1,T2.b,T2.c,T2.e,"+existQcFlag+","+existComHandler+")");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.addBatch();
			}

			pstmt.executeBatch();
			pstmt.clearBatch();
		} catch (SQLException e) {
			DbUtils.rollbackAndCloseQuietly(conn);
			log.error(e.getMessage(), e);
			throw new SQLException("往poi_column_status表插入作业标记信息出错，原因：" + e.getMessage(), e);
		}

	}

	/**
	 * 从poi_column_status表删除作业标记信息
	 * 
	 * @param classifyRules
	 * @param conn
	 * @param pid
	 * @throws Exception
	 */
	public void deleteWorkItem(List<String> classifyRules, Connection conn, int pid) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from poi_column_status where pid=? and work_item_id=? ");

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sb.toString());
			for (String workItem : classifyRules) {
				pstmt.setInt(1, pid);
				pstmt.setString(2, workItem);
				pstmt.addBatch();
			}

			pstmt.executeBatch();
			pstmt.clearBatch();

		} catch (SQLException e) {
			DbUtils.rollbackAndCloseQuietly(conn);
			log.error(e.getMessage(), e);
			throw new SQLException("从poi_column_status表删除作业标记信息出错，原因：" + e.getMessage(), e);
		}

	}
	/**
	 * 从poi_column_status表获取重分类前该POI的qc_flag值
	 * 
	 * @param pidList
	 * @param userId
	 * @param conn
	 * @param comSubTaskId
	 * @throws Exception
	 */
	public Map<Integer,JSONObject> getColumnDataQcFlag(List<Integer> pidList,int userId,Connection conn,int comSubTaskId,int isQuality) throws Exception {
		Map<Integer,JSONObject> result = new HashMap<Integer,JSONObject>();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ps.pid,ps.qc_flag,ps.common_handler FROM POI_COLUMN_STATUS PS ");
		sql.append(" WHERE PS.PID IN ("+StringUtils.join(pidList, ",")+") ");
		sql.append("	AND PS.HANDLER = "+userId);
		if(isQuality==0){
			sql.append("	AND PS.COMMON_HANDLER = "+userId);
		}else if(isQuality==1){
			sql.append("	AND PS.COMMON_HANDLER <> "+userId);
		}
		sql.append("	AND PS.task_id = "+comSubTaskId);

		try {
			pstmt = conn.prepareStatement(sql.toString());
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				JSONObject data=new JSONObject();
				data.put("qc_flag", resultSet.getInt("qc_flag"));
				data.put("common_handler", resultSet.getInt("common_handler"));
				result.put(resultSet.getInt("pid"),data);
			} 
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
		return result;
	}

	public static void main(String[] args) throws Exception {

		/**
		 * Connection conn = DriverManager.getConnection(
		 * "jdbc:oracle:thin:@192.168.4.61:1521/orcl", "fm_regiondb_sp6_m_1",
		 * "fm_regiondb_sp6_m_1");
		 * 
		 * ColumnCoreOperation columnCoreOperation = new ColumnCoreOperation();
		 * HashMap<String, Object> classifyMap = new HashMap<String, Object>();
		 * classifyMap.put("ckRules", "FM-A04-04,FM-A09-01");
		 * classifyMap.put("classifyRules", "FM-A04-04"); List pidList = new
		 * ArrayList(); pidList.add(123); classifyMap.put("pids", pidList);
		 * columnCoreOperation.runClassify(classifyMap, conn);
		 **/

	}

}
