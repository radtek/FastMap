/**
 * 
 */
package com.navinfo.dataservice.dao.glm.selector.rd.same;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.model.rd.same.RdSameNode;
import com.navinfo.dataservice.dao.glm.model.rd.same.RdSameNodePart;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;
import com.navinfo.navicommons.database.sql.DBUtils;
import com.vividsolutions.jts.geom.Geometry;

import oracle.sql.STRUCT;

/**
 * @ClassName: RdSameNodeSelector
 * @author Zhang Xiaolong
 * @date 2016年8月8日 下午8:23:21
 * @Description: TODO
 */
public class RdSameNodeSelector extends AbstractSelector {

	private Connection conn;

	/**
	 * @param cls
	 * @param conn
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public RdSameNodeSelector(Connection conn) throws InstantiationException, IllegalAccessException {
		super(RdSameNode.class, conn);
		this.conn = conn;
	}

	/**
	 * 根据nodePid和表名称查询同一点关系组成点
	 * 
	 * @param nodePid
	 * @param tableName
	 * @param isLock
	 * @return
	 * @throws Exception
	 */
	public RdSameNodePart loadByNodePidAndTableName(int nodePid, String tableName, boolean isLock) throws Exception {
		RdSameNodePart sameNodePart = new RdSameNodePart();

		StringBuilder sb = new StringBuilder("select group_id from " + sameNodePart.tableName()
				+ " where node_pid = :1 and upper(table_name) = :2 and u_record!=2");

		if (isLock) {
			sb.append(" for update nowait");
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setInt(1, nodePid);

			pstmt.setString(2, tableName.toUpperCase());

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				sameNodePart.setGroupId(resultSet.getInt("group_id"));

				sameNodePart.setNodePid(nodePid);

				sameNodePart.setTableName(tableName);
			} else {
				return null;
			}
		} catch (Exception e) {

			throw e;

		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}

		return sameNodePart;
	}

	/**
	 * 根据表名称和nodePid查询几何
	 * @param nodePid
	 * @param tableName
	 * @param isLock
	 * @return
	 * @throws Exception
	 */
	public Geometry getGeoByNodePidAndTableName(int nodePid, String tableName, boolean isLock) throws Exception {
		StringBuilder sb = new StringBuilder(
				"select geometry from " + tableName + " where node_pid = :1 and u_record!=2");

		if (isLock) {
			sb.append(" for update nowait");
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setInt(1, nodePid);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				return GeoTranslator.struct2Jts((STRUCT) resultSet.getObject("geometry"));
			} else {
				return null;
			}
		} catch (Exception e) {

			throw e;

		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
	}
}
