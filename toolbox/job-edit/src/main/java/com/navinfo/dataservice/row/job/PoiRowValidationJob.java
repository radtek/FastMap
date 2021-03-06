package com.navinfo.dataservice.row.job;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;

import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.api.man.model.Subtask;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.database.ConnectionUtil;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.control.column.core.DeepCoreControl;
import com.navinfo.dataservice.dao.plus.log.LogDetail;
import com.navinfo.dataservice.dao.plus.log.ObjHisLogParser;
import com.navinfo.dataservice.dao.plus.log.PoiLogDetailStat;
import com.navinfo.dataservice.dao.plus.log.SamepoiLogDetailStat;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.dataservice.dao.plus.operation.OperationResult;
import com.navinfo.dataservice.dao.plus.selector.ObjBatchSelector;
import com.navinfo.dataservice.dao.plus.selector.custom.IxPoiSelector;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.check.Check;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.check.CheckCommand;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.common.CheckRuleFactory;
import com.navinfo.dataservice.jobframework.exception.JobException;
import com.navinfo.dataservice.jobframework.runjob.AbstractJob;
import com.navinfo.navicommons.database.QueryRunner;

import net.sf.json.JSONObject;
/**
 * poi行编检查
 * @author zhangxiaoyi
 *
 */
public class PoiRowValidationJob extends AbstractJob {

