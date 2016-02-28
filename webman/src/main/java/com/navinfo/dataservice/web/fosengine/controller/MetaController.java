package com.navinfo.dataservice.web.fosengine.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.navinfo.dataservice.meta.MeshSelector;
import org.navinfo.dataservice.meta.PatternImageExporter;
import org.navinfo.dataservice.meta.PatternImageSelector;
import org.navinfo.dataservice.meta.PinyinConverter;
import org.navinfo.dataservice.meta.RdNameSelector;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.navinfo.dataservice.commons.db.DBOraclePoolManager;
import com.navinfo.dataservice.commons.util.Log4jUtils;
import com.navinfo.dataservice.man.version.VersionSelector;
import com.navinfo.dataservice.web.util.ResponseUtil;
import com.navinfo.navicommons.springmvc.BaseController;

@Controller
public class MetaController extends BaseController {
	private static final Logger logger = Logger
			.getLogger(MetaController.class);

	@RequestMapping(value = "/meta/rdname/search")
	public void searchRdName(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseUtil.setResponseHeader(response);

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String name = jsonReq.getString("name");

			int pageSize = jsonReq.getInt("pageSize");

			int pageNum = jsonReq.getInt("pageNum");

			Connection metaConn = DBOraclePoolManager
					.getConnectionByName("meta");

			RdNameSelector selector = new RdNameSelector(metaConn);

			JSONObject result = selector.searchByName(name, pageSize, pageNum);

			response.getWriter().println(
					ResponseUtil.assembleRegularResult(result));

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}

	@RequestMapping(value = "/meta/pinyin/convert")
	public void convertPinyin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseUtil.setResponseHeader(response);

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String word = jsonReq.getString("word");

			Connection conn = DBOraclePoolManager.getConnectionByName("meta");

			PinyinConverter py = new PinyinConverter(conn);

			String[] result = py.convert(word);

			if (result != null) {
				JSONObject json = new JSONObject();

				json.put("voicefile", result[0]);

				json.put("phonetic", result[1]);

				response.getWriter().println(
						ResponseUtil.assembleRegularResult(json));
			} else {
				throw new Exception("转拼音失败");
			}

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}

	@RequestMapping(value = "/meta/province/getByLocation")
	public void getProvinceByLocation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseUtil.setResponseHeader(response);

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			double lon = jsonReq.getDouble("lon");

			double lat = jsonReq.getDouble("lat");

			Connection conn = DBOraclePoolManager.getConnectionByName("meta");

			MeshSelector selector = new MeshSelector(conn);

			JSONObject data = selector.getProvinceByLocation(lon, lat);

			if (data != null) {
				response.getWriter().println(
						ResponseUtil.assembleRegularResult(data));
			} else {
				throw new Exception("不在中国省市范围内");
			}

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}

	@RequestMapping(value = "/meta/patternImage/checkUpdate")
	public void checkPatternImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseUtil.setResponseHeader(response);

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String date = jsonReq.getString("date");

			Connection metaConn = DBOraclePoolManager
					.getConnectionByName("meta");

			PatternImageSelector selector = new PatternImageSelector(metaConn);

			boolean flag = selector.checkUpdate(date);

			response.getWriter().println(
					ResponseUtil.assembleRegularResult(flag));

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}

	@RequestMapping(value = "/meta/patternImage/download")
	public void downloadPatternImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseUtil.setResponseHeader(response);

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			Connection metaConn = DBOraclePoolManager
					.getConnectionByName("meta");

			PatternImageExporter exporter = new PatternImageExporter(metaConn);

			String fileName = "";

			String url = "http://192.168.4.130:8080/download/patternimage";

			String path = "";

			if (jsonReq.containsKey("names")) {
				JSONArray names = jsonReq.getJSONArray("names");

				path = "/root/download/patternimage/byname";

				fileName = exporter.export2SqliteByNames(path, names);

				url += "/byname/" + fileName;
			} else if (jsonReq.containsKey("date")) {
				String date = jsonReq.getString("date");

				path = "/root/download/patternimage/bydate";

				fileName = exporter.export2SqliteByDate(path, date);

				url += "/bydate/" + fileName;
			} else {
				throw new Exception("错误的参数");
			}

			String fullPath = path + "/" + fileName;

			File f = new File(fullPath);

			long filesize = f.length();

			String version = fileName.replace(".zip", "");

			JSONObject json = new JSONObject();

			Connection conn = DBOraclePoolManager.getConnectionByName("man");

			VersionSelector selector = new VersionSelector(conn);

			String specVersion = selector.getByType(3);

			json.put("version", version);

			json.put("url", url);

			json.put("filesize", filesize);

			json.put("specVersion", specVersion);

			response.getWriter().println(
					ResponseUtil.assembleRegularResult(json));

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}

	@RequestMapping(value = "/meta/patternImage/getById")
	public void getPatternImageById(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/jpeg;charset=GBK");

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE,PUT");

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String id = jsonReq.getString("id");

			Connection metaConn = DBOraclePoolManager
					.getConnectionByName("meta");

			PatternImageSelector selector = new PatternImageSelector(metaConn);

			byte[] data = selector.getById(id);

			response.getOutputStream().write(data);

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}

	@RequestMapping(value = "/meta/patternImage/search")
	public void searchPatternImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseUtil.setResponseHeader(response);

		String parameter = request.getParameter("parameter");

		try {
			JSONObject jsonReq = JSONObject.fromObject(parameter);

			String name = jsonReq.getString("name");

			int pageSize = jsonReq.getInt("pageSize");

			int pageNum = jsonReq.getInt("pageNum");

			Connection metaConn = DBOraclePoolManager
					.getConnectionByName("meta");

			PatternImageSelector selector = new PatternImageSelector(metaConn);

			JSONObject obj = selector.searchByName(name, pageSize, pageNum);

			response.getWriter().println(
					ResponseUtil.assembleRegularResult(obj));

		} catch (Exception e) {

			String logid = Log4jUtils.genLogid();

			Log4jUtils.error(logger, logid, parameter, e);

			response.getWriter().println(
					ResponseUtil.assembleFailResult(e.getMessage(), logid));
		}
	}
}
