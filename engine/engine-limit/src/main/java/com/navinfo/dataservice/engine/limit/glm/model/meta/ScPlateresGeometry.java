
package com.navinfo.dataservice.engine.limit.glm.model.meta;


import com.navinfo.dataservice.dao.glm.iface.ObjLevel;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.engine.limit.glm.iface.*;
import com.vividsolutions.jts.geom.Geometry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.geom.Geojson;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScPlateresGeometry  implements IObj {

  private   String geometryId = "";//GEOMETRY_ID

    private String groupId = ""; //GROUP_ID

    private  Geometry geometry;

    private   String  boundaryLink="1";//BOUNDARY_LINK

    public String getGeometryId() {
        return geometryId;
    }

    public void setGeometryId(String geometryId) {
        this.geometryId = geometryId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBoundaryLink() {
        return boundaryLink;
    }

    public void setBoundaryLink(String boundarylink) {
        this.boundaryLink = boundarylink;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    protected ObjStatus status;

    private Map<String, Object> changedFields = new HashMap<>();


    @Override
    public List<IRow> relatedRows() {
        return null;
    }

    @Override
    public String primaryKeyValue() {
        return geometryId;
    }

    @Override
    public String primaryKey() {
        return "GEOMETRY_ID";
    }

    @Override
    public Map<Class<? extends IRow>, List<IRow>> childList() {
        return null;
    }

    @Override
    public Map<Class<? extends IRow>, Map<String, ?>> childMap() {
        return null;
    }

    @Override
    public String tableName() {
        return "SC_PLATERES_GEOMETRY";
    }

    @Override
    public ObjStatus status() {

        return status;
    }

    @Override
    public void setStatus(ObjStatus os) {
        status = os;
    }

    @Override
    public LimitObjType objType() {
        return LimitObjType.SCPLATERESGEOMETRY;
    }

    @Override
    public Map<String, Object> changedFields()
    {
        return changedFields;
    }

    @Override
    public String parentPKName() {
        return "GROUP_ID";
    }

    @Override
    public String parentPKValue() {
        return null;
    }

    @Override
    public String parentTableName() {
        return "SC_PLATERES_GROUP";
    }

    @Override
    public List<List<IRow>> children() {
        return null;
    }

    @Override
    public boolean fillChangeFields(JSONObject json) throws Exception {
        Iterator keys = json.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();

            if (json.get(key) instanceof JSONArray) {
                continue;
            }
            if ("geometry".equals(key)) {

                JSONObject geojson = json.getJSONObject(key);

                String wkt = Geojson.geojson2Wkt(geojson.toString());

                String oldwkt = GeoTranslator.jts2Wkt(geometry, 0.00001, 5);

                if (!wkt.equals(oldwkt)) {
                    changedFields.put(key, json.getJSONObject(key));
                }
            } else if (!"objStatus".equals(key)) {

                Field field = this.getClass().getDeclaredField(key);

                field.setAccessible(true);

                Object objValue = field.get(this);

                String oldValue;

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

        return changedFields.size() >0;
    }

    @Override
    public JSONObject Serialize(ObjLevel objLevel) throws Exception {
        JsonConfig jsonConfig = Geojson.geoJsonConfig(0.00001, 5);

        JSONObject json = JSONObject.fromObject(this, jsonConfig);
        if (objLevel == ObjLevel.HISTORY) {
            json.remove("status");
        }

        return json;
    }

    @Override
    public boolean Unserialize(JSONObject json) throws Exception {

        Iterator keys = json.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();

            if ("geometry".equals(key)) {

                Geometry jts = GeoTranslator.geojson2Jts(json.getJSONObject(key), 100000, 0);

                this.setGeometry(jts);

            } else {
                Field f = this.getClass().getDeclaredField(key);

                f.setAccessible(true);

                f.set(this, json.get(key));
            }
        }

        return true;
    }
}
