package com.navinfo.dataservice.web.fcc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.constant.PropConstant;
import com.navinfo.dataservice.commons.photo.Photo;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.commons.util.DateUtils;
import com.navinfo.dataservice.commons.util.ResponseUtils;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.commons.util.ZipUtils;
import com.navinfo.dataservice.engine.audio.Audio;
import com.navinfo.dataservice.engine.audio.AudioImport;
import com.navinfo.dataservice.engine.dropbox.manger.UploadService;
import com.navinfo.dataservice.engine.fcc.patternImage.PatternImageExporter;
import com.navinfo.dataservice.engine.fcc.patternImage.PatternImageImporter;
import com.navinfo.dataservice.engine.fcc.tips.TipsExporter;
import com.navinfo.dataservice.engine.fcc.tips.TipsOperator;
import com.navinfo.dataservice.engine.fcc.tips.TipsSelector;
import com.navinfo.dataservice.engine.fcc.tips.TipsUpload;
import com.navinfo.dataservice.engine.photo.CollectorImport;

@Controller
public class TipsController extends BaseController {

	private static final Logger logger = Logger.getLogger(TipsController.class);

	@RequestMapping(value = "/tip/checkUpdate")
	public ModelAndView checkUpdate(HttpServletRequest request
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			//grid和date的对象数组
			JSONArray condition = jsonReq.getJSONArray("condition");
			
			 if (condition==null||condition.isEmpty()) {
	                throw new IllegalArgumentException("参数错误:condition不能为空");
	         }
			
			TipsSelector selector = new TipsSelector();
			
			JSONArray  resutArr=new JSONArray();
			
			for (Object object : condition) {
				
				JSONObject obj=JSONObject.fromObject(object);
				
				String grid=obj.getString("grid");
				
				 if (StringUtils.isEmpty(grid)) {
		                throw new IllegalArgumentException("参数错误：grid不能为空。");
		            }
				
				String date=obj.getString("date");
				
				if("null".equalsIgnoreCase(date)){
				    
				    date=null;
				}
				
				JSONObject result=new JSONObject();
				
				result.put("grid", grid);
				
				result.put("result", selector.checkUpdate(
						grid,date));
				
				resutArr.add(result);
			}
			
			return new ModelAndView("jsonView", success(resutArr));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/tip/edit")
	public ModelAndView edit(HttpServletRequest request )
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String rowkey = jsonReq.getString("rowkey");

			//int stage = jsonReq.getInt("stage");

			int handler = jsonReq.getInt("handler");
			
			String mdFlag= jsonReq.getString("mdFlag");
			
			 if (StringUtils.isEmpty(rowkey)) {
	                throw new IllegalArgumentException("参数错误:rowkey不能为空");
	         }
			
			 if (StringUtils.isEmpty(mdFlag)) {
	                throw new IllegalArgumentException("参数错误:mdFlag不能为空");
	         }
			
			  //值域验证
            if(!"m".equals(mdFlag)&&!"d".equals(mdFlag)){
            	 throw new IllegalArgumentException("参数错误:mdflag值域错误。");
            }


			String pid = null;

			if (jsonReq.containsKey("pid")) {
				pid = jsonReq.getString("pid");
			}

			TipsOperator op = new TipsOperator();

			op.update(rowkey,  handler, pid,mdFlag);

			return new ModelAndView("jsonView", success());

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	
	
	/**
	 * @Description:批量编辑tips状态
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @author: y
	 * @time:2017-2-8 上午9:15:07
	 */
	@RequestMapping(value = "/tip/batchEditStatus")
	public ModelAndView batchEditStatus(HttpServletRequest request )
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			//{mdflag:'',handler:'',data:[{rowkey:'',status:''}]}

			JSONArray data = jsonReq.getJSONArray("data");

			int handler = jsonReq.getInt("handler");
			
			String mdFlag= jsonReq.getString("mdFlag");
			
			 if (data==null||data.size()==0) {
	                throw new IllegalArgumentException("参数错误:data不能为空");
	         }
			
			 if (StringUtils.isEmpty(mdFlag)) {
	                throw new IllegalArgumentException("参数错误:mdFlag不能为空");
	         }
			
			  //值域验证
            if(!"m".equals(mdFlag)&&!"d".equals(mdFlag)){
            	 throw new IllegalArgumentException("参数错误:mdflag值域错误。");
            }

			TipsOperator op = new TipsOperator();

			op.batchUpdateStatus(data,handler,mdFlag);

			return new ModelAndView("jsonView", success());

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	@RequestMapping(value = "/tip/import")
	public ModelAndView importTips(HttpServletRequest request
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");
		
		logger.info("开始上传tips,parameter:"+parameter);
		try {
		    
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject json = JSONObject.fromObject(parameter);

			int jobId = json.getInt("jobId");
			
			int subtaskId = 0;
			
			//外业，有可能没有任务号
			if(json.containsKey("subtaskId")){
				
				subtaskId=json.getInt("subtaskId");
			}

			UploadService upload = UploadService.getInstance();

			String filePath = upload.unzipByJobId(jobId);
			
			logger.info("jobId"+jobId+"\tfilePath:"+filePath);
			
			TipsUpload tipsUploader = new TipsUpload(subtaskId);
			
			Map<String, Photo> photoMap=new HashMap<String, Photo>();
			
			Map<String, Audio> audioMap=new HashMap<String, Audio>();
			
			tipsUploader.run(filePath + "/"+ "tips.txt",photoMap,audioMap);
			
			//CollectorImport.importPhoto(map, filePath + "/photo");
			
			CollectorImport.importPhoto(photoMap, filePath );
			
			AudioImport.importAudio(audioMap,filePath);
			
			JSONArray patternImageResultImpResult=PatternImageImporter.importImage(filePath + "/"+ "JVImage.txt",filePath +"/JVImage"); //JVImage为模式图的文件夹
			
			JSONObject result = new JSONObject();

			result.put("total", tipsUploader.getTotal());

			result.put("failed", tipsUploader.getFailed());

			result.put("reasons", tipsUploader.getReasons());
			
			result.put("JVImageResult", patternImageResultImpResult);
			
			logger.info("开始上传tips完成，jobId:"+jobId+"\tresult:"+result);

			return new ModelAndView("jsonView", success(result));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}

	}

	@RequestMapping(value = "/tip/export")
	public ModelAndView exportTips(HttpServletRequest request )
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");
		
		logger.info("下载tips,parameter:"+parameter);
		
		try {
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String day = StringUtils.getCurrentDay();

			String uuid = UuidUtils.genUuid();
			
			String downloadFilePath = SystemConfigFactory.getSystemConfig().getValue(
					PropConstant.downloadFilePathTips);

			String parentPath = downloadFilePath +File.separator+ day + "/";

			String filePath = parentPath + uuid + "/";

			File file = new File(filePath);

			if (!file.exists()) {
				file.mkdirs();
			}
			//grid和date的对象数组
			JSONArray condition = jsonReq.getJSONArray("condition");
			
			 if (condition==null||condition.isEmpty()) {
	                throw new IllegalArgumentException("参数错误:condition不能为空");
	         }

			TipsExporter op = new TipsExporter();
			
			Set<String> images = new HashSet<String>();
            //1.下载tips、照片、语音(照片的语音根据附件的id下载)
			int expCount=op.export(condition, filePath, "tips.txt", images);
			
			//2.模式图下载： 1406和1401需要导出模式图
			if(images.size()>0){
			
				PatternImageExporter exporter = new PatternImageExporter();
				
				exporter.export2SqliteByNames(filePath, images);
			}

			String zipFileName = uuid + ".zip";

			String zipFullName = parentPath + zipFileName;
            //3.打zip包
			ZipUtils.zipFile(filePath, zipFullName);
			
			String serverUrl =  SystemConfigFactory.getSystemConfig().getValue(
					PropConstant.serverUrl);
			
			String downloadUrlPath = SystemConfigFactory.getSystemConfig().getValue(
					PropConstant.downloadUrlPathTips);
            //4.返回的url
			String url = serverUrl + downloadUrlPath +File.separator+ day + "/"
					+ zipFileName;
			
			logger.info("url:"+url);
			
			JSONObject result=null; //如果没有数据，则返回 {"errmsg":"success","data":null，errcode":0} ,不返回url
			if(expCount>0){
				result=new JSONObject();
				
				result.put("url", url);
				
				result.put("downloadDate",  DateUtils.dateToString(new Date(),
						DateUtils.DATE_COMPACTED_FORMAT));	
				
				logger.info("下载tips完成,resut :"+result);
			}else{
				logger.info("下载tips完成,没有可下载的数据");
			}
			
			return new ModelAndView("jsonView", success(result));

		} catch (Exception e) {

			logger.error("下载tips出错："+e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	@RequestMapping(value = "/tip/getByRowkey")
	public void getByRowkey(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String rowkey = jsonReq.getString("rowkey");
			
			 if (StringUtils.isEmpty(rowkey)) {
                 throw new IllegalArgumentException("参数错误：rowkey不能为空");
             }


			TipsSelector selector = new TipsSelector();

			JSONObject data = selector.searchDataByRowkey(rowkey);

			response.getWriter().println(
					ResponseUtils.assembleRegularResult(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			response.getWriter().println(
					ResponseUtils.assembleFailResult(e.getMessage()));
		}
	}
	
	
	@RequestMapping(value = "/tip/getByRowkeyNew")
	public void getByRowkeyNew(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String rowkey = jsonReq.getString("rowkey");
			
			 if (StringUtils.isEmpty(rowkey)) {
                 throw new IllegalArgumentException("参数错误：rowkey不能为空");
             }


			TipsSelector selector = new TipsSelector();

			JSONObject data = selector.searchDataByRowkeyNew(rowkey);

			response.getWriter().println(
					ResponseUtils.assembleRegularResult(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			response.getWriter().println(
					ResponseUtils.assembleFailResult(e.getMessage()));
		}
	}
	
	
	
	@RequestMapping(value = "/tip/getByRowkeys")
	public void getByRowkeys(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			JSONArray rowkeyArr = jsonReq.getJSONArray("rowkey");
			
			 if (rowkeyArr==null||rowkeyArr.isEmpty()||rowkeyArr.size()==0) {
                 throw new IllegalArgumentException("参数错误：rowkeys不能为空");
             }

			TipsSelector selector = new TipsSelector();

			JSONArray data = selector.searchDataByRowkeyArr(rowkeyArr);

			response.getWriter().println(
					ResponseUtils.assembleRegularResult(data));
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			response.getWriter().println(
					ResponseUtils.assembleFailResult(e.getMessage()));
		}
	}

	
	@RequestMapping(value = "/tip/getBySpatial")
	public ModelAndView getBySpatial(HttpServletRequest request
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String wkt = jsonReq.getString("wkt");
			
			 if (StringUtils.isEmpty(wkt)) {
	                throw new IllegalArgumentException("参数错误：wkt不能为空");
	            }

			TipsSelector selector = new TipsSelector();

			JSONArray array = selector.searchDataBySpatial(wkt);

			return new ModelAndView("jsonView", success(array));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/tip/getSnapshot")
	public void getSnapshot(HttpServletRequest request, HttpServletResponse response
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
		    
		    if (StringUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("parameter参数不能为空。");
            }
		    
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			JSONArray grids = jsonReq.getJSONArray("grids");
			
			if (grids==null||grids.size()==0) {
                throw new IllegalArgumentException("参数错误:grids不能为空。");
            }

			String type = jsonReq.getString("type");

			JSONArray stage = jsonReq.getJSONArray("stage");

			int dbId = jsonReq.getInt("dbId");
			
			int subtaskId = jsonReq.getInt("subtaskId");
			
			String mdFlag = jsonReq.getString("mdFlag");
			
			if (grids==null||grids.size()==0) {
                throw new IllegalArgumentException("参数错误:grids不能为空。");
            }
            
            if (stage==null||stage.size()==0) {
                throw new IllegalArgumentException("参数错误:stage不能为空。");
            }
            
            if (StringUtils.isEmpty(mdFlag)) {
                throw new IllegalArgumentException("参数错误:mdFlag不能为空。");
            }
            
            //值域验证
            if(!"m".equals(mdFlag)&&!"d".equals(mdFlag)&&!"f".equals(mdFlag)){
            	 throw new IllegalArgumentException("参数错误:mdflag值域错误。");
            }

			TipsSelector selector = new TipsSelector();
			

			JSONArray array = selector.getSnapshot(grids, stage, Integer.parseInt(type),
					dbId,mdFlag,subtaskId);

			response.getWriter().println(
					ResponseUtils.assembleRegularResult(array));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			response.getWriter().println(
					ResponseUtils.assembleFailResult(e.getMessage()));
		}
	}

	@RequestMapping(value = "/tip/getStats")
	public ModelAndView getStats(HttpServletRequest request
			) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			JSONArray grids = jsonReq.getJSONArray("grids");

			JSONArray stages = jsonReq.getJSONArray("stage");
			
			int subtaskId = jsonReq.getInt("subtaskId");
			
			if (grids==null||grids.size()==0) {
                throw new IllegalArgumentException("参数错误:grids不能为空。");
            }
			
			if (stages==null||stages.size()==0) {
                throw new IllegalArgumentException("参数错误:stages不能为空。");
            }

			TipsSelector selector = new TipsSelector();
			
			JSONObject data = selector.getStats(grids, stages,subtaskId);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/tip/getByWkt")
	public void getTipsByWkt(HttpServletRequest request,
							  HttpServletResponse response) throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String wkt = jsonReq.getString("wkt");

			String flag = jsonReq.getString("flag");

			JSONArray types = new JSONArray();

			TipsSelector selector = new TipsSelector();

			JSONArray array = selector.searchDataByWkt(wkt,
					types, flag);

			response.getWriter().println(
					ResponseUtils.assembleRegularResult(array));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			response.getWriter().println(
					ResponseUtils.assembleFailResult(e.getMessage()));
		}
	}

}
