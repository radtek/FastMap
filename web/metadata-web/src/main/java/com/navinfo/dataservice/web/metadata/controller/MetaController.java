package com.navinfo.dataservice.web.metadata.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.uima.pear.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navinfo.dataservice.api.fcc.iface.FccApi;
import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.api.man.model.Subtask;
import com.navinfo.dataservice.commons.config.SystemConfig;
import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.constant.PropConstant;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.commons.util.ResponseUtils;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.commons.util.ZipUtils;
import com.navinfo.dataservice.engine.meta.area.ScPointAdminArea;
import com.navinfo.dataservice.engine.meta.chain.ChainSelector;
import com.navinfo.dataservice.engine.meta.chain.FocusSelector;
import com.navinfo.dataservice.engine.meta.kindcode.KindCodeSelector;
import com.navinfo.dataservice.engine.meta.mesh.MeshSelector;
import com.navinfo.dataservice.engine.meta.patternimage.PatternImageExporter;
import com.navinfo.dataservice.engine.meta.patternimage.PatternImageSelector;
import com.navinfo.dataservice.engine.meta.pinyin.PinyinConverter;
import com.navinfo.dataservice.engine.meta.rdname.RdNameImportor;
import com.navinfo.dataservice.engine.meta.rdname.RdNameOperation;
import com.navinfo.dataservice.engine.meta.rdname.RdNameSelector;
import com.navinfo.dataservice.engine.meta.rdname.ScRoadnameTypename;
import com.navinfo.dataservice.engine.meta.workitem.Workitem;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class MetaController extends BaseController {
	private static final Logger logger = Logger.getLogger(MetaController.class);

	@RequestMapping(value = "/rdname/search")
	public ModelAndView searchRdName(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String name = jsonReq.getString("name");

			int pageSize = jsonReq.getInt("pageSize");

			int pageNum = jsonReq.getInt("pageNum");

			RdNameSelector selector = new RdNameSelector();

			JSONObject data = selector.searchByName(name, pageSize, pageNum);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/pinyin/convert")
	public ModelAndView convertPinyin(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String word = jsonReq.getString("word");

			PinyinConverter py = new PinyinConverter();

			String[] result = py.convert(word);

			if (result != null) {
				JSONObject json = new JSONObject();

				json.put("voicefile", result[0]);

				json.put("phonetic", result[1]);

				return new ModelAndView("jsonView", success(json));
			} else {
				throw new Exception("转拼音失败");
			}

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/province/getByLocation")
	public ModelAndView getProvinceByLocation(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			double lon = jsonReq.getDouble("lon");

			double lat = jsonReq.getDouble("lat");

			MeshSelector selector = new MeshSelector();

			JSONObject data = selector.getProvinceByLocation(lon, lat);

			if (data != null) {
				return new ModelAndView("jsonView", success(data));
			} else {
				throw new Exception("不在中国省市范围内");
			}

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/patternImage/checkUpdate")
	public ModelAndView checkPatternImage(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String date = jsonReq.getString("date");

			PatternImageSelector selector = new PatternImageSelector();

			boolean flag = selector.checkUpdate(date);

			return new ModelAndView("jsonView", success(flag));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/patternImage/download")
	public ModelAndView exportPatternImage(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			PatternImageExporter exporter = new PatternImageExporter();

			SystemConfig config = SystemConfigFactory.getSystemConfig();

			String url = config.getValue(PropConstant.serverUrl);

			url += config.getValue(PropConstant.downloadUrlPathPatternimg);

			String path = config
					.getValue(PropConstant.downloadFilePathPatternimg);

			String dir = null;

			String currentDate = StringUtils.getCurrentTime();

			String zipFileName = currentDate + ".zip";

			if (jsonReq.containsKey("names")) {
				JSONArray names = jsonReq.getJSONArray("names");

				path += "/byname";

				dir = path + "/" + currentDate;

				Set<String> set = new HashSet<String>();

				for (int i = 0; i < names.size(); i++) {
					set.add(names.getString(i));
				}

				exporter.export2SqliteByNames(dir, set);

				url += "/byname/" + zipFileName;
			} else if (jsonReq.containsKey("date")) {
				String date = jsonReq.getString("date");

				path += "/bydate";

				dir = path + "/" + currentDate;

				exporter.export2SqliteByDate(dir, date);

				url += "/bydate/" + zipFileName;
			} else {
				throw new Exception("错误的参数");
			}

			ZipUtils.zipFile(dir, path + "/" + currentDate + ".zip");

			FileUtil.deleteDirectory(new File(dir));

			String fullPath = path + "/" + zipFileName;

			File f = new File(fullPath);

			long filesize = f.length();

			String version = zipFileName.replace(".zip", "");

			JSONObject json = new JSONObject();

			ManApi man = (ManApi) ApplicationContextUtil.getBean("manApi");

			String specVersion = man.querySpecVersionByType(3);

			json.put("version", version);

			json.put("url", url);

			json.put("filesize", filesize);

			json.put("specVersion", specVersion);

			return new ModelAndView("jsonView", success(json));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/patternImage/getById")
	public void getPatternImageById(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/jpeg;charset=GBK");

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String id = jsonReq.getString("id");

			PatternImageSelector selector = new PatternImageSelector();

			byte[] data = selector.getById(id);

			response.getOutputStream().write(data);

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			response.getWriter().println(
					ResponseUtils.assembleFailResult(e.getMessage()));
		}
	}

	@RequestMapping(value = "/patternImage/search")
	public ModelAndView searchPatternImage(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String name = jsonReq.getString("name");

			int pageSize = jsonReq.getInt("pageSize");

			int pageNum = jsonReq.getInt("pageNum");

			PatternImageSelector selector = new PatternImageSelector();

			JSONObject data = selector.searchByName(name, pageSize, pageNum);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryTelByProvince")
	public ModelAndView searchTelByProvince(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String name = jsonReq.getString("province");

			ScPointAdminArea selector = new ScPointAdminArea();

			JSONArray data = selector.searchByProvince(name);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryChain")
	public ModelAndView queryChain(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String kindCode = null;

			if (jsonReq.has("kindCode")) {
				kindCode = jsonReq.getString("kindCode");
			}

			ChainSelector selector = new ChainSelector();

			JSONObject data = selector.getChainByKindCode(kindCode);

			if (kindCode != null && data.has(kindCode)) {
				JSONArray array = data.getJSONArray(kindCode);

				return new ModelAndView("jsonView", success(array));
			}

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/chainLevel")
	public ModelAndView queryChainLevel(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String kindCode = jsonReq.getString("kindCode");

			String chainCode = jsonReq.getString("chainCode");

			ChainSelector selector = new ChainSelector();

			String data = selector.getLevelByChain(chainCode, kindCode);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryFocus")
	public ModelAndView queryFocus(HttpServletRequest request)
			throws ServletException, IOException {

		try {

			FocusSelector selector = new FocusSelector();

			JSONArray data = selector.getPoiNum();

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryTelLength")
	public ModelAndView searchTelLength(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String code = jsonReq.getString("code");

			ScPointAdminArea selector = new ScPointAdminArea();

			String data = selector.searchTelLength(code);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryFoodType")
	public ModelAndView searchFoodType(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String kindId = jsonReq.getString("kindId");

			ScPointAdminArea selector = new ScPointAdminArea();

			JSONArray data = selector.searchFoodType(kindId);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/kindLevel")
	public ModelAndView searchKindLevel(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String kindId = jsonReq.getString("kindCode");

			KindCodeSelector selector = new KindCodeSelector();

			JSONObject data = selector.searchkindLevel(kindId);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryTopKind")
	public ModelAndView QueryTopKind(HttpServletRequest request)
			throws ServletException, IOException {

		try {
			KindCodeSelector selector = new KindCodeSelector();

			JSONArray data = selector.queryTopKindInfo();

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryMediumKind")
	public ModelAndView queryMediumKind(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String topId = jsonReq.getString("topId");

			KindCodeSelector selector = new KindCodeSelector();

			JSONArray data = selector.queryMediumKindInfo(topId);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	@RequestMapping(value = "/queryKind")
	public ModelAndView queryKind(HttpServletRequest request)
			throws ServletException, IOException {

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			int region = jsonReq.getInt("region");

			KindCodeSelector selector = new KindCodeSelector();

			JSONArray data = selector.queryKindInfo(region);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * web端查询rdName
	 * @author wangdongbin
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/rdname/websearch")
	public ModelAndView searchRdNameForWeb(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			RdNameSelector selector = new RdNameSelector();
			
			int subtaskId = jsonReq.getInt("subtaskId");
			
			ManApi apiService=(ManApi) ApplicationContextUtil.getBean("manApi");
			
			Subtask subtask = apiService.queryBySubtaskId(subtaskId);
			
			FccApi apiFcc=(FccApi) ApplicationContextUtil.getBean("fccApi");
			
			JSONArray tips = apiFcc.searchDataBySpatial(subtask.getGeometry(),1901,new JSONArray());
			
			JSONObject data = selector.searchForWeb(jsonReq,tips);

			return new ModelAndView("jsonView", success(data));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * web端插入或更新rdName
	 * @author wangdongbin
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/rdname/websave")
	public ModelAndView webSave(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			JSONObject data = jsonReq.getJSONObject("data");
			
			RdNameImportor importor = new RdNameImportor();
			
			JSONObject result = importor.importRdNameFromWeb(data);
			
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * web端道路名拆分接口
	 * @author wangdongbin
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/rdname/webteilen")
	public ModelAndView webTeilen(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			int flag = jsonReq.getInt("flag");
			
			RdNameOperation operation = new RdNameOperation();
			
			if (flag>0) {
				JSONArray dataList = jsonReq.getJSONArray("data");
				
				operation.teilenRdName(dataList);
			} else {
				int subtaskId = jsonReq.getInt("subtaskId");
				
				ManApi apiService=(ManApi) ApplicationContextUtil.getBean("manApi");
				
				Subtask subtask = apiService.queryBySubtaskId(subtaskId);
				
				FccApi apiFcc=(FccApi) ApplicationContextUtil.getBean("fccApi");
				
				JSONArray tips = apiFcc.searchDataBySpatial(subtask.getGeometry(),1901,new JSONArray());
				
				operation.teilenRdNameByTask(tips);
			}

			return new ModelAndView("jsonView", success());
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 获取nametype
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/rdname/nametype")
	public ModelAndView webNameType(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			int pageSize = jsonReq.getInt("pageSize");
			int pageNum = jsonReq.getInt("pageNum");
			
			String name = "";
			if (jsonReq.containsKey("name")) {
				name = jsonReq.getString("name");
			}
			
			String sortby = "";
			if (jsonReq.containsKey("sortby")) {
				sortby = jsonReq.getString("sortby");
			}
			
			ScRoadnameTypename typename = new ScRoadnameTypename();
			
			JSONObject data = typename.getNameType(pageNum,pageSize,name,sortby);
			
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}

	/**
	 * 获取行政区划
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/rdname/adminarea")
	public ModelAndView webAdminarea(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			int pageSize = jsonReq.getInt("pageSize");
			int pageNum = jsonReq.getInt("pageNum");
			
			String name = "";
			if (jsonReq.containsKey("name")) {
				name = jsonReq.getString("name");
			}
			
			String sortby = "";
			if (jsonReq.containsKey("sortby")) {
				sortby = jsonReq.getString("sortby");
			}
			
			ScPointAdminArea adminarea = new ScPointAdminArea();
			
			JSONObject data = adminarea.getAdminArea(pageSize,pageNum,name,sortby);
			
			return new ModelAndView("jsonView", success(data));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	/**
	 * 查询该组下是否存在英文/葡文名
	 * @param request
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/rdname/group")
	public ModelAndView webEngGroup(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			int nameGroupid = jsonReq.getInt("nameGroupid");
			
			RdNameOperation operation = new RdNameOperation();
			
			boolean result = operation.checkEngName(nameGroupid);
			
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
	
	@RequestMapping(value = "/deep/workitem")
	public ModelAndView getWorkItemMap(HttpServletRequest request)
			throws ServletException, IOException {
		String parameter = request.getParameter("parameter");
		
		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);
			
			int type = 0;
			if (jsonReq.containsKey("type")) {
				type = jsonReq.getInt("type");
			}
			
			Workitem workitem = new Workitem();
			
			JSONArray result = workitem.getDataMap(type);
			
			return new ModelAndView("jsonView", success(result));
		} catch (Exception e) {
	
			logger.error(e.getMessage(), e);
	
			return new ModelAndView("jsonView", fail(e.getMessage()));
		}
	}
}
