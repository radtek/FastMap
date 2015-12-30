package com.navinfo.dataservice.FosEngine.edit.operation.topo.deletecross;

import com.navinfo.dataservice.FosEngine.edit.model.IRow;
import com.navinfo.dataservice.FosEngine.edit.model.ObjStatus;
import com.navinfo.dataservice.FosEngine.edit.model.Result;
import com.navinfo.dataservice.FosEngine.edit.operation.IOperation;

public class OpRefRdRestriction implements IOperation {
	
	private Command command;

	public OpRefRdRestriction(Command command) {
		this.command = command;

	}
	
	@Override
	public String run(Result result) throws Exception {

		for( IRow row : command.getRestricts()){
			
			result.insertObject(row, ObjStatus.DELETE);
		}
		
		return null;
	}

}
