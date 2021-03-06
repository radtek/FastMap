package com.navinfo.dataservice.web.man.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.engine.man.city.CityService;

import net.sf.json.JSONObject;

/**
 * @ClassName: CityController
 * @author code generator
 * @date 2016年4月6日 下午6:25:24
 * @Description: 
 */
@Controller
public class CityController extends BaseController {
	private Logger log = LoggerRepos.getLogger(this.getClass());
	
	private CityService service=CityService.getInstance();


	@RequestMapping(value = "/city/listByWkt")
	public ModelAndView queryListByWkt(HttpServletRequest request) {
		try {
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			if (dataJson == null) {
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			List<HashMap<String, Object>> data = service.queryListByWkt(dataJson);
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {
			log.error("获取城市列表失败，原因：" + e.getMessage(), e);
			return new ModelAndView("jsonView", exception(e));
		}
	}
	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/city/monitor")
	public ModelAndView cityMonitor(HttpServletRequest request){
		try{
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			if (dataJson == null) {
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			List<Map<String,Object>> res=service.cityMonitor(dataJson);
			return new ModelAndView("jsonView",success(res));
		}catch(Exception e){
			log.error("", e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	/**
	 * 常规项目点击后高亮地图区域
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/city/query")
	public ModelAndView query(HttpServletRequest request) {
		try {
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			if (dataJson == null) {
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			HashMap<String,Object> data = service.query(dataJson);
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {
			log.error("获取明细失败，原因：" + e.getMessage(), e);
			return new ModelAndView("jsonView", exception(e));
		}
	}
	
	/*@RequestMapping(value = "/city/listByAlloc")
	public ModelAndView queryListByAlloc(HttpServletRequest request) {
		try {
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			if (dataJson == null) {
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			List<HashMap> data = service.queryListByAlloc(dataJson);
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {
			log.error("获取城市列表失败，原因：" + e.getMessage(), e);
			return new ModelAndView("jsonView", exception(e));
		}
	}*/
	
	/**
	 * city列表
	 * @author songhe
	 * @return List
	 * 
	 */
	@RequestMapping(value = "/city/listAll")
	public ModelAndView queryListAll(HttpServletRequest request){
		try{
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));
			//TODO 这里编码集需要确认
//			JSONObject dataJson = JSONObject.fromObject(new String(request.getParameter("parameter").getBytes("iso8859-1"),"utf-8"));
			JSONObject parame = new JSONObject();
			
			if(dataJson.containsKey("condition")){
				parame = JSONObject.fromObject(dataJson.getString("condition"));
			}
			
			List<Map<String,Object>> data = service.queryListAll(parame);
			
			return new ModelAndView("jsonView", success(data));
		}catch (Exception e){
			log.error("获取城市列表失败，原因：" + e.getMessage(), e);
			return new ModelAndView("jsonView", exception(e));
		}
	 }
	
}
