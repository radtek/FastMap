package com.navinfo.dataservice.engine.edit.edit.operation.obj.poi.create;

import java.sql.Connection;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdAdmin;
import com.navinfo.dataservice.dao.glm.model.poi.index.IxPoi;
import com.navinfo.dataservice.dao.pidservice.PidService;
import com.navinfo.navicommons.geo.computation.CompGeometryUtil;

import net.sf.json.JSONObject;

/**
 * 
 * @Title: Operation.java
 * @Description: 新增行政区划代表点操作类
 * @author 赵凯凯
 * @date 2016年6月15日 下午2:31:50
 * @version V1.0
 */
public class Operation implements IOperation {

	private Command command;

	private Connection conn;

	public Operation(Command command, Connection conn) {
		this.command = command;

		this.conn = conn;

	}

	@Override
	public String run(Result result) throws Exception {
		
		IxPoi ixPoi = new IxPoi();
		
		String msg = null;
		
		// 构造几何对象
		JSONObject geoPoint = new JSONObject();

		geoPoint.put("type", "Point");

		geoPoint.put("coordinates", new double[] { command.getLongitude(), command.getLatitude() });
		
		// 根据经纬度计算图幅ID
		String meshIds[] = CompGeometryUtil.geo2MeshesWithoutBreak(GeoTranslator.geojson2Jts(geoPoint, 1, 5));

		if (meshIds.length >1) {
			throw new Exception("不能在图幅线上创建POI");
		}
		if(meshIds.length == 1)
		{
			ixPoi.setMeshId(Integer.parseInt(meshIds[0]));
		}
		ixPoi.setPid(PidService.getInstance().applyPoiPid());
		result.setPrimaryPid(ixPoi.getPid());
		ixPoi.setGeometry(GeoTranslator.geojson2Jts(geoPoint, 100000, 0));
		ixPoi.setxGuide(command.getXguide());
		ixPoi.setyGuide(command.getYguide());
		result.insertObject(ixPoi, ObjStatus.INSERT, ixPoi.getPid());
		return msg;
	}

}
