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
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiBuilding;
import com.navinfo.dataservice.dao.glm.model.poi.deep.IxPoiChargingPlot;
import com.navinfo.dataservice.dao.glm.operator.rd.branch.RdBranchOperator;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 索引:POI 深度信息(充电桩类)操作
 * 
 * @author zhaokk
 * 
 */
public class IxPoiChargingPlotPhOperator implements IOperator {

	private static Logger logger = Logger
			.getLogger(IxPoiChargingPlotPhOperator.class);

	private Connection conn;
	private IxPoiChargingPlot ixPoiChargingPlot;

	public IxPoiChargingPlotPhOperator(Connection conn, IxPoiChargingPlot ixPoiChargingPlot) {
		this.conn = conn;
		this.ixPoiChargingPlot = ixPoiChargingPlot;
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
		StringBuilder sb = new StringBuilder("update " + ixPoiChargingPlot.tableName()
				+ " set u_record=3,");

		PreparedStatement pstmt = null;

		try {

			Set<Entry<String, Object>> set = ixPoiChargingPlot.changedFields().entrySet();

			Iterator<Entry<String, Object>> it = set.iterator();

			boolean isChanged = false;

			while (it.hasNext()) {
				Entry<String, Object> en = it.next();

				String column = en.getKey();

				Object columnValue = en.getValue();

				Field field = ixPoiChargingPlot.getClass().getDeclaredField(column);

				field.setAccessible(true);

				Object value = field.get(ixPoiChargingPlot);

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
			sb.append(" where row_id=hextoraw('" + ixPoiChargingPlot.getRowId() + "')");

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
		ixPoiChargingPlot.setRowId(UuidUtils.genUuid());
		StringBuilder sb = new StringBuilder("insert into ");
		sb.append(ixPoiChargingPlot.tableName());
		sb.append("(poi_pid, group_id, count, acdc, plug_type, power, voltage, \"current\", \"mode\", memo, plug_num, prices, open_type, available_state, manufacturer, factory_num, plot_num, product_num, parking_num, floor, location_type, payment, u_record,row_id) values (");
		
		sb.append(ixPoiChargingPlot.getPoiPid());
		sb.append("," + ixPoiChargingPlot.getGroupId());
		sb.append("," + ixPoiChargingPlot.getCount() );
		sb.append("," + ixPoiChargingPlot.getAcdc() );
		sb.append(",'" + ixPoiChargingPlot.getPlugType()+"'" );
		sb.append(",'" + ixPoiChargingPlot.getPower()+"'");
		sb.append(",'" + ixPoiChargingPlot.getVoltage()+"'");
		sb.append(",'" + ixPoiChargingPlot.getCurrent()+"'");
		sb.append("," + ixPoiChargingPlot.getMode());
		sb.append(",'" + ixPoiChargingPlot.getMemo()+"'");
		sb.append("," + ixPoiChargingPlot.getPlugNum());
		sb.append(",'" + ixPoiChargingPlot.getPrices()+"'");
		sb.append(",'" + ixPoiChargingPlot.getOpenType()+"'");
		sb.append("," + ixPoiChargingPlot.getAvailableState());
		sb.append(",'" + ixPoiChargingPlot.getManufacturer()+"'");
		sb.append(",'" + ixPoiChargingPlot.getFactoryNum()+"'");
		sb.append(",'" + ixPoiChargingPlot.getPlotNum()+"'");
		sb.append(",'" + ixPoiChargingPlot.getProductNum()+"'");
		sb.append(",'" + ixPoiChargingPlot.getParkingNum()+"'");
		sb.append("," + ixPoiChargingPlot.getFloor());
		sb.append("," + ixPoiChargingPlot.getLocationType());
		sb.append(",'" + ixPoiChargingPlot.getPayment()+"'");

		sb.append(",1,'" + ixPoiChargingPlot.rowId() + "')");

		stmt.addBatch(sb.toString());
	}

	@Override
	public void updateRow2Sql(List<String> fieldNames, Statement stmt)
			throws Exception {

	}

	@Override
	public void deleteRow2Sql(Statement stmt) throws Exception {
		
		String sql = "update " + ixPoiChargingPlot.tableName() + " set u_record=2 where row_id=hextoraw('" + ixPoiChargingPlot.rowId()
				+ "')";
		stmt.addBatch(sql);
	}

}
