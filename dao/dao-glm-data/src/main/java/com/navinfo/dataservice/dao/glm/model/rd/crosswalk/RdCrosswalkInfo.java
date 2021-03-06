/**
 * 
 */
package com.navinfo.dataservice.dao.glm.model.rd.crosswalk;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.commons.util.JsonUtils;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjLevel;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.ObjType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: RdCrosswalkInfo
 * @author Zhang Xiaolong
 * @date 2016年8月23日 下午5:11:12
 * @Description: TODO
 */
public class RdCrosswalkInfo implements IRow {

	private int pid;

	private int nodePid;
	
	private int linkPid;
	
	//过道类型
	private int type;
	
	//连接属性
	private int attr;
	
	//过道标牌
	private int signage;

	private int nonmotorFlag = 3;

	private String rowId;

	private Map<String, Object> changedFields = new HashMap<String, Object>();

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getNodePid() {
		return nodePid;
	}

	public void setNodePid(int nodePid) {
		this.nodePid = nodePid;
	}

	public int getLinkPid() {
		return linkPid;
	}

	public void setLinkPid(int linkPid) {
		this.linkPid = linkPid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAttr() {
		return attr;
	}

	public void setAttr(int attr) {
		this.attr = attr;
	}

	public int getSignage() {
		return signage;
	}

	public void setSignage(int signage) {
		this.signage = signage;
	}

	public String getRowId() {
		return rowId;
	}

	public int getNonmotorFlag() {
		return nonmotorFlag;
	}

	public void setNonmotorFlag(int nonmotorFlag) {
		this.nonmotorFlag = nonmotorFlag;
	}

	@Override
	public String rowId() {
		return this.rowId;
	}

	@Override
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@Override
	public String tableName() {
		return "RD_CROSSWALK_INFO";
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
		return ObjType.RDCROSSWALKINFO;
	}

	@Override
	public void copy(IRow row) {
		RdCrosswalkInfo info = (RdCrosswalkInfo) row;

		this.pid = info.pid;

		this.attr = info.attr;
		
		this.linkPid = info.linkPid;
		
		this.nodePid = info.nodePid;
		
		this.signage = info.signage;
		
		this.type = info.type;

		this.nonmotorFlag = info.nonmotorFlag;

		this.rowId = info.rowId;
	}

	@Override
	public Map<String, Object> changedFields() {
		return this.changedFields;
	}

	@Override
	public String parentPKName() {
		return "pid";
	}

	@Override
	public int parentPKValue() {
		return this.pid;
	}

	@Override
	public String parentTableName() {
		return "RD_CROSSWALK";
	}

	@Override
	public List<List<IRow>> children() {
		return null;
	}

	@Override
	public boolean fillChangeFields(JSONObject json) throws Exception {
		@SuppressWarnings("rawtypes")
		Iterator keys = json.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();

			if (json.get(key) instanceof JSONArray) {
				continue;
			} else {
				if (!"objStatus".equals(key)) {

					Field field = this.getClass().getDeclaredField(key);

					field.setAccessible(true);

					Object objValue = field.get(this);

					String oldValue = null;

					if (objValue == null) {
						oldValue = "null";
					} else {
						oldValue = String.valueOf(objValue);
					}

					String newValue = json.getString(key);

					if (!newValue.equals(oldValue)) {
						Object value = json.get(key);

						if (value instanceof String) {
							changedFields.put(key, newValue.replace("'", "''"));
						} else {
							changedFields.put(key, value);
						}

					}

				}
			}
		}

		if (changedFields.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int mesh() {
		return 0;
	}

	@Override
	public void setMesh(int mesh) {
	}

	@Override
	public JSONObject Serialize(ObjLevel objLevel) throws Exception {
		JSONObject json = JSONObject.fromObject(this, JsonUtils.getStrConfig());

		return json;
	}

	@Override
	public boolean Unserialize(JSONObject json) throws Exception {
		@SuppressWarnings("rawtypes")
		Iterator keys = json.keys();

		while (keys.hasNext()) {

			String key = (String) keys.next();

			if (!"objStatus".equals(key)) {

				Field f = this.getClass().getDeclaredField(key);

				f.setAccessible(true);

				f.set(this, json.get(key));
			}

		}
		return true;
	}
}
