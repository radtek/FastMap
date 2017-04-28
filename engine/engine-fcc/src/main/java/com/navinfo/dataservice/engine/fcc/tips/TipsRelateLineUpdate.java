package com.navinfo.dataservice.engine.fcc.tips;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: TipsRelateLineUpdate.java
 * @author y
 * @date 2017-4-13 上午9:54:08
 * @Description: TODO
 * 
 */
public class TipsRelateLineUpdate {

	private JSONObject json; // tips信息（solr）
	private JSONObject line1; // 测线1
	private JSONObject line2; // 测线2
	private String sourceType = "";

	/**
	 * @param json
	 *            :要维护的tips
	 * @param line1
	 * @param line2
	 */
	public TipsRelateLineUpdate(JSONObject json, JSONObject line1,
			JSONObject line2) {
		super();
		this.json = json;
		this.line1 = line1;
		this.line2 = line2;
		sourceType = json.getString("s_sourceType");
	}

	/*
	 * 26类情报的tips 1.道路形状（测线） 2001 2.道路挂接 1803 3.立交(分层) 1116 4.道路种别 1201
	 * 5.道路方向（含时间段单方向道路） 1203 6.车道数 1202 7.SA 1205 8.PA 1206 9.匝道 1207 10.IC\JCT
	 * 1211 11.红绿灯（点属性） 1102 12.收费站（点属性） 1107 13.点限速（点属性） 1101 14.车道信息（关系属性）
	 * 1301 15.交通限制（关系属性）nk 1302 16.上下分离 1501 17.步行街 1507 18.公交专用道 1508 19.桥
	 * 1510 20.隧道 1511 21.施工 1514 22.环岛 1601 23.区域内道路 1604 24.铁路道口 1702 25.道路名
	 * 1901 26.删除标记 2101
	 */

	public JSONObject excute() {

		switch (sourceType) {
		case "1803":// 2.挂接  null
			return updateHookTips();
		case "1116":// 3.立交   [f_array].id
			return updateGSCTips();
		case "1201":// 4.种别   f.id
			return updateKindTips();
		case "1203":// 5.道路通行方向   f.id
			return updateLinkDirTips();
		case "1202":// 6. 车道数  f.id
			return updateKindLaneTips();
			// 7.SA、PA、匝道  f.id
		case "1205": 
			return updateSATips();
		case "1206": // 8 .PA  f.id
			return updatePATips();
		case "1207": // 9.匝道  f.id
			return updateRampTips();
		case "1211": // 10.IC\JCT  f.id
			return updateJCTTips();
		case "1102":// 11 .红绿灯 [f_array].f
			return updateTrafficSignalTips();
		case "1107":// 12.收费站 in.id+out.id ??
			return updateTollgateTips();
		case "1101":// 13. 点限速 f.id
			return updateSpeedLimitTips();
		case "1301":// 14. 车道信息   复杂的----？？
			return updateRdLaneTips();
		case "1302":// 15. 普通交限  复杂的----？？
			return updateRestrictionTips();
		case "1501": // 16. 上下线分离 [f_array].id 
			return updateUpDownSeparateLine();
		case "1507":// 17.步行街 [f_array].id 
			return updateWalkStreetTips();
		case "1508":// 18.公交专用道 [f_array].id 
			return updateLineAttrTips();
			// 起终点类
		case "1510":// 19. 桥 [f_array].id 
			return updateBridgeTips();
		case "1511":// 20. 隧道 [f_array].id 
			return updateTunnel();
		case "1514":// 21.施工 [f_array].id 
			return updateConstruction();
			// 范围线类
		case "1601":// 22. 环岛 [f_array].id 
			return updateFArray_Id();
		case "1604":// 23. 区域内道路 [f_array].id 
			return updateFArray_Id();
		case "1702":// 24. 铁路道口 f.id
			return updateSimpleF();
		case "1901":// 25. 道路名  null
			return null;
		case "2101":// 26.删除道路标记  null
			return null; 
		default:
			return null;
		}

	}

