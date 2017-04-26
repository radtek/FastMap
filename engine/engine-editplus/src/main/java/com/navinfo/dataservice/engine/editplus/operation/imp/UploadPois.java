package com.navinfo.dataservice.engine.editplus.operation.imp;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class UploadPois {
	protected Map<String,JSONObject> addPois;//key:fid
	protected Map<String,JSONObject> deletePois;//key:fid
	protected Map<String,JSONObject> updatePois;//key:fid
	public void addJsonPoi(JSONObject jo){
		if(jo.getInt("addFlag")==1){
			if(addPois==null){
				addPois=new HashMap<String,JSONObject>();
			}
			addPois.put(jo.getString("fid"), jo);
		}else if(jo.getInt("delFlag")==1){
			if(deletePois==null){
				deletePois=new HashMap<String,JSONObject>();
			}
			deletePois.put(jo.getString("fid"), jo);
		}else{
			if(updatePois==null){
				updatePois= new HashMap<String,JSONObject>();
			}
			updatePois.put(jo.getString("fid"), jo);
		}
	}
	public Map<String, JSONObject> getAddPois() {
		return addPois;
	}
	public Map<String, JSONObject> getDeletePois() {
		return deletePois;
	}
	public Map<String, JSONObject> getUpdatePois() {
		return updatePois;
	}
	
}