package com.navinfo.dataservice.FosEngine.edit.operation.obj.rdrestriction.create;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.FosEngine.edit.model.IRow;
import com.navinfo.dataservice.FosEngine.edit.model.ObjStatus;
import com.navinfo.dataservice.FosEngine.edit.model.Result;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.restrict.RdRestriction;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.restrict.RdRestrictionDetail;
import com.navinfo.dataservice.FosEngine.edit.model.bean.rd.restrict.RdRestrictionVia;
import com.navinfo.dataservice.FosEngine.edit.operation.IOperation;
import com.navinfo.dataservice.commons.geom.AngleCalculator;
import com.navinfo.dataservice.commons.service.PidService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class Operation implements IOperation {

	private Command command;

	private Connection conn;

	private LineSegment inLinkSegment;

	/**
	 * key为退出线pid，value为退出线线段
	 */
	private Map<Integer, LineSegment> outLinkSegmentMap;

	/**
	 * key为退出线pid， value为经过线pid列表
	 */
	private Map<Integer, List<Integer>> viaLinkPidMap;

	/**
	 * key为退出线pid，value为交限类型
	 */
	private Map<Integer, Integer> relationTypeMap;

	public Operation(Command command, Connection conn) {
		this.command = command;

		this.conn = conn;
	}

	@Override
	public String run(Result result) throws Exception {

		RdRestriction restrict = new RdRestriction();

		restrict.setPid(PidService.getInstance().applyRestrictionPid());

		result.setPrimaryPid(restrict.getPid());

		restrict.setInLinkPid(command.getInLinkPid());

		restrict.setNodePid(command.getNodePid());

		List<Integer> outLinkPids = null;

		if (command.getOutLinkPids() != null) {
			outLinkPids = command.getOutLinkPids();
		}

		else {
			outLinkPids = calOutLinks(command.getInLinkPid(),
					command.getNodePid());
			
			if (outLinkPids.size() == 0){
				throw new Exception("进入点不在路口上或者该路口没有退出线");
			}
		}
		
		this.calViaLinks(command.getInLinkPid(), command.getNodePid(),
				outLinkPids);

		this.calViaLinks(command.getInLinkPid(), command.getNodePid(),
				outLinkPids);

		List<IRow> details = new ArrayList<IRow>();

		List<Integer> restricInfos = new ArrayList<Integer>();
		
		List<Integer> newRestricInfos = command.getRestricInfos();
		
		for (int outLinkPid : outLinkPids) {

			RdRestrictionDetail detail = new RdRestrictionDetail();

			detail.setRestricPid(restrict.getPid());

			detail.setOutLinkPid(outLinkPid);

			LineSegment outLinkSegment = outLinkSegmentMap.get(detail
					.getOutLinkPid());

			double angle = AngleCalculator.getAngle(inLinkSegment,
					outLinkSegment);

			int restricInfo = this.calRestricInfo(angle);
			
			if (newRestricInfos != null){
				if (newRestricInfos.contains(restricInfo)){
					newRestricInfos.remove(Integer.valueOf(restricInfo));
				}
				else{
					continue;
				}
			}
			
			detail.setPid(PidService.getInstance().applyRestrictionDetailPid());

			detail.setRestricInfo(restricInfo);

			detail.setRelationshipType(relationTypeMap.get(detail
					.getOutLinkPid()));

			List<Integer> viaLinkPids = viaLinkPidMap.get(detail
					.getOutLinkPid());

			int seqNum = 1;

			List<IRow> vias = new ArrayList<IRow>();

			for (Integer viaLinkPid : viaLinkPids) {

				RdRestrictionVia via = new RdRestrictionVia();

				via.setDetailId(detail.getPid());

				via.setSeqNum(seqNum);

				via.setLinkPid(viaLinkPid);

				vias.add(via);

				seqNum++;
			}

			detail.setVias(vias);

			details.add(detail);

		}
		
		if(details.size() == 0){
			throw new Exception("找不到对应的退出线！");
		}

		restrict.setDetails(details);

		if (restricInfos.size() > 0) {
			StringBuilder sb = new StringBuilder();

			for (Integer restricInfo : restricInfos) {
				sb.append("[");

				sb.append(restricInfo);

				sb.append("]");

				sb.append(",");
			}

			sb.deleteCharAt(sb.length() - 1);

			restrict.setRestricInfo(sb.toString());
		}

		result.insertObject(restrict, ObjStatus.INSERT);

		return null;
	}

	/**
	 * 计算退出线
	 * 
	 * @param inLinkPid
	 * @param nodePid
	 * @param restricInfos
	 */
	private List<Integer> calOutLinks(int inLinkPid, int nodePid)
			throws Exception {

		List<Integer> outLinkPids = new ArrayList<Integer>();

		String sql = "with c1 as  (select * from rd_cross_node where node_pid = 185526), c2 as  (select *   from rd_link b  where exists (select null from c1 c where c.node_pid in (b.s_node_pid))  and link_pid not in    (select d.link_pid     from rd_cross_link d    where d.pid in (select pid from c1)) union all select *   from rd_link b  where exists (select null from c1 c where c.node_pid in (b.e_node_pid))  and link_pid not in    (select d.link_pid     from rd_cross_link d    where d.pid in (select pid from c1)))  select * from c2  where exists  (select null     from c1    where (c2.direct = 1)     or (c2.direct = 2 and c2.s_node_pid = c1.node_pid)     or (c2.direct = 3 and c2.e_node_pid = c1.node_pid)) ";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, nodePid);

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				int outLinkPid = resultSet.getInt("link_pid");
				
				if (outLinkPid == inLinkPid){
					continue;
				}

				outLinkPids.add(outLinkPid);
			}

			return outLinkPids;
		} catch (Exception e) {
			throw e;

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}

		}
	}

	/**
	 * 计算经过线
	 * 
	 * @param inLinkPid
	 * @param nodePid
	 * @param outLinkPids
	 * @throws Exception
	 */
	private void calViaLinks(int inLinkPid, int nodePid,
			List<Integer> outLinkPids) throws Exception {

		outLinkSegmentMap = new HashMap<Integer, LineSegment>();

		viaLinkPidMap = new HashMap<Integer, List<Integer>>();

		relationTypeMap = new HashMap<Integer, Integer>();

		String sql = "select * from table(package_utils.get_restrict_points(:1,:2,:3))";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, inLinkPid);

			pstmt.setInt(2, nodePid);

			StringBuilder sb = new StringBuilder();

			for (int pid : outLinkPids) {

				sb.append(pid);

				sb.append(",");
			}

			sb.deleteCharAt(sb.length() - 1);

			pstmt.setString(3, sb.toString());

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				if (inLinkSegment == null) {
					String inNode1 = resultSet.getString("in_node1");

					String inNode2 = resultSet.getString("in_node2");

					String[] splits = inNode1.split(",");

					Coordinate p1 = new Coordinate(Double.valueOf(splits[0]),
							Double.valueOf(splits[1]));

					splits = inNode2.split(",");

					Coordinate p2 = new Coordinate(Double.valueOf(splits[0]),
							Double.valueOf(splits[1]));

					inLinkSegment = new LineSegment(p1, p2);
				}

				int outLinkPid = resultSet.getInt("link_pid");

				int relationType = resultSet.getInt("relation_type");

				relationTypeMap.put(outLinkPid, relationType);

				String outNode1 = resultSet.getString("out_node1");

				String outNode2 = resultSet.getString("out_node2");

				String[] splits = outNode1.split(",");

				Coordinate p1 = new Coordinate(Double.valueOf(splits[0]),
						Double.valueOf(splits[1]));

				splits = outNode2.split(",");

				Coordinate p2 = new Coordinate(Double.valueOf(splits[0]),
						Double.valueOf(splits[1]));

				LineSegment line = new LineSegment(p1, p2);

				outLinkSegmentMap.put(outLinkPid, line);

				String viaPath = resultSet.getString("via_path");

				List<Integer> viaLinks = new ArrayList<Integer>();

				if (viaPath != null) {

					splits = viaPath.split(",");

					for (String s : splits) {
						if (!s.equals("")) {
							viaLinks.add(Integer.valueOf(s));
						}
					}

				}

				viaLinkPidMap.put(outLinkPid, viaLinks);
			}

		} catch (Exception e) {
			throw e;

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}

		}

	}

	/**
	 * 计算限制信息
	 * 
	 * @param angle
	 * @return
	 */
	private int calRestricInfo(double angle) {
		if (angle > 45 && angle <= 135) {
			return 3;
		} else if (angle > 135 && angle <= 225) {
			return 4;
		} else if (angle > 225 && angle <= 315) {
			return 2;
		} else {
			return 1;
		}

	}
}
