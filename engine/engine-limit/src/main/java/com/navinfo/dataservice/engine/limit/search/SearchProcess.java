
package com.navinfo.dataservice.engine.limit.search;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.util.JsonUtils;
import com.navinfo.dataservice.dao.glm.iface.ObjLevel;
import com.navinfo.dataservice.dao.glm.iface.SearchSnapshot;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.engine.limit.glm.iface.IRow;
import com.navinfo.dataservice.engine.limit.glm.iface.ISearch;
import com.navinfo.dataservice.engine.limit.glm.iface.LimitObjType;
import com.navinfo.dataservice.engine.limit.search.gdb.AdAdminSearch;
import com.navinfo.dataservice.engine.limit.search.gdb.RdLinkSearch;
import com.navinfo.dataservice.engine.limit.search.limit.ScPlateresFaceSearch;
import com.navinfo.dataservice.engine.limit.search.limit.ScPlateresInfoSearch;
import com.navinfo.dataservice.engine.limit.search.limit.ScPlateresLinkSearch;
import com.navinfo.dataservice.engine.limit.search.meta.ScPlateresGeometrySearch;
import com.navinfo.dataservice.engine.limit.search.meta.ScPlateresGroupSearch;
import com.navinfo.dataservice.engine.limit.search.meta.ScPlateresManoeuvreSearch;
import com.navinfo.dataservice.engine.limit.search.meta.ScPlateresRdlinkSearch;
import com.navinfo.navicommons.database.QueryRunner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询进程
 */
public class SearchProcess {

    private static final Logger logger = Logger.getLogger(SearchProcess.class);

    Connection conn;

    public SearchProcess(Connection conn) throws Exception {

        this.conn = conn;
    }

    public SearchProcess() throws Exception {

    }

    public int searchMetaDataByCondition(LimitObjType type, JSONObject condition, List<IRow> rows)
            throws Exception {
        int total = 0;
        try {
            switch (type) {
                case SCPLATERESGEOMETRY:
                    total = new ScPlateresGeometrySearch(this.conn).searchDataByCondition(condition, rows);
                    break;
                case SCPLATERESGROUP:
                    total = new ScPlateresGroupSearch(this.conn).searchDataByCondition(condition, rows);
                    break;

                case SCPLATERESMANOEUVRE:
                    total = new ScPlateresManoeuvreSearch(this.conn).searchDataByCondition(condition, rows);
                    break;
                case SCPLATERESRDLINK:
                    total = new ScPlateresRdlinkSearch(this.conn).searchDataByCondition(condition, rows);
                    break;
                default:
                    return total;
            }

            return total;
        } catch (Exception e) {

            throw e;

        } finally {

        }
    }

    public int searchLimitDataByCondition(LimitObjType type, JSONObject condition, List<IRow> objList)
            throws Exception {
        int total = 0;
        try {
            switch (type) {
                case SCPLATERESINFO:
                    total = new ScPlateresInfoSearch(this.conn).searchDataByCondition(condition, objList);
                    break;
                case SCPLATERESFACE:
                    total = new ScPlateresFaceSearch(this.conn).searchDataByCondition(condition, objList);
                    break;
                case SCPLATERESLINK:
                    total = new ScPlateresLinkSearch(this.conn).searchDataByCondition(condition, objList);
                    break;
                default:
                    return total;
            }

            return total;
        } catch (Exception e) {

            throw e;

        } finally {

        }
    }

    public JSONObject searchRdLinkDataByCondition(int type, JSONObject condition) throws Exception {
        JSONObject result = new JSONObject();

        RdLinkSearch search = new RdLinkSearch(this.conn);

        try {
            switch (type) {
                case 1: //名称模糊查询
                case 2: //名称精准查询
                    result = search.searchDataByCondition(type, condition);
                    break;
                case 3: //linkPid查询名称
                    result = search.searchDataByPid(condition);
                default:
                    return result;
            }

            return result;
        } catch (Exception e) {

            throw e;

        }
    }
    
