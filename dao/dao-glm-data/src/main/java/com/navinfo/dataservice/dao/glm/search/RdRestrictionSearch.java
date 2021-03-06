package com.navinfo.dataservice.dao.glm.search;

import com.navinfo.dataservice.commons.geom.AngleCalculator;
import com.navinfo.dataservice.commons.geom.Geojson;
import com.navinfo.dataservice.commons.mercator.MercatorProjection;
import com.navinfo.dataservice.commons.util.DisplayUtils;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISearch;
import com.navinfo.dataservice.dao.glm.iface.SearchSnapshot;
import com.navinfo.dataservice.dao.glm.selector.rd.restrict.RdRestrictionSelector;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;
import oracle.sql.STRUCT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RdRestrictionSearch implements ISearch {

	private Connection conn;

	public RdRestrictionSearch(Connection conn) {
		this.conn = conn;
	}

	@Override
	public IObj searchDataByPid(int pid) throws Exception {

		RdRestrictionSelector selector = new RdRestrictionSelector(conn);
		
		IObj obj = (IObj)selector.loadById(pid, false);
		
		return obj;
	}
	
	@Override
	public List<IRow> searchDataByPids(List<Integer> pidList) throws Exception {
		
		RdRestrictionSelector selector = new RdRestrictionSelector(conn);		

		List<IRow> rows = selector.loadByIds(pidList, false, true);

		return rows;
	}
	
	@Override
	public List<SearchSnapshot> searchDataBySpatial(String box)
			throws SQLException {

		List<SearchSnapshot> list = new ArrayList<SearchSnapshot>();

		String sql = "select a.pid,        a.restric_info,        a.node_pid,        b.geometry,        b.s_node_pid,        b.e_node_pid,        c.vt   from rd_restriction a,        rd_link b,        (select a.pid, sum(decode(vt, null, 0, 0, 0, 1)) vt           from rd_restriction a,                rd_restriction_detail b,                (select detail_id, sum(package_utils.parse_vehicle(vehicle)) vt                   from rd_restriction_condition                  group by detail_id) c          where a.pid = b.restric_pid            and b.detail_id = c.detail_id(+)                   group by a.pid) c  where a.in_link_pid = b.link_pid    and a.pid = c.pid    and sdo_within_distance(b.geometry,                            sdo_geometry(:1,                                         8307),                            'DISTANCE=0') = 'TRUE'";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, box);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				SearchSnapshot snapshot = new SearchSnapshot();
				
				JSONObject jsonM = new JSONObject();

				snapshot.setI(resultSet.getInt("pid"));
				
				snapshot.setT(3);

				jsonM.put("b",resultSet.getString("restric_info"));

				int vt = resultSet.getInt("vt");

				if (vt > 0) {
					vt = 1;
				}

				jsonM.put("a", vt);

				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				JGeometry geom = JGeometry.load(struct);

				int sNodePid = resultSet.getInt("s_node_pid");

				int eNodePid = resultSet.getInt("e_node_pid");

				int nodePid = resultSet.getInt("node_pid");

				double angle = AngleCalculator.getDisplayAngle(nodePid,
						sNodePid, eNodePid, geom);

				jsonM.put("c", String.valueOf((int)angle));

				double[] point = geom.getPoint();

				JSONArray geo = new JSONArray();

				geo.add(point[0]);

				geo.add(point[1]);

				snapshot.setG(JSONArray.fromObject(point));

				list.add(snapshot);
			}
		} catch (Exception e) {
			
			throw new SQLException(e);
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

		String sql = "WITH TMP1 AS (SELECT LINK_PID, GEOMETRY FROM RD_LINK WHERE SDO_RELATE(GEOMETRY, SDO_GEOMETRY(:1, 8307), 'mask=anyinteract') = 'TRUE' AND U_RECORD != 2), TMP2 AS (SELECT A.NODE_PID, A.IN_LINK_PID, A.PID, A.RESTRIC_INFO FROM RD_RESTRICTION A WHERE EXISTS (SELECT NULL FROM TMP1 B WHERE A.IN_LINK_PID = B.LINK_PID) AND A.U_RECORD != 2), TMP3 AS (SELECT A.RESTRIC_PID,LISTAGG(B.VEHICLE, ',') WITHIN GROUP(ORDER BY A.RESTRIC_PID) VEHICLES FROM RD_RESTRICTION_DETAIL A, RD_RESTRICTION_CONDITION B WHERE EXISTS (SELECT NULL FROM TMP2 C WHERE A.RESTRIC_PID = C.PID) AND A.DETAIL_ID = B.DETAIL_ID AND A.U_RECORD != 2 AND B.U_RECORD != 2 group by A.RESTRIC_PID) select tmp.*,tmp3.VEHICLES from ( SELECT /*+ index(d) */ A.PID, A.RESTRIC_INFO, C.GEOMETRY     LINK_GEOM, D.GEOMETRY     POINT_GEOM ,A.IN_LINK_PID FROM TMP2 A, TMP1 C, RD_NODE D WHERE A.IN_LINK_PID = C.LINK_PID AND A.NODE_PID = D.NODE_PID AND D.U_RECORD != 2) tmp left join tmp3 on tmp.pid = tmp3.RESTRIC_PID ORDER BY TMP.PID";
		
		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		//进入linkPid，交限所在进入link的序号
		Map<Integer, Integer> indexs = new HashMap<>();

		try {
			pstmt = conn.prepareStatement(sql);
			
			String wkt = MercatorProjection.getWktWithGap(x, y, z, gap);

			pstmt.setString(1, wkt);

			resultSet = pstmt.executeQuery();
			
			double px = MercatorProjection.tileXToPixelX(x);
			
			double py = MercatorProjection.tileYToPixelY(y);

			while (resultSet.next()) {

				SearchSnapshot snapshot = new SearchSnapshot();
				
				JSONObject jsonM = new JSONObject();

				snapshot.setI(resultSet.getInt("pid"));
				
				snapshot.setT(3);
				
				String b = resultSet.getString("restric_info");
				
				if(b.startsWith("[") && b.endsWith("]")){
					b=b+StringUtils.PlaceHolder;
				}

				jsonM.put("b",b);
				
				String type = resultSet.getString("VEHICLES");
				
				jsonM.put("a", "0");
				
				if(type != null)
				{
					String typeArray[] = type.split(",");
					
					for(String tp : typeArray)
					{
						String typ = Integer.toBinaryString(Integer.valueOf(tp));
						
						if(typ.length()<=3)
						{
							if(Integer.parseInt(tp) !=0 && Integer.parseInt(tp) !=1)
							{
								jsonM.put("a", "1");
								break;
							}
						}
						else
						{
							
							if(typ.charAt(typ.length() - 2) == '1' || typ.charAt(typ.length() - 3) == '1')
							{
								jsonM.put("a", "1");
								break;
							}
						}
					}
				}

				STRUCT struct1 = (STRUCT) resultSet.getObject("link_geom");

				JGeometry geom1 = JGeometry.load(struct1);

				String linkWkt = new String(new WKT().fromJGeometry(geom1));

				STRUCT struct2 = (STRUCT) resultSet.getObject("point_geom");

				JGeometry geom2 = JGeometry.load(struct2);

				String pointWkt = new String(new WKT().fromJGeometry(geom2));

				int direct = DisplayUtils.getDirect(linkWkt, pointWkt);
				
				double angle = DisplayUtils.calIncloudedAngle(linkWkt, direct);

				jsonM.put("c", String.valueOf((int)angle));
				
				double offset = 10;
				switch(z){
				case 16:
				case 17:
					offset = 14; break;
				case 18:
					offset = 4; break;
				case 19:
					offset = 1; break;
				case 20:
					offset = 0; break;
				}

				int inLinkPid = resultSet.getInt("IN_LINK_PID");

				if (!indexs.containsKey(inLinkPid)) {

					indexs.put(inLinkPid, 1);
				}

				int index = indexs.get(inLinkPid);

				indexs.put(inLinkPid, index + 1);

				double[][] point = DisplayUtils.getGdbPointPos(linkWkt, pointWkt, index, (21 - z) * 6 + offset, 4);

				snapshot.setG(Geojson.lonlat2Pixel(point[1][0], point[1][1], z, px, py));

				snapshot.setM(jsonM);

				list.add(snapshot);
			}
		} catch (Exception e) {
			
			throw new SQLException(e);
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
		//"z":20,"x":864106,"y":397405

		String wkt = MercatorProjection.getWktWithGap(864106, 397405, 20, 80);
	System.out.println(wkt);
		
	}

}
