package com.navinfo.dataservice.dao.glm.selector.rd.cross;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.navinfo.dataservice.commons.exception.DataNotFoundException;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCross;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCrossLink;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCrossName;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCrossNode;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;
import com.navinfo.dataservice.dao.glm.selector.ReflectionAttrUtils;
import com.navinfo.navicommons.database.sql.DBUtils;

public class RdCrossSelector extends AbstractSelector {

	private static Logger logger = Logger.getLogger(RdCrossSelector.class);

	private Connection conn;

	public RdCrossSelector(Connection conn) {
		super(conn);
		this.conn = conn;
		this.setCls(RdCross.class);
	}

	@Override
	public IRow loadById(int id, boolean isLock, boolean... noChild) throws Exception {

		RdCross cross = new RdCross();

		String sql = "select * from rd_cross where pid=:1";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				ReflectionAttrUtils.executeResultSet(cross, resultSet);

				List<IRow> links = loadRowsByClassParentId(RdCrossLink.class, id, isLock, "");

				for (IRow row : links) {
					row.setMesh(cross.mesh());
				}

				cross.setLinks(links);

				for (IRow row : cross.getLinks()) {
					RdCrossLink obj = (RdCrossLink) row;

					cross.linkMap.put(obj.rowId(), obj);
				}

				List<IRow> nodes = loadRowsByClassParentId(RdCrossNode.class, id, isLock, "");

				for (IRow row : nodes) {
					row.setMesh(cross.mesh());
				}

				cross.setNodes(nodes);

				for (IRow row : cross.getNodes()) {
					RdCrossNode obj = (RdCrossNode) row;

					cross.nodeMap.put(obj.rowId(), obj);
				}

				List<IRow> names = loadRowsByClassParentId(RdCrossName.class, id, isLock, "");

				for (IRow row : names) {
					row.setMesh(cross.mesh());
				}

				cross.setNames(names);

				for (IRow row : cross.getNames()) {
					RdCrossName obj = (RdCrossName) row;

					cross.nameMap.put(obj.rowId(), obj);
				}

				cross.setRowId(resultSet.getString("row_id"));

			} else {
				throw new DataNotFoundException("该点未创建路口！");
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

		return cross;
	}

	/**
	 * 
	 * 获取node或者link所在的路口
	 * 
	 * @param nodePid
	 * @param isLock
	 * @return
	 * @throws Exception
	 */
	public List<RdCross> loadRdCrossByNodeOrLink(List<Integer> nodePids, List<Integer> linkPids, boolean isLock)
			throws Exception {

		List<RdCross> result = new ArrayList<RdCross>();
		
		boolean nodePidEmptyFlag = CollectionUtils.isEmpty(nodePids);

		boolean linkPidEmptyFlag = CollectionUtils.isEmpty(linkPids);

		if (nodePidEmptyFlag && linkPidEmptyFlag) {
			return result;
		}

		String nodeStr = "";
		
		String linkStr= "";
		
		if(!nodePidEmptyFlag)
		{
			nodeStr = nodePids.toString();

			nodeStr = nodeStr.replace("[", "(");

			nodeStr = nodeStr.replace("]", ")");
		}
		if(!linkPidEmptyFlag)
		{
			linkStr = linkPids.toString();

			linkStr = linkStr.replace("[", "(");

			linkStr = linkStr.replace("]", ")");
		}

		String sql = "";

		if (nodePidEmptyFlag) {
			sql = "select a.*, c.mesh_id   from rd_cross a, RD_LINK c  where   exists (select null           from rd_cross_link d          where a.pid = d.pid            and d.link_pid in "
					+ linkStr
					+ "          AND D.LINK_PID = C.LINK_PID            and d.u_record != 2)    and a.u_record != 2";
		} else if (linkPidEmptyFlag) {
			sql = "select a.*, c.mesh_id   from rd_cross a, rd_node_mesh c  where  exists (select null           from rd_cross_node d          where a.pid = d.pid            and d.node_pid in "
					+ nodeStr
					+ "          and d.u_record != 2            and c.node_pid = d.node_pid            )    and a.u_record != 2";
		} else {
			sql = "select a.*, c.mesh_id   from rd_cross a, rd_node_mesh c  where  exists (select null           from rd_cross_node d          where a.pid = d.pid            and d.node_pid in "
					+ nodeStr
					+ "           and d.u_record != 2            and c.node_pid = d.node_pid            )    and a.u_record != 2    union    select a.*, c.mesh_id   from rd_cross a, RD_LINK c  where   exists (select null           from rd_cross_link d          where a.pid = d.pid            and d.link_pid in "
					+ linkStr
					+ "           AND D.LINK_PID = C.LINK_PID            and d.u_record != 2)    and a.u_record != 2";
		}

		Statement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.createStatement();

			resultSet = pstmt.executeQuery(sql);

			while (resultSet.next()) {

				RdCross cross = new RdCross();

				ReflectionAttrUtils.executeResultSet(cross, resultSet);

				setChildData(cross, isLock);

				result.add(cross);
			}
		} catch (Exception e) {

			throw e;

		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}

		return result;
	}

	public RdCross loadCrossByNodePid(int nodePid, boolean isLock) throws Exception {

		RdCross cross = new RdCross();

		String sql = "select a.*, c.mesh_id   from rd_cross a, rd_node_mesh c  where  exists (select null           from rd_cross_node d          where a.pid = d.pid            and d.node_pid =:1      and d.u_record != 2            and c.node_pid = d.node_pid            )    and a.u_record != 2";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, nodePid);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				ReflectionAttrUtils.executeResultSet(cross, resultSet);

				setChildData(cross, isLock);

			} else {

				throw new DataNotFoundException("该点未创建路口！");
			}
		} catch (Exception e) {

			throw e;

		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
		return cross;
	}

	private void setChildData(RdCross cross, boolean isLock) throws Exception {
		List<IRow> links = new AbstractSelector(RdCrossLink.class, conn).loadRowsByParentId(cross.getPid(), isLock);

		for (IRow row : links) {
			row.setMesh(cross.mesh());
		}

		cross.setLinks(links);

		for (IRow row : cross.getLinks()) {
			RdCrossLink obj = (RdCrossLink) row;

			cross.linkMap.put(obj.rowId(), obj);
		}

		List<IRow> nodes = new AbstractSelector(RdCrossNode.class, conn).loadRowsByParentId(cross.getPid(), isLock);

		for (IRow row : nodes) {
			row.setMesh(cross.mesh());
		}

		cross.setNodes(nodes);

		for (IRow row : cross.getNodes()) {
			RdCrossNode obj = (RdCrossNode) row;

			cross.nodeMap.put(obj.rowId(), obj);
		}

		List<IRow> names = new AbstractSelector(RdCrossName.class, conn).loadRowsByParentId(cross.getPid(), isLock);

		for (IRow row : names) {
			row.setMesh(cross.mesh());
		}

		cross.setNames(names);

		for (IRow row : cross.getNames()) {
			RdCrossName obj = (RdCrossName) row;

			cross.nameMap.put(obj.rowId(), obj);
		}
	}

	public List<RdCross> loadCrossBySql(String sql, boolean isLock) throws Exception {

		List<RdCross> result = new ArrayList<RdCross>();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				RdCross cross = new RdCross();
				ReflectionAttrUtils.executeResultSet(cross, resultSet);
				result.add(cross);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(pstmt);
		}
		return result;
	}
}
