package com.navinfo.dataservice.engine.man.job.Tips2Mark;

import com.navinfo.dataservice.api.datahub.iface.DatahubApi;
import com.navinfo.dataservice.api.datahub.model.DbInfo;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.constant.PropConstant;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.util.ServiceInvokeUtil;
import com.navinfo.dataservice.engine.man.job.JobPhase;
import com.navinfo.dataservice.engine.man.job.bean.InvokeType;
import com.navinfo.dataservice.engine.man.job.bean.JobProgressStatus;
import com.navinfo.dataservice.engine.man.job.operator.JobProgressOperator;
import net.sf.json.JSONObject;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangshishuai3966 on 2017/7/6.
 */
public class CreateCMSTaskPhase extends JobPhase {
    private static Logger log = LoggerRepos.getLogger(CreateCMSTaskPhase.class);

    @Override
    public void initInvokeType() {
        this.invokeType = InvokeType.SYNC;
    }

    @Override
    public JobProgressStatus run() throws Exception {
        Connection conn = null;
        JobProgressOperator jobProgressOperator = null;
        try {
            conn = DBConnector.getInstance().getManConnection();
            jobProgressOperator = new JobProgressOperator(conn);
            if (lastJobProgress.getStatus() == JobProgressStatus.NODATA) {
                jobProgressOperator.updateStatus(jobProgress, JobProgressStatus.SUCCESS);
                return jobProgress.getStatus();
            }

            //更新状态为进行中
            jobProgressOperator.updateStatus(jobProgress, JobProgressStatus.RUNNING);
            conn.commit();

            //业务逻辑
//            Map<String, Object> cmsInfo = Tips2MarkUtils.getItemInfo(conn, jobRelation.getItemId(), jobRelation.getItemType());
//            JSONObject parameter=new JSONObject();
//            DatahubApi datahub = (DatahubApi) ApplicationContextUtil
//                    .getBean("datahubApi");
//            DbInfo metaDb = datahub.getOnlyDbByType("metaRoad");
//            parameter.put("metaIp", metaDb.getDbServer().getIp());
//            parameter.put("metaUserName", metaDb.getDbUserName());
//
//            DbInfo auDb = datahub.getOnlyDbByType("gen2Au");
//            parameter.put("fieldDbIp", auDb.getDbServer().getIp());
//            parameter.put("fieldDbName", auDb.getDbUserName());
//
//            JSONObject taskPar=new JSONObject();
//            taskPar.put("taskName", cmsInfo.get("collectName"));
//            taskPar.put("fieldTaskId", cmsInfo.get("collectId"));
//            taskPar.put("taskId", cmsInfo.get("collectId"));
//            taskPar.put("province", cmsInfo.get("provinceName"));
//            taskPar.put("city", cmsInfo.get("cityName"));
//            taskPar.put("town", cmsInfo.get("blockName"));
//
//            String jobType="中线一体化作业";
//            String jobNature = "更新";
//            int taskType=1;
//            switch (jobRelation.getItemType()) {
//                case PROJECT:
//                    jobType = "快线一体化作业";
//                    jobNature = "快速更新";
//                    taskType = 4;
//                    break;
//                case SUBTASK:
//                    taskType = 2;
////                if(type!=0){
////                    throw new Exception("非采集子任务不允许执行tips转mark");
////                }
//                case TASK:
////                if(type!=0){
////                    throw new Exception("非采集任务不允许执行tips转mark");
////                }
//                    break;
//            }
//            taskPar.put("workType", jobNature);
//            taskPar.put("area", jobType);
//
//            taskPar.put("userId", cmsInfo.get("userNickName"));
//            taskPar.put("workSeason", SystemConfigFactory.getSystemConfig().getValue(PropConstant.seasonVersion));
//            taskPar.put("markTaskType",jobRelation.getItemType().value());
//
//            parameter.put("taskInfo", taskPar);
//
//            String cmsUrl = SystemConfigFactory.getSystemConfig().getValue(PropConstant.cmsUrl);
//            Map<String,String> parMap = new HashMap<String,String>();
//            parMap.put("parameter", parameter.toString());
//            log.info(parameter.toString());
//            jobProgress.setMessage(parameter.toString());
//            String result = ServiceInvokeUtil.invoke(cmsUrl, parMap, 10000);
//            //result="{success:false, msg:\"没有找到用户名为【fm_meta_all_sp6】元数据库版本信息！\"}";
//            JSONObject res = null;
//            try {
//                res = JSONObject.fromObject(result);
//            }catch (Exception ex){
//                res=null;
//                jobProgress.setStatus(JobProgressStatus.FAILURE);
//                jobProgress.setMessage(jobProgress.getMessage()+result);
//            }
//            if(res!=null) {
//                boolean success = res.getBoolean("success");
//                if (success) {
//                    jobProgress.setStatus(JobProgressStatus.SUCCESS);
//                } else {
//                    log.error("cms error msg" + res.get("msg"));
//                    jobProgress.setStatus(JobProgressStatus.FAILURE);
//                    jobProgress.setMessage(jobProgress.getMessage() + "cms error:" + res.get("msg").toString());
//                }
//            }
            jobProgressOperator.updateStatus(jobProgress, JobProgressStatus.SUCCESS);
            return jobProgress.getStatus();
        } catch (Exception ex) {
            //有异常，更新状态为执行失败
            DbUtils.rollback(conn);
            if (jobProgressOperator != null && jobProgress != null) {
                jobProgress.setStatus(JobProgressStatus.FAILURE);
                jobProgress.setMessage(jobProgress.getMessage() + ex.getMessage());
                jobProgressOperator.updateStatus(jobProgress, JobProgressStatus.FAILURE);
            }
            throw ex;
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }
}