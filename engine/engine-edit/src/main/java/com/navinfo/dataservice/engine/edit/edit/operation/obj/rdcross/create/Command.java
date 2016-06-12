package com.navinfo.dataservice.engine.edit.edit.operation.obj.rdcross.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.navinfo.dataservice.dao.glm.iface.ICommand;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.engine.edit.edit.operation.AbstractCommand;

public class Command extends AbstractCommand {

	private String requester;

//	private int projectId;

	private List<Integer> nodePids;

	private List<Integer> linkPids;

//	public int getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(int projectId) {
//		this.projectId = projectId;
//	}

	public List<Integer> getNodePids() {
		return nodePids;
	}

	public void setNodePids(List<Integer> nodePids) {
		this.nodePids = nodePids;
	}

	public List<Integer> getLinkPids() {
		return linkPids;
	}

	public void setLinkPids(List<Integer> linkPids) {
		this.linkPids = linkPids;
	}

	@Override
	public OperType getOperType() {
		return OperType.CREATE;
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
		this.setDbId(json.getInt("subTaskId"));

		JSONObject data = json.getJSONObject("data");

		JSONArray array = data.getJSONArray("nodePids");

		nodePids = new ArrayList<Integer>();

		for (int i = 0; i < array.size(); i++) {

			int pid = Integer.valueOf(array.getString(i));

			if (!nodePids.contains(pid)) {
				nodePids.add(pid);
			}
		}
		
		Collections.sort(nodePids);
		
		array = data.getJSONArray("linkPids");

		linkPids = new ArrayList<Integer>();

		for (int i = 0; i < array.size(); i++) {

			int pid = Integer.valueOf(array.getString(i));

			if (!linkPids.contains(pid)) {
				linkPids.add(pid);
			}
		}
	}
}
