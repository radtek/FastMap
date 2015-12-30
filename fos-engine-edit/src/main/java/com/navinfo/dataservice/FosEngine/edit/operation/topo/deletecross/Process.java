package com.navinfo.dataservice.FosEngine.edit.operation.topo.deletecross;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.FosEngine.edit.log.LogWriter;
import com.navinfo.dataservice.FosEngine.edit.model.IRow;
import com.navinfo.dataservice.FosEngine.edit.model.Result;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdBranch;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdBranchDetail;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdBranchRealimage;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdBranchSchematic;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdBranchVia;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdSeriesbranch;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdSignasreal;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.branch.RdSignboard;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.cross.RdCross;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.laneconnexity.RdLaneConnexity;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.restrict.RdRestriction;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdBranchDetailSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdBranchRealimageSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdBranchSchematicSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdBranchViaSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdSeriesbranchSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdSignasrealSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.branch.RdSignboardSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.cross.RdCrossSelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.laneconnexity.RdLaneTopologySelector;
import com.navinfo.dataservice.FosEngine.edit.model.selector.rd.restrict.RdRestrictionDetailSelector;
import com.navinfo.dataservice.FosEngine.edit.operation.ICommand;
import com.navinfo.dataservice.FosEngine.edit.operation.IOperation;
import com.navinfo.dataservice.FosEngine.edit.operation.IProcess;
import com.navinfo.dataservice.FosEngine.edit.operation.OperatorFactory;
import com.navinfo.dataservice.commons.db.DBOraclePoolManager;

public class Process implements IProcess {

	private static Logger logger = Logger.getLogger(Process.class);

	private Command command;

	private Result result;

	private Connection conn;

	private String postCheckMsg;

	public Process(ICommand command) throws Exception {
		this.command = (Command) command;

		this.result = new Result();

		this.conn = DBOraclePoolManager.getConnection(this.command
				.getProjectId());

	}

	@Override
	public ICommand getCommand() {

		return command;
	}

	@Override
	public Result getResult() {

		return result;
	}

	public String preCheck() throws Exception {

		return null;
	}

	public void lockRdCross() throws Exception {
		// 获取该cross对象
		RdCrossSelector selector = new RdCrossSelector(this.conn);

		RdCross cross = (RdCross) selector.loadById(command.getPid(), true);

		command.setCross(cross);
	}

	public void lockRdRestriction() throws Exception {

		List<RdRestriction> result = new ArrayList<RdRestriction>();

		String sql = "select * from rd_restriction a where exists (select null from rd_cross_node b where b.pid=:1 and a.node_pid=b.node_pid) and u_record!=2";

		sql = sql + " for update nowait";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, command.getPid());

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				RdRestriction restrict = new RdRestriction();

				restrict.setPid(resultSet.getInt("pid"));

				restrict.setInLinkPid(resultSet.getInt("in_link_pid"));

				restrict.setNodePid(resultSet.getInt("node_pid"));

				restrict.setRestricInfo(resultSet.getString("restric_info"));

				restrict.setKgFlag(resultSet.getInt("kg_flag"));

				restrict.setRowId(resultSet.getString("row_id"));

				RdRestrictionDetailSelector detail = new RdRestrictionDetailSelector(
						conn);

				restrict.setDetails(detail.loadRowsByParentId(
						restrict.getPid(), true));

