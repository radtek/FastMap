package com.navinfo.dataservice.FosEngine.edit.model.bean.rd.laneconnexity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.navinfo.dataservice.FosEngine.comm.util.JsonUtils;
import com.navinfo.dataservice.FosEngine.edit.model.IObj;
import com.navinfo.dataservice.FosEngine.edit.model.IRow;
import com.navinfo.dataservice.FosEngine.edit.model.ObjLevel;
import com.navinfo.dataservice.FosEngine.edit.model.ObjStatus;
import com.navinfo.dataservice.FosEngine.edit.model.ObjType;

public class RdLaneTopology implements IObj {

	private String rowId;

	private int pid;

	private int connexityPid;
	
	private int outLinkPid;
	
	private int inLaneInfo;
	
	private int busLaneInfo;
	
	private int reachDir;
	
	private int relationshipType=1;
	
	private List<IRow> vias = new ArrayList<IRow>();
	
	private Map<String, Object> changedFields = new HashMap<String, Object>();
	
	public Map<String,RdLaneVia> viaMap = new HashMap<String,RdLaneVia>();

	public RdLaneTopology() {

	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public List<IRow> getVias() {
		return vias;
	}

	public void setVias(List<IRow> vias) {
		this.vias = vias;
	}
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getConnexityPid() {
		return connexityPid;
	}

	public void setConnexityPid(int connexityPid) {
		this.connexityPid = connexityPid;
	}

	public int getOutLinkPid() {
		return outLinkPid;
	}

	public void setOutLinkPid(int outLinkPid) {
		this.outLinkPid = outLinkPid;
	}

	public int getInLaneInfo() {
		return inLaneInfo;
	}

	public void setInLaneInfo(int inLaneInfo) {
		this.inLaneInfo = inLaneInfo;
	}

	public int getBusLaneInfo() {
		return busLaneInfo;
	}

	public void setBusLaneInfo(int busLaneInfo) {
		this.busLaneInfo = busLaneInfo;
	}

	public int getReachDir() {
		return reachDir;
	}

	public void setReachDir(int reachDir) {
		this.reachDir = reachDir;
	}

	public int getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(int relationshipType) {
		this.relationshipType = relationshipType;
	}

	@Override
	public JSONObject Serialize(ObjLevel objLevel) {

		return JSONObject.fromObject(this,JsonUtils.getStrConfig());
	}

	@Override
	public boolean Unserialize(JSONObject json) throws Exception {

		Iterator keys = json.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();

			JSONArray ja = null;

			if (json.get(key) instanceof JSONArray) {

				switch (key) {
				case "vias":
					vias.clear();

					ja = json.getJSONArray(key);

					for (int i = 0; i < ja.size(); i++) {
						JSONObject jo = ja.getJSONObject(i);

						RdLaneVia row = new RdLaneVia();

						row.Unserialize(jo);

						vias.add(row);
					}

					break;

				default:
					break;
				}

			} else {
				if (!"objStatus".equals(key)) {
					Field f = this.getClass().getDeclaredField(key);
					f.setAccessible(true);
					f.set(this, json.get(key));
				}
			}
		}

		return true;
	}

	@Override
	public String tableName() {

		return "rd_lane_topology";
	}

	@Override
	public ObjStatus status() {

		return null;
	}

	@Override
	public void setStatus(ObjStatus os) {

	}

	@Override
	public ObjType objType() {

		return ObjType.RDLANETOPOLOGY;
	}

	@Override
	public void copy(IRow row) {

	}

	@Override
	public Map<String, Object> changedFields() {

		return changedFields;
	}

	@Override
	public String primaryKey() {

		return "topology_id";
	}

	@Override
	public int primaryValue() {

		return this.getPid();
	}

	@Override
	public List<List<IRow>> children() {
		List<List<IRow>> children = new ArrayList<List<IRow>>();

		children.add(this.getVias());
	
		return children;
	}

	@Override
	public String primaryTableName() {

		return "rd_lane_topology";
	}

	@Override
	public String rowId() {

		return this.getRowId();
	}

	@Override
	public boolean fillChangeFields(JSONObject json) throws Exception {
		
		Iterator keys = json.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();

			JSONArray ja = null;

			if (json.get(key) instanceof JSONArray) {
				continue;
			}  else {
				if (!"objStatus".equals(key)) {
					
					Field field = this.getClass().getDeclaredField(key);
					
					field.setAccessible(true);
					
					Object objValue = field.get(this);
					
					String oldValue = null;
					
					if (objValue == null){
						oldValue = "null";
					}else{
						oldValue = String.valueOf(objValue);
					}
					
					String newValue = json.getString(key);
					
					if (!newValue.equals(oldValue)){
						changedFields.put(key, json.get(key));
						
					}

					
				}
			}
		}
		
		if (changedFields.size() >0){
			return true;
		}else{
			return false;
		}

	}

	@Override
	public List<IRow> relatedRows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int pid() {
		// TODO Auto-generated method stub
		return pid;
	}
}
