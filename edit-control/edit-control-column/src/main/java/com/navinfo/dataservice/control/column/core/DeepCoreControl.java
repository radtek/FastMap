package com.navinfo.dataservice.control.column.core;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.navinfo.dataservice.api.edit.upload.EditJson;
import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.api.man.model.Subtask;
import com.navinfo.dataservice.api.man.model.UserInfo;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.database.ConnectionUtil;
import com.navinfo.dataservice.commons.exception.DataNotChangeException;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.util.DateUtils;
import com.navinfo.dataservice.dao.glm.selector.poi.deep.IxPoiColumnStatusSelector;
import com.navinfo.dataservice.dao.glm.selector.poi.deep.IxPoiDeepStatusSelector;
import com.navinfo.dataservice.dao.glm.selector.poi.index.IxPoiSelector;
import com.navinfo.dataservice.dao.plus.log.LogDetail;
import com.navinfo.dataservice.dao.plus.log.ObjHisLogParser;
import com.navinfo.dataservice.dao.plus.log.PoiLogDetailStat;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.operation.OperationResult;
import com.navinfo.dataservice.dao.plus.operation.OperationSegment;
import com.navinfo.dataservice.dao.plus.selector.ObjSelector;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.Batch;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.BatchCommand;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.check.Check;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.check.CheckCommand;
import com.navinfo.dataservice.engine.editplus.operation.imp.DefaultObjImportorCommand;
import com.navinfo.dataservice.engine.editplus.operation.imp.PoiDeepObjImportor;
import com.navinfo.navicommons.database.QueryRunner;
import com.navinfo.navicommons.database.sql.DBUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DeepCoreControl {
	private static final Logger logger = Logger.getLogger(DeepCoreControl.class);

	/**
	 * 深度信息库存统计
	 * 
	 * @param subtask
	 * @param dbId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getLogCount(Subtask subtask,int dbId) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(1) AS num,p.type");
		sb.append(" FROM ix_poi i,poi_deep_status p");
		sb.append(" WHERE sdo_within_distance(i.geometry, sdo_geometry(    :1  , 8307), 'mask=anyinteract') = 'TRUE'");
		sb.append(" AND i.u_record!=2");
		sb.append(" AND i.row_id=p.row_id ");
		sb.append(" AND p.status=1");
		sb.append(" AND p.handler is null");
		sb.append(" GROUP BY p.type");
		
		Connection conn = null;
		
		PreparedStatement pstmt = null;
		
		ResultSet resultSet = null;
		
		logger.debug("sql:"+sb);
		
		logger.debug("wkt:"+subtask.getGeometry());
		
		try {
			conn = DBConnector.getInstance().getConnectionById(dbId);
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, subtask.getGeometry());
			
			resultSet = pstmt.executeQuery();
			
			JSONObject resutlObj = new JSONObject();
			
			while (resultSet.next()) {
				if (resultSet.getInt("type")==1) {
					resutlObj.put("detail", resultSet.getInt("num"));
				} else if (resultSet.getInt("type")==2) {
					resutlObj.put("parking", resultSet.getInt("num"));
				} else if (resultSet.getInt("type")==3) {
					resutlObj.put("carrental", resultSet.getInt("num"));
				}
			}
			
			logger.debug("result:"+resutlObj);
			
			return resutlObj;
		}catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeConnection(conn);
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
	}
	
	/**
	 * 清理检查结果
	 * 
	 * @param pids
	 * @param conn
	 * @throws Exception
	 */
	public void cleanCheckResult(List<Integer> pids,Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		
		ResultSet resultSet = null;
		
		Clob pidClod = null;
		try {
			logger.debug("开始清理检查结果");
			String pois = StringUtils.join(pids, ",");
			pidClod = ConnectionUtil.createClob(conn);
			pidClod.setString(1, pois);
			String sql = "SELECT md5_code FROM ck_result_object WHERE table_name='IX_POI' AND pid in (select column_value from table(clob_to_table(?)))";
			pstmt = conn.prepareStatement(sql);
			pstmt.setClob(1, pidClod);
			resultSet = pstmt.executeQuery();
			List<String> md5List = new ArrayList<String>();
			while (resultSet.next()) {
				md5List.add(resultSet.getString("md5_code"));
			}
			cleanCheckException(md5List,conn);
			cleanCheckObj(md5List,conn);
			logger.debug("清理完成");
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
	}
	
	/**
	 * 删除ni_val_exception表 
	 * 
	 * @param md5List
	 * @param conn
	 * @throws Exception
	 */
	private void cleanCheckException (List<String> md5List,Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		
		Clob md5Clod = null;
		
		String sql = "DELETE FROM ni_val_exception WHERE md5_code in (select column_value from table(clob_to_table(?)))";
		try {
			logger.debug("清理ni_val_exception");
			logger.debug(md5List);
			logger.debug("sql:"+sql);
			String md5s = "";
			String tmep = "";
			for (int i=0;i<md5List.size();i++) {
				String md5Code = md5List.get(i);
				md5s += tmep;
				tmep = ",";
				md5s += md5Code;
			}
			md5Clod = ConnectionUtil.createClob(conn);
			md5Clod.setString(1, md5s);
			pstmt = conn.prepareStatement(sql);
			pstmt.setClob(1, md5Clod);
			pstmt.execute();
			logger.debug("ni_val_exception表清理完成");
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeStatement(pstmt);
		}
	}
	
	/**
	 * 删除ck_result_object表
	 * 
	 * @param md5List
	 * @param conn
	 * @throws Exception
	 */
	private void cleanCheckObj (List<String> md5List,Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		
		Clob md5Clod = null;
		
		String sql = "DELETE FROM ck_result_object WHERE md5_code in (select column_value from table(clob_to_table(?)))";
		try {
			logger.debug("清理ck_result_object");
			logger.debug(md5List);
			logger.debug("sql:"+sql);
			String md5s = "";
			String tmep = "";
			for (int i=0;i<md5List.size();i++) {
				String md5Code = md5List.get(i);
				md5s += tmep;
				tmep = ",";
				md5s += md5Code;
			}
			md5Clod = ConnectionUtil.createClob(conn);
			md5Clod.setString(1, md5s);
			pstmt = conn.prepareStatement(sql);
			pstmt.setClob(1, md5Clod);
			pstmt.execute();
			logger.debug("ck_result_object表清理完成");
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeStatement(pstmt);
		}
	}
	
	
	/**
	 * 清ni_val_exception_grid表
	 * @param md5List
	 * @param conn
	 * @throws Exception
	 */
	public void cleanExceptionGrid(List<String> md5List, Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		
		Clob md5Clod = null;
		
		String sql = "DELETE FROM ni_val_exception_grid WHERE md5_code in (select column_value from table(clob_to_table(?)))";
		try {
			logger.debug("清理ni_val_exception_grid");
			logger.debug(md5List);
			logger.debug("sql:"+sql);
			String md5s = "";
			String temp = "";
			for (int i=0;i<md5List.size();i++) {
				String md5Code = md5List.get(i);
				md5s += temp;
				temp = ",";
				md5s += md5Code;
			}
			md5Clod = ConnectionUtil.createClob(conn);
			md5Clod.setString(1, md5s);
			pstmt = conn.prepareStatement(sql);
			pstmt.setClob(1, md5Clod);
			pstmt.execute();
			logger.debug("ni_val_exception_grid表清理完成");
			
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}
	
    /**
     * @param parameter
     * @param userId
     * @return
     * @throws Exception
     * @Gaopr POI深度信息作业保存
     */
	public JSONObject save(String parameter, long userId) throws Exception {

        Connection conn = null;
        JSONObject result = null;
        
        List<Integer> pids = new ArrayList<Integer>();
        
        try {

            JSONObject json = JSONObject.fromObject(parameter);

            int dbId = json.getInt("dbId");
            int objId = json.getInt("objId");
            String secondWorkItem = json.getString("secondWorkItem");

            conn = DBConnector.getInstance().getConnectionById(dbId);

            JSONObject poiData = json.getJSONObject("data");
            
            pids.add(objId);
            //rowIdList = getRowIdsByPids(conn,pids);
            
            if (poiData.size() == 0) {
            	updateDeepStatus(pids, conn, 0,secondWorkItem);
                return result;
            }
            
            json.put("command", "UPDATE");
            
            PoiDeepObjImportor importor = new PoiDeepObjImportor(conn,null);
			EditJson editJson = new EditJson();
			editJson.addJsonPoi(json);
			DefaultObjImportorCommand command = new DefaultObjImportorCommand(editJson);
			importor.operate(command);
			importor.persistChangeLog(OperationSegment.SG_COLUMN, userId);

//            EditApiImpl editApiImpl = new EditApiImpl(conn);
//            editApiImpl.setToken(userId);
//            result = editApiImpl.runPoi(json);
            
			OperationResult operationResult=importor.getResult();

            //更新数据状态
            updateDeepStatus(pids, conn, 0,secondWorkItem);
           
            // 深度信息保存时去掉检查 zpp 2017.03.02
    		//获取后检查需要执行规则列表
//            List<String> checkList=getCheckRuleList(conn,secondWorkItem);
            
            //调用清理检查结果方法
//            cleanExByCkRule(conn, pids, checkList, "IX_POI");
            
    		//执行检查
//			CheckCommand checkCommand=new CheckCommand();		
//			checkCommand.setRuleIdList(checkList);
//			Check check=new Check(conn,operationResult);
//			check.operate(checkCommand);
            
            return result;
        } catch (DataNotChangeException e) {
            DbUtils.rollback(conn);
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            DbUtils.rollback(conn);
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            DbUtils.commitAndClose(conn);
        }
    }
    
    /**
     * @param parameter
     * @param userId
     * @return
     * @throws Exception
     * @Gaopr POI深度信息作业提交
     */
	@SuppressWarnings("static-access")
	public JSONObject release(String parameter, long userId) throws Exception {
		
		List<Integer> pidList = new ArrayList<Integer>();
        Connection conn = null;
        JSONObject result = new JSONObject();
        int sucReleaseTotal = 0;
        try {

            JSONObject json = JSONObject.fromObject(parameter);

            int dbId = json.getInt("dbId");
            int subtaskId = json.getInt("subtaskId");
            String secondWorkItem = json.getString("secondWorkItem");

            //Subtask subtask = apiService.queryBySubtaskId(subtaskId);
            conn = DBConnector.getInstance().getConnectionById(dbId);  
			
    		//获取后检查需要执行规则列表
			List<String> checkList=getCheckRuleList(conn,secondWorkItem);
            
			// 查询可提交数据
            IxPoiColumnStatusSelector ixPoiColumnStatusSelector = new IxPoiColumnStatusSelector(conn);
			pidList = ixPoiColumnStatusSelector.getpidsForRelease(subtaskId,2,userId, secondWorkItem);
			
			//调用清理检查结果方法
			cleanExByCkRule(dbId, pidList, checkList, "IX_POI");
			
			OperationResult operationResult=new OperationResult();
			
			List<Long> pids = new ArrayList<Long>();
			for (int pid:pidList) {
				pids.add((long)pid);
			}
			
			PoiLogDetailStat logDetail = new PoiLogDetailStat();
			Map<Long,List<LogDetail>> submitLogs = logDetail.loadByColEditStatus(conn, pids, userId, subtaskId, "poi_deep", secondWorkItem);
			List<BasicObj> objList = new ArrayList<BasicObj>();
			ObjHisLogParser logParser = new ObjHisLogParser();
			Set<String> tabNames = new HashSet<String>();
			tabNames.add("IX_POI_DETAIL");
			tabNames.add("IX_POI_CONTACT");
			tabNames.add("IX_POI_BUSINESSTIME");
			tabNames.add("IX_POI_PARKING");
			tabNames.add("IX_POI_CARRENTAL");
			for (int pid:pidList) {
				BasicObj obj=ObjSelector.selectByPid(conn, "IX_POI", tabNames,false, pid, false);
				if (submitLogs.containsKey(new Long((long)pid))) {
					logParser.parse(obj, submitLogs.get(new Long((long)pid)));
				}
				objList.add(obj);
			}
			
			operationResult.putAll(objList);
			CheckCommand checkCommand=new CheckCommand();
			checkCommand.setRuleIdList(checkList);
			Check check=new Check(conn,operationResult);
			check.operate(checkCommand);
			
			// pidList替换为无检查错误的pidList
			Map<String, Map<Long, Set<String>>> errorMap = check.getErrorPidMap();
			if (errorMap != null) {
				Map<Long, Set<String>> poiMap = errorMap.get("IX_POI");
				for (long pid:poiMap.keySet()) {
					Iterator <Integer> it = pidList.iterator();
					while (it.hasNext()) {
						if (it.next() == pid) {
							it.remove();
							break;
						}
					}
				}
			}
			
			sucReleaseTotal = pidList.size();
			
			// 修改poi_deep_status表作业项状态
			updateDeepStatus(pidList, conn, 1,secondWorkItem);
			
			result.put("sucReleaseTotal", sucReleaseTotal);
            return result;
        } catch (DataNotChangeException e) {
            DbUtils.rollback(conn);
            System.out.println(e);
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            DbUtils.rollback(conn);
            System.out.println(e);
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            DbUtils.commitAndClose(conn);
        }
    }
    
    /**
     * 深度信息更新配置表状态 
     * @param pids conn flag(0:保存 1:提交)
     * @throws Exception
     */
	public void updateDeepStatus(List<Integer> pidList,Connection conn,int flag,String secondWorkItem) throws Exception {
		StringBuilder sb = new StringBuilder();
		
        if (pidList.isEmpty()){
        	return;
        }
        if (flag==0) {
        	sb.append(" UPDATE poi_column_status T1 SET T1.SECOND_WORK_STATUS= 2  WHERE  T1.work_item_id IN (SELECT cf.work_item_id FROM POI_COLUMN_WORKITEM_CONF cf WHERE cf.second_work_item='"+secondWorkItem+"') AND T1.pid in (");
        } else {
        	sb.append(" UPDATE poi_column_status T1 SET T1.SECOND_WORK_STATUS= 3,T1.HANDLER=0  WHERE  T1.work_item_id IN (SELECT cf.work_item_id FROM POI_COLUMN_WORKITEM_CONF cf WHERE cf.second_work_item='"+secondWorkItem+"') AND T1.pid in (");
        }
		
		PreparedStatement pstmt = null;

		ResultSet resultSet = null;
		try {
			String temp="";
			for (int pid:pidList) {
				sb.append(temp);
				sb.append("'"+pid+"'");
				temp = ",";
			}
			sb.append(")");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}
    
    
	/**
	 * 深度信息申请数据
	 * @param taskId
	 * @param userId
	 * @param firstWorkItem
	 * @param secondWorkItem
	 * @return
	 * @throws Exception
	 */
	public int applyData(int taskId, long userId, String firstWorkItem, String secondWorkItem) throws Exception {
		int applyCount = 0;
		int hasApply = 0;
		
		Connection conn = null;
		
		// 默认为大陆数据
		int type = 1;
		try {
			ManApi apiService = (ManApi) ApplicationContextUtil.getBean("manApi");
			Subtask subtask = apiService.queryBySubtaskId(taskId);
			
			if (subtask == null) {
				throw new Exception("subtaskid未找到数据");
			}
			
			int dbId = subtask.getDbId();
			conn = DBConnector.getInstance().getConnectionById(dbId);
			
			int isQuality =subtask.getIsQuality();
			
			//查询当前作业员已占有数据量
			IxPoiColumnStatusSelector poiColumnSelector = new IxPoiColumnStatusSelector(conn);
			hasApply = poiColumnSelector.queryHandlerCount(firstWorkItem, secondWorkItem, userId, type, taskId,0);
			
			// 可申请数据的条数
			int canApply = 100 - hasApply;
			if (canApply == 0) {
				throw new Exception("该作业员名下已存在100条数据，不可继续申请");
			}
			
			JSONObject conditions=new JSONObject();
			
			//获取从状态表查询到能够申请数据的pids
			List<Integer> pids = poiColumnSelector.getApplyPids(subtask, firstWorkItem, secondWorkItem, type,0,conditions,userId);
			if (pids.size() == 0){
				//未查询到可以申请的数据
				return 0;
			}
			
			//实际申请到的数据pids
			List<Integer> applyDataPids = new ArrayList<Integer>();
			if (pids.size() >= canApply){
				applyDataPids = pids.subList(0, canApply);
			}else{
				//库里面查询出的数据量小于当前用户可申请的量，即锁定库中查询出的数据
				applyDataPids = pids;
			}
			
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			
			//数据加锁， 赋值handler，维护update_date,task_id
			applyCount += applyDataPids.size();
			List<String> workItemIds = poiColumnSelector.getWorkItemIds(firstWorkItem, secondWorkItem);
			poiColumnSelector.dataSetLock(applyDataPids, workItemIds, userId, taskId, timeStamp,0);
			
			OperationResult operationResult=new OperationResult();
			List<BasicObj> objList = new ArrayList<BasicObj>();
			for (int pid:applyDataPids) {
				BasicObj obj=ObjSelector.selectByPid(conn, "IX_POI", null,true, pid, false);
				objList.add(obj);
			}
			operationResult.putAll(objList);
			
			// 深度信息批处理 -- 作业前批
			List<String> batchRuleList = getDeepBatchRules(secondWorkItem);
			BatchCommand batchCommand=new BatchCommand();
			for (String rule:batchRuleList) {
				batchCommand.setRuleId(rule);
			}
			Batch batch=new Batch(conn,operationResult);
			batch.operate(batchCommand);
			batch.persistChangeLog(OperationSegment.SG_COLUMN, userId);
			
			//常规申请需要打质检标记
			if(isQuality==0){
				double sampleLevel =((double )apiService.queryQualityLevel((int) userId, secondWorkItem))/100.0;
				int ct=(int) Math.round(applyDataPids.size()*sampleLevel);
				if(ct!=0){
					applyDataPids = createRandomList(applyDataPids,ct);
					if(applyDataPids.size()>0){updateQCFlag(applyDataPids,conn,taskId,userId);}
				}
			}
			
			return applyCount;
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.commitAndClose(conn);
		}

	}
	
    /**
     * 从list中随机抽取元素 
     * @Title: createRandomList  
     * @param list 
     * @param n  
     * @return void   
     * @throws  
     */   
    private static List<Integer> createRandomList(List<Integer> list, int n) {  
        Map<Integer,String> map = Maps.newHashMap();
        List<Integer> listNew = Lists.newArrayList();
        if(list.size()<=n){  
            return list;  
        }else{  
            while(map.size()<n){  
                int random = (int) (Math.random() * list.size());  
                if (!map.containsKey(random)) {  
                    map.put(random, "");  
                    listNew.add(list.get(random));  
                }  
            }  
            return listNew;  
        }  
    }  
	
	/**
	 * 质检提交时调用，更新质检问题表状态
	 * @param rowIdList
	 * @param conn
	 * @throws Exception
	 */
	public void updateQCFlag(List<Integer> pidList,Connection conn,int taskId,long userId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE poi_column_status SET qc_flag=1 ");
		sb.append(" WHERE TASK_ID ="+taskId);
		sb.append(" AND PID IN ("+StringUtils.join(pidList, ",")+")");
		sb.append(" AND handler="+userId);
		sb.append(" AND first_work_status=1");
		sb.append(" AND second_work_status=1");
		
		PreparedStatement pstmt = null;
		try {
			
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.executeUpdate();
			
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	/**
	 * 获取深度信息批处理的规则
	 * @param secondWorkItem
	 * @return
	 * @throws Exception 
	 */
	public List<String> getDeepBatchRules(String secondWorkItem) throws Exception {
		List<String> rules = new ArrayList<String>();
		if ("deepDetail".equals(secondWorkItem)){
			// 通用
			rules.add("FM-BAT-20-195");
			rules.add("FM-BAT-20-196");
			rules.add("FM-BAT-TEMP-7");
		}else if ("deepParking".equals(secondWorkItem)){
			// 停车场
			rules.add("FM-BAT-20-198");
			rules.add("FM-BAT-TEMP-8");
			rules.add("FM-BAT-TEMP-9");
		}else if ("deepCarrental".equals(secondWorkItem)){
			// 汽车租赁
			rules.add("FM-BAT-20-197");
			rules.add("FM-BAT-TEMP-10");
			rules.add("FM-BAT-TEMP-11");
			rules.add("FM-BAT-TEMP-12");
		}
			return rules;
	}
	
	/**
	 * 根据rowIds 获取 pids
	 * @param conn
	 * @param rowIds
	 * @return
	 * @throws Exception 
	 */
	public List<Integer> getPidsByRowIds(Connection conn, List<String> rowIds) throws Exception{
		List<Integer> pids = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT pid FROM ix_poi WHERE row_id in (");
		String temp = "";
		for (String rowId:rowIds) {
			sb.append(temp);
			sb.append("'"+rowId+"'");
			temp = ",";
		}
		sb.append(")");
		
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			
			resultSet = pstmt.executeQuery();
			
			while (resultSet.next()) {
				pids.add(resultSet.getInt("pid"));
			}
			
			return pids;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}
	/**
	 * 根据pids 获取 RowIds
	 * @param conn
	 * @param rowIds
	 * @return
	 * @throws Exception 
	 */
	public List<String> getRowIdsByPids(Connection conn, List<Integer> pids) throws Exception{
		List<String> rowIds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT row_id FROM ix_poi WHERE pid in (");
		String temp = "";
		for (int pid:pids) {
			sb.append(temp);
			sb.append(pid);
			temp = ",";
		}
		sb.append(")");
		
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			
			resultSet = pstmt.executeQuery();
			
			while (resultSet.next()) {
				rowIds.add(resultSet.getString("row_id"));
			}
			
			return rowIds;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	/**
	 * 深度信息查询poi
	 * @param json
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public JSONObject queryPoi(JSONObject jsonReq, long userId) throws Exception{

		JSONObject result = new JSONObject();
		int subtaskId = jsonReq.getInt("subtaskId");
		int dbId = jsonReq.getInt("dbId");

		Connection conn = null;
		try {
			ManApi apiService = (ManApi) ApplicationContextUtil.getBean("manApi");
			Subtask subtask = apiService.queryBySubtaskId(subtaskId);
			
			if (subtask == null) {
				throw new Exception("subtaskid未找到数据");
			}
			
			conn = DBConnector.getInstance().getConnectionById(dbId);
			
			IxPoiDeepStatusSelector deepSelector = new IxPoiDeepStatusSelector(conn);
			result = deepSelector.loadDeepPoiByCondition(jsonReq, subtaskId, userId);
			
			return result;
		} catch(Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	
	/**
	 * 清检查结果，用于POI行编和月编
	 * 月编 ：
	 * jsonReq JSONObject
	 * pids：[123,123],ckRules:["rule1","rule2"],checkType:1 //0行编 1精编 
	 * @param jsonReq JSONObject
	 * @param jsonReq
	 * @param userId
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void cleanCheck(JSONObject jsonReq, long userId) throws Exception {
		int taskId = jsonReq.getInt("subtaskId");
		
		Connection conn = null;
		
		try {
			ManApi apiService = (ManApi) ApplicationContextUtil.getBean("manApi");
			Subtask subtask = apiService.queryBySubtaskId(taskId);
			
			int dbId = subtask.getDbId();
			
			conn = DBConnector.getInstance().getConnectionById(dbId);
			
			int checkType = jsonReq.getInt("checkType");
			
			List<Integer> pids = new ArrayList<Integer>();
			if (jsonReq.containsKey("pids")) {
				pids = jsonReq.getJSONArray("pids");
			}
			List<String> ckRules = new JSONArray();
			if (jsonReq.containsKey("ckRules")) {
				ckRules = jsonReq.getJSONArray("ckRules");
			}
			
			// POI行编
			if (checkType == 0){

				if (ckRules.size() == 0) {
					throw new Exception("检查规则checkType=0为行编时，ckRules不能为空");
				}
				if (pids.size() == 0) {
					IxPoiSelector poiSelector = new IxPoiSelector(conn);
					pids = poiSelector.getPidsBySubTask(subtask);
				}
			} 
			
			// POI精编
			if (checkType == 1) {
				
				IxPoiColumnStatusSelector columnSelector = new IxPoiColumnStatusSelector(conn);
				
				if (ckRules.size() == 0) {
					// 如果没有ckRules,则根据firstWorkItem和secondWorkItem从精编配置表POI_COLUMN_WORKITEM_CONF获取
					String firstWorkItem = new String();
					if (jsonReq.containsKey("firstWorkItem")){
						firstWorkItem = jsonReq.getString("firstWorkItem");
					}
					String secondWorkItem = new String();
					if (jsonReq.containsKey("secondWorkItem")) {
						secondWorkItem = jsonReq.getString("secondWorkItem");
					}
					if (StringUtils.isEmpty(firstWorkItem) || StringUtils.isEmpty(secondWorkItem)) {
						throw new Exception("检查规则checkType=1为精编，ckRules为空时，firstWorkItem和secondWorkItem不能为空");
					} else {
						// 根据一级项和二级项获取ckRules
						ckRules = columnSelector.getWorkItemIds(firstWorkItem, secondWorkItem);
					}
				}
				if (pids.size() == 0) {
					pids = columnSelector.getPids(taskId, userId);
				}
			}
			
			String objType = new String();
			if (checkType == 0 || checkType == 1) {
				objType = "IX_POI";
			}
			// 根据pids和ckRules获取md5List
			List<String> md5List = new ArrayList<String>();
			md5List = getMd5List(conn, pids, ckRules, objType);
			
			// 清ni_val_exception表
			cleanCheckException(md5List,conn);
			// 清ck_result_object表
			cleanCheckObj(md5List,conn);
			// 清ni_val_exception_grid表
			cleanExceptionGrid(md5List,conn);
			 
		} catch (DataNotChangeException e) {
            DbUtils.rollback(conn);
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            DbUtils.rollback(conn);
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            DbUtils.commitAndClose(conn);
        }
	}
	
	
	/**
	 * 根据pids和ckRules获取md5List
	 * @param conn
	 * @param pids
	 * @param ckRules
	 * @param objType
	 * @return
	 * @throws Exception
	 */
	public List<String> getMd5List(Connection conn, List<Integer> pids, List<String> ckRules, String objType) throws Exception {
		
		PreparedStatement pstmt = null;
		
		ResultSet resultSet = null;
		
		try {
			Clob pidClob = null;
			String pois = StringUtils.join(pids, ",");
			pidClob = ConnectionUtil.createClob(conn);
			pidClob.setString(1, pois);
			
			Clob ckRuleClob = null;
			String rules = "";
			String temp = "";
			for (int i=0;i<ckRules.size();i++) {
				String rule = ckRules.get(i);
				rules += temp;
				temp = ",";
				rules += rule;
			}
			ckRuleClob = ConnectionUtil.createClob(conn);
			ckRuleClob.setString(1, rules);
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT o.md5_code FROM ck_result_object o,ni_val_exception e ");
			sb.append(" WHERE o.md5_code=e.md5_code ");
			sb.append(" AND o.table_name=? ");
			sb.append(" AND o.pid in (select column_value from table(clob_to_table(?)))");
			sb.append(" AND e.ruleid in (select column_value from table(clob_to_table(?)))");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, objType);
			pstmt.setClob(2, pidClob);
			pstmt.setClob(3, ckRuleClob);
			
			resultSet = pstmt.executeQuery();
			
			List<String> md5List = new ArrayList<String>();
			
			while (resultSet.next()) {
				md5List.add(resultSet.getString("md5_code"));
			}
			return md5List;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	/**
	 * 根据规则号清理检查结果
	 * @param conn
	 * @param pids
	 * @param ckRules
	 * @param objType
	 * @throws Exception
	 */
	public void cleanExByCkRule(int dbId, List<Integer> pids, List<String> ckRules, String objType) throws Exception {
		Connection conn=null;
		try{
			conn=DBConnector.getInstance().getConnectionById(dbId);
			List<String> md5List = getMd5List(conn,pids,ckRules,objType);
			cleanCheckException(md5List,conn);
			cleanCheckObj(md5List,conn);
		}catch(Exception e){
			DbUtils.rollbackAndCloseQuietly(conn);
			throw e;
		}finally{
			DbUtils.commitAndCloseQuietly(conn);
		}
	}
	
	/**
	 * 查询深度信息检查项
	 * @param conn
	 * @param secondWorkItem
	 * @return
	 * @throws Exception
	 */
	private List<String> getCheckRuleList(Connection conn,String secondWorkItem) throws Exception{
		try{
			List<String> rules = new ArrayList<String>();
			
			String sql="SELECT DISTINCT WORK_ITEM_ID"
					+ "  FROM POI_COLUMN_WORKITEM_CONF C"
					+ " WHERE C.FIRST_WORK_ITEM = 'poi_deep'"
					+ "   AND CHECK_FLAG IN (2, 3)"
					+ "   AND C.SECOND_WORK_ITEM='" + secondWorkItem + "'";

			QueryRunner run=new QueryRunner();
			rules=run.query(conn, sql, new ResultSetHandler<List<String>>(){

				@Override
				public List<String> handle(ResultSet rs) throws SQLException {
					List<String> rules=new ArrayList<String>();
					while(rs.next()){
						rules.add(rs.getString("WORK_ITEM_ID"));
					}
					return rules;
				}});
			return rules;
		}catch(Exception e){
			DbUtils.rollbackAndCloseQuietly(conn);
			throw e;
		}
	}

	/**
	 * 深度信息抽取数据
	 * @param taskId
	 * @param userId
	 * @param firstWorkItem
	 * @param secondWorkItem
	 * @return
	 * @throws Exception
	 */
	public int qcExtractData(int taskId, long userId, String firstWorkItem, String secondWorkItem) throws Exception {
		Connection conn = null;
		
		// 默认为大陆数据
		int type = 1;
		try {
			ManApi apiService = (ManApi) ApplicationContextUtil.getBean("manApi");
			Subtask subtask = apiService.queryBySubtaskId(taskId);
			
			if (subtask == null) {
				throw new Exception("subtaskid未找到数据");
			}
			
			int dbId = subtask.getDbId();
			conn = DBConnector.getInstance().getConnectionById(dbId);
			
			int isQuality =subtask.getIsQuality();
			if(isQuality==1){
				subtask = apiService.queryBySubTaskIdAndIsQuality(taskId, "2", isQuality);
			}
			
			IxPoiColumnStatusSelector poiColumnSelector = new IxPoiColumnStatusSelector(conn);
			
			
			//获取从状态表查询到能够抽取数据的pids
			List<Integer> pids = poiColumnSelector.getExtractPids(subtask, firstWorkItem, secondWorkItem, type, userId);
			if (pids.size() == 0){
				//未查询到可以抽取的数据
				return 0;
			}
			
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			poiColumnSelector.updateExtractColumnStatus(pids,userId, taskId, timeStamp);
			
			
			return pids.size();
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.commitAndClose(conn);
		}
	}
	
	/**
	 * 查询问题页面初始值
	 * @param pid
	 * @param qualitySubtaskId
	 * @param firstWorkItem
	 * @param secondWorkItem
	 * @return
	 * @throws Exception
	 */
	public JSONObject qcProblemInit(long pid, int qualitySubtaskId, String firstWorkItem, String secondWorkItem) throws Exception{
		Connection regionConn = null;
		Connection manConn = null;
		JSONObject resultJson  = new JSONObject();
		try {
			ManApi apiService = (ManApi) ApplicationContextUtil.getBean("manApi");
			Subtask subtask = apiService.queryBySubTaskIdAndIsQuality(qualitySubtaskId, "2", 1);
			int dbId = subtask.getDbId();
			regionConn = DBConnector.getInstance().getConnectionById(dbId);
			manConn = DBConnector.getInstance().getManConnection();
			
			Integer userId = queryUserId(regionConn, pid, firstWorkItem, secondWorkItem);
			UserInfo userInfo = apiService.getUserInfoByUserId(userId);
			String realName = userInfo.getUserRealName();
			
			Integer subtaskId = subtask.getSubtaskId();
			String groupName = getGroupNameBySubtaskId(manConn, subtaskId);
			
			long operationTime = queryOperationTime(regionConn, pid, subtaskId, userId);
			
			resultJson.put("commonWorker", realName + "-" + groupName);
			resultJson.put("workTime", operationTime == 0L ? "" : DateUtils.longToString(operationTime, "yyyy.MM.dd"));
			return resultJson;
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
		} finally {
			DbUtils.closeQuietly(manConn);
			DbUtils.closeQuietly(regionConn);
		}
	}
	
	private Integer queryUserId(Connection regionConn, long pid,String firstWorkItem, String secondWorkItem) throws Exception{
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT COMMON_HANDLER FROM POI_COLUMN_STATUS T1, POI_COLUMN_WORKITEM_CONF T2 WHERE T1.WORK_ITEM_ID = T2.WORK_ITEM_ID ");
			sb.append("AND T1.PID = ? AND T2.FIRST_WORK_ITEM = ? AND T2.SECOND_WORK_ITEM = ?");
			pstmt = regionConn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, pid);
			pstmt.setString(2, firstWorkItem);
			pstmt.setString(3, secondWorkItem);
			
			resultSet = pstmt.executeQuery();
			Integer userId = null;
			if (resultSet.next()) {
				userId = resultSet.getInt(1);
			}
			return userId;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	private String getGroupNameBySubtaskId(Connection manConn, Integer subtaskId) throws Exception{
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT T3.GROUP_NAME FROM TASK T1, SUBTASK T2, USER_GROUP T3 WHERE T1.GROUP_ID = T3.GROUP_ID ");
			sb.append("AND T1.TASK_ID = T2.TASK_ID AND T2.SUBTASK_ID = ?");
			pstmt = manConn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, subtaskId);
			
			resultSet = pstmt.executeQuery();
			String groupName = null;
			if (resultSet.next()) {
				groupName = resultSet.getString(1);
			}
			return groupName;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	private long queryOperationTime(Connection regionConn, long pid, Integer subtaskId, Integer userId) throws Exception{
		 
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT OP_DT FROM (SELECT LO.OP_DT FROM LOG_ACTION LA, ");
			sb.append("LOG_OPERATION LO, LOG_DETAIL LD WHERE LA.ACT_ID = LO.ACT_ID ");
			sb.append("AND LO.OP_ID = LD.OP_ID AND LD.OB_PID = ? AND LA.STK_ID = ? AND LA.US_ID = ? ");
			sb.append("AND LA.OP_CMD = 'IXPOIDEEPSAVE' ORDER BY LO.OP_DT DESC) WHERE ROWNUM = 1");
			pstmt = regionConn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, pid);
			pstmt.setInt(2, subtaskId);
			pstmt.setInt(3, userId);
			
			resultSet = pstmt.executeQuery();
			long operationTime = 0L;
			if (resultSet.next()) {
				operationTime = resultSet.getTimestamp(1).getTime();
			}
			return operationTime;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
	}

	/**
	 * 质检问题列表
	 * @param pid
	 * @param subtaskId
	 * @param secondWorkItem
	 * @param poiProperty
	 * @return
	 * @throws Exception
	 */
	public JSONArray qcProblemList(long pid, int subtaskId, String secondWorkItem, String poiProperty) throws Exception{
		Connection manConn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			manConn = DBConnector.getInstance().getManConnection();
			List<Object> params = new ArrayList<>();
			params.add(pid);
			params.add(subtaskId);
			params.add(secondWorkItem);
			
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT T.PROBLEM_ID, T.SUBTASK_ID, T.SECOND_WORKITEM, T.PID, T.POI_PROPERTY, T.NEW_VALUE, ");
			builder.append("T.OLD_VALUE, T.COMMON_WORKER, T.WORK_TIME, T.QC_WORKER, T.QC_TIME, T.PROBLEM_LEVEL, T.PROBLEM_DESC ");
			builder.append("FROM DEEP_QC_PROBLEM T WHERE T.PID = ? AND T.SUBTASK_ID = ? AND T.SECOND_WORKITEM = ? ");
			
			if(poiProperty != null){
				builder.append("AND T.POI_PROPERTY = ?");
				params.add(poiProperty);
			}
			
			pstmt = manConn.prepareStatement(builder.toString());
			for(int i = 0 ; i < params.size(); i ++){
				pstmt.setObject(i + 1, params.get(i));
			}
			resultSet = pstmt.executeQuery();
			JSONArray resultJson = new JSONArray();
			while (resultSet.next()) {
				JSONObject jo = new JSONObject();
				jo.put("problemId", resultSet.getInt("PROBLEM_ID") != 0 ? resultSet.getInt("PROBLEM_ID") : "");
				jo.put("subtaskId", resultSet.getInt("SUBTASK_ID") != 0 ? resultSet.getInt("SUBTASK_ID") : "");
				jo.put("secondWorkItem", resultSet.getString("SECOND_WORKITEM") != null ? resultSet.getString("SECOND_WORKITEM") : "");
				jo.put("pid", resultSet.getInt("PID") != 0 ? resultSet.getInt("PID") : "");
				jo.put("poiProperty", resultSet.getString("POI_PROPERTY") != null ? resultSet.getString("POI_PROPERTY") : "");
				jo.put("newValue", resultSet.getString("NEW_VALUE") != null ? resultSet.getString("NEW_VALUE") : "");
				jo.put("oldValue", resultSet.getString("OLD_VALUE") != null ? resultSet.getString("OLD_VALUE") : "");
				jo.put("commonWorker", resultSet.getString("COMMON_WORKER") != null ? resultSet.getString("COMMON_WORKER") : "");
				
				Timestamp workTime = resultSet.getTimestamp("WORK_TIME");
				if(workTime != null){
					jo.put("workTime", DateUtils.longToString(workTime.getTime(), "yyyy.MM.dd"));
				} else {
					jo.put("workTime", "");
				}
				jo.put("qcWorker", resultSet.getString("QC_WORKER")  != null ? resultSet.getString("QC_WORKER") : "");
				Timestamp qcTime = resultSet.getTimestamp("QC_TIME");
				if(qcTime != null){
					jo.put("qcTime", DateUtils.longToString(qcTime.getTime(), "yyyy.MM.dd"));
				} else {
					jo.put("qcTime", "");
				}
				jo.put("problemLevel", resultSet.getString("PROBLEM_LEVEL")  != null ? resultSet.getString("PROBLEM_LEVEL") : "");
				jo.put("problemDesc", resultSet.getString("PROBLEM_DESC")  != null ? resultSet.getString("PROBLEM_DESC") : "");
				
				resultJson.add(jo);
			}
			return resultJson;
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
			DbUtils.closeQuietly(manConn);
		}
	}
}
