package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.navinfo.dataservice.commons.database.ConnectionUtil;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.dao.plus.model.basic.OperationType;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.navicommons.database.sql.DBUtils;
import com.vividsolutions.jts.geom.Geometry;

import oracle.sql.STRUCT;

/**
 * @Title: GLM55048
 * @Package: com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule
 * @Description:检查条件：
					非删除点门牌对象
				检查原则：
					点门牌的显示坐标，不应落在类别为非删除水系LC_FACE.KIND（1～６）的土地覆盖面内，否则报Log：点门牌显示坐标落入XX中！
				排除：“外业LABEL”字段标注有“绿地”字样的
 * @Author: LittleDog
 * @Date: 2017年10月9日
 * @Version: V1.0
 */
public class GLM55048 extends BasicCheckRule {

	@Override
	public void run() throws Exception {

        List<Long> pids = new ArrayList<>();
        for (Map.Entry<Long, BasicObj> entryRow : getRowList().entrySet()) {
            BasicObj basicObj = entryRow.getValue();
            
            // 已删除的数据不检查
            if (basicObj.opType().equals(OperationType.PRE_DELETED)) {
				continue;
			}
            
            if (basicObj.objName().equals(ObjectName.IX_POINTADDRESS)) {
                pids.add(entryRow.getKey());
            }
        }

        if (CollectionUtils.isEmpty(pids)) {
            return;
        }

        String pidStr = org.apache.commons.lang.StringUtils.join(pids, ",");

        Connection conn = getCheckRuleCommand().getConn();

        List<Clob> values = new ArrayList<>();
        String pidString;
        if (pids.size() > 1000) {
            Clob clob = ConnectionUtil.createClob(conn);
            clob.setString(1, pidStr);
            pidString = " PID IN (select to_number(column_value) from table(clob_to_table(?)))";
            values.add(clob);
        } else {
            pidString = " PID IN (" + pidStr + ")";
        }

        String sql = "SELECT T1.PID, T1.MEMOIRE, T1.MESH_ID, T1.GEOMETRY, T2.KIND" + 
                "  FROM IX_POINTADDRESS T1, LC_FACE T2" + 
                " WHERE T1." + pidString +
                "   AND T1.U_RECORD <> 2" + 
                "   AND SDO_RELATE(T2.GEOMETRY, T1.GEOMETRY, 'MASK=ANYINTERACT') = 'TRUE'" + 
                "   AND T2.KIND IN (1, 2, 3, 4, 5, 6)" + 
                "   AND T2.U_RECORD <> 2";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = conn.prepareStatement(sql);
            if (CollectionUtils.isNotEmpty(values)) {
                for (int i = 0; i < values.size(); i++) {
                    pstmt.setClob(i + 1, values.get(i));
                }
            }

            Set<String> validate = new HashSet<>();

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int pid = resultSet.getInt("PID");
                String label = resultSet.getString("MEMOIRE") == null ? "" : resultSet.getString("MEMOIRE");
                int kind = resultSet.getInt("KIND");
                int meshId = resultSet.getInt("MESH_ID");
                STRUCT struct = (STRUCT)resultSet.getObject("GEOMETRY");
                Geometry geo = GeoTranslator.struct2Jts(struct);

                String targets = null;
                if (label.indexOf("绿地") == -1) {
                	targets = String.format("[IX_POINTADDRESS,%s]", pid);
                }

                if (StringUtils.isNotEmpty(targets) && !validate.contains(targets)) {
                	String log = "点门牌显示坐标落入%s中！";
                	switch (kind) {
					case 1:
						log = String.format(log, "海域");
						break;
					case 2:
						log = String.format(log, "河川域");
						break;
					case 3:
						log = String.format(log, "湖沼池");
						break;
					case 4:
						log = String.format(log, "水库");
						break;
					case 5:
						log = String.format(log, "港湾");
						break;
					case 6:
						log = String.format(log, "运河");
						break;
					}
					
                    setCheckResult(geo, targets, meshId, log);
                    validate.add(targets);
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            DBUtils.closeResultSet(resultSet);
            DBUtils.closeStatement(pstmt);
        }
    }

	@Override
	public void runCheck(BasicObj obj) throws Exception {
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList) throws Exception {

	}
}
