package com.navinfo.dataservice.dao.glm.operator.poi.index;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.dao.glm.iface.IOperator;
import com.navinfo.dataservice.dao.glm.model.poi.index.IxPoiVideo;
import com.navinfo.dataservice.dao.glm.operator.AbstractOperator;
import com.navinfo.dataservice.dao.glm.operator.rd.branch.RdBranchOperator;

/**
 * 索引:POI视频表 操作
 * 
 * @author luyao
 * 
 */
public class IxPoiVideoOperator extends AbstractOperator {

	private static Logger logger = Logger.getLogger(RdBranchOperator.class);

	private IxPoiVideo ixPoiVideo;

	public IxPoiVideoOperator(Connection conn, IxPoiVideo ixPoiVideo) {
		super(conn);

		this.ixPoiVideo = ixPoiVideo;
	}

	@Override
	public void insertRow2Sql(Statement stmt) throws Exception {
		ixPoiVideo.setRowId(UuidUtils.genUuid());

		StringBuilder sb = new StringBuilder("insert into ");

		sb.append(ixPoiVideo.tableName());

		sb.append("(poi_pid, video_id, status,memo, row_id,u_date,u_record) values (");

		sb.append(ixPoiVideo.getPoiPid());

		sb.append("," + ixPoiVideo.getVideoId());

		if (StringUtils.isNotEmpty(ixPoiVideo.getStatus())) {

			sb.append(",'" + ixPoiVideo.getStatus() + "'");
		} else {
			sb.append(",null");
		}

		if (StringUtils.isNotEmpty(ixPoiVideo.getMemo())) {

			sb.append(",'" + ixPoiVideo.getMemo() + "'");
		} else {
			sb.append(",null");
		}

		sb.append(",'" + ixPoiVideo.getRowId() + "'");

		sb.append(",'" + StringUtils.getCurrentTime() + "'");

		sb.append(",'1')");

		stmt.addBatch(sb.toString());

	}

	@Override
	public void updateRow2Sql(Statement stmt) throws Exception {
		StringBuilder sb = new StringBuilder("update " + ixPoiVideo.tableName()
				+ " set u_record=3,u_date='" + StringUtils.getCurrentTime()
				+ "',");

		Set<Entry<String, Object>> set = ixPoiVideo.changedFields().entrySet();

		Iterator<Entry<String, Object>> it = set.iterator();

		while (it.hasNext()) {
			Entry<String, Object> en = it.next();

			String column = en.getKey();

			Object columnValue = en.getValue();

			Field field = ixPoiVideo.getClass().getDeclaredField(column);

			field.setAccessible(true);

			Object value = field.get(ixPoiVideo);

			column = StringUtils.toColumnName(column);

			if (value instanceof String || value == null) {

				if (!StringUtils.isStringSame(String.valueOf(value),
						String.valueOf(columnValue))) {

					if (columnValue == null) {
						sb.append(column + "=null,");
					} else {
						sb.append(column + "='" + String.valueOf(columnValue)
								+ "',");
					}
					this.setChanged(true);

				}

			} else if (value instanceof Double) {

				if (Double.parseDouble(String.valueOf(value)) != Double
						.parseDouble(String.valueOf(columnValue))) {
					sb.append(column + "="
							+ Double.parseDouble(String.valueOf(columnValue))
							+ ",");
					this.setChanged(true);
				}

			} else if (value instanceof Integer) {

				if (Integer.parseInt(String.valueOf(value)) != Integer
						.parseInt(String.valueOf(columnValue))) {
					sb.append(column + "="
							+ Integer.parseInt(String.valueOf(columnValue))
							+ ",");
					this.setChanged(true);
				}

			}
		}
		sb.append(" where row_id=hextoraw('" + ixPoiVideo.getRowId() + "')");

		String sql = sb.toString();

		sql = sql.replace(", where", " where");

		stmt.addBatch(sql);
	}

	@Override
	public void deleteRow2Sql(Statement stmt) throws Exception {
		String sql = "update " + ixPoiVideo.tableName()
				+ " set u_record=2,u_date='" + StringUtils.getCurrentTime()
				+ "' where row_id=hextoraw('" + ixPoiVideo.rowId() + "')";

		stmt.addBatch(sql);
	}

}
