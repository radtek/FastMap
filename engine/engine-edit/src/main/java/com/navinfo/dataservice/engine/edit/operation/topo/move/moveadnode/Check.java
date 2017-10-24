package com.navinfo.dataservice.engine.edit.operation.topo.move.moveadnode;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFace;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFaceTopo;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdLink;
import com.navinfo.dataservice.dao.glm.selector.ad.geo.AdLinkSelector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class Check {
	
	 /**
     * SHAPING_CHECK_FACE_SELFINTERSECT：不自相交的既有线构成的背景面，拖动背景node造成自相交
     * @param conn
     * @param command
     * @throws Exception
     */
	public void checkIntersectFace(Connection conn, Command command) throws Exception {
		if (command.getLinks() == null || command.getLinks().size() == 0) {
			return;
		}

		List<AdLink> newlinks = updateLinkGeometry(command);

		for (int i = 0; i < newlinks.size(); i++) {

			AdLink current = newlinks.get(i);

			if (current.getGeometry().isSimple() == false) {
				throw new Exception("背景面不能自相交");
			}

			for (int j = i + 1; j < newlinks.size(); j++) {

				AdLink next = newlinks.get(j);

				if (GeoTranslator.transform(current.getGeometry(), 0.00001, 5)
						.crosses(GeoTranslator.transform(next.getGeometry(), 0.00001, 5))) {
					throw new Exception("背景面不能自相交");
				}
			}
		}

		AdLinkSelector linkselector = new AdLinkSelector(conn);

		for (AdFace face : command.getFaces()) {

			List<IRow> topos = face.getFaceTopos();

			for (AdLink newlink : newlinks) {

				for (IRow row : topos) {

					AdFaceTopo topo = (AdFaceTopo) row;

					if (isContainTopoLink(topo.getLinkPid(), newlinks)) {

						continue;
					}

					AdLink link = (AdLink) linkselector.loadById(topo.getLinkPid(), true, false);
					if (newlink.getGeometry().crosses(GeoTranslator.transform(link.getGeometry(), 0.00001, 5))) {
						throw new Exception("背景面不能自相交");
					}
				} // for
			} // for
		} // for
	}

	private boolean isContainTopoLink(int linkPid, List<AdLink> links) {
		boolean contain = false;

		for (AdLink link : links) {
			if (link.getPid() == linkPid) {
				contain = true;
				break;
			}
		}

		return contain;
	}

	private List<AdLink> updateLinkGeometry(Command command) throws Exception {
		List<AdLink> newlink = new ArrayList<>();

		for (AdLink link : command.getLinks()) {
			int nodePid = command.getNodePid();

			double lon = command.getLongitude();

			double lat = command.getLatitude();

			Geometry geom = GeoTranslator.transform(link.getGeometry(), 0.00001, 5);
			Coordinate[] cs = geom.getCoordinates();
			Coordinate[] ps = new Coordinate[cs.length];

			for (int i = 0; i < cs.length; i++) {
				ps[i] = new Coordinate();

				ps[i].x = cs[i].x;

				ps[i].y = cs[i].y;
			}

			if (link.getsNodePid() == nodePid) {
				ps[0].x = lon;

				ps[0].y = lat;
			}
			if (link.geteNodePid() == nodePid) {
				ps[ps.length - 1].x = lon;

				ps[ps.length - 1].y = lat;
			}

			LineString newgeo = GeoTranslator.createLineString(ps);

			AdLink adLink = new AdLink();
			adLink.setPid(link.getPid());
			adLink.setGeometry(newgeo);
			newlink.add(adLink);
		}
		return newlink;
	}
}
