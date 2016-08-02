package com.navinfo.dataservice.dao.glm.selector.ad.geo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdNode;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdNodeMesh;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;
import com.navinfo.dataservice.dao.glm.selector.ReflectionAttrUtils;
import com.navinfo.navicommons.database.sql.DBUtils;

public class AdNodeSelector extends AbstractSelector {

	private Connection conn;

	public AdNodeSelector(Connection conn) {
		super(conn);
		this.conn = conn;
		this.setCls(AdNode.class);
	}

	// 加载盲端节点
	public List<AdNode> loadEndAdNodeByLinkPid(int linkPid, boolean isLock)
			throws Exception {

		List<AdNode> nodes = new ArrayList<AdNode>();
		String sql = "with tmp1 as  (select s_node_pid, e_node_pid from ad_link where link_pid = :1), tmp2 as  (select b.link_pid, s_node_pid     from ad_link b    where exists (select null from tmp1 a where a.s_node_pid = b.s_node_pid) and b.u_record!=2   union all   select b.link_pid, e_node_pid     from ad_link b    where exists (select null from tmp1 a where a.s_node_pid = b.e_node_pid) and b.u_record!=2) , tmp3 as  (select b.link_pid, s_node_pid as e_node_pid     from ad_link b    where exists (select null from tmp1 a where a.e_node_pid = b.s_node_pid) and b.u_record!=2   union all   select b.link_pid, e_node_pid     from ad_link b    where exists (select null from tmp1 a where a.e_node_pid = b.e_node_pid) and b.u_record!=2), tmp4 as  (select s_node_pid pid from tmp2 group by s_node_pid having count(*) = 1), tmp5 as  (select e_node_pid pid from tmp3 group by e_node_pid having count(*) = 1), tmp6 as  (select pid from tmp4 union select pid from tmp5) select *   from ad_node a  where exists (select null from tmp6 b where a.node_pid = b.pid) and a.u_record!=2";

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

				AdNode node = new AdNode();
				ReflectionAttrUtils.executeResultSet(node, resultSet);
				node.setMeshes(new AbstractSelector(AdNodeMesh.class, conn)
						.loadRowsByParentId(node.getPid(), isLock));

				nodes.add(node);
			}
		} catch (Exception e) {

			throw e;

		} finally {
			DBUtils.closeResultSet(resultSet);
		    DBUtils.closeStatement(pstmt);

		}

		return nodes;
	}

	public int loadAdLinkCountOnNode(int nodePid) throws Exception {

		String sql = "select count(1) count from ad_link a where a.s_node_pid=:1 or a.e_node_pid=:2";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setInt(1, nodePid);

			pstmt.setInt(2, nodePid);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (Exception e) {

			throw e;

		} finally {
			DBUtils.closeResultSet(resultSet);
		    DBUtils.closeStatement(pstmt);
		}

		return 0;
	}

}