				result.add(restrict);
			}
			
			command.setRestricts(result);
		} catch (Exception e) {
			
			throw e;
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				
			}

			try {
				pstmt.close();
			} catch (Exception e) {
				
			}
		}

	}
	
	public void lockRdLaneConnexity() throws Exception {
		List<RdLaneConnexity> result = new ArrayList<RdLaneConnexity>();

		String sql = "select * from rd_lane_connexity a where exists (select null from rd_cross_node b where b.pid=:1 and a.node_pid=b.node_pid) and u_record!=2";

		sql = sql + " for update nowait";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, command.getPid());

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				RdLaneConnexity laneConn = new RdLaneConnexity();
				
				laneConn.setPid(resultSet.getInt("pid"));
				
				laneConn.setRowId(resultSet.getString("row_id"));
				
				laneConn.setInLinkPid(resultSet.getInt("in_link_pid"));
				
				laneConn.setNodePid(resultSet.getInt("node_pid"));
				
				laneConn.setLaneInfo(resultSet.getString("lane_info"));
				
				laneConn.setConflictFlag(resultSet.getInt("conflict_flag"));
				
				laneConn.setKgFlag(resultSet.getInt("kg_flag"));
				
				laneConn.setLaneNum(resultSet.getInt("lane_num"));
				
				laneConn.setLeftExtend(resultSet.getInt("left_extend"));
				
				laneConn.setRightExtend(resultSet.getInt("right_extend"));
				
				laneConn.setSrcFlag(resultSet.getInt("src_flag"));
				
				RdLaneTopologySelector topoSelector = new RdLaneTopologySelector(
						conn);

				laneConn.setTopos(topoSelector.loadRowsByParentId(laneConn.getPid(), true));
				
				result.add(laneConn);
			}
			
			command.setLanes(result);
		} catch (Exception e) {
			
			throw e;
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				
			}

			try {
				pstmt.close();
			} catch (Exception e) {
				
			}
		}
	}
	
	public void lockRdBranch() throws Exception {

		List<RdBranch> result = new ArrayList<RdBranch>();

		String sql = "select * from rd_branch a where exists (select null from rd_cross_node b where b.pid=:1 and a.node_pid=b.node_pid) and u_record!=2";

		sql = sql + " for update nowait";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, command.getPid());

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				
				RdBranch branch = new RdBranch();

				branch.setPid(resultSet.getInt("branch_pid"));

				branch.setInLinkPid(resultSet.getInt("in_link_pid"));

				branch.setNodePid(resultSet.getInt("node_pid"));

				branch.setOutLinkPid(resultSet.getInt("out_link_pid"));

				branch.setRelationshipType(resultSet
						.getInt("relationship_type"));

				branch.setRowId(resultSet.getString("row_id"));

				RdBranchDetailSelector detailSelector = new RdBranchDetailSelector(
						conn);

				branch.setDetails(detailSelector.loadRowsByParentId(branch.getPid(), true));

				RdSignboardSelector signboardSelector = new RdSignboardSelector(
						conn);

				branch.setSignboards(signboardSelector.loadRowsByParentId(branch.getPid(), true));

				RdSignasrealSelector signasrealSelector = new RdSignasrealSelector(
						conn);

				branch.setSignasreals(signasrealSelector.loadRowsByParentId(branch.getPid(),
						true));

				RdSeriesbranchSelector seriesbranchSelector = new RdSeriesbranchSelector(
						conn);

				branch.setSeriesbranches(seriesbranchSelector
						.loadRowsByParentId(branch.getPid(), true));

				RdBranchRealimageSelector realimageSelector = new RdBranchRealimageSelector(
						conn);

				branch.setRealimages(realimageSelector.loadRowsByParentId(branch.getPid(),
						true));

				RdBranchSchematicSelector schematicSelector = new RdBranchSchematicSelector(
						conn);

				branch.setSchematics(schematicSelector.loadRowsByParentId(branch.getPid(),
						true));

				RdBranchViaSelector viaSelector = new RdBranchViaSelector(conn);

				branch.setVias(viaSelector.loadRowsByParentId(branch.getPid(), true));

			}
			
			command.setBranches(result);
		} catch (Exception e) {
			
			throw e;
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				
			}

			try {
				pstmt.close();
			} catch (Exception e) {
				
			}
		}
	
	}

	@Override
	public boolean prepareData() throws Exception {

		String msg = preCheck();

		if (null != msg) {
			throw new Exception(msg);
		}
		
		lockRdCross();

		if (command.getCross() == null) {

			throw new Exception("指定删除的路口不存在！");
		}

		lockRdRestriction();
		
		lockRdLaneConnexity();
		
		lockRdBranch();

		return true;
	}

	@Override
	public String run() throws Exception {

		try {
			conn.setAutoCommit(false);

			String preCheckMsg = this.preCheck();

			if (preCheckMsg != null) {
				throw new Exception(preCheckMsg);
			}

			prepareData();

			IOperation op = new OpTopo(command, conn);

			op.run(result);

			IOperation opRefRestrict = new OpRefRdRestriction(command);

			opRefRestrict.run(result);
			
			IOperation opRefLaneConnexity = new OpRefRdLaneConnexity(command);

			opRefLaneConnexity.run(result);
			
			IOperation opRefBranch = new OpRefRdBranch(command);

			opRefBranch.run(result);

			recordData();

			postCheck();

			conn.commit();

		} catch (Exception e) {

			conn.rollback();

			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {

			}
		}

		return null;
	}

	@Override
	public boolean recordData() throws Exception {

		OperatorFactory.recordData(conn, result);

		LogWriter lw = new LogWriter(conn);

		lw.recordLog(command, result);

		return true;

	}

	private void releaseResource(PreparedStatement pstmt, ResultSet resultSet) {
		try {
			resultSet.close();
		} catch (Exception e) {

		}

		try {
			pstmt.close();
		} catch (Exception e) {

		}
	}

	@Override
	public void postCheck() {

		// 对数据进行检查、检查结果存储在数据库，并存储在临时变量postCheckMsg中
	}

	@Override
	public String getPostCheck() throws Exception {

		return postCheckMsg;
	}

}
