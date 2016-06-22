package com.navinfo.dataservice.web.man.controller;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navinfo.dataservice.api.job.iface.JobApiService;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.commons.token.AccessToken;
import com.navinfo.dataservice.engine.man.block.BlockService;

/** 
* @ClassName: BlockController 
* @author code generator 
* @date 2016年4月6日 下午6:25:24 
* @Description: TODO
*/
@Controller
public class ProduceController extends BaseController {
	private Logger log = LoggerRepos.getLogger(this.getClass());
	@Autowired 
	private BlockService service;

	/**
	 * 日出品管理--生成POI&Road日出品包,生成POI日出品包
	 * 判断类型，如果是POI，将grid范围内的POI数据刷到出品库；否则将grid范围内的全部数据刷到出品库。再调用出品转换脚本生成出品包。
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/produce/generateDaily")
	public ModelAndView generateDaily(HttpServletRequest request){
		try{	
			String parameter = request.getParameter("parameter");
			if (StringUtils.isEmpty(parameter)){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}		
			JSONObject dataJson = JSONObject.fromObject(URLDecode(parameter));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			//long userId=tokenObj.getUserId();
			long userId=2;
			JobApiService jobApi=(JobApiService) ApplicationContextUtil.getBean("jobApiService");
			/*
			 * {"gridIds":[213424,343434,23423432],"stopTime":"yyyymmddhh24miss","dataType":"POI"//POI,ALL}
			 * jobType:releaseFmIdbDailyJob/releaseFmIdbMonthlyJob
			 */
			//TODO
			JSONObject jobDataJson=new JSONObject();
			jobDataJson.put("gridList", dataJson.get("gridIds"));
			jobDataJson.put("dataType", dataJson.get("dataType"));
			jobDataJson.put("stopTime", "20160616000000");
			long jobId=jobApi.createJob("releaseFmIdbDailyJob", jobDataJson, userId, "日出品");
			return new ModelAndView("jsonView", success(jobId));
		}catch(Exception e){
			log.error("创建失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	
	/**
	 * 月出品管理
	 * 选中需要出品的city，点击“生成月出品包”按钮，后台提取需要出品的数据履历并灌月出品库，进行月出品的转换：
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/produce/generateMonthly")
	public ModelAndView generateMonthly(HttpServletRequest request){
		try{	
			String parameter = request.getParameter("parameter");
			if (StringUtils.isEmpty(parameter)){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}		
			JSONObject dataJson = JSONObject.fromObject(URLDecode(parameter));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			//long userId=tokenObj.getUserId();
			long userId=2;
			JobApiService jobApi=(JobApiService) ApplicationContextUtil.getBean("jobApiService");
			/*
			 * {"gridIds":[213424,343434,23423432],"stopTime":"yyyymmddhh24miss","dataType":"POI"//POI,ALL}
			 * jobType:releaseFmIdbDailyJob/releaseFmIdbMonthlyJob
			 */
			//TODO
			JSONObject jobDataJson=new JSONObject();
			jobDataJson.put("gridList", dataJson.get("gridIds"));
			jobDataJson.put("stopTime", "20160616000000");
			long jobId=jobApi.createJob("releaseFmIdbMonthlyJob", jobDataJson,userId, "月出品");
			return new ModelAndView("jsonView", success(jobId));
		}catch(Exception e){
			log.error("创建失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	
	/**
	 * 融合管理--融合月编辑库
	 * 选中需要融合的grid，点击“融合月编辑库”按钮，后台进行融合
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/produce/merge")
	public ModelAndView merge(HttpServletRequest request){
		try{	
			String parameter = request.getParameter("parameter");
			if (StringUtils.isEmpty(parameter)){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}		
			JSONObject dataJson = JSONObject.fromObject(URLDecode(parameter));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			AccessToken tokenObj=(AccessToken) request.getAttribute("token");
			//long userId=tokenObj.getUserId();
			long userId=2;
			JobApiService jobApi=(JobApiService) ApplicationContextUtil.getBean("jobApiService");
			/*
			 * {"gridIds":[213424,343434,23423432],"stopTime":"yyyymmddhh24miss"}
			 * jobType:releaseFmidbDaily/releaseFmidbMonthly
			 */
			dataJson.put("stopTime", "20160616000000");
			//String featureType = (String) dataJson.get("featureType");//featureType:POI,ROAD
			//dataJson.put("featureType", dataJson.get("featureType"));//featureType:POI,ROAD
			//TODO 道路日落月，poi后台定时脚本
			/*long jobId=0;
			if (featureType.equals("POI")){
				jobId=jobApi.createJob("day2MonthPoiJob", dataJson, userId, "POI月融合");	
				return new ModelAndView("jsonView", success(jobId));
			}*/
			long jobId=jobApi.createJob("day2MonthRoadJob", dataJson, userId, "ROAD月融合");	
			return new ModelAndView("jsonView", success(jobId));
			
		}catch(Exception e){
			log.error("创建失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	
}
