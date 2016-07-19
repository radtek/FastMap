package com.navinfo.dataservice.dao.glm.operator.poi.index;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.dao.glm.iface.IOperator;
import com.navinfo.dataservice.dao.glm.model.poi.index.IxPoiNameTone;
import com.navinfo.dataservice.dao.glm.operator.AbstractOperator;

/**
 * POI名称语音语调表 操作类
 * 
 * @author zhangxiaolong
 * 
 */
public class IxPoiNameToneOperator extends AbstractOperator {

	private IxPoiNameTone ixPoiNameTone;

	public IxPoiNameToneOperator(Connection conn, IxPoiNameTone ixPoiNameTone) {
		super(conn);

		this.ixPoiNameTone = ixPoiNameTone;
	}

	@Override
	public void insertRow2Sql(Statement stmt) throws Exception {
		ixPoiNameTone.setRowId(UuidUtils.genUuid());

		StringBuilder sb = new StringBuilder("insert into ");

		sb.append(ixPoiNameTone.tableName());

		sb.append("(NAME_ID, TONE_A, TONE_B, LH_A, LH_B, JYUTP, MEMO,U_DATE,U_RECORD, ROW_ID) values (");

		sb.append(ixPoiNameTone.getNameId());

		if (StringUtils.isNotEmpty(ixPoiNameTone.getToneA())) {

			sb.append(",'" + ixPoiNameTone.getToneA() + "'");
		} else {
			sb.append(",null");
		}

		if (StringUtils.isNotEmpty(ixPoiNameTone.getToneB())) {

			sb.append(",'" + ixPoiNameTone.getToneB() + "'");
		} else {
			sb.append(",null");
		}

		if (StringUtils.isNotEmpty(ixPoiNameTone.getLhA())) {

			sb.append(",'" + ixPoiNameTone.getLhA() + "'");
		} else {
			sb.append(",null");
		}
		if (StringUtils.isNotEmpty(ixPoiNameTone.getLhB())) {

			sb.append(",'" + ixPoiNameTone.getLhB() + "'");
		} else {
			sb.append(",null");
		}
		if (StringUtils.isNotEmpty(ixPoiNameTone.getJyutp())) {

			sb.append(",'" + ixPoiNameTone.getJyutp() + "'");
		} else {
			sb.append(",null");
		}
		if (StringUtils.isNotEmpty(ixPoiNameTone.getMemo())) {

			sb.append(",'" + ixPoiNameTone.getMemo() + "'");
		} else {
			sb.append(",null");
		}

		sb.append(",'" + StringUtils.getCurrentTime() + "'");

		sb.append(",1,'" + ixPoiNameTone.rowId() + "')");

		stmt.addBatch(sb.toString());
	}

	@Override
	public void updateRow2Sql(Statement stmt) throws Exception {
		StringBuilder sb = new StringBuilder("update "
				+ ixPoiNameTone.tableName() + " set u_record=3,u_date= '"
				+ StringUtils.getCurrentTime() + "' ,");

		Set<Entry<String, Object>> set = ixPoiNameTone.changedFields()
				.entrySet();

		Iterator<Entry<String, Object>> it = set.iterator();

		while (it.hasNext()) {
			Entry<String, Object> en = it.next();

			String column = en.getKey();

			Object columnValue = en.getValue();

			Field field = ixPoiNameTone.getClass().getDeclaredField(column);

			field.setAccessible(true);

			Object value = field.get(ixPoiNameTone);

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
		sb.append(" where name_id= " + ixPoiNameTone.getNameId());

		sb.append("')");

		String sql = sb.toString();

		sql = sql.replace(", where", " where");
		stmt.addBatch(sql);
	}

	@Override
	public void deleteRow2Sql(Statement stmt) throws Exception {
		String sql = "update " + ixPoiNameTone.tableName()
				+ " set u_record=2,u_date= '" + StringUtils.getCurrentTime()
				+ "'  where name_id=" + ixPoiNameTone.getNameId();

		stmt.addBatch(sql);
	}

}
