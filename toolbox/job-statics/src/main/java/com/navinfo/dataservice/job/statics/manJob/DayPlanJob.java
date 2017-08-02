package com.navinfo.dataservice.job.statics.manJob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import com.mongodb.client.model.Filters;
import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.api.man.model.Region;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.constant.PropConstant;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.thread.VMThreadPoolExecutor;
import com.navinfo.dataservice.control.dealership.MongoDao;
import com.navinfo.dataservice.job.statics.AbstractStatJob;
import com.navinfo.dataservice.jobframework.exception.JobException;
import com.navinfo.navicommons.database.QueryRunner;
import com.navinfo.navicommons.exception.ServiceRtException;
import com.navinfo.navicommons.exception.ThreadExecuteException;

import net.sf.json.JSONArray;

/**
 * DayPlanJob
 * Day_规划量统计job
 * @author sjw
 *
 */
public class DayPlanJob extends AbstractStatJob {

	protected VMThreadPoolExecutor threadPoolExecutor;

	/**
	 * @param jobInfo
	 */
	public DayPlanJob(JobInfo jobInfo) {
		super(jobInfo);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.navinfo.dataservice.job.statics.AbstractStatJob#stat()
	 */
	@Override
	public String stat() throws JobException {
		try {
			List<Map<String, Double>> stats = new ArrayList<Map<String,Double>>();
			long t = System.currentTimeMillis();
			initThreadPool(1);
			final CountDownLatch latch = new CountDownLatch(1);
			threadPoolExecutor.addDoneSignal(latch);
			// 执行转数据
		    threadPoolExecutor.execute(new DayPlanThread(latch,stats));
			latch.await();
			if (threadPoolExecutor.getExceptions().size() > 0) {
				throw new Exception(threadPoolExecutor.getExceptions().get(0));
			}
			log.debug("所有Day_规划量数据统计完毕。用时："+((System.currentTimeMillis()-t)/1000)+"s.");
			
			Map<String,List<Map<String,Double>>> result = new HashMap<String,List<Map<String,Double>>>();
			result.put("task_day_plan", stats);

			return result.toString();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobException(e.getMessage(),e);
		}
	}
	
	private void initThreadPool(int poolSize)throws Exception{
		log.debug("开始初始化线程池");
        threadPoolExecutor = new VMThreadPoolExecutor(poolSize,
        		poolSize,
				3,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue(),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	private void shutDownPoolExecutor(){
		if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown()) {
			log.debug("关闭线程池");
			threadPoolExecutor.shutdownNow();
			try {
				while (!threadPoolExecutor.isTerminated()) {
					log.debug("等待线程结束：线程数为" + threadPoolExecutor.getActiveCount());
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
				log.error("关闭线程池失败");
				throw new ServiceRtException("关闭线程池失败", e);
			}
		}
	}
	
	class DayPlanThread implements Runnable{
		CountDownLatch latch = null;
		List<Map<String, Double>> stats;
		DayPlanThread(CountDownLatch latch,List<Map<String, Double>> stat){
			this.latch=latch;
			this.stats = stat;
		}
		
		@Override
		public void run() {
			List<Map<String, Integer>> taskIdMapList = null;
			try {
				taskIdMapList = getTaskIdList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Map<String, Integer> taskIdMap : taskIdMapList) {
				
				Integer dbId = taskIdMap.get("dbId");
				Integer taskId = taskIdMap.get("taskId");
				
				String dbName=SystemConfigFactory.getSystemConfig().getValue(PropConstant.fmStat);
				MongoDao md = new MongoDao(dbName);
				if(md.find("task_day_plan",Filters.eq("taskId", taskId)).iterator().hasNext()){
					continue;
				}
				Connection conn=null;
				try{
					conn=DBConnector.getInstance().getConnectionById(dbId);
					QueryRunner run = new QueryRunner();
					
					
					String rdLinkSql = "SELECT NVL(SUM(r.length),0) FROM rd_link r,DATA_PLAN d WHERE r.link_pid = d.pid AND d.data_type = 2 AND d.task_id = ";
					String poiSql = "SELECT COUNT(1) FROM ix_poi p,DATA_PLAN d WHERE p.pid = d.pid AND d.data_type = 1 AND d.task_id = ";
					String planSuffix = " AND d.is_plan_selected=1 ";
					
						
					Map<String,Double> map  = new HashMap<>();
					
					String sql1 = rdLinkSql+taskId+planSuffix;
					String sql2 = rdLinkSql+taskId;
					String sql3 = poiSql+taskId+planSuffix;
					String sql4 = poiSql+taskId;
					String sql5 = rdLinkSql+taskId+" AND r.kind >= 1 AND r.kind <= 7";
					String sql6 = rdLinkSql+taskId+" AND r.kind >= 2 AND r.kind <= 7";
					
					Double linkPlanLen = run.query(conn, sql1,new ResultSetHandler<Double>() {
						@Override
						public Double handle(ResultSet rs)
								throws SQLException {
							if(rs.next()){
								return rs.getDouble(1);
							}
							return 0.0;
						}
					});
					
					Double linkAllLen = run.query(conn, sql2,new ResultSetHandler<Double>() {
						@Override
						public Double handle(ResultSet rs)
								throws SQLException {
							if(rs.next()){
								return rs.getDouble(1);
							}
							return 0.0;
						}
					});
					
					
					Double poiPlanNum = run.query(conn, sql3,new ResultSetHandler<Double>() {
						@Override
						public Double handle(ResultSet rs)
								throws SQLException {
							if(rs.next()){
								return rs.getDouble(1);
							}
							return 0.0;
						}
					});
					
					Double poiAllNum = run.query(conn, sql4,new ResultSetHandler<Double>() {
						@Override
						public Double handle(ResultSet rs)
								throws SQLException {
							if(rs.next()){
								return rs.getDouble(1);
							}
							return 0.0;
						}
					});
					
					Double link17AllLen = run.query(conn, sql5,new ResultSetHandler<Double>() {
						@Override
						public Double handle(ResultSet rs)
								throws SQLException {
							if(rs.next()){
								return rs.getDouble(1);
							}
							return 0.0;
						}
					});
					
					Double link27AllLen = run.query(conn, sql6,new ResultSetHandler<Double>() {
						@Override
						public Double handle(ResultSet rs)
								throws SQLException {
							if(rs.next()){
								return rs.getDouble(1);
							}
							return 0.0;
						}
					});
					
					
					map.put("taskId", taskId.doubleValue());
					map.put("linkPlanLen", linkPlanLen);
					map.put("linkAllLen", linkAllLen);
					map.put("poiPlanNum", poiPlanNum);
					map.put("poiAllNum", poiAllNum);
					map.put("link17AllLen", link17AllLen);
					map.put("link27AllLen", link27AllLen);
					
					stats.add(map);
					

				}catch(Exception e){
					log.error(e.getMessage(),e);
					throw new ThreadExecuteException("dbId("+dbId+")Day_规划量数据统计失败");
				}finally{
					DbUtils.closeQuietly(conn);
					if(latch!=null){
						latch.countDown();
					}
				}
			
			}
			
			log.debug(JSONArray.fromObject(stats));

			
		}
		
	}
	
	public List<Map<String,Integer>> getTaskIdList() throws Exception{
		Connection conn = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String,Integer>> taskIdList = new ArrayList<>();
		try {
			conn = DBConnector.getInstance().getManConnection();
			String sql  = "SELECT task_id,r.DAILY_DB_ID FROM task t,region r WHERE t.data_plan_status=1 and T.REGION_ID = R.REGION_ID";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				Map<String,Integer> map = new HashMap<>();
				map.put("taskId", rs.getInt(1));
				map.put("dbId", rs.getInt(2));
				taskIdList.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtils.commitAndClose(conn);
		}
		return taskIdList;
		
	}
	

}
