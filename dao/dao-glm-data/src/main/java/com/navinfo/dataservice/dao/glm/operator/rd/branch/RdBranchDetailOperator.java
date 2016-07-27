package com.navinfo.dataservice.dao.glm.operator.rd.branch;

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
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.rd.branch.RdBranchDetail;
import com.navinfo.dataservice.dao.glm.model.rd.branch.RdBranchName;
import com.navinfo.dataservice.dao.glm.operator.AbstractOperator;

public class RdBranchDetailOperator extends AbstractOperator {

	private static Logger logger = Logger
			.getLogger(RdBranchDetailOperator.class);

	private RdBranchDetail detail;

	public RdBranchDetailOperator(Connection conn, RdBranchDetail detail) {
		super(conn);

		this.detail = detail;
	}

	@Override
	public void insertRow2Sql(Statement stmt) throws Exception {

		detail.setRowId(UuidUtils.genUuid());

		StringBuilder sb = new StringBuilder("insert into ");

		sb.append(detail.tableName());

		sb.append("(detail_id, branch_pid, voice_dir, estab_type, name_kind, exit_num, branch_type, pattern_code, arrow_code, arrow_flag, guide_code, u_record, row_id) values (");

		sb.append(detail.getPid());

		sb.append("," + detail.getBranchPid());

		sb.append("," + detail.getVoiceDir());

		sb.append("," + detail.getEstabType());

		sb.append("," + detail.getNameKind());

		if (detail.getExitNum() == null) {
			sb.append(",null");
		} else {
			sb.append(",'" + detail.getExitNum() + "'");
		}

		sb.append("," + detail.getBranchType());

		if (detail.getPatternCode() == null) {
			sb.append(",null");
		} else {
			sb.append(",'" + detail.getPatternCode() + "'");
		}

		if (detail.getArrowCode() == null) {
			sb.append(",null");
		} else {
			sb.append(",'" + detail.getArrowCode() + "'");
		}

		sb.append("," + detail.getArrowFlag());

		sb.append("," + detail.getGuideCode());

		sb.append(",1,'" + detail.rowId() + "')");

		stmt.addBatch(sb.toString());

		for (IRow r : detail.getNames()) {
			RdBranchNameOperator op = new RdBranchNameOperator(conn,
					(RdBranchName) r);

			op.insertRow2Sql(stmt);
		}

	}

	@Override
	public void updateRow2Sql(Statement stmt) throws Exception {
		StringBuilder sb = new StringBuilder("update " + detail.tableName()
				+ " set u_record=3,");

		Set<Entry<String, Object>> set = detail.changedFields().entrySet();

		Iterator<Entry<String, Object>> it = set.iterator();

		while (it.hasNext()) {
			Entry<String, Object> en = it.next();

			String column = en.getKey();

			Object columnValue = en.getValue();

			Field field = detail.getClass().getDeclaredField(column);

			field.setAccessible(true);

			Object value = field.get(detail);

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
		sb.append(" where detail_id=" + detail.getPid());

		String sql = sb.toString();

		sql = sql.replace(", where", " where");
		stmt.addBatch(sql);
	}

	@Override
	public void deleteRow2Sql(Statement stmt) throws Exception {

		String sql = "update " + detail.tableName()
				+ " set u_record=2 where detail_id=" + detail.getPid();

		stmt.addBatch(sql);

		for (IRow r : detail.getNames()) {
			RdBranchNameOperator op = new RdBranchNameOperator(conn,
					(RdBranchName) r);

			op.deleteRow2Sql(stmt);
		}
	}

}
