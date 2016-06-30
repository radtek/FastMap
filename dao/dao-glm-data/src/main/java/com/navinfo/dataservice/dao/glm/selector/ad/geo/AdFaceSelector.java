package com.navinfo.dataservice.dao.glm.selector.ad.geo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import oracle.sql.STRUCT;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.commons.exception.DataNotFoundException;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISelector;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFace;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFaceTopo;

public class AdFaceSelector implements ISelector {

	private static Logger logger = Logger.getLogger(AdFaceSelector.class);

	private Connection conn;

	public AdFaceSelector(Connection conn) {
		this.conn = conn;
	}

	@Override
	public IRow loadById(int id, boolean isLock) throws Exception {

		AdFace face = new AdFace();

		String sql = "select * from " + face.tableName() + " where face_pid=:1 and  u_record !=2";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				face.setPid(resultSet.getInt("face_pid"));
				
				face.setRegionId(resultSet.getInt("region_id"));
				
				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				face.setGeometry(GeoTranslator.struct2Jts(struct, 100000, 0));
				
				face.setArea(resultSet.getDouble("area"));
				
				face.setPerimeter(resultSet.getDouble("perimeter"));
				
				face.setMeshId(resultSet.getInt("mesh_id"));
				
				face.setEditFlag(resultSet.getInt("edit_flag"));
				
				face.setRowId(resultSet.getString("row_id"));
				
				// ad_face_topo
				List<IRow> adFaceTopo = new AdFaceTopoSelector(conn).loadRowsByParentId(id, isLock);

				for (IRow row : adFaceTopo) {
					row.setMesh(face.mesh());
				}

				face.setFaceTopos(adFaceTopo);

				for (IRow row : adFaceTopo) {
					AdFaceTopo obj = (AdFaceTopo) row;

					face.adFaceTopoMap.put(obj.rowId(), obj);
				}

			} else {
				throw new DataNotFoundException("数据不存在");
			}
		} catch (Exception e) {
			
			throw e;

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				
			}

			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				
			}

		}

		return face;
	}

	@Override
	public List<IRow> loadRowsByParentId(int id, boolean isLock)
			throws Exception {

		return null;
	}

	@Override
	public IRow loadByRowId(String rowId, boolean isLock) throws Exception {

		return null;
	}
   
	
	public List<AdFace> loadAdFaceByLinkGeometry(String wkt, boolean isLock)
			throws Exception {

		List<AdFace> faces = new ArrayList<AdFace>();

		String sql = "select  a.*  from ad_face a where a.u_record != 2   and sdo_within_distance(a.geometry,  sdo_geom.sdo_mbr(sdo_geometry(:1, 8307)), 'DISTANCE=0') = 'TRUE'";
		
		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setString(1, wkt);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				
				AdFace face = new AdFace();

				face.setPid(resultSet.getInt("face_pid"));
				
				face.setRegionId(resultSet.getInt("region_id"));
				
				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				face.setGeometry(GeoTranslator.struct2Jts(struct, 100000, 0));
				
				face.setArea(resultSet.getDouble("area"));
				
				face.setPerimeter(resultSet.getDouble("perimeter"));
				
				face.setMeshId(resultSet.getInt("mesh_id"));
				
				face.setEditFlag(resultSet.getInt("edit_flag"));
				
				face.setRowId(resultSet.getString("row_id"));

				faces.add(face);
				
				// ad_face_topo
				List<IRow> adFaceTopo = new AdFaceTopoSelector(conn).loadRowsByParentId(face.getPid(), isLock);

				for (IRow row : adFaceTopo) {
					row.setMesh(face.mesh());
				}

				face.setFaceTopos(adFaceTopo);

				for (IRow row : adFaceTopo) {
					AdFaceTopo obj = (AdFaceTopo) row;

					face.adFaceTopoMap.put(obj.rowId(), obj);
				}
			}
		} catch (Exception e) {
			
			throw e;

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				
			}

			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				
			}

		}

		return faces;
	}
	
	
	public List<AdFace> loadAdFaceByLinkId(int linkPid, boolean isLock)
			throws Exception {

		List<AdFace> faces = new ArrayList<AdFace>();

		String sql = "select  a.*  from ad_face a ,ad_face_topo t where a.u_record != 2  and a.face_pid = t.face_pid and t.link_pid = :1 ";
		
		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, linkPid);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				
				AdFace face = new AdFace();

				face.setPid(resultSet.getInt("face_pid"));
				
				face.setRegionId(resultSet.getInt("region_id"));
				
				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				face.setGeometry(GeoTranslator.struct2Jts(struct, 100000, 0));
				
				face.setArea(resultSet.getDouble("area"));
				
				face.setPerimeter(resultSet.getDouble("perimeter"));
				
				face.setMeshId(resultSet.getInt("mesh_id"));
				
				face.setEditFlag(resultSet.getInt("edit_flag"));
				
				face.setRowId(resultSet.getString("row_id"));

				faces.add(face);
				
				// ad_face_topo
				List<IRow> adFaceTopo = new AdFaceTopoSelector(conn).loadRowsByParentId(face.getPid(), isLock);

				for (IRow row : adFaceTopo) {
					row.setMesh(face.mesh());
				}

				face.setFaceTopos(adFaceTopo);

				for (IRow row : adFaceTopo) {
					AdFaceTopo obj = (AdFaceTopo) row;

					face.adFaceTopoMap.put(obj.rowId(), obj);
				}
			}
		} catch (Exception e) {
			
			throw e;

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				
			}

			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				
			}

		}

		return faces;
	}
	
	

	public List<AdFace> loadAdFaceByNodeId(int nodePid, boolean isLock)
			throws Exception {

		List<AdFace> faces = new ArrayList<AdFace>();

		String sql = "select  a.*  from ad_face a ,ad_face_topo t,ad_link l where a.u_record != 2  and a.face_pid = t.face_pid and t.link_pid = l.link_pid and (l.s_node_pid = :1 or l.e_node_pid = :2) ";
		
		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, nodePid);
			
			pstmt.setInt(2, nodePid);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				
				AdFace face = new AdFace();

				face.setPid(resultSet.getInt("face_pid"));
				
				face.setRegionId(resultSet.getInt("region_id"));
				
				STRUCT struct = (STRUCT) resultSet.getObject("geometry");

				face.setGeometry(GeoTranslator.struct2Jts(struct, 100000, 0));
				
				face.setArea(resultSet.getDouble("area"));
				
				face.setPerimeter(resultSet.getDouble("perimeter"));
				
				face.setMeshId(resultSet.getInt("mesh_id"));
				
				face.setEditFlag(resultSet.getInt("edit_flag"));
				
				face.setRowId(resultSet.getString("row_id"));

				faces.add(face);
				
				// ad_face_topo
				List<IRow> adFaceTopo = new AdFaceTopoSelector(conn).loadRowsByParentId(face.getPid(), isLock);

				for (IRow row : adFaceTopo) {
					row.setMesh(face.mesh());
				}

				face.setFaceTopos(adFaceTopo);

				for (IRow row : adFaceTopo) {
					AdFaceTopo obj = (AdFaceTopo) row;

					face.adFaceTopoMap.put(obj.rowId(), obj);
				}
			}
		} catch (Exception e) {
			
			throw e;

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				
			}

			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				
			}

		}

		return faces;
	}

}
