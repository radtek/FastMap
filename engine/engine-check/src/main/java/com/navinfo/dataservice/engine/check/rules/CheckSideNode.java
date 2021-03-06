package com.navinfo.dataservice.engine.check.rules;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCross;
import com.navinfo.dataservice.dao.glm.model.rd.cross.RdCrossNode;
import com.navinfo.dataservice.engine.check.core.baseRule;

/** 
 * @ClassName: CheckSideNode
 * @author songdongyan
 * @date 下午3:02:10
 * @Description: 盲端不允许创建路口
 * 新增路口前检查
 */
public class CheckSideNode extends baseRule {
	
	public void preCheck(CheckCommand checkCommand) throws Exception {
		List<Integer> nodePids = new ArrayList<Integer>();
		
		for(IRow obj:checkCommand.getGlmList()){
			if(obj instanceof RdCross ){
				RdCross rdCross = (RdCross)obj;
						
				for(IRow deObj:rdCross.getNodes()){
					if(deObj instanceof RdCrossNode){
						RdCrossNode rdCrossNode = (RdCrossNode)deObj;
						nodePids.add(rdCrossNode.getNodePid());
					}
				}
			}
			else if(obj instanceof RdCrossNode)
			{
				RdCrossNode rdCrossNode = (RdCrossNode)obj;
				
				nodePids.add(rdCrossNode.getNodePid());
			}
					
		}
		
		String sql = "select count(1) count from rd_link where (e_node_pid=:1 or s_node_pid=:2) AND U_RECORD != 2";

		PreparedStatement pstmt = null;
		try {
			pstmt = getConn().prepareStatement(sql);
			for (int nodePid : nodePids) {
				ResultSet resultSet = null;
				try {
					pstmt.setInt(1, nodePid);
					pstmt.setInt(2, nodePid);
					resultSet = pstmt.executeQuery();
					boolean flag = false;
					if (resultSet.next()) {
						int count = resultSet.getInt("count");
						if (count <= 1) {
							flag = true;
						}
					}
					resultSet.close();
					if (flag) {		
							
						this.setCheckResult("", "", 0);
						return;
					}
				}catch (SQLException e) {
					throw e;
				} finally {
					DbUtils.closeQuietly(resultSet);
				}
			}
		}catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(pstmt);
		}

	}

	
	public void postCheck(CheckCommand checkCommand) {
		
	}

}
