package com.navinfo.dataservice.dao.glm.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.geom.Geojson;
import com.navinfo.dataservice.commons.mercator.MercatorProjection;
import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISearch;
import com.navinfo.dataservice.dao.glm.iface.SearchSnapshot;
import com.navinfo.dataservice.dao.glm.selector.lc.LcFaceSelector;

import net.sf.json.JSONObject;
import oracle.sql.STRUCT;

public class LcFaceSearch implements ISearch {

	private Connection conn;

	public LcFaceSearch(Connection conn) {
		this.conn = conn;
	}

	@Override
	public IObj searchDataByPid(int pid) throws Exception {
		LcFaceSelector lcFaceSelector = new LcFaceSelector(conn);

		IObj lcFace = (IObj) lcFaceSelector.loadById(pid, false);

		return lcFace;
	}

	@Override
	public List<IRow> searchDataByPids(List<Integer> pidList) throws Exception {

		LcFaceSelector selector = new LcFaceSelector(conn);

		List<IRow> rows = selector.loadByIds(pidList, false, true);

		return rows;
	}

	@Override
	public List<SearchSnapshot> searchDataBySpatial(String wkt)
			throws Exception {

		List<SearchSnapshot> list = new ArrayList<SearchSnapshot>();

		String sql = "select a.face_pid, a.geometry,a.kind,(select count(1) from lc_face_name ln where ln.face_pid = a.face_pid and ln.u_record !=2) count from lc_face a where a.u_record != 2 and sdo_within_distance(a.geometry, sdo_geometry(:1, 8307), 'DISTANCE=0') = 'TRUE'";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, wkt);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				SearchSnapshot snapshot = new SearchSnapshot();
				JSONObject m = new JSONObject();
				m.put("a", resultSet.getInt("kind"));
				m.put("b", resultSet.getInt("count") > 0 ? 1 : 0);
				snapshot.setT(32);
				snapshot.setM(m);
				snapshot.setI(resultSet.getInt("face_pid"));

				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				JSONObject jo = Geojson.spatial2Geojson(struct);

				snapshot.setG(jo.getJSONArray("coordinates"));

				list.add(snapshot);
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

	@Override
	public List<SearchSnapshot> searchDataByCondition(String condition)
			throws Exception {

		return null;
	}

	@Override
	public List<SearchSnapshot> searchDataByTileWithGap(int x, int y, int z,
			int gap) throws Exception {

		List<SearchSnapshot> list = new ArrayList<SearchSnapshot>();

		String sql = "select a.face_pid, a.geometry,a.kind,(select count(1) from lc_face_name ln where ln.face_pid = a.face_pid and ln.u_record !=2) count from lc_face a where a.u_record != 2 and sdo_within_distance(a.geometry, sdo_geometry(:1, 8307), 'DISTANCE=0') = 'TRUE'";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			String wkt = MercatorProjection.getWktWithGap(x, y, z, gap);

			pstmt.setString(1, wkt);

			resultSet = pstmt.executeQuery();

			double px = MercatorProjection.tileXToPixelX(x);

			double py = MercatorProjection.tileYToPixelY(y);

			while (resultSet.next()) {
				SearchSnapshot snapshot = new SearchSnapshot();

				JSONObject m = new JSONObject();

				m.put("a", resultSet.getInt("kind"));
				m.put("b", resultSet.getInt("count") > 0 ? 1 : 0);

				snapshot.setM(m);

				snapshot.setT(32);

				snapshot.setI(resultSet.getInt("face_pid"));

				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				JSONObject geojson = Geojson.spatial2Geojson(struct);

				JSONObject jo = Geojson.face2Pixel(geojson, px, py, z);

				snapshot.setG(jo.getJSONArray("coordinates"));

				list.add(snapshot);
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

	public static void main(String[] args) throws Exception {

		Connection conn = DBConnector.getInstance().getConnectionById(11);

		LcFaceSearch s = new LcFaceSearch(conn);

		IObj obj = s.searchDataByPid(83804);

		System.out.println(obj.Serialize(null));
	}
}
