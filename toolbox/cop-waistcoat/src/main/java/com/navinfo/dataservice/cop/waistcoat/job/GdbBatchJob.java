package com.navinfo.dataservice.cop.waistcoat.job;

import java.util.HashSet;
import java.util.Set;

import com.navinfo.dataservice.api.datahub.iface.DatahubApi;
import com.navinfo.dataservice.api.datahub.model.BizType;
import com.navinfo.dataservice.api.datahub.model.DbInfo;
import com.navinfo.dataservice.api.edit.iface.DatalockApi;
import com.navinfo.dataservice.api.edit.model.FmEditLock;
import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.bizcommons.datarow.CkResultTool;
import com.navinfo.dataservice.bizcommons.datarow.PhysicalDeleteRow;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.database.DbConnectConfig;
import com.navinfo.dataservice.commons.database.OracleSchema;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.jobframework.exception.JobException;
import com.navinfo.dataservice.jobframework.exception.LockException;
import com.navinfo.dataservice.jobframework.runjob.AbstractJob;
import com.navinfo.dataservice.jobframework.runjob.JobCreateStrategy;

import net.sf.json.JSONArray;

/**
 * @ClassName: GdbBatchJob
 * @author Xiao Xiaowen
 * @date 2016年6月17日 下午6:01:43
 * @Description: TODO
 * 
 */
public class GdbBatchJob extends AbstractJob {

