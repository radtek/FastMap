package com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.delete;

import java.util.List;

import com.navinfo.dataservice.dao.glm.iface.ICommand;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCross;
import com.navinfo.dataservice.dao.glm.model.rd.trafficsignal.RdTrafficsignal;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;

import net.sf.json.JSONObject;

/**
 * 
* @ClassName: Command 
* @author Zhang Xiaolong
* @date 2016年7月20日 下午7:39:09 
* @Description: TODO
 */
public class Command extends AbstractCommand  implements ICommand {

	private String requester;

	private List<RdTrafficsignal> rdTrafficsignalList;
	
	private RdCross rdCross;

	private int pid;
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}


	@Override
	public OperType getOperType() {
		return OperType.DELETE;
	}
	
	@Override
	public ObjType getObjType() {
		return ObjType.RDTRAFFICSIGNAL;
	}

	public List<RdTrafficsignal> getRdTrafficsignalList() {
		return rdTrafficsignalList;
	}

	public void setRdTrafficsignalList(List<RdTrafficsignal> rdTrafficsignalList) {
		this.rdTrafficsignalList = rdTrafficsignalList;
	}

	@Override
	public String getRequester() {
		return requester;
	}

	public Command(JSONObject json, String requester) {
		this.requester = requester;

		this.setDbId(json.getInt("dbId"));

		this.pid = json.getInt("objId");
		
	}

	public RdCross getRdCross() {
		return rdCross;
	}

	public void setRdCross(RdCross rdCross) {
		this.rdCross = rdCross;
	}

}
