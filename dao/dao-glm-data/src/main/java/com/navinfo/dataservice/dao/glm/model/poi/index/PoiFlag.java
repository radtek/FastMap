package com.navinfo.dataservice.dao.glm.model.poi.index;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.navinfo.dataservice.commons.util.JsonUtils;
import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjLevel;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.ObjType;

/**
 * POI_FLAG
 * @author ZL
 *
 */
public class PoiFlag implements IObj {
	private Logger logger = Logger.getLogger(PoiFlag.class);

	private int pid;
	
	private int verRecord;
	
	private int srcRecord;
	
	private int srcNameCh;
	
	private int srcAddress;
	
	private int srcTelephone;
	
	private int srcCoordinate;
	
	private int srcNameEng;
	
	private int srcNamePor;
	
	private int fieldVerified;
	
	private int refreshCycle;
	
	private String refreshDate;
	
	private String rowId;
	
	private Map<String, Object> changedFields = new HashMap<String, Object>();

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getVerRecord() {
		return verRecord;
	}

	public void setVerRecord(int verRecord) {
		this.verRecord = verRecord;
	}

	public int getSrcRecord() {
		return srcRecord;
	}

	public void setSrcRecord(int srcRecord) {
		this.srcRecord = srcRecord;
	}

	public int getSrcNameCh() {
		return srcNameCh;
	}

	public void setSrcNameCh(int srcNameCh) {
		this.srcNameCh = srcNameCh;
	}

	public int getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(int srcAddress) {
		this.srcAddress = srcAddress;
	}

	public int getSrcTelephone() {
		return srcTelephone;
	}

	public void setSrcTelephone(int srcTelephone) {
		this.srcTelephone = srcTelephone;
	}

	public int getSrcCoordinate() {
		return srcCoordinate;
	}

	public void setSrcCoordinate(int srcCoordinate) {
		this.srcCoordinate = srcCoordinate;
	}

	public int getSrcNameEng() {
		return srcNameEng;
	}

	public void setSrcNameEng(int srcNameEng) {
		this.srcNameEng = srcNameEng;
	}

	public int getSrcNamePor() {
		return srcNamePor;
	}

	public void setSrcNamePor(int srcNamePor) {
		this.srcNamePor = srcNamePor;
	}

	public int getFieldVerified() {
		return fieldVerified;
	}

	public void setFieldVerified(int fieldVerified) {
		this.fieldVerified = fieldVerified;
	}

	public int getRefreshCycle() {
		return refreshCycle;
	}

	public void setRefreshCycle(int refreshCycle) {
		this.refreshCycle = refreshCycle;
	}

	public String getRefreshDate() {
		return refreshDate;
	}

	public void setRefreshDate(String refreshDate) {
		this.refreshDate = refreshDate;
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
		return "poi_flag";
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
		return ObjType.POIFLAG;
	}

	@Override
	public void copy(IRow row) {
	}

	@Override
	public Map<String, Object> changedFields() {
		return this.changedFields;
	}

	@Override
	public String parentPKName() {
		return "POI_PID";
	}

	@Override
	public int parentPKValue() {
		return this.getPid();
	}

	@Override
	public String parentTableName() {
		return "ix_poi";
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
					String newValue = json.getString(key);
					if("null".equalsIgnoreCase(newValue))newValue=null;
					logger.info("objValue:"+objValue);
					logger.info("newValue:"+newValue);
					if (!isEqualsString(objValue,newValue)) {
						logger.info("isEqualsString:false");
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
	
	private static boolean isEqualsString(Object oldValue,Object newValue){
		
		if (oldValue instanceof Double) {
			newValue = Double.parseDouble(newValue.toString());
		}
		
		if(null==oldValue&&null==newValue)
			return true;
		if(StringUtils.isEmpty(oldValue)&&StringUtils.isEmpty(newValue)){
			return true;
		}
		if(oldValue==null&&newValue!=null){
			return false;
		}
		if(oldValue!=null&&newValue==null){
			return false;
		}
		return oldValue.toString().equals(newValue.toString());
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

	@Override
	public List<IRow> relatedRows() {
		return null;
	}

	@Override
	public int pid() {
		return this.pid;
	}

	@Override
	public String primaryKey() {
		return "pid";
	}

	@Override
	public Map<Class<? extends IRow>, List<IRow>> childList() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.navinfo.dataservice.dao.glm.iface.IObj#childMap()
	 */
	@Override
	public Map<Class<? extends IRow>,Map<String,?>> childMap() {
		// TODO Auto-generated method stub
		return null;
	}

}