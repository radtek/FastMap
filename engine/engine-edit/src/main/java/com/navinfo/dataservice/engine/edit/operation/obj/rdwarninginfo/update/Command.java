package com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.update;

import net.sf.json.JSONObject;

import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.dao.glm.model.rd.warninginfo.RdWarninginfo;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;

public class Command extends AbstractCommand {

	
	
	private String requester;

	private JSONObject content;
	
	private int pid;
	
	private RdWarninginfo  rdWarninginfo;
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public JSONObject getContent() {
		return content;
	}

	public void setContent(JSONObject content) {
		this.content = content;
	}	
	
	public RdWarninginfo getRdWarninginfo() {
		return rdWarninginfo;
	}

	public void setRdWarninginfo(RdWarninginfo rdWarninginfo) {
		this.rdWarninginfo = rdWarninginfo;
	}

	@Override
	public OperType getOperType() {
		return OperType.UPDATE;
	}
	
	@Override
	public ObjType getObjType() {
		return ObjType.RDWARNINGINFO;
	}

	@Override
	public String getRequester() {
		return requester;
	}

	public Command(JSONObject json, String requester) {
		this.requester = requester;

		this.setDbId(json.getInt("dbId"));

		this.content = json.getJSONObject("data");
		
		this.pid = this.content.getInt("pid");

	}

}
