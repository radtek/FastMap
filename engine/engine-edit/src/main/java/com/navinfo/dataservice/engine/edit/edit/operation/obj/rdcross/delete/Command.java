package com.navinfo.dataservice.engine.edit.edit.operation.obj.rdcross.delete;

import net.sf.json.JSONObject;

import com.navinfo.dataservice.dao.glm.iface.ICommand;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.engine.edit.edit.operation.AbstractCommand;

public class Command extends AbstractCommand {

	private String requester;

//	private int projectId;

	private int pid;
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

//	public int getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(int projectId) {
//		this.projectId = projectId;
//	}

	@Override
	public OperType getOperType() {
		return OperType.DELETE;
	}
	
	@Override
	public ObjType getObjType() {
		return ObjType.RDCROSS;
	}

	@Override
	public String getRequester() {
		return requester;
	}

	public Command(JSONObject json, String requester) {
		this.requester = requester;

//		this.projectId = json.getInt("projectId");
		this.setProjectId(json.getInt("projectId"));

		this.pid = json.getInt("objId");
		
	}

}
