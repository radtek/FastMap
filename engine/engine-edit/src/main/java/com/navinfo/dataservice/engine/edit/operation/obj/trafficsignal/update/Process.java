package com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.update;

import com.navinfo.dataservice.dao.glm.iface.IProcess;
import com.navinfo.dataservice.dao.glm.model.rd.trafficsignal.RdTrafficsignal;
import com.navinfo.dataservice.dao.glm.selector.rd.trafficsignal.RdTrafficsignalSelector;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.AbstractProcess;

/**
 * 
* @ClassName: Process 
* @author Zhang Xiaolong
* @date 2016年7月20日 下午7:39:42 
* @Description: TODO
 */
public class Process extends  AbstractProcess<Command>  implements IProcess {
	public Process(AbstractCommand command) throws Exception {
		super(command);
	}

	@Override
	public boolean prepareData() throws Exception {

		RdTrafficsignalSelector selector = new RdTrafficsignalSelector(this.getConn());

		RdTrafficsignal rdTrafficsignal = (RdTrafficsignal) selector.loadById(this.getCommand().getPid(),
				true);
		
		this.getCommand().setRdTrafficsignal(rdTrafficsignal);
		
		return true;
	}	

	@Override
	public String exeOperation() throws Exception {
		return new Operation(this.getCommand()).run(this.getResult());
	}

}
