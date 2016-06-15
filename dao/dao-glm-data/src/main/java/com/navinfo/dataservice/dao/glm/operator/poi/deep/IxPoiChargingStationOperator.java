package com.navinfo.dataservice.dao.glm.operator.poi.deep;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.geom.Geojson;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.dao.glm.iface.IOperator;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFace;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFaceTopo;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiAdvertisement;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiBuilding;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiChargingStation;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiDetail;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiGasstation;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiIntroduction;
import com.navinfo.dataservice.dao.glm.operator.rd.branch.RdBranchOperator;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 索引:POI 深度信息(充电站类)操作
 * 
 * @author zhaokk
 * 
 */
public class IxPoiChargingStationOperator implements IOperator {

	private static Logger logger = Logger
			.getLogger(IxPoiChargingStationOperator.class);

	private Connection conn;
	private IxPoiChargingStation ixPoiChargingStation;

	public IxPoiChargingStationOperator(Connection conn, IxPoiChargingStation ixPoiChargingStation) {
		this.conn = conn;
		this.ixPoiChargingStation = ixPoiChargingStation;
	}

	@Override
	public void insertRow() throws Exception {
		Statement stmt = null;

		try {
			stmt = conn.createStatement();

			this.insertRow2Sql(stmt);

			stmt.executeBatch();

		} catch (Exception e) {

			throw e;

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {

			}

		}

	}

	@Override
	public void updateRow() throws Exception {
		StringBuilder sb = new StringBuilder("update " + ixPoiChargingStation.tableName()
				+ " set u_record=3,");

		PreparedStatement pstmt = null;

		try {

			Set<Entry<String, Object>> set = ixPoiChargingStation.changedFields().entrySet();

			Iterator<Entry<String, Object>> it = set.iterator();

			boolean isChanged = false;

			while (it.hasNext()) {
				Entry<String, Object> en = it.next();

				String column = en.getKey();

				Object columnValue = en.getValue();

				Field field = ixPoiChargingStation.getClass().getDeclaredField(column);

				field.setAccessible(true);

				Object value = field.get(ixPoiChargingStation);

				column = StringUtils.toColumnName(column);

				if (value instanceof String || value == null) {

					if (!StringUtils.isStringSame(String.valueOf(value),
							String.valueOf(columnValue))) {

						if (columnValue == null) {
							sb.append(column + "=null,");
						} else {
							sb.append(column + "='"
									+ String.valueOf(columnValue) + "',");
						}
						isChanged = true;
					}

				} else if (value instanceof Double) {

					if (Double.parseDouble(String.valueOf(value)) != Double
							.parseDouble(String.valueOf(columnValue))) {
						sb.append(column
								+ "="
								+ Double.parseDouble(String
										.valueOf(columnValue)) + ",");

						isChanged = true;
					}

				} else if (value instanceof Integer) {

					if (Integer.parseInt(String.valueOf(value)) != Integer
							.parseInt(String.valueOf(columnValue))) {
						sb.append(column + "="
								+ Integer.parseInt(String.valueOf(columnValue))
								+ ",");

						isChanged = true;
					}

				} 
			}
			sb.append(" where charging_id  =" + ixPoiChargingStation.getPid());

			String sql = sb.toString();

			sql = sql.replace(", where", " where");

			if (isChanged) {

				pstmt = conn.prepareStatement(sql);

				pstmt.executeUpdate();

			}

		} catch (Exception e) {
			logger.debug("");
			throw e;

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {

			}

		}

	}

	@Override
	public void deleteRow() throws Exception {
		Statement stmt = null;

		try {
			stmt = conn.createStatement();

			this.deleteRow2Sql(stmt);

			stmt.executeBatch();

		} catch (Exception e) {

			throw e;

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {

			}
		}

	}

	@Override
	public void insertRow2Sql(Statement stmt) throws Exception {
		ixPoiChargingStation.setRowId(UuidUtils.genUuid());
		StringBuilder sb = new StringBuilder("insert into ");
		sb.append(ixPoiChargingStation.tableName());
		sb.append("(charging_id, poi_pid, charging_type, change_brands, change_open_type, charging_num, service_prov, memo, photo_name, open_hour, parking_fees, parking_info, available_state, u_record, row_id) values (");
		sb.append(ixPoiChargingStation.getPid());
		sb.append("," + ixPoiChargingStation.getPoiPid());
		sb.append("," + ixPoiChargingStation.getChargingType());
		sb.append(",'"  + ixPoiChargingStation.getChangeBrands()+"'");
		sb.append(","  + ixPoiChargingStation.getChangeOpenType());
		sb.append(","  + ixPoiChargingStation.getChargingNum());
		sb.append(",'"  + ixPoiChargingStation.getServiceProv()+"'");
		sb.append(",'"  + ixPoiChargingStation.getMemo()+"'");
		sb.append(",'"  + ixPoiChargingStation.getPhotoName()+"'");
		sb.append(",'"  + ixPoiChargingStation.getOpenHour()+"'");
		sb.append(","  + ixPoiChargingStation.getParkingFees());
		sb.append(",'"  + ixPoiChargingStation.getParkingInfo()+"'");
		sb.append(","  + ixPoiChargingStation.getAvailableState());
		sb.append(",1,'" + ixPoiChargingStation.rowId() + "')");
		stmt.addBatch(sb.toString());
	}

	@Override
	public void updateRow2Sql(List<String> fieldNames, Statement stmt)
			throws Exception {

	}

	@Override
	public void deleteRow2Sql(Statement stmt) throws Exception {
		String sql = "update " + ixPoiChargingStation.tableName() + " set u_record=2 where charging_id   =" + ixPoiChargingStation.getPid();
		stmt.addBatch(sql);
	}

}
