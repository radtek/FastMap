package com.navinfo.dataservice.dao.glm.selector.rd.branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.commons.exception.DataNotFoundException;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ISelector;
import com.navinfo.dataservice.dao.glm.model.rd.branch.RdSignasreal;

public class RdSignasrealSelector implements ISelector {

	private static Logger logger = Logger.getLogger(RdSignasrealSelector.class);

	private Connection conn;

	public RdSignasrealSelector(Connection conn) {
		this.conn = conn;
	}

	@Override
	public IRow loadById(int id, boolean isLock) throws Exception {

		RdSignasreal signasreal = new RdSignasreal();

		String sql = "select * from " + signasreal.tableName()
				+ " where signboard_id =:1";

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

				setAttr(signasreal, resultSet);
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

		return signasreal;
	}

	@Override
	public IRow loadByRowId(String rowId, boolean isLock) throws Exception {
		return null;
	}

	@Override
	public List<IRow> loadRowsByParentId(int id, boolean isLock)
			throws Exception {

		List<IRow> rows = new ArrayList<IRow>();

		String sql = "select * from rd_signasreal where branch_pid=:1 and u_record!=:2";

		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, id);

			pstmt.setInt(2, 2);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				RdSignasreal signasreal = new RdSignasreal();

				setAttr(signasreal, resultSet);

				rows.add(signasreal);
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

		return rows;
	}

	private void setAttr(RdSignasreal signasreal, ResultSet resultSet)
			throws SQLException {

		signasreal.setPid(resultSet.getInt("signboard_id"));

		signasreal.setBranchPid(resultSet.getInt("branch_pid"));

		signasreal.setSvgfileCode(resultSet.getString("svgfile_code"));

		signasreal.setArrowCode(resultSet.getString("arrow_code"));

		signasreal.setMemo(resultSet.getString("memo"));

		signasreal.setRowId(resultSet.getString("row_id"));
	}

}
