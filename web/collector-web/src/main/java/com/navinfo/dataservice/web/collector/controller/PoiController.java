package com.navinfo.dataservice.web.collector.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.navinfo.dataservice.bizcommons.sys.SysLogConstant;
import com.navinfo.dataservice.bizcommons.sys.SysLogOperator;
import com.navinfo.dataservice.bizcommons.sys.SysLogStats;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.commons.token.AccessToken;
import com.navinfo.dataservice.commons.util.DateUtils;
import com.navinfo.dataservice.control.service.PoiServiceNew;
import com.navinfo.dataservice.control.service.UploadResult;
import com.navinfo.dataservice.engine.editplus.operation.imp.ErrorLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 
 * @ClassName: DataHubController 
 * @author Xiao Xiaowen 
 * @date 2015-11-27 上午11:44:30 
 * @Description: TODO
 */
@Controller
public class PoiController extends BaseController {
	protected Logger log = Logger.getLogger(this.getClass());

	@RequestMapping(value = "/poi/help")
	public ModelAndView getDb(HttpServletRequest request){
		PoiServiceNew.getInstance().logTest();
		log.info("Hello,Poi Controller...");
		return new ModelAndView("jsonView", "data", "Hello,Datahub.你好，数据中心！");
	}
	
	@RequestMapping(value = "/poi/upload")
	public ModelAndView createDb(HttpServletRequest request){
		try{
			JSONObject paraJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			if (paraJson == null) {
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			if(paraJson.get("jobId")==null){
				throw new IllegalArgumentException("jobId参数不能为空。");
			}
			int jobId = paraJson.getInt("jobId");

			int subtaskId = 0;
			if(paraJson.containsKey("subtaskId")){
				subtaskId = paraJson.getInt("subtaskId");
			}

			AccessToken tokenObj = (AccessToken) request.getAttribute("token");
			long userId = tokenObj.getUserId();
			UploadResult result = PoiServiceNew.getInstance().upload(jobId, subtaskId, userId);
			
			//记录上传日志。不抛出异常
            insertStatisticsInfoNoException(jobId, subtaskId, userId,
            		result);
			
			return new ModelAndView("jsonView", success(result));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * @Title: insertStatisticsInfoNoException
	 * @Description: 增加统计日志
	 * @param jobId
	 * @param subtaskId
	 * @param userId
	 * @param result  void
	 * @throws 
	 * @author zl zhangli5174@navinfo.com
	 * @date 2017年8月10日 下午12:29:41 
	 */
	private void insertStatisticsInfoNoException(int jobId, int subtaskId,
			long userId, UploadResult result)  {
		try{
			SysLogStats log = new SysLogStats();
			log.setLogType(SysLogConstant.POI_UPLOAD_TYPE);
			log.setLogDesc(SysLogConstant.POI_UPLOAD_TYPE+",jobId :"+jobId+",subtaskId:"+subtaskId);
			log.setFailureTotal(result.getTotal()-result.getSuccess());
			log.setSuccessTotal(result.getSuccess());  
			log.setTotal(result.getTotal());
			log.setBeginTime(DateUtils.getSysDateFormat());
			log.setEndTime(DateUtils.getSysDateFormat());
			JSONArray jsonArrFail = JSONArray.fromObject(result.getFail());
			log.setErrorMsg(jsonArrFail.toString());
			log.setUserId(String.valueOf(userId));
			SysLogOperator.getInstance().insertSysLog(log);
		
		}catch (Exception e) {
			logger.error("记录日志出错："+e.getMessage(), e);
		}
	}
	
	
	public static void main(String[] args){
//		List<ErrorLog> fail = new ArrayList<ErrorLog>();
		ErrorLog errorLog = new ErrorLog();
		errorLog.setErrorCode(0);
		errorLog.setFid("123214214");
		errorLog.setReason("测试错误日志");
		ErrorLog errorLog1 = new ErrorLog();
		errorLog1.setErrorCode(1);
		errorLog1.setFid("4444444");
		errorLog1.setReason("测试错误日志1");
//		fail.add(errorLog);
//		fail.add(errorLog1);
		
		UploadResult results = new UploadResult();
		results.setTotal(10);
		results.addSuccess(5);
		results.addFail(errorLog);
		results.addFail(errorLog1);
		 JSONArray jsonArr = JSONArray.fromObject(results.getFail());
		System.out.println(jsonArr.toString());
	}
}