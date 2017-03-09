package com.navinfo.dataservice.engine.edit.operation.obj.rdnode.update;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNodeForm;
import com.navinfo.dataservice.dao.glm.selector.rd.node.RdNodeSelector;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.AbstractProcess;

public class Process extends AbstractProcess<Command> {

	public Process(AbstractCommand command) throws Exception {
		super(command);
	}

	public Process(Command command, Result result, Connection conn) throws Exception {
		super(command, result, conn);
	}

	private RdNode rdnode;

	@Override
	public boolean prepareData() throws Exception {
		if (this.getCommand().getNode() != null) {
			this.rdnode = this.getCommand().getNode();
			return true;
		}

		RdNodeSelector selector = new RdNodeSelector(this.getConn());

		this.rdnode = (RdNode) selector.loadById(this.getCommand().getPid(), true);

		return true;
	}

	public String innerRun() throws Exception {
		String msg;
		try {
			this.prepareData();

			String preCheckMsg = this.preCheck();

			if (preCheckMsg != null) {
				throw new Exception(preCheckMsg);
			}

			IOperation operation = new Operation(this.getCommand(), this.rdnode, getConn());

			msg = operation.run(this.getResult());

			//super.recordData();
			//this.postCheck();

		} catch (Exception e) {

			this.getConn().rollback();

			throw e;
		}

		return msg;
	}

	@Override
	public String exeOperation() throws Exception {
		return new Operation(this.getCommand(), this.rdnode).run(this.getResult());
	}
	
	@Override
	public void postCheck() throws Exception {
		// TODO Auto-generated method stub
		// this.createPostCheckGlmList();
		List<IRow> glmList = new ArrayList<IRow>();
		glmList.addAll(this.getResult().getAddObjects());
		glmList.addAll(this.getResult().getUpdateObjects());
		for(IRow irow:this.getResult().getDelObjects()){
			if(irow instanceof RdNodeForm){
				glmList.add(irow);
			}
		}
		this.checkCommand.setGlmList(glmList);
		this.checkEngine.postCheck();

	}

}
