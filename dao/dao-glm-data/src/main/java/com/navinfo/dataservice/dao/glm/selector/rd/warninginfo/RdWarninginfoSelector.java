package com.navinfo.dataservice.dao.glm.selector.rd.warninginfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.navinfo.dataservice.commons.exception.DataNotFoundException;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISelector;
import com.navinfo.dataservice.dao.glm.model.rd.warninginfo.RdWarninginfo;

public class RdWarninginfoSelector implements ISelector{

	
	private Connection conn;

	public RdWarninginfoSelector(Connection conn) {
		this.conn = conn;
	}
	@Override
	public IRow loadById(int id, boolean isLock) throws Exception {
		RdWarninginfo obj = new RdWarninginfo();

		String sql = "select a.* from "
				+ obj.tableName()
				+ " a where a.pid=:1 and a.u_record!=2 ";

		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				setAttr(obj, resultSet);

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

		return obj;
	}

	@Override
	public IRow loadByRowId(String rowId, boolean isLock) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IRow> loadRowsByParentId(int id, boolean isLock)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	private void setAttr(RdWarninginfo obj, ResultSet resultSet)
			throws SQLException {

		obj.setPid(resultSet.getInt("pid"));

		obj.setLinkPid(resultSet.getInt("link_pid"));

		obj.setNodePid(resultSet.getInt("node_pid"));

		obj.setTypeCode(resultSet.getString("type_code"));

		obj.setValidDis(resultSet.getInt("valid_dis"));

		obj.setWarnDis(resultSet.getInt("warn_dis"));

		obj.setTimeDomain(resultSet.getString("time_domain"));

		obj.setVehicle(resultSet.getInt("vehicle"));

		obj.setDescript(resultSet.getString("descript"));
		
		obj.setRowId(resultSet.getString("row_id"));
	}
	
	
}
