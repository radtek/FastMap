package com.navinfo.dataservice.engine.edit.operation.obj.rdinter.create;

import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.AbstractProcess;

public class Process extends AbstractProcess<Command> {

	public Process(AbstractCommand command) throws Exception {
		super(command);
	}

	private Check check = new Check(this.getCommand());

	@Override
	public boolean prepareData() throws Exception {
		check.hasRdInter(getConn());
		check.checkLink(getConn());
		check.checkNodeDirect(getConn());
		check.hasMakedCRFR(this.getConn());
		
		return true;
	}

	@Override
	public String exeOperation() throws Exception {
		Operation operation = new Operation(this.getCommand(),this.getConn());
		String msg = operation.run(this.getResult());
		return msg;
	}

}
