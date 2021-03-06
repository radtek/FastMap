package com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakzonepoint;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneLink;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneNode;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class OpRefRelationObj {

	private Connection conn = null;

	public OpRefRelationObj(Connection conn) {

		this.conn = conn;
	}

	public String handleSameLink(ZoneLink breakLink,
			Command command, Result result) throws Exception {
		
		breakLink.setGeometry(GeoTranslator.transform(breakLink.getGeometry(),
				1, 0));

		Map<IRow, Geometry> breakNodeMap = new HashMap<IRow, Geometry>();

		LinkedHashMap<IRow, Geometry> linkMap = new LinkedHashMap<IRow, Geometry>();
		
		Set<Integer> pidFlags = new HashSet<Integer>();

		pidFlags.add(breakLink.geteNodePid());

		pidFlags.add(breakLink.getsNodePid());

		for (ZoneLink link : command.getNewLinks()) {

			linkMap.put(link, GeoTranslator.transform(link.getGeometry(),
					1, 0));

			int sNodePid = link.getsNodePid();

			int eNodePid = link.geteNodePid();

			if (!pidFlags.contains(sNodePid) ) {
				
				ZoneNode node = new ZoneNode();

				node.setPid(sNodePid);

				LineString linkGeo = (LineString) link.getGeometry();

				node.setGeometry(linkGeo.getStartPoint());

				breakNodeMap.put(node, node.getGeometry());
				
				pidFlags.add(sNodePid);
			}

			if (!pidFlags.contains(eNodePid) ) {
				
				ZoneNode node = new ZoneNode();

				node.setPid(eNodePid);

				LineString linkGeo = (LineString) link.getGeometry();

				node.setGeometry(linkGeo.getEndPoint());

				breakNodeMap.put(node, node.getGeometry());
				
				pidFlags.add(eNodePid);
			}
		}

		// 打断link维护同一线
		com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.update.Operation operation = new com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.update.Operation(
				this.conn);
		operation.breakLink(breakLink, breakNodeMap, linkMap, result);

		return null;
	}

}