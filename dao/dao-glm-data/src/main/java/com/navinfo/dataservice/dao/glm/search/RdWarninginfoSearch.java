package com.navinfo.dataservice.dao.glm.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.commons.util.DisplayUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;
import oracle.sql.STRUCT;

import com.navinfo.dataservice.commons.geom.Geojson;
import com.navinfo.dataservice.commons.mercator.MercatorProjection;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISearch;
import com.navinfo.dataservice.dao.glm.iface.SearchSnapshot;
import com.navinfo.dataservice.dao.glm.selector.rd.warninginfo.RdWarninginfoSelector;

public class RdWarninginfoSearch implements ISearch {

	private Connection conn;

	private WKT wktSpatial = new WKT();

	public RdWarninginfoSearch(Connection conn) {
		this.conn = conn;
	}

	@Override
	public IObj searchDataByPid(int pid) throws Exception {
		RdWarninginfoSelector selector = new RdWarninginfoSelector(conn);

		IObj obj = (IObj) selector.loadById(pid, false);

		return obj;
	}

	@Override
	public List<IRow> searchDataByPids(List<Integer> pidList) throws Exception {

		RdWarninginfoSelector selector = new RdWarninginfoSelector(conn);

		List<IRow> rows = selector.loadByIds(pidList, false, true);

		return rows;
	}

	@Override
	public List<SearchSnapshot> searchDataBySpatial(String wkt)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SearchSnapshot> searchDataByCondition(String condition)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SearchSnapshot> searchDataByTileWithGap(int x, int y, int z,
														int gap) throws Exception {
		List<SearchSnapshot> list = new ArrayList<SearchSnapshot>();

		String sql = "WITH TMP1 AS (SELECT A.GEOMETRY, A.NODE_PID FROM RD_NODE A WHERE SDO_RELATE(A.GEOMETRY, SDO_GEOMETRY(:1, 8307), 'mask=anyinteract') = 'TRUE' AND A.U_RECORD != 2) SELECT A.PID, A.TYPE_CODE, A.LINK_PID, A.NODE_PID, TMP1.GEOMETRY AS GEOMETRY, C.GEOMETRY AS LINK_GEOM FROM RD_WARNINGINFO A, TMP1, RD_LINK C WHERE A.NODE_PID = TMP1.NODE_PID AND A.U_RECORD != 2 AND C.U_RECORD != 2 AND A.LINK_PID = C.LINK_PID ORDER BY A.NODE_PID, A.LINK_PID";


		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			String wkt = MercatorProjection.getWktWithGap(x, y, z, gap);

			pstmt.setString(1, wkt);

			resultSet = pstmt.executeQuery();

			double px = MercatorProjection.tileXToPixelX(x);

			double py = MercatorProjection.tileYToPixelY(y);
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			JSONArray array = new JSONArray();
			JSONObject jsonObject = null;
			SearchSnapshot snapshot = new SearchSnapshot();
			while (resultSet.next()) {

				int nodePid = resultSet.getInt("NODE_PID");
				int linkPid = resultSet.getInt("LINK_PID");
				if (resultSet.isFirst()) {
					jsonObject = new JSONObject();
					this.setAttr(resultSet, snapshot, jsonObject, nodePid,
							linkPid, z, px, py, list, array, map);
					if (resultSet.isLast()) {
						jsonObject.put("info", array);
						snapshot.setM(jsonObject);
						list.add(snapshot);
					}
					continue;
				}
				if (map.containsKey(nodePid) && map.get(nodePid) == linkPid) {
					JSONObject info = new JSONObject();
					info.put("pid", resultSet.getInt("PID"));
					info.put(
							"type",
							StringUtils.isEmpty(resultSet
									.getString("TYPE_CODE")) ? "" : resultSet
									.getString("TYPE_CODE"));
					array.add(info);

				} else {
					jsonObject.put("info", array);
					snapshot.setM(jsonObject);
					list.add(snapshot);
					array = new JSONArray();
					snapshot = new SearchSnapshot();
					jsonObject = new JSONObject();
					this.setAttr(resultSet, snapshot, jsonObject, nodePid,
							linkPid, z, px, py, list, array, map);

				}
				if (resultSet.isLast()) {
					jsonObject.put("info", array);
					snapshot.setM(jsonObject);
					list.add(snapshot);
				}

			}

			for (SearchSnapshot searchSnapshot : list) {

				JSONArray arrayInfo = searchSnapshot.getM().getJSONArray("info");

				if (arrayInfo.size() > 1) {

					double angle = arrayInfo.getJSONObject(0).getDouble("angle");

					for (int i = 1; i < arrayInfo.size(); i++) {

						JSONObject info = arrayInfo.getJSONObject(i);

						if (!info.containsKey("angle")) {

							info.put("angle", String.valueOf((int) angle));
						}
					}

				}
			}

		} catch (Exception e) {

			throw new Exception(e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {

				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {

				}
			}

		}

		return list;
	}

	/***
	 * 警示信息渲染特殊处理 同一进入点进入线的合并其信息
	 *
	 * @param resultSet
	 * @param snapshot
	 * @param jsonObject
	 * @param nodePid
	 * @param linkPid
	 * @param z
	 * @param px
	 * @param py
	 * @param list
	 * @param array
	 * @param map
	 * @throws Exception
	 */
	private void setAttr(ResultSet resultSet, SearchSnapshot snapshot,
						 JSONObject jsonObject, int nodePid, int linkPid, int z, double px,
						 double py, List<SearchSnapshot> list, JSONArray array,
						 Map<Integer, Integer> map) throws Exception {
		snapshot.setT(25);

		STRUCT struct = (STRUCT) resultSet.getObject("GEOMETRY");

		STRUCT struct1 = (STRUCT) resultSet.getObject("link_geom");

		JGeometry geom1 = JGeometry.load(struct1);

		String linkWkt = new String(wktSpatial.fromJGeometry(geom1));

		JGeometry geom2 = JGeometry.load(struct);

		String pointWkt = new String(wktSpatial.fromJGeometry(geom2));

		JSONObject info = new JSONObject();

		info.put("pid", resultSet.getInt("PID"));
		info.put("type",
				StringUtils.isEmpty(resultSet.getString("TYPE_CODE")) ? ""
						: resultSet.getString("TYPE_CODE"));

		int direct = DisplayUtils.getDirect(linkWkt, pointWkt);

		double angle = DisplayUtils.calIncloudedAngle(linkWkt, direct);

		info.put("angle", String.valueOf((int) angle));


		array.add(info);
		jsonObject.put("nodePid", nodePid);
		jsonObject.put("linkPid", linkPid);
		JSONObject geojson = Geojson.spatial2Geojson(struct);
		Geojson.point2Pixel(geojson, z, px, py);
		snapshot.setG(geojson.getJSONArray("coordinates"));
		map.put(nodePid, linkPid);
	}

	public static void main(String[] args) {

	}

}
