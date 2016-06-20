package com.navinfo.dataservice.engine.edit.comm.util.operate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.rw.RwLink;
import com.navinfo.dataservice.dao.glm.model.rd.rw.RwNode;
import com.navinfo.dataservice.dao.pidservice.PidService;
import com.navinfo.navicommons.geo.computation.CompGeometryUtil;
import com.navinfo.navicommons.geo.computation.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * RWLINK公共方法类
 * @author zhangxiaolong
 *
 */
public class RwLinkOperateUtils {
	
	/*
	 * 创建生成一条RwLink,未赋值图幅号
	 * */
	public static RwLink addLink(Geometry geo,int sNodePid, int eNodePid,Result result) throws Exception{
		RwLink link = new RwLink();
		
		Set<String> meshes = CompGeometryUtil.geoToMeshesWithoutBreak(geo);
		
		if(meshes.size()>1)
		{
			throw new Exception("创建rwLink失败：对应多个图幅");
		}
		else
		{
			link.setMesh(Integer.parseInt(meshes.iterator().next()));
		}
		
		link.setPid(PidService.getInstance().applyRwLinkPid());

		System.out.println(link.getPid());
		
		result.setPrimaryPid(link.getPid());

		double linkLength = GeometryUtils.getLinkLength(geo);

		if(linkLength<=2){
			throw new Exception("道路link长度应大于2米");
		}

		link.setLength(linkLength);

		link.setGeometry(GeoTranslator.transform(geo, 100000, 0));

		link.setsNodePid(sNodePid);
		
		link.seteNodePid(eNodePid);
		
		return link;
	}
	
	/*
	 * 分割线
	 * 
	 * @param geometry 要分割线的几何 sNodePid 起点pid eNodePid 终点pid catchLinks
	 * 挂接的线和点的集合 1.生成所有不存在的rwNODE 2.标记挂接的link被打断的点 3.返回线被分割的几何属性和起点和终点的List集合
	 */
	public static Map<Geometry, JSONObject> splitRwLink(Geometry geometry, int sNodePid,
			int eNodePid, JSONArray catchLinks, Result result) throws Exception {
		Map<Geometry, JSONObject> maps = new HashMap<Geometry, JSONObject>();
		JSONArray coordinates = GeoTranslator.jts2Geojson(geometry)
				.getJSONArray("coordinates");
		JSONObject tmpGeom = new JSONObject();
		// 组装要生成的link
		tmpGeom.put("type", "LineString");
		JSONArray tmpCs = new JSONArray();
		// 添加第一个点几何
		tmpCs.add(coordinates.get(0));

		int p = 0;

		int pc = 1;
		// 挂接的第一个点是LINK的几何属性第一个点
		if (tmpCs.getJSONArray(0).getDouble(0) == catchLinks.getJSONObject(0)
				.getDouble("lon")
				&& tmpCs.getJSONArray(0).getDouble(1) == catchLinks
						.getJSONObject(0).getDouble("lat")) {
			p = 1;
		}
		JSONObject se = new JSONObject();
        // 生成起点RWNODE
		if (0 == sNodePid) {
			double x = coordinates.getJSONArray(0).getDouble(0);

			double y = coordinates.getJSONArray(0).getDouble(1);

			RwNode node = NodeOperateUtils.createRwNode(x, y);
			result.insertObject(node, ObjStatus.INSERT, node.pid());
			se.put("s", node.getPid());

			if (p == 1 && catchLinks.getJSONObject(0).containsKey("linkPid")) {
				catchLinks.getJSONObject(0).put("breakNode", node.getPid());
			}
		} else {
			se.put("s", sNodePid);
		}
        //循环当前要分割LINK的几何 循环挂接的集合
		// 当挂接几何和link的集合有相同的点 生成新的link
		//如果挂接的存在linkPid 则被打断，且生成新的点
		//如果挂接只有RWNODE则不需要生成新的RWNODE
		while (p < catchLinks.size() && pc < coordinates.size()) {
			tmpCs.add(coordinates.getJSONArray(pc));

			if (coordinates.getJSONArray(pc).getDouble(0) == catchLinks
					.getJSONObject(p).getDouble("lon")
					&& coordinates.getJSONArray(pc).getDouble(1) == catchLinks
							.getJSONObject(p).getDouble("lat")) {

				tmpGeom.put("coordinates", tmpCs);
				if (catchLinks.getJSONObject(p).containsKey("nodePid")) {
					se.put("e", catchLinks.getJSONObject(p).getInt("nodePid"));
					maps.put(GeoTranslator.geojson2Jts(tmpGeom), se);
					se = new JSONObject();

					se.put("s", catchLinks.getJSONObject(p).getInt("nodePid"));
				} else {
					double x = catchLinks.getJSONObject(p).getDouble("lon");

					double y = catchLinks.getJSONObject(p).getDouble("lat");

					RwNode node = NodeOperateUtils.createRwNode(x, y);

					result.insertObject(node, ObjStatus.INSERT, node.pid());

					se.put("e", node.getPid());
					maps.put(GeoTranslator.geojson2Jts(tmpGeom), se);
					se = new JSONObject();

					se.put("s", node.getPid());

					catchLinks.getJSONObject(p).put("breakNode", node.getPid());
				}

				tmpGeom = new JSONObject();

				tmpGeom.put("type", "LineString");

				tmpCs = new JSONArray();

				tmpCs.add(coordinates.getJSONArray(pc));

				p++;
			}

			pc++;
		}
        //循环挂接的线是否完毕 如果>1 则表示完毕
		if (tmpCs.size() > 1) {
			tmpGeom.put("coordinates", tmpCs);
			if (eNodePid != 0) {
				se.put("e", eNodePid);

			} else {
				double x = tmpCs.getJSONArray(tmpCs.size() - 1).getDouble(0);

				double y = tmpCs.getJSONArray(tmpCs.size() - 1).getDouble(1);

				RwNode node = NodeOperateUtils.createRwNode(x, y);

				result.insertObject(node, ObjStatus.INSERT, node.pid());

				se.put("e", node.getPid());
			}
			maps.put(GeoTranslator.geojson2Jts(tmpGeom), se);
		}
		return maps;

	}
	
