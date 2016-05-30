package com.navinfo.dataservice.web.job.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.api.job.model.JobStep;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.commons.util.DateUtils;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.jobframework.service.JobService;

import net.sf.json.JSONObject;

/** 
* @ClassName: JobController 
* @author Xiao Xiaowen 
* @date 2016年4月6日 下午6:25:24 
* @Description: TODO
*/
@Controller
public class JobController extends BaseController {
	protected Logger log = LoggerRepos.getLogger(this.getClass());
	
	@Resource(name="jobService")
	private JobService service;

	@RequestMapping(value = "/hello")
	public ModelAndView hello(HttpServletRequest request){
		try{
			return new ModelAndView("jsonView", success(service.hello()));
		}catch(Exception e){
			log.error("内部错误，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	@RequestMapping(value = "/create")
	public ModelAndView create(HttpServletRequest request){
		try{
			String jobType = URLDecode(request.getParameter("jobType"));
			JSONObject jobRequest = JSONObject.fromObject(URLDecode(request.getParameter("request")));
			String projectId = URLDecode(request.getParameter("projectId"));
			String userId = URLDecode(request.getParameter("userId"));
			String descp = URLDecode(request.getParameter("descp"));
			if(StringUtils.isEmpty(jobType)){
				throw new IllegalArgumentException("jobType参数不能为空。");
			}
			if(jobRequest==null){
				throw new IllegalArgumentException("request参数不能为空。");
			}
			long jobId = service.create(jobType, jobRequest, Long.valueOf(projectId), Long.valueOf(userId), descp);
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("jobId", jobId);
			return new ModelAndView("jsonView", success("job已创建。",data));
		}catch(Exception e){
			log.error("job创建失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	@RequestMapping(value = "/get")
	public ModelAndView get(HttpServletRequest request){
		try{
			String jobId = URLDecode(request.getParameter("jobId"));
			if(StringUtils.isEmpty(jobId)){
				throw new IllegalArgumentException("jobId参数不能为空。");
			}
			JobInfo jobInfo = service.getJobById(Long.valueOf(jobId));
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("jobId", jobInfo.getId());
			data.put("createTime", DateUtils.dateToString(jobInfo.getCreateTime()));
			data.put("beginTime", DateUtils.dateToString(jobInfo.getBeginTime()));
			data.put("endTime", DateUtils.dateToString(jobInfo.getEndTime()));
			data.put("status", jobInfo.getStatus());
			data.put("stepCount", jobInfo.getStepCount());
			if(jobInfo.getStepListSize()>0){
				JobStep step = jobInfo.getSteps().get(jobInfo.getStepListSize()-1);
				data.put("latestStepSeq", step.getStepSeq());
				data.put("latestStepMsg", step.getStepMsg());
			}else{
				data.put("latestStepSeq", null);
				data.put("latestStepMsg", null);
				
			}
			return new ModelAndView("jsonView", success(data));
		}catch(Exception e){
			log.error("job创建失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
}
