package com.navinfo.dataservice.engine.edit.operation.topo.depart.departrdnode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.navinfo.dataservice.dao.glm.selector.rd.link.RdLinkSelector;
import com.navinfo.dataservice.dao.glm.selector.rd.node.RdNodeSelector;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.AbstractProcess;
import com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrdpoint.OpTopo;

public class Process extends AbstractProcess<Command> {

	private Check check = new Check();

	public Process(AbstractCommand command) throws Exception {
		super(command);
	}

	public Process(AbstractCommand command, Connection conn) throws Exception {
		this(command);
		this.setConn(conn);
	}

	/*
	 * // 锁定进入线为该link的交限 public void lockRdRestriction() throws Exception {
	 * RdRestrictionSelector restriction = new
	 * RdRestrictionSelector(this.getConn());
	 * 
	 * List<RdRestriction> restrictions = restriction
	 * .loadRdRestrictionByLinkNode(this.getCommand().getLinkPid(),
	 * this.getCommand().getsNodePid(), this.getCommand().geteNodePid(), true);
	 * 
	 * this.getCommand().setRestrictions(restrictions); }
	 */

	/*
	 * public void lockRdLaneConnexity() throws Exception {
	 * 
	 * RdLaneConnexitySelector selector = new RdLaneConnexitySelector(
	 * this.getConn());
	 * 
	 * List<RdLaneConnexity> lanes = selector.loadRdLaneConnexityByLinkNode(
	 * this.getCommand().getLinkPid(), this.getCommand().getsNodePid(),
	 * this.getCommand().geteNodePid(), true);
	 * 
	 * this.getCommand().setLanes(lanes); }
	 */
	/*
	 * public void lockRdBranch() throws Exception {
	 * 
	 * RdBranchSelector selector = new RdBranchSelector(this.getConn());
	 * 
	 * List<RdBranch> branches = selector.loadRdBranchByLinkNode(
	 * this.getCommand().getLinkPid(), this.getCommand().getsNodePid(),
	 * this.getCommand().geteNodePid(), true);
	 * 
	 * this.getCommand().setBranches(branches); }
	 */

	@Override
	public boolean prepareData() throws Exception {

		RdLinkSelector linkSelector = new RdLinkSelector(this.getConn());

		RdNodeSelector nodeSelector = new RdNodeSelector(this.getConn());

		RdLink link = (RdLink) linkSelector.loadById(this.getCommand()
				.getLinkPid(), true);
		List<RdLink> links = linkSelector.loadByNodePidOnlyRdLink(this
				.getCommand().getNodePid(), true);
		RdNode node = (RdNode) nodeSelector.loadById(this.getCommand()
				.getNodePid(), true);
		this.getCommand().setLinks(links);
		this.getCommand().setRdLink(link);
		this.getCommand().setNode(node);
		return true;
	}

	@Override
	public String preCheck() throws Exception {

		check.checkIsVia(this.getConn(), this.getCommand().getLinkPid());
		return super.preCheck();
	}

	@Override
	public String exeOperation() throws Exception {
		Operation operation = new Operation(this.getCommand(), this.getConn());
		String msg = operation.run(this.getResult());
		return msg;

	}

	public String innerRun() throws Exception {
		String msg;
		try {

			this.prepareData();

			String preCheckMsg = this.preCheck();

			if (preCheckMsg != null) {
				throw new Exception(preCheckMsg);
			}

			Operation operation = new Operation(this.getCommand(),
					this.getConn());
			msg = operation.run(this.getResult());

			this.postCheck();
		} catch (Exception e) {

			this.getConn().rollback();

			throw e;
		}
		return msg;
	}

}