	public PoiRowValidationJob(JobInfo jobInfo) {
		super(jobInfo);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws JobException {
		Date startTime = new Date();
		log.info("start time:"+startTime);
		log.info("start PoiRowValidationJob");
		PoiRowValidationJobRequest myRequest = (PoiRowValidationJobRequest) request;
		Connection conn=null;
		try{
			conn=DBConnector.getInstance().getConnectionById(myRequest.getTargetDbId());
			log.info("PoiRowValidationJob:获取要检查的数据pid");
			//获取要检查的数据pid
			getCheckPidList(conn,myRequest);
			log.info("PoiRowValidationJob:需要检查的数据共计："+myRequest.getPids().size());
			log.info("PoiRowValidationJob:获取要检查的数据的履历");
			//获取log
			Map<Long, List<LogDetail>> logs = PoiLogDetailStat.loadByRowEditStatus(conn, myRequest.getPids());
			Set<String> tabNames=getChangeTableSet(logs);
			log.info("PoiRowValidationJob:加载检查对象");
			//获取poi对象			
			Map<Long, BasicObj> objs = ObjBatchSelector.selectByPids(conn, ObjectName.IX_POI, tabNames, false,
					myRequest.getPids(), false, false);
			log.info("PoiRowValidationJob:本次检查POI数量："+objs.size());
			//将poi对象与履历合并起来
			ObjHisLogParser.parse(objs, logs);
			log.info("PoiRowValidationJob:加载同一关系检查对象");
			//获取poi对象			
			List<Long> groupIds = IxPoiSelector.getIxSamePoiGroupIdsByPids(conn, myRequest.getPids());
			log.info("PoiRowValidationJob:获取要检查的同一关系数据的履历");
			//获取log
			Map<Long, List<LogDetail>> samelogs = SamepoiLogDetailStat.loadByRowEditStatus(conn, groupIds);
			Set<String> sametabNames=getChangeTableSet(samelogs);
			Map<Long, BasicObj> sameobjs = ObjBatchSelector.selectByPids(conn, ObjectName.IX_SAMEPOI, sametabNames, false,
					groupIds, false, false);
			//将poi对象与履历合并起来
			ObjHisLogParser.parse(sameobjs, samelogs);
			log.info("PoiRowValidationJob:执行检查");
			//构造检查参数，执行检查
			OperationResult operationResult=new OperationResult();
			Map<String,Map<Long,BasicObj>> objsMap=new HashMap<String, Map<Long,BasicObj>>();
			objsMap.put(ObjectName.IX_POI, objs);
			objsMap.put(ObjectName.IX_SAMEPOI, sameobjs);
			operationResult.putAll(objsMap);
			
			CheckCommand checkCommand=new CheckCommand();
						
			if(myRequest.getRules()!=null && myRequest.getRules().size()>0){
				checkCommand.setRuleIdList(myRequest.getRules());}
			else{checkCommand.setOperationName(getOperationName());}
			
			// 清理检查结果
			log.info("start 清理检查结果");
			DeepCoreControl deepControl = new DeepCoreControl();
			List<Integer> pidIntList=new ArrayList<Integer>();
			for(Long pidTmp:myRequest.getPids()){
				pidIntList.add(Integer.valueOf(pidTmp.toString()));
			}
			deepControl.cleanExByCkRule(myRequest.getTargetDbId(), pidIntList, checkCommand.getRuleIdList(), ObjectName.IX_POI);
			log.info("end 清理检查结果");
			
			Check check=new Check(conn, operationResult);
			check.operate(checkCommand);
			
			//查询检查结果数量
			int resultCount = 0;
			resultCount = getListPoiResultCount(conn,myRequest);
			JSONObject data =new JSONObject();
			data.put("type", "检查");
			data.put("resNum", resultCount);
			this.exeResultMsg=" #"+data.toString()+"#";
			log.info("查询poi检查结果数量:" +resultCount);
			log.info("end PoiRowValidationJob");
			Date endTime = new Date();
			log.info("end time:"+endTime);
			log.info("本次检查共计耗时："+(endTime.getTime()-startTime.getTime())/1000+"s");
		}catch(Exception e){
			log.error("PoiRowValidationJob错误", e);
			DbUtils.rollbackAndCloseQuietly(conn);
			throw new JobException(e);
		}finally{
			DbUtils.commitAndCloseQuietly(conn);
		}
	}
	/**
	 * 分析履历，将履历中涉及的变更过的子表集合返回
	 * @param logs
	 * @return [IX_POI_NAME,IX_POI_ADDRESS]
	 */
	private Set<String> getChangeTableSet(Map<Long, List<LogDetail>> logs) {
		Set<String> subtables=new HashSet<String>();
		if(logs==null || logs.size()==0){return subtables;}
		String mainTable="IX_POI";
		for(Long objId:logs.keySet()){
			List<LogDetail> logList = logs.get(objId);
			for(LogDetail logTmp:logList){
				String tableName = logTmp.getTbNm();
				if(!mainTable.equals(tableName)){subtables.add(tableName);}
			}
		}
		return subtables;
	}

	/**
	 * 获取行编检查对象pid
	 * 1.pids有值，则直接针对改pid进行检查
	 * 2.pids无值,根据子任务圈查询，待作业/已作业状态的非删除poi列表
	 * @param conn
	 * @param myRequest
	 * @throws JobException
	 */
	private void getCheckPidList(Connection conn,
			PoiRowValidationJobRequest myRequest) throws JobException {
		try{
			List<Long> pids = myRequest.getPids();
			if(pids!=null&&pids.size()>0){return;}
			//ManApi apiService = (ManApi) ApplicationContextUtil.getBean("manApi");
			//Subtask subtask = apiService.queryBySubtaskId((int)jobInfo.getTaskId());
			//行编有针对删除数据进行的检查，此处要把删除数据也加载出来
			String sql="SELECT ip.pid"
					+ "  FROM ix_poi ip, poi_edit_status ps"
					+ " WHERE ip.pid = ps.pid"
					+ "   AND ps.work_type = 1 AND ps.status in (1,2)"
					//+ "   and ip.u_record!=2"
					+ " AND (ps.QUICK_SUBTASK_ID="+(int)jobInfo.getTaskId()+" or ps.MEDIUM_SUBTASK_ID="+(int)jobInfo.getTaskId()+") ";
					//+ "   AND sdo_within_distance(ip.geometry,"
					//+ "                           sdo_geometry('"+subtask.getGeometry()+"', 8307),"
					//+ "                           'mask=anyinteract') = 'TRUE'";
			QueryRunner run=new QueryRunner();
			pids=run.query(conn, sql,new ResultSetHandler<List<Long>>(){

				@Override
				public List<Long> handle(ResultSet rs) throws SQLException {
					List<Long> pids =new ArrayList<Long>();
					while (rs.next()) {
						pids.add(rs.getLong("PID"));						
					}
					return pids;
				}});
			myRequest.setPids(pids);
		}catch(Exception e){
			log.error("行编获取检查数据报错", e);
			DbUtils.rollbackAndCloseQuietly(conn);
			throw new JobException(e);
		}
	}
	
	public String getOperationName() {
		//return "POI_ROW_VALIDATION";
		return "POI_ROW_COMMIT";
	}
	
	/**
	 * @Title: getListPoiResultCount
	 * @Description: 获取去子任务范围内所有poi 检查结果的总条数
	 * @param conn
	 * @param subtaskId
	 * @return
	 * @throws Exception
	 *             int
	 * @throws
	 * @author zl zhangli5174@navinfo.com
	 * @date 2017年7月4日 下午20:30:51
	 */
	private int getListPoiResultCount(Connection conn,PoiRowValidationJobRequest myRequest)
			throws Exception {

		List<Long> pids = myRequest.getPids();
		int poiResCount = 0;
		if (pids != null && pids.size() > 0) {
			try {
				Clob clob = ConnectionUtil.createClob(conn);
				clob.setString(1, StringUtils.join(pids, ","));
				// 行编有针对删除数据进行的检查，此处要把删除数据也加载出来
				StringBuilder sql = new StringBuilder(
						"select count(1) total from ( "
								+ "select O.PID "
								+ "from "
								+ "ni_val_exception a  , CK_RESULT_OBJECT O  "
								+ "WHERE  (O.table_name like 'IX_POI\\_%' ESCAPE '\\' OR O.table_name ='IX_POI')  AND O.MD5_CODE=a.MD5_CODE "
								+ " and o.pid in (select column_value from table(clob_to_table(?)) "
								+ ") "
								+ " union all "
								+ "select O.PID "
								+ "from "
								+ "ck_exception c , CK_RESULT_OBJECT O "
								+ "  WHERE (O.table_name like 'IX_POI\\_%' ESCAPE '\\' OR O.table_name ='IX_POI')  AND O.MD5_CODE=c.MD5_CODE "
								+ " and o.pid in (select column_value from table(clob_to_table(?)) "
								+ " )  " + " )  b ");
				
				log.info("sql: " +sql.toString());
				QueryRunner run = new QueryRunner();
				poiResCount = run.query(conn, sql.toString(),
						new ResultSetHandler<Integer>() {

							@Override
							public Integer handle(ResultSet rs)
									throws SQLException {
								Integer resCount = 0;
								if (rs.next()) {
									resCount = rs.getInt("total");
								}
								return resCount;
							}
						},clob,clob);

			} catch (Exception e) {
				log.error("行编获取检查数据报错", e);
				// DbUtils.rollbackAndCloseQuietly(conn);
				throw new Exception(e);
			}
		}
		log.info("poiResCount: " + poiResCount);
		return poiResCount;
	}
}
