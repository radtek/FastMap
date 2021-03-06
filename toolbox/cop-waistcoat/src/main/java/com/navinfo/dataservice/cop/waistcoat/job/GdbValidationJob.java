package com.navinfo.dataservice.cop.waistcoat.job;

import java.util.HashSet;
import java.util.Set;

import com.navinfo.dataservice.api.datahub.iface.DatahubApi;
import com.navinfo.dataservice.api.datahub.model.BizType;
import com.navinfo.dataservice.api.datahub.model.DbInfo;
import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.bizcommons.datarow.CkResultTool;
import com.navinfo.dataservice.bizcommons.datarow.PhysicalDeleteRow;
import com.navinfo.dataservice.commons.database.DbConnectConfig;
import com.navinfo.dataservice.commons.database.OracleSchema;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.jobframework.exception.JobException;
import com.navinfo.dataservice.jobframework.runjob.AbstractJob;
import com.navinfo.dataservice.jobframework.runjob.JobCreateStrategy;

import net.sf.json.JSONArray;

/** 
* @ClassName: GdbValidationJob 
* @author Xiao Xiaowen 
* @date 2016年6月21日 下午3:52:09 
* @Description: TODO
*  
*/
public class GdbValidationJob extends AbstractJob {

	public GdbValidationJob(JobInfo jobInfo) {
		super(jobInfo);
	}

	@Override
	public void execute() throws JobException {
		GdbValidationJobRequest req = (GdbValidationJobRequest)request;
		//1. 创建检查子版本
		try {
			// 1. 创建检查子版本库库
			OracleSchema valSchema = null;
			DatahubApi datahub = (DatahubApi)ApplicationContextUtil.getBean("datahubApi");
			int valDbId = 0;
			//先找是否有传入的库，传入的库不需要导数据
			if(req.getValDbId()>0){
				valDbId = req.getValDbId();
				DbInfo valDb = datahub.getDbById(valDbId);
				valSchema = new OracleSchema(
						DbConnectConfig.createConnectConfig(valDb.getConnectParam()));
			}else{
				//在找是否利用可重复使用的库,重用的库是空库，需要导数据
				if(req.isReuseDb()){
					DbInfo valDb = datahub.getReuseDb(BizType.DB_COP_VERSION);
					
					if(valDb!=null){
						valDbId=valDb.getDbId();
					}else{//未设置利用重用的库，或者未找到可重用的库，需要新建库
						if(req.getSubJobRequest("createValDb")!=null){
							JobInfo createValDbJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
							AbstractJob createValDbJob = JobCreateStrategy.createAsSubJob(createValDbJobInfo,
									req.getSubJobRequest("createValDb"), this);
							createValDbJob.run();
							if (createValDbJob.getJobInfo().getStatus() != 3) {
								String msg = (createValDbJob.getException()==null)?"未知错误。":"错误："+createValDbJob.getException().getMessage();
								throw new Exception("创建检查子版本库时job内部发生"+msg);
							}
							valDbId = createValDbJob.getJobInfo().getResponse().getInt("outDbId");
							valDb = datahub.getDbById(valDbId);
						}else{
							throw new Exception("未设置创建检查子版本库request参数。");
						}
					}
					jobInfo.addResponse("valDbId", valDbId);
					valSchema = new OracleSchema(
							DbConnectConfig.createConnectConfig(valDb.getConnectParam()));
				}
				// 给检查子版本库导数据
				if(req.getSubJobRequest("expValDb")!=null){
					req.getSubJobRequest("expValDb").setAttrValue("sourceDbId", req.getTargetDbId());
					req.getSubJobRequest("expValDb").setAttrValue("targetDbId", valDbId);
					Set<String> meshes = new HashSet<String>();
					for (Integer g : req.getGrids()) {
						int m = g / 100;
						meshes.add(m < 99999 ? "0" + String.valueOf(m) : String.valueOf(m));
					}
					req.getSubJobRequest("expValDb").setAttrValue("conditionParams", JSONArray.fromObject(meshes));
					JobInfo expValDbJobInfo = new JobInfo(jobInfo.getId(), jobInfo.getGuid());
					AbstractJob expValDbJob = JobCreateStrategy.createAsSubJob(expValDbJobInfo, req.getSubJobRequest("expValDb"), this);
					expValDbJob.run();
					if (expValDbJob.getJobInfo().getStatus() != 3) {
						String msg = (expValDbJob.getException()==null)?"未知错误。":"错误："+expValDbJob.getException().getMessage();
						throw new Exception("检查子版本导数据时job内部发生"+msg);
					}
					//cop 子版本物理删除逻辑删除数据
					DbInfo valDb = datahub.getDbById(valDbId);
					valSchema = new OracleSchema(
							DbConnectConfig.createConnectConfig(valDb.getConnectParam()));
					PhysicalDeleteRow.doDelete(valSchema);
				}else{
					throw new Exception("未设置给检查子版本库导数据的request参数。");
				}
			}
			// 2. 在检查子版本上执行检查
			req.getSubJobRequest("val").setAttrValue("executeDBId", valDbId);
			req.getSubJobRequest("val").setAttrValue("ruleIds", req.getRules());
			DbInfo metaDb = datahub.getOnlyDbByType("metaRoad");
			req.getSubJobRequest("val").setAttrValue("kdbDBId", metaDb.getDbId());
			req.getSubJobRequest("val").setAttrValue("timeOut", req.getTimeOut());
			JobInfo valJobInfo = new JobInfo(jobInfo.getId(),jobInfo.getGuid());
			AbstractJob valJob = JobCreateStrategy.createAsSubJob(valJobInfo, req.getSubJobRequest("val"), this);
			valJob.run();
			if(valJob.getJobInfo().getStatus()!=3){
				String msg = (valJob.getException()==null)?"未知错误。":"错误："+valJob.getException().getMessage();
				throw new Exception("检查job内部发生"+msg);
			}
			// 3. 检查结果搬迁
			CkResultTool.generateCkMd5(valSchema);
			CkResultTool.generateCkResultObject(valSchema);
			CkResultTool.generateCkResultGrid(valSchema);
			DbInfo tarDb = datahub.getDbById(req.getTargetDbId());
			OracleSchema tarSchema = new OracleSchema(
					DbConnectConfig.createConnectConfig(tarDb.getConnectParam()));
			CkResultTool.moveNiVal(valSchema, tarSchema, req.getGrids());
			response("检查生成的检查结果后处理及搬迁完毕。",null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobException(e.getMessage(), e);
		}
	}

}