    public List<RdLink> searchDataByPids(JSONArray pids)
			throws Exception {

    	RdLinkSearch search = new RdLinkSearch(this.conn);
    	
		try {
			@SuppressWarnings("unchecked")
			List<Integer> pidList = JSONArray.toList(pids, Integer.class,
					JsonUtils.getJsonConfig());

			List<RdLink> objList = search.searchDataByPids(pidList);

			return objList;
		} catch (Exception e) {

			throw e;

		} finally {

		}
	}

    /**
     * 根据瓦片空间查询
     *
     * @return 查询结果
     * @throws Exception
     */
    public JSONObject searchDataByTileWithGap(
            List<LimitObjType> types, RenderParam param) throws Exception {

        Map<String, List<SearchSnapshot>> map = new HashMap<>();

        for (LimitObjType type : types) {

            List<SearchSnapshot> list = searchDataByTileWithGap(type, param);

            map.put(type.toString(), list);
        }

        JSONObject json = new JSONObject();

        for (Map.Entry<String, List<SearchSnapshot>> entry : map.entrySet()) {

            JSONArray array = new JSONArray();

            for (SearchSnapshot snap : entry.getValue()) {

                array.add(snap.Serialize(ObjLevel.BRIEF), getJsonConfig());
            }

            json.accumulate(entry.getKey(), array, getJsonConfig());
        }

        return json;
    }

    /**
     * 根据瓦片空间查询
     *
     * @return 查询结果
     */
    private List<SearchSnapshot> searchDataByTileWithGap(
            LimitObjType type, RenderParam param) throws Exception {

        Connection conn = null;

        try {

            if (LimitObjType.SCPLATERESFACE.equals(type) || LimitObjType.SCPLATERESLINK.equals(type)) {

                conn = DBConnector.getInstance().getLimitConnection();

            } else if (LimitObjType.SCPLATERESGEOMETRY.equals(type) || LimitObjType.SCPLATERESRDLINK.equals(type)) {

              conn = DBConnector.getInstance().getMetaConnection();
            } else {
                throw new Exception("不支持的渲染类型：" + type.toString());
            }

            ISearch search = createSearch(type, conn);

            if (search == null) {
                return new ArrayList<>();
            }

            return search.searchDataByTileWithGap(param);

        } catch (Exception e) {

            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public ISearch createSearch(LimitObjType ot, Connection conn) {

        switch (ot) {
            case SCPLATERESFACE:
                return new ScPlateresFaceSearch(conn);

            case SCPLATERESLINK:
                return new ScPlateresLinkSearch(conn);

            case SCPLATERESGEOMETRY:
                return new ScPlateresGeometrySearch(conn);

            case SCPLATERESRDLINK:
                return new ScPlateresRdlinkSearch(conn);

            default:
                return null;
        }
    }

    /**
     * 控制输出JSON的格式
     *
     * @return JsonConfig
     */
    private JsonConfig getJsonConfig() {
        JsonConfig jsonConfig = new JsonConfig();

        jsonConfig.registerJsonValueProcessor(String.class,
                new JsonValueProcessor() {

                    @Override
                    public Object processObjectValue(String key, Object value,
                                                     JsonConfig arg2) {
                        if (value == null) {
                            return null;
                        }

                        if (JSONUtils.mayBeJSON(value.toString())) {
                            return "\"" + value + "\"";
                        }

                        return value;

                    }

                    @Override
                    public Object processArrayValue(Object value,
                                                    JsonConfig arg1) {
                        return value;
                    }
                });

        return jsonConfig;
    }

    
	public int searchDbId(String adminCode) throws Exception {

		AdAdminSearch search = new AdAdminSearch(conn);

		return search.searchDbId(adminCode);
	}

	public JSONObject searchAdminPosition(String adminCode) throws Exception {
		AdAdminSearch search = new AdAdminSearch(conn);

		return search.searchAdAdminPosition(adminCode);
	}
    
    public static void main(String[] args) throws Exception {
    }
}