	private JSONObject updateJCTTips() {
		// TODO Auto-generated method stub
		return updateSimpleF();
	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:15:32
	 */
	private JSONObject updateDelRoadMarkTips() {

		return null;

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:15:17
	 */
	private JSONObject updateRoadNameTips() {
		return null;

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:15:00
	 */
	private JSONObject updateRailwayCrossingTips() {
		return null;

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:13:39
	 */
	private JSONObject updateRegionalRoad() {
		return null;

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:12:49
	 */
	private JSONObject updatRrotaryIsland() {
		return null;

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:10:36
	 */
	private JSONObject updateConstruction() {
		return updateFArray_Id();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:10:04
	 */
	private JSONObject updateTunnel() {
		return updateFArray_Id();

	}

	/**
	 * @Description:TOOD
	 * [f_array].id 
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:09:26
	 */
	private JSONObject updateBridgeTips() {
		return updateFArray_Id();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:08:54
	 */
	private JSONObject updateLineAttrTips() {
		return updateFArray_Id();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:08:42
	 */
	private JSONObject updateWalkStreetTips() {
		return updateFArray_Id();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:08:20
	 */
	private JSONObject updateUpDownSeparateLine() {
		return updateFArray_Id();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:08:04
	 */
	private JSONObject updateRestrictionTips() {
		return null;

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:07:50
	 */
	private JSONObject updateRdLaneTips() {
		return null;

	}

	/**
	 * @Description:1101 点限速，关联测线修改
	 * f.id
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:07:31
	 */
	private JSONObject updateSpeedLimitTips() {

		return updateSimpleF();

	}

	/**
	 * 简单的关联link修改
	 * 规格：deep.f.id
	 * @return
	 */
	private JSONObject updateSimpleF() {
		JSONObject deep = JSONObject.fromObject(this.json.getString("deep"));

		JSONObject f = deep.getJSONObject("f");

		// 关联link是测线的
		if (f != null && f.getInt("type") == 2) {

			String id = getNearlestLineId();

			f.put("id", id);

			deep.put("f", f);

			json.put("deep", deep);

			return json;
		}
		// 关联的不是测线，则不返回
		else {
			return null;
		}
	}

	/**
	 * @Description:TOOD
	 * @return
	 * @author: y
	 * @time:2017-4-17 下午5:55:08
	 */
	private String getNearlestLineId() {
		String id;
		// tip的引导坐标
		JSONObject geometryTips = JSONObject.fromObject(this.line1
				.getString("geometry"));

		JSONObject g_guide = geometryTips.getJSONObject("g_guide");

		Point point = (Point) GeoTranslator.geojson2Jts(g_guide);

		// 两个线的显示坐标

		JSONObject geometry1 = JSONObject.fromObject(this.line1
				.getString("geometry"));

		JSONObject g_location1 = geometry1.getJSONObject("g_location");

		Geometry geo1 = GeoTranslator.geojson2Jts(g_location1);

		JSONObject geometry2 = JSONObject.fromObject(this.line2
				.getString("geometry"));

		JSONObject g_location2 = geometry2.getJSONObject("g_location");

		Geometry geo2 = GeoTranslator.geojson2Jts(g_location2);

		// 计算 tips的引导坐标到显示坐标的距离，取最近的测线作为引导link

		if (point.distance(geo1) <= point.distance(geo2)) {

			id = line1.getString("id");
		} else {
			id = line1.getString("id");
		}
		return id;
	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:07:15
	 */
	private JSONObject updateTollgateTips() {
		return null;

	}

	/**
	 * @Description:"1102":// 红绿灯 [f_array].f.id (f唯一是对象)
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:06:58
	 */
	private JSONObject updateTrafficSignalTips() {

		return updateFAarrary_F();

	}

	private JSONObject updateFAarrary_F() {
		boolean hasMeasuringLine = false;

		JSONObject deep = JSONObject.fromObject(this.json.getString("deep"));

		JSONArray f_array = deep.getJSONArray("f_array");

		JSONArray f_array_new = new JSONArray(); // 一个新的f_array数组

		for (Object object : f_array) {

			JSONObject fInfo = JSONObject.fromObject(object);

			JSONObject f = fInfo.getJSONObject("f"); // 是个对象

			// 关联link是测线的
			if (f != null && f.getInt("type") == 2) {

				String id = getNearlestLineId();

				f.put("id", id);

				fInfo.put("f", f);

				hasMeasuringLine = true;

			}

			f_array_new.add(fInfo); //添加到新数组

		}

		if (hasMeasuringLine) {
			deep.put("f_array", f_array_new);// 新的

			json.put("deep", deep);
			return json;
		}

		return null;
	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:06:25
	 */
	private JSONObject updateRampTips() {
		return updateSimpleF();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:06:23
	 */
	private JSONObject updatePATips() {
		return updateSimpleF();

	}

	/**
	 * @Description:TOOD
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午10:06:20
	 */
	private JSONObject updateSATips() {
		return updateSimpleF();

	}

	/**
	 * @Description:1203 （道路方向）
	 * f.id
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午9:59:13
	 */
	private JSONObject updateKindLaneTips() {
		return updateSimpleF();

	}

	/**
	 * @Description:挂接 无关联link，不维护
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午9:58:32
	 */
	private JSONObject updateHookTips() {
		return null;

	}

	/**
	 * @Description:1116 立交
	 *[f_array].id
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午9:58:25
	 */
	private JSONObject updateGSCTips() {

		return updateFArray_Id();

	}

	private JSONObject updateFArray_Id() {
		boolean hasMeasuringLine = false;

		JSONObject deep = JSONObject.fromObject(this.json.getString("deep"));

		JSONArray f_array = deep.getJSONArray("f_array");

		JSONArray f_array_new = new JSONArray(); // 一个新的f_array数组

		for (Object object : f_array) {

			JSONObject fInfo = JSONObject.fromObject(object); // 是个对象

			// 关联link是测线的
			if (fInfo != null && fInfo.getInt("type") == 2) {

				String id = getNearlestLineId();

				fInfo.put("id", id);

				hasMeasuringLine = true;

			}

			f_array_new.add(fInfo); // 添加到新数组

		}

		// 如果有测线，则修改，并返回
		if (hasMeasuringLine) {

			deep.put("f_array", f_array_new);// 新的

			json.put("deep", deep);

			return json;
		}

		return null;
	}

	/**
	 * @Description:种别
	 * f.id
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午9:58:15
	 */
	private JSONObject updateKindTips() {
		
		return updateSimpleF();

	}

	/**
	 * @Description:1203
	 * f.id
	 * @author: y
	 * @return
	 * @time:2017-4-13 上午9:58:10
	 */
	private JSONObject updateLinkDirTips() {
		return updateSimpleF();

	}
}
