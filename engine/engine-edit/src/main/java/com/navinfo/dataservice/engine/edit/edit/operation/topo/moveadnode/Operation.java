package com.navinfo.dataservice.engine.edit.edit.operation.topo.moveadnode;

import net.sf.json.JSONObject;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.util.GeometryUtils;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFace;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdLink;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdNode;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class Operation implements IOperation {

	private Command command;

	private AdNode updateNode;

	public Operation(Command command, AdNode updateNode) {
		this.command = command;

		this.updateNode = updateNode;
	}

	@Override
	public String run(Result result) throws Exception {

		result.setPrimaryPid(updateNode.getPid());
		
		this.updateNodeGeometry(result);
		
		this.updateLinkGeomtry(result);
		this.updateFaceGeomtry(result);
		return null;
	}

	private void updateLinkGeomtry(Result result) throws Exception {
		
		for (AdLink link : command.getLinks()) {

			Geometry geom = GeoTranslator.transform(link.getGeometry(), 0.00001, 5);

			Coordinate[] cs = geom.getCoordinates();

			double[][] ps = new double[cs.length][2];

			for (int i = 0; i < cs.length; i++) {
				ps[i][0] = cs[i].x;

				ps[i][1] = cs[i].y;
			}

			if (link.getStartNodePid() == command.getNodePid()) {
				ps[0][0] = command.getLongitude();

				ps[0][1] = command.getLatitude();
			} else {
				ps[ps.length - 1][0] = command.getLongitude();

				ps[ps.length - 1][1] = command.getLatitude();
			}

			JSONObject geojson = new JSONObject();

			geojson.put("type", "LineString");

			geojson.put("coordinates", ps);

			JSONObject updateContent = new JSONObject();

			updateContent.put("geometry", geojson);
			updateContent.put("length", GeometryUtils.getLinkLength((GeoTranslator.geojson2Jts(geojson, 1, 5))));

			link.fillChangeFields(updateContent);
			
			result.insertObject(link, ObjStatus.UPDATE, link.pid());
		}
	}

	private void updateNodeGeometry(Result result) throws Exception {
		JSONObject geojson = new JSONObject();

		geojson.put("type", "Point");

		geojson.put("coordinates", new double[] { command.getLongitude(),
				command.getLatitude() });

		JSONObject updateContent = new JSONObject();

		updateContent.put("geometry", geojson);

		updateNode.fillChangeFields(updateContent);
		
		result.insertObject(updateNode, ObjStatus.UPDATE, updateNode.pid());
	}
	
	private void updateFaceGeomtry(Result result) throws Exception {
		Geometry geomNode  = GeoTranslator.transform(updateNode.getGeometry(), 0.00001, 5);
		double lon =geomNode.getCoordinates()[0].x;
		double lat =geomNode.getCoordinates()[0].y;
		for (AdFace face : command.getFaces()) {
		
			Geometry geomFace = GeoTranslator.transform(face.getGeometry(), 0.00001, 5);
			Coordinate[] cd = geomFace.getCoordinates();
			double[][] ps = new double[cd.length][2];
			for (int i = 0; i < cd.length; i++) {
				if (cd[i].x == lon&&cd[i].y== lat){
					cd[i].x = command.getLongitude();
					cd[i].y = command.getLongitude();
				}
			}
			for (int i = 0; i < cd.length; i++) {
				ps[i][0] = cd[i].x;

				ps[i][1] = cd[i].y;
			}
			
			JSONObject geojson = new JSONObject();
			geojson.put("type", "Polygon");
			geojson.put("coordinates", ps);
			JSONObject updateContent = new JSONObject();
			updateContent.put("geometry", geojson);
			updateContent.put("length", GeometryUtils.getLinkLength((GeoTranslator.geojson2Jts(geojson, 1, 5))));
			updateContent.put("area", GeometryUtils.getCalculateArea((GeoTranslator.geojson2Jts(geojson, 1, 5))));
			face.fillChangeFields(updateContent);
			result.insertObject(face, ObjStatus.UPDATE, face.pid());
		}
	}
}
