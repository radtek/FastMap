package com.navinfo.dataservice.web.column.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navinfo.dataservice.api.job.iface.JobApi;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.commons.token.AccessToken;
import com.navinfo.dataservice.control.column.core.ColumnCoreControl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * POI精编作业
 * @author wangdongbin
 *
 */
@Controller
public class ColumnController extends BaseController {
	private static final Logger logger = Logger.getLogger(ColumnController.class);
	
	/**
	 * POI月编作业数据申请接口
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/applyPoi")
	public ModelAndView applyPoi(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			AccessToken tokenObj = (AccessToken) request.getAttribute("token");
			
			long userId = tokenObj.getUserId();

			ColumnCoreControl control = new ColumnCoreControl();

			int count = control.applyData(jsonReq, userId);

			return new ModelAndView("jsonView", success(count));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 作业数据查询接口
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/columnQuery")
	public ModelAndView columnQuery(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			AccessToken tokenObj = (AccessToken) request.getAttribute("token");
			
			long userId = tokenObj.getUserId();
			
			ColumnCoreControl control = new ColumnCoreControl();
			
			JSONObject data = control.columnQuery(userId, jsonReq);
			
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 精编保存接口
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/columnSave")
	public ModelAndView columnSave(HttpServletRequest request)
			throws ServletException, IOException {
		
		try {
			String parameter = request.getParameter("parameter");
			JSONObject dataJson = JSONObject.fromObject(parameter);
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			long userId=tokenObj.getUserId();
			int taskId = dataJson.getInt("taskId");
			
			JobApi jobApi=(JobApi) ApplicationContextUtil.getBean("jobApi");
			
			JSONObject jobDataJson=new JSONObject();
			jobDataJson.put("userId", userId);
			jobDataJson.put("param", dataJson);
			
			long jobId=jobApi.createJob("columnSaveJob", jobDataJson, userId,taskId, "精编保存");
			
			
			return new ModelAndView("jsonView", success(jobId));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
		
	}
	
	/**
	 * 精编提交接口
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/columnSubmit")
	public ModelAndView columnSubmit(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject dataJson = JSONObject.fromObject(parameter);
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			long userId=tokenObj.getUserId();
			
			int taskId = dataJson.getInt("taskId");
			String firstWorkItem = dataJson.getString("firstWorkItem");
			String secondWorkItem = dataJson.getString("secondWorkItem");
			
			JobApi jobApi=(JobApi) ApplicationContextUtil.getBean("jobApi");
			
			JSONObject jobDataJson=new JSONObject();
			jobDataJson.put("taskId", taskId);
			jobDataJson.put("userId", userId);
			jobDataJson.put("firstWorkItem", firstWorkItem);
			jobDataJson.put("secondWorkItem", secondWorkItem);
			
			long jobId=jobApi.createJob("columnSubmitJob", jobDataJson, userId,taskId, "精编提交");
			
			return new ModelAndView("jsonView", success(jobId));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 精编任务的统计查询
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/task/statistics")
	public ModelAndView taskStatistics(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(URLDecode(parameter));
			
			ColumnCoreControl control = new ColumnCoreControl();
			JSONObject result = control.taskStatistics(jsonReq);
			
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 查询二级作业项的统计信息
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/secondWorkStatistics")
	public ModelAndView secondWorkStatistics(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(URLDecode(parameter));
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			long userId=tokenObj.getUserId();
			
			ColumnCoreControl control = new ColumnCoreControl();
			JSONObject result = control.secondWorkStatistics(jsonReq, userId);
			
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 根据作业组，查询精编任务列表
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "poi/column/queryTaskList")
	public ModelAndView queryTaskList(HttpServletRequest request)
			throws ServletException, IOException {
		try {
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			JSONObject jsonReq = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			
			long userId=tokenObj.getUserId();
			
			ColumnCoreControl control = new ColumnCoreControl();
			List result = control.queryTaskList(userId,jsonReq);
			
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}


	
	/**
	 * 月编专项库存统计接口
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/queryKcLog")
	public ModelAndView getLogCount(HttpServletRequest request) throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		logger.info("start getLogCount");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			logger.debug("parameter="+jsonReq);
			
			AccessToken tokenObj = (AccessToken) request.getAttribute("token");
			
			long userId = tokenObj.getUserId();
			
			int subtaskId = jsonReq.getInt("subtaskId");
			
			ColumnCoreControl columnControl = new ColumnCoreControl();
			
			JSONObject result = columnControl.getLogCount(subtaskId, userId);
			logger.info("end getLogCount");
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	
	/**
	 * 常规作业员下拉列表
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/queryWorkerList")
	public ModelAndView queryWorkerList(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			AccessToken tokenObj = (AccessToken) request.getAttribute("token");
			
			long userId = tokenObj.getUserId();
			
			ColumnCoreControl control = new ColumnCoreControl();
			
			JSONArray data = control.getQueryWorkerList(userId, jsonReq);
			
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	
	/**
	 * 质检问题查询
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/queryQcProblem")
	public ModelAndView queryQcProblem(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			ColumnCoreControl control = new ColumnCoreControl();
			
			JSONArray data = control.queryQcProblem(jsonReq);
			
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 质检问题保存
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/saveQcProblem")
	public ModelAndView saveQcProblem(HttpServletRequest request)
			throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			ColumnCoreControl control = new ColumnCoreControl();
			
			JSONObject data = control.saveQcProblem(jsonReq);
			
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	
	/**
	 * 月编子任务统计接口,关闭月编子任务判断使用(查询改子任务范围内,是否有未提交的数据)
	 * @param request
	 * @return 1有数据-不可关闭;0无数据-可关闭
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/poi/column/querySubtaskStatics")
	public ModelAndView getSubTaskStatics(HttpServletRequest request) throws ServletException, IOException {
		
		String parameter = request.getParameter("parameter");
		
		logger.info("start getSubTaskStatics");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			logger.debug("parameter="+jsonReq);
			
			int subtaskId = jsonReq.getInt("subtaskId");
			
			ColumnCoreControl columnControl = new ColumnCoreControl();
			
			int result = columnControl.getSubTaskStatics(subtaskId);
			logger.info("end getSubTaskStatics");
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
}
