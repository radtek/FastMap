package com.navinfo.dataservice.dao.glm.selector.poi.index;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.poi.index.IxPoiName;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;

/**
 * POI名称表selector
 * @author zhangxiaolong
 *
 */
public class IxPoiNameSelector extends AbstractSelector {
	
	private Connection conn;

	public IxPoiNameSelector(Connection conn) {
		super(conn);
		this.conn = conn;
		this.setCls(IxPoiName.class);
	}
	
	/**
	 * add by wangdongbin
	 * for android download
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<IRow> loadByIdForAndroid(int id) throws Exception{
		List<IRow> rows = new ArrayList<IRow>();
		IxPoiName ixPoiName = new IxPoiName();
		ResultSet resultSet = null;
		PreparedStatement pstmt = null;
		try {
			String sql = "SELECT name FROM " + ixPoiName.tableName() + " where poi_pid=:1 AND name_class=1 AND name_type=2 AND lang_code='CHI' AND u_record!=2";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			resultSet = pstmt.executeQuery();
			if (resultSet.next()){
				ixPoiName.setName(resultSet.getString("name"));
			}
			rows.add(ixPoiName);
			return rows;
		} catch (Exception e) {
			throw e;
		}finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				
			}

			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				
			}

		}
		
	}
	
}
