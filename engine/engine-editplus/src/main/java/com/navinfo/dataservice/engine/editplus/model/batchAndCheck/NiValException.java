package com.navinfo.dataservice.engine.editplus.model.batchAndCheck;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.common.GeoHelper;
import com.vividsolutions.jts.geom.Geometry;


public class NiValException {
	//String ruleId, String loc, String targets,int meshId, String worker
	private String ruleId;
	private String loc;
	private String targets;
	private int meshId;
	private String information;
	private int logLevel;
	//private List<Map<String, Object>> targetsList=new ArrayList<Map<String,Object>>();
	
	public NiValException(String ruleId, String loc, String targets,int meshId,String information,int logLevel){
		this.setRuleId(ruleId);
		this.setLoc(loc);
		this.setTargets(targets);
		this.setMeshId(meshId);
		this.setInformation(information);
		this.setLogLevel(logLevel);
	}
	
	public NiValException(String ruleId, Geometry geo, String targets,int meshId,String information,int logLevel) throws Exception{
		Geometry pointGeo=GeoHelper.getPointFromGeo(geo);
		String pointWkt = GeoTranslator.jts2Wkt(pointGeo, 0.00001, 5);
		
		this.setRuleId(ruleId);
		this.setLoc(pointWkt);
		this.setTargets(targets);
		this.setMeshId(meshId);
		this.setInformation(information);
		this.setLogLevel(logLevel);
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public int getMeshId() {
		return meshId;
	}

	public void setMeshId(int meshId) {
		this.meshId = meshId;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}
	
}
