/**
 * 
 */
package com.navinfo.dataservice.dao.glm.selector.rd.crf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.rd.inter.RdInter;
import com.navinfo.dataservice.dao.glm.model.rd.inter.RdInterLink;
import com.navinfo.dataservice.dao.glm.model.rd.inter.RdInterNode;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;
import com.navinfo.dataservice.dao.glm.selector.ReflectionAttrUtils;
import com.navinfo.navicommons.database.sql.DBUtils;

/**
 * @ClassName: RdInterSelector
 * @author Zhang Xiaolong
 * @date 2016年8月5日 下午2:11:13
 * @Description: TODO
 */
public class RdInterSelector extends AbstractSelector {

	private Connection conn = null;

	/**
	 * @param cls
	 * @param conn
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public RdInterSelector(Connection conn) {
		super(conn);
		this.setCls(RdInter.class);
		this.conn = conn;
	}
	
	/**
	 * 根据nodePid查询CRF交叉点对象
	 * @param nodePidStr
	 * @param isLock
	 * @return
	 * @throws Exception
	 */
	public List<RdInter> loadInterByNodePid(String nodePidStr,boolean isLock) throws Exception
	{
		List<RdInter> interList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			StringBuilder sb = new StringBuilder(
					"select a.* from rd_inter a,RD_INTER_NODE b where a.PID = b.PID and b.NODE_PID in("+nodePidStr+") and a.u_record !=2 and b.u_record !=2");
//			if (isLock) {
//				sb.append(" for update nowait");
//			}
			pstmt = conn.prepareStatement(sb.toString());

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				RdInter rdInter = new RdInter();
				
				ReflectionAttrUtils.executeResultSet(rdInter, resultSet);
				
				List<IRow> interLinkList = new AbstractSelector(RdInterLink.class,conn).loadRowsByParentId(rdInter.getPid(), isLock);
				
				rdInter.setLinks(interLinkList);
				
				List<IRow> interNodeList = new AbstractSelector(RdInterNode.class,conn).loadRowsByParentId(rdInter.getPid(), isLock);
				
				rdInter.setNodes(interNodeList);
				
				interList.add(rdInter);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
		return interList;
	}
	/**
	 * 更具nodepid查询crf交叉点pid
	 * @param nodePidsStr
	 * @param isLock
	 * @return
	 * @throws Exception
	 */
	public List<Integer> loadInterPidByNodePid(String nodePidsStr, boolean isLock) throws Exception {
		List<Integer> interPidList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			StringBuilder sb = new StringBuilder(
					"select distinct pid from rd_inter_node where node_pid in(" + nodePidsStr + ") and u_record !=2");
			if (isLock) {
				sb.append(" for update nowait");
			}
			pstmt = conn.prepareStatement(sb.toString());

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				interPidList.add(resultSet.getInt("pid"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
		return interPidList;
	}
	
	/**
	 * 根据linkPid查询CRF交叉点对象
	 * @param linkPid link Pid
	 * @param isLock
	 * @return CRF交叉点对象
	 * @throws Exception 
	 */
	public RdInterLink loadByLinkPid(int linkPid,boolean isLock) throws Exception
	{
		RdInterLink rdInterLink = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			StringBuilder sb = new StringBuilder(
					"select * from rd_inter_link where link_pid =:1 and u_record !=2");
			if (isLock) {
				sb.append(" for update nowait");
			}
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, linkPid);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				rdInterLink = new RdInterLink();
				ReflectionAttrUtils.executeResultSet(rdInterLink, resultSet);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DBUtils.closeResultSet(resultSet);
			DBUtils.closeStatement(pstmt);
		}
		return rdInterLink;
	}
}