	protected FmEditLock editLock=null;
	public GdbBatchJob(JobInfo jobInfo) {
		super(jobInfo);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.navinfo.dataservice.jobframework.runjob.AbstractJob#execute()
	 */
	@Override
	public void execute() throws JobException {
		GdbBatchJobRequest req = (GdbBatchJobRequest) request;
		try {
			// 1. 创建批处理库
			int batchDbId = 0;
			OracleSchema batSchema = null;
			DatahubApi datahub = (DatahubApi)ApplicationContextUtil.getBean("datahubApi");
			if(req.getBatchDbId()!=0){
				batchDbId = req.getBatchDbId();
				jobInfo.getResponse().put("batDbId", batchDbId);
				DbInfo batDb = datahub.getDbById(batchDbId);
				batSchema = new OracleSchema(
						DbConnectConfig.createConnectConfig(batDb.getConnectParam()));
			}else{
				//在找是否利用可重复使用的库,重用的库是空库，需要导数据
				if(req.isReuseDb()){
					DbInfo batDb = datahub.getReuseDb(BizType.DB_COP_VERSION);
					if(batDb!=null){
						batSchema = new OracleSchema(
								DbConnectConfig.createConnectConfig(batDb.getConnectParam()));
					}
				}
				//未设置利用重用的库，或者未找到可重用的库，需要新建库
				if(batSchema==null&&req.getSubJobRequest("createBatchDb")!=null){
					JobInfo createBatchDbJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
					AbstractJob createBatchDbJob = JobCreateStrategy.createAsSubJob(createBatchDbJobInfo,
							req.getSubJobRequest("createBatchDb"), this);
					createBatchDbJob.run();
					if (createBatchDbJob.getJobInfo().getResponse().getInt("exeStatus") != 3) {
						throw new Exception("创建批处理子版本库是job执行失败。");
					}
					batchDbId = createBatchDbJob.getJobInfo().getResponse().getInt("outDbId");
					jobInfo.getResponse().put("batDbId", batchDbId);
				}else{
					throw new Exception("未设置创建批处理子版本库request参数。");
				}
				// 2.批处理库导数据
				if(req.getSubJobRequest("expBatchDb")!=null){
					req.getSubJobRequest("expBatchDb").setAttrValue("sourceDbId", req.getTargetDbId());
					req.getSubJobRequest("expBatchDb").setAttrValue("targetDbId", batchDbId);
					req.getSubJobRequest("expBatchDb").setAttrValue("meshExtendCount", req.getExtendCount());
					Set<String> meshes = new HashSet<String>();
					for (Integer g : req.getGrids()) {
						int m = g / 100;
						meshes.add(m < 99999 ? "0" + String.valueOf(m) : String.valueOf(m));
					}
					req.getSubJobRequest("expBatchDb").setAttrValue("conditionParams", JSONArray.fromObject(meshes));
					JobInfo expBatchDbJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
					AbstractJob expBatchDbJob = JobCreateStrategy.createAsSubJob(expBatchDbJobInfo, req.getSubJobRequest("expBatchDb"), this);
					expBatchDbJob.run();
					if (expBatchDbJob.getJobInfo().getResponse().getInt("exeStatus") != 3) {
						throw new Exception("批处理子版本库导数据时job执行失败。");
					}
					//cop 子版本物理删除逻辑删除数据
					DbInfo batDb = datahub.getDbById(batchDbId);
					batSchema = new OracleSchema(
							DbConnectConfig.createConnectConfig(batDb.getConnectParam()));
					PhysicalDeleteRow.doDelete(batSchema);
				}else{
					throw new Exception("未设置给批处理子版本库导数据的request参数。");
				}
			}
			// 3.创建批处理子版本备份库
			int bakDbId=0;
			//在找是否利用可重复使用的库,重用的库是空库，需要导数据
			if(req.isReuseDb()){
				DbInfo bakDbInfo = datahub.getReuseDb(BizType.DB_COP_VERSION,batchDbId);
				if(bakDbInfo!=null)bakDbId=bakDbInfo.getDbId();
			}
			if(bakDbId>0&&req.getSubJobRequest("createBakDb")!=null){
				req.getSubJobRequest("createBakDb").setAttrValue("refDbId", batchDbId);
				JobInfo createBakDbJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
				AbstractJob createBakDbJob = JobCreateStrategy.createAsSubJob(createBakDbJobInfo, req.getSubJobRequest("createBakDb"),
						this);
				createBakDbJob.run();
				if (createBakDbJob.getJobInfo().getResponse().getInt("exeStatus") != 3) {
					throw new Exception("创建备份子版本库时job执行失败。");
				}
				bakDbId = createBakDbJob.getJobInfo().getResponse().getInt("outDbId");
			}else{
				throw new Exception("未设置创建批处理备份子版本库的request参数。");
			}
			// 4.给批处理备份子版本复制数据
			req.getSubJobRequest("copyBakDb").setAttrValue("sourceDbId", batchDbId);
			req.getSubJobRequest("copyBakDb").setAttrValue("targetDbId", bakDbId);
			JobInfo copyBakDbJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
			AbstractJob copyBakDbJob = JobCreateStrategy.createAsSubJob(copyBakDbJobInfo, req.getSubJobRequest("copyBakDb"), this);
			copyBakDbJob.run();
			if (copyBakDbJob.getJobInfo().getResponse().getInt("exeStatus") != 3) {
				throw new Exception("批处理备份子版本库复制数据时job执行失败。");
			}
			// 5. 在批处理子版本上执行批处理s
			req.getSubJobRequest("batch").setAttrValue("executeDBId", batchDbId);
			req.getSubJobRequest("batch").setAttrValue("backupDBId", bakDbId);
			DbInfo metaDb = datahub.getOnlyDbByType("metaRoad");
			req.getSubJobRequest("batch").setAttrValue("kdbDBId", metaDb.getDbId());
			req.getSubJobRequest("batch").setAttrValue("pidDbInfo",
					SystemConfigFactory.getSystemConfig().getValue("dms.pid.dbInfo"));
//			req.getSubJobRequest("batch").setAttrValue("pidDbInfo", req.getPidDbInfo());
			req.getSubJobRequest("batch").setAttrValue("ruleIds", req.getRules());
			JobInfo batchJobInfo = new JobInfo(jobInfo.getId(),jobInfo.getGuid());
			AbstractJob batchJob = JobCreateStrategy.createAsSubJob(batchJobInfo, req.getSubJobRequest("batch"),this);
			batchJob.run();
			if(batchJob.getJobInfo().getResponse().getInt("exeStatus")!=3){
				throw new Exception("批处理过程中job执行失败。");
			}
			// 6. 执行差分
			req.getSubJobRequest("diff").setAttrValue("leftDbId", batchDbId);
			req.getSubJobRequest("diff").setAttrValue("rightDbId", bakDbId);
			JobInfo diffJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
			AbstractJob diffJob = JobCreateStrategy.createAsSubJob(diffJobInfo, req.getSubJobRequest("diff"), this);
			diffJob.run();
			if (diffJob.getJobInfo().getResponse().getInt("exeStatus") != 3) {
				throw new Exception("差分时job执行失败。");
			}
			//7. 差分履历会大区库
			req.getSubJobRequest("commit").setAttrValue("logDbId", batchDbId);
			req.getSubJobRequest("commit").setAttrValue("targetDbId", req.getTargetDbId());
			req.getSubJobRequest("commit").setAttrValue("grids", JSONArray.fromObject(req.getGrids()));
			JobInfo commitJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
			AbstractJob commitJob = JobCreateStrategy.createAsSubJob(commitJobInfo, req.getSubJobRequest("commit"), this);
			commitJob.run();
			if (commitJob.getJobInfo().getResponse().getInt("exeStatus") != 3) {
				throw new Exception("回区域库时job执行失败。");
			}
			//8. 检查结果搬迁
			CkResultTool.generateCkMd5(batSchema);
			CkResultTool.generateCkResultObject(batSchema);
			CkResultTool.generateCkResultGrid(batSchema);
			DbInfo tarDb = datahub.getDbById(req.getTargetDbId());
			OracleSchema tarSchema = new OracleSchema(
					DbConnectConfig.createConnectConfig(tarDb.getConnectParam()));
			CkResultTool.moveNiVal(batSchema, tarSchema, req.getGrids());
			response("批处理生成的检查结果搬迁完毕。",null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobException(e.getMessage(), e);
		}

	}

	@Override
	public void lockResources() throws LockException {
		GdbBatchJobRequest req = (GdbBatchJobRequest) request;
		// 根据批处理的目标库找到对应的大区
		try {
			DatalockApi datalock = (DatalockApi) ApplicationContextUtil.getBean("datalockApi");
			editLock = datalock.lockGrid(req.getTargetDbId(), FmEditLock.LOCK_OBJ_ALL, req.getGrids(), FmEditLock.TYPE_BATCH, jobInfo.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new LockException("加锁发生错误," + e.getMessage(), e);
		}
	}

	@Override
	public void unlockResources() throws LockException {
		if (editLock==null)
			return;
		try {
			DatalockApi datalock = (DatalockApi) ApplicationContextUtil.getBean("datalockApi");
			datalock.unlockGrid(editLock.getLockSeq(), editLock.getDbType());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new LockException("解锁时发生错误," + e.getMessage(), e);
		}
	}

}