	/*
	 * 创建一条rwLink对应的端点
	 */
	public static JSONObject createRwNodeForLink(Geometry g, int sNodePid, int eNodePid,Result result)
			throws Exception {
		JSONObject node = new JSONObject();
		if (0 == sNodePid) {
			Coordinate point = g.getCoordinates()[0];
			RwNode rwNODE =NodeOperateUtils.createRwNode(point.x, point.y);
			result.insertObject(rwNODE, ObjStatus.INSERT, rwNODE.pid());
			node.put("s", rwNODE.getPid());
		}else{
			node.put("s", sNodePid);
		}
		//创建终止点信息
		if (0 == eNodePid) {
			Coordinate point = g.getCoordinates()[g.getCoordinates().length - 1];
			RwNode rwNODE =NodeOperateUtils.createRwNode(point.x, point.y);
			result.insertObject(rwNODE, ObjStatus.INSERT, rwNODE.pid());
			node.put("e", rwNODE.getPid());
		}else{
			node.put("e", eNodePid);
		}
		return node;
		
	}
	
	/*
	 * 创建生成一条RwLink
	 * 继承原有LINK的属性
	 * */
	public static IRow addLinkBySourceLink(Geometry g,int sNodePid, int eNodePid,RwLink sourcelink,Result result) throws Exception{
		RwLink link = new RwLink();
		link.copy(sourcelink);
		Set<String> meshes = CompGeometryUtil.geoToMeshesWithoutBreak(g);
		link.setPid(PidService.getInstance().applyRwLinkPid());
		if(meshes.size()>1)
		{
			throw new Exception("打断生成新RwLink失败：对应多个图幅");
		}
		else
		{
			link.setMesh(Integer.parseInt(meshes.iterator().next()));
		}
		double linkLength = GeometryUtils.getLinkLength(g);
		link.setLength(linkLength);
		link.setGeometry(GeoTranslator.transform(g, 100000, 0));
		link.setsNodePid(sNodePid);
		link.seteNodePid(eNodePid);
		result.insertObject(link, ObjStatus.INSERT, link.pid());
		return link;
	}
}
