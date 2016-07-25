package com.navinfo.dataservice.dao.glm.selector.lu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISelector;
import com.navinfo.dataservice.dao.glm.model.lu.LuFace;
import com.navinfo.dataservice.dao.glm.model.lu.LuFaceName;
import com.navinfo.dataservice.dao.glm.model.lu.LuFaceTopo;
import com.navinfo.dataservice.dao.glm.selector.ReflectionAttrUtils;

public class LuFaceSelector implements ISelector {

	private Logger logger = Logger.getLogger(LuFaceSelector.class);

	private Connection conn;

	public LuFaceSelector(Connection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public IRow loadById(int id, boolean isLock) throws Exception {
		LuFace face = new LuFace();

		StringBuilder sb = new StringBuilder(
				"select * from " + face.tableName() + " WHERE face_pid = :1 and  u_record !=2");

		if (isLock) {
			sb.append(" for update nowait");
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setInt(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
//				setAttr(isLock, face, resultSet);
				ReflectionAttrUtils.executeResultSet(face, resultSet);
				this.setChildren(isLock, face, resultSet);
				
				return face;
			} else {

				throw new Exception("对应LU_FACE不存在!");
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
	}

	private void setAttr(boolean isLock, LuFace face, ResultSet resultSet) throws SQLException, Exception {

		List<IRow> luFaceTopo = new LuFaceTopoSelector(conn).loadRowsByParentId(face.pid(), isLock);

		for (IRow row : luFaceTopo) {
			row.setMesh(face.mesh());
		}

		face.setFaceTopos(luFaceTopo);

		for (IRow row : luFaceTopo) {
			LuFaceTopo obj = (LuFaceTopo) row;

			face.luFaceTopoMap.put(obj.rowId(), obj);
		}

		List<IRow> luFaceNames = new LuFaceNameSelector(conn).loadRowsByParentId(face.getPid(), isLock);

		for (IRow row : luFaceNames) {
			LuFaceName obj = (LuFaceName) row;

			face.luFaceNameMap.put(obj.rowId(), obj);
		}
		face.setFaceNames(luFaceNames);

	}

	private void setChildren(boolean isLock, LuFace face, ResultSet resultSet) throws SQLException, Exception {

		List<IRow> luFaceTopo = new LuFaceTopoSelector(conn).loadRowsByParentId(face.pid(), isLock);

		for (IRow row : luFaceTopo) {
			row.setMesh(face.mesh());
		}

		face.setFaceTopos(luFaceTopo);

		for (IRow row : luFaceTopo) {
			LuFaceTopo obj = (LuFaceTopo) row;

			face.luFaceTopoMap.put(obj.rowId(), obj);
		}

		List<IRow> luFaceNames = new LuFaceNameSelector(conn).loadRowsByParentId(face.getPid(), isLock);

		for (IRow row : luFaceNames) {
			LuFaceName obj = (LuFaceName) row;

			face.luFaceNameMap.put(obj.rowId(), obj);
		}
		face.setFaceNames(luFaceNames);

	}
	
	@Override
	public IRow loadByRowId(String rowId, boolean isLock) throws Exception {
		return null;
	}

	@Override
	public List<IRow> loadRowsByParentId(int id, boolean isLock) throws Exception {
		List<IRow> faces = new ArrayList<IRow>();

		String sql = "select  a.*  from lu_face a where a.u_record != 2  and a.feature_pid = :1 ";

		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, id);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				LuFace face = new LuFace();

//				this.setAttr(isLock, face, resultSet);
				ReflectionAttrUtils.executeResultSet(face, resultSet);
				this.setChildren(isLock, face, resultSet);

				faces.add(face);

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

	public List<LuFace> loadLuFaceByLinkId(int linkPid, boolean isLock) throws Exception {

		List<LuFace> faces = new ArrayList<LuFace>();

		StringBuilder bf = new StringBuilder();
		bf.append("select b.* from lu_face b where b.face_pid in (select a.face_pid ");
		bf.append("  FROM lu_face a, lu_face_topo t");
		bf.append(" WHERE     a.u_record != 2  AND T.U_RECORD != 2");
		bf.append("  AND a.face_pid = t.face_pid");
		bf.append(" AND t.link_pid = :1 group by a.face_pid)");

		if (isLock) {
			bf.append(" for update nowait");
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(bf.toString());

			pstmt.setInt(1, linkPid);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				LuFace face = new LuFace();

//				this.setAttr(isLock, face, resultSet);
				ReflectionAttrUtils.executeResultSet(face, resultSet);
				this.setChildren(isLock, face, resultSet);

				faces.add(face);

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

	public List<LuFace> loadLuFaceByNodeId(int nodePid, boolean isLock) throws Exception {

		List<LuFace> faces = new ArrayList<LuFace>();

		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT b.* from lu_face b where b.face_pid in (select a.face_pid ");
		builder.append(" FROM lu_face a, lu_face_topo t, lu_link l ");
		builder.append(" WHERE a.u_record != 2 and t.u_record != 2 and l.u_record != 2");
		builder.append(" AND a.face_pid = t.face_pid");
		builder.append(" AND t.link_pid = l.link_pid");
		builder.append(" AND (l.s_node_pid = :1 OR l.e_node_pid = :2) group by a.face_pid)");
		
		if (isLock) {
			builder.append(" for update nowait");
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(builder.toString());

			pstmt.setInt(1, nodePid);

			pstmt.setInt(2, nodePid);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				LuFace face = new LuFace();

//				this.setAttr(isLock, face, resultSet);
				ReflectionAttrUtils.executeResultSet(face, resultSet);
				this.setChildren(isLock, face, resultSet);
				
				faces.add(face);
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
