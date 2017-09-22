package com.navinfo.dataservice.engine.limit.search.meta;

import com.navinfo.dataservice.engine.limit.glm.iface.IRow;
import com.navinfo.dataservice.engine.limit.glm.model.ReflectionAttrUtils;
import com.navinfo.dataservice.engine.limit.glm.model.meta.ScPlateresGeometry;
import com.navinfo.navicommons.database.sql.DBUtils;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


public class ScPlateresGeometrySearch {

    private Connection conn;

    public ScPlateresGeometrySearch(Connection conn) {
        this.conn = conn;
    }

    public int searchDataByCondition(JSONObject condition, List<IRow> rows) throws Exception {
        StringBuilder sql = new StringBuilder();

        if (!condition.containsKey("groupId") || !condition.containsKey("pageSize") || !condition.containsKey("pageNum")) {
            throw new Exception("参数不足，无法查询SC_PLATERES_GEOMETRY信息");
        }

        String groupId = condition.getString("groupId");
        int pageSize = condition.getInt("pageSize");
        int pageNum = condition.getInt("pageNum");

        sql.append("WITH query AS (");
        sql.append(" SELECT * FROM SC_PLATERES_GEOMETRY WHERE GROUP_ID = " + groupId + ")");
        sql.append(" SELECT *,(SELECT COUNT(1) FROM query) AS TOTAL_ROW_NUM FROM query WHERE");
        sql.append(" rownum BETWEEN " + ((pageSize - 1) * pageNum + 1) + " AND " + (pageSize * pageNum) + "FOR UPDATE NOWAIT");

        PreparedStatement pstmt = null;
        int total = 0;

        ResultSet resultSet = null;

        try {
            pstmt = this.conn.prepareStatement(sql.toString());

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {

                ScPlateresGeometry geometry = new ScPlateresGeometry();

                ReflectionAttrUtils.executeResultSet(geometry, resultSet);

                rows.add(geometry);
            }
        } catch (Exception e) {

            throw e;

        } finally {
            DBUtils.closeResultSet(resultSet);
            DBUtils.closeStatement(pstmt);
        }

        return total;
    }

}