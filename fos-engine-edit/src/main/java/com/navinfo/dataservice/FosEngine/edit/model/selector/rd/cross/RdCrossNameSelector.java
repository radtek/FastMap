package com.navinfo.dataservice.FosEngine.edit.model.selector.rd.cross;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.FosEngine.edit.model.IRow;
import com.navinfo.dataservice.FosEngine.edit.model.ISelector;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.cross.RdCrossName;
import com.navinfo.dataservice.commons.exception.DataNotFoundException;

public class RdCrossNameSelector implements ISelector {

	private static Logger logger = Logger.getLogger(RdCrossNameSelector.class);

	private Connection conn;

	public RdCrossNameSelector(Connection conn) {
		this.conn = conn;

	}

	@Override
	public List<IRow> loadRowsByParentId(int id, boolean isLock)
			throws Exception {

		List<IRow> rows = new ArrayList<IRow>();

		String sql = "select * from rd_cross_name where pid=:1 and u_record!=2";

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

				RdCrossName name = new RdCrossName();

				name.setNameId(resultSet.getInt("name_id"));

				name.setNameGroupid(resultSet.getInt("name_groupid"));

				name.setPid(resultSet.getInt("pid"));

				name.setLangCode(resultSet.getString("lang_code"));

				name.setName(resultSet.getString("name"));

				name.setPhonetic(resultSet.getString("phonetic"));

				name.setSrcFlag(resultSet.getInt("src_flag"));

				name.setRowId(resultSet.getString("row_id"));

				rows.add(name);
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

	@Override
	public IRow loadById(int id, boolean isLock) throws Exception {

		RdCrossName name = new RdCrossName();

		String sql = "select * from " + name.tableName() + " where name_id=:1";

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

				name.setNameId(resultSet.getInt("name_id"));

				name.setNameGroupid(resultSet.getInt("name_groupid"));

				name.setPid(resultSet.getInt("pid"));

				name.setLangCode(resultSet.getString("langCode"));

				name.setName(resultSet.getString("name"));

				name.setPhonetic(resultSet.getString("phonetic"));

				name.setSrcFlag(resultSet.getInt("src_flag"));

				name.setRowId(resultSet.getString("row_id"));
			} else {
				
				throw new DataNotFoundException(null);
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
		return name;
	}

	@Override
	public IRow loadByRowId(String rowId, boolean isLock) throws Exception {

		return null;
	}

}
