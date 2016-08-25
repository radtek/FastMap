package com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakzonepoint;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneLink;

public class OpRefRelationObj {

	private Connection conn = null;

	public OpRefRelationObj(Connection conn) {

		this.conn = conn;
	}

	public String handleSameLink(ZoneLink breakLink,
			Command command, Result result) throws Exception {

		List<IObj> newLinks = new ArrayList<IObj>();

		newLinks.add(command.getsZoneLink());

		newLinks.add(command.geteZoneLink());

		// 打断link维护同一线
		com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.update.Operation operation = new com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.update.Operation(
				this.conn);
		operation.breakLink(breakLink, newLinks, command.getBreakNode(),
				command.getRequester(), result);

		return null;
	}

}