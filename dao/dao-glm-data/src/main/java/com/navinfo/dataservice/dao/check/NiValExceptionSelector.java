package com.navinfo.dataservice.dao.check;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.sql.STRUCT;
import com.navinfo.dataservice.commons.database.ConnectionUtil;
import com.navinfo.dataservice.commons.database.MultiDataSourceFactory;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.util.DateUtilsEx;
import com.navinfo.navicommons.database.Page;
import com.navinfo.navicommons.database.QueryRunner;
import com.vividsolutions.jts.geom.Geometry;

public class NiValExceptionSelector {

	private Connection conn;

	public NiValExceptionSelector(Connection conn) {
		this.conn = conn;
	}

	public NiValException loadById(String id, boolean isLock) throws Exception {

		NiValException exception = new NiValException();

		String sql = "select * from ni_val_exception where md5_code=:1";

		if (isLock) {
			sql += " for update nowait";
		}

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		try {
			pstmt = this.conn.prepareStatement(sql);

			pstmt.setString(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				exception.setValExceptionId(resultSet
						.getInt("val_exception_id"));

				exception.setRuleid(resultSet.getString("ruleid"));

				exception.setTaskName(resultSet.getString("task_name"));

				exception.setGroupid(resultSet.getInt("groupid"));

				exception.setLevel(resultSet.getInt("level"));

				exception.setSituation(resultSet.getString("situation"));

				exception.setInformation(resultSet.getString("information"));

				exception.setSuggestion(resultSet.getString("suggestion"));
				if (resultSet.getObject("location") != null) {
					STRUCT struct = (STRUCT) resultSet.getObject("location");

					exception.setLocation(GeoTranslator.struct2Jts(struct));
				}

				exception.setTargets(resultSet.getString("targets"));

				exception.setAdditionInfo(resultSet.getString("addition_info"));

				exception.setDelFlag(resultSet.getInt("del_flag"));

				exception.setCreated(resultSet.getString("created"));

				exception.setUpdated(resultSet.getString("updated"));

				exception.setMeshId(resultSet.getInt("mesh_id"));

				exception.setScopeFlag(resultSet.getInt("scope_flag"));

				exception.setProvinceName(resultSet.getString("province_name"));

				exception.setMapScale(resultSet.getInt("map_scale"));

				exception.setReserved(resultSet.getString("reserved"));

				exception.setExtended(resultSet.getString("extended"));

				exception.setTaskId(resultSet.getString("task_id"));

				exception.setQaTaskId(resultSet.getString("qa_task_id"));

				exception.setQaStatus(resultSet.getInt("qa_status"));

				exception.setWorker(resultSet.getString("worker"));

				exception.setQaWorker(resultSet.getString("qa_worker"));

				exception.setLogType(resultSet.getInt("log_type"));

				exception.setMd5Code(resultSet.getString("md5_code"));

			} 
		} catch (Exception e) {

			throw e;

		} finally {
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

		return exception;

	}

	public JSONArray loadByMesh(JSONArray meshes, int pageSize, int page)
			throws Exception {

		JSONArray results = new JSONArray();

		Statement stmt = null;

		ResultSet rs = null;

		StringBuilder sql = new StringBuilder(
				"select * from (select b.*,rownum rn from (select md5_code,ruleid,situation,\"LEVEL\" level_,targets,information,a.location.sdo_point.x x,"
						+ "a.location.sdo_point.y y,created,worker from ni_val_exception a where mesh_id in (");

		for (int i = 0; i < meshes.size(); i++) {
			if (i > 0) {
				sql.append(",");

				sql.append(meshes.getInt(i));
			} else {
				sql.append(meshes.getInt(i));
			}
		}

		sql.append(") order by created desc ) b where rownum<=");

		sql.append(pageSize * page);

		sql.append(") where rn>");

		sql.append((page - 1) * pageSize);

		try {

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql.toString());

			while (rs.next()) {
				JSONObject json = new JSONObject();

				json.put("id", rs.getString("md5_code"));

				json.put("ruleid", rs.getString("ruleid"));

				json.put("situation", rs.getString("situation"));

				json.put("rank", rs.getInt("level_"));

				json.put("targets", rs.getString("targets"));

				json.put("information", rs.getString("information"));

				json.put("geometry",
						"(" + rs.getDouble("x") + "," + rs.getDouble("y") + ")");

				json.put("create_date", rs.getString("created"));

				json.put("worker", rs.getString("worker"));

				results.add(json);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return results;
	}

	public int loadCountByMesh(JSONArray meshes) throws Exception {

		Statement stmt = null;

		ResultSet rs = null;

		StringBuilder sql = new StringBuilder(
				"select count(1) count from ni_val_exception a where mesh_id in (");

		for (int i = 0; i < meshes.size(); i++) {
			if (i > 0) {
				sql.append(",");

				sql.append(meshes.getInt(i));
			} else {
				sql.append(meshes.getInt(i));
			}
		}

		sql.append(")");

		try {

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql.toString());

			if (rs.next()) {
				return rs.getInt("count");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return 0;
	}

	/***
	 * 
	 * @param subtaskType
	 * @param grids
	 * @param pageSize
	 * @param pageNum
	 * @param flag
	 *            0 待处理 1 例外 2 确认(修改 和 不修改)
	 * @return
	 * @throws Exception
	 */
	public Page list(int subtaskType, Collection<String> grids,
			final int pageSize, final int pageNum, int flag) throws Exception {

		Clob pidsClob = ConnectionUtil.createClob(conn);
		pidsClob.setString(1, StringUtils.join(grids, ","));
		StringBuilder sql = null;
		if (flag == 0) {
			sql = new StringBuilder(
					"select a.md5_code,ruleid,situation,\"LEVEL\" level_,0 state,"
							+ "targets,information,a.location.sdo_point.x x,a.location.sdo_point.y y,created,updated,"
							+ "worker,qa_worker,qa_status from ni_val_exception a where exists(select 1 from ni_val_exception_grid b,"
							+ "(select to_number(COLUMN_VALUE) COLUMN_VALUE from table(clob_to_table(?))) grid_table "
							+ "where a.md5_code=b.md5_code and b.grid_id =grid_table.COLUMN_VALUE)");
		}
		if (flag == 1) {
			sql = new StringBuilder(
					"select a.md5_code,rule_id as ruleid,situation,rank level_,1 state,"
							+ "targets,information,decode(a.geometry,null,0.0,(sdo_util.from_wktgeometry(a.geometry)).sdo_point.x) x,decode(a.geometry,null,0.0,(sdo_util.from_wktgeometry(a.geometry)).sdo_point.y )y,create_date as created,update_date as updated,"
							+ "worker,qa_worker,qa_status from ck_exception a where a.status = 1 and  exists(select 1 from ck_exception_grid b,"
							+ "(select to_number(COLUMN_VALUE) COLUMN_VALUE from table(clob_to_table(?))) grid_table "
							+ "where a.row_id=b.ck_row_id and b.grid_id =grid_table.COLUMN_VALUE)");

		}
		if (flag == 2) {
			sql = new StringBuilder(
					"select a.md5_code,rule_id as ruleid,situation,rank level_,2 state,"
							+ "targets,information,decode(a.geometry,null,0.0,(sdo_util.from_wktgeometry(a.geometry)).sdo_point.x )x,decode(a.geometry,null,0.0,(sdo_util.from_wktgeometry(a.geometry)).sdo_point.y )y,create_date as created,update_date as updated,"
							+ "worker,qa_worker,qa_status from ck_exception a where a.status = 2 and  exists(select 1 from ck_exception_grid b,"
							+ "(select to_number(COLUMN_VALUE) COLUMN_VALUE from table(clob_to_table(?))) grid_table "
							+ "where a.row_id=b.ck_row_id and b.grid_id =grid_table.COLUMN_VALUE)"
							+ "  union all  select a.md5_code,ruleid,situation,\"LEVEL\" level_,3 state,"
							+ "targets,information,a.location.sdo_point.x x,a.location.sdo_point.y y,created,updated,"
							+ "worker ,qa_worker,qa_status from ni_val_exception_history a where exists(select 1 from ni_val_exception_history_grid b,"
							+ "(select to_number(COLUMN_VALUE) COLUMN_VALUE from table(clob_to_table(?))) grid_table "
							+ "where a.md5_code=b.md5_code and b.grid_id =grid_table.COLUMN_VALUE)");
		}

		if (subtaskType == 0 || subtaskType == 5 || subtaskType == 6
				|| subtaskType == 7) {
			sql.append(" and EXISTS ("
					+ " SELECT 1 FROM CK_RESULT_OBJECT O "
					+ " WHERE (O.table_name like 'IX_POI\\_%' ESCAPE '\\' OR O.table_name ='IX_POI')"
					+ "   AND O.MD5_CODE=a.MD5_CODE)");
		}

		sql.append(" order by created desc,md5_code desc");
		Page page = null;
		if (flag == 2) {
			page = new QueryRunner().query(pageNum, pageSize, conn,
					sql.toString(), new ResultSetHandler<Page>() {

						@Override
						public Page handle(ResultSet rs) throws SQLException {
							return handResult(pageNum, pageSize, rs);
						}
					}, pidsClob, pidsClob);
		} else {
			page = new QueryRunner().query(pageNum, pageSize, conn,
					sql.toString(), new ResultSetHandler<Page>() {

						@Override
						public Page handle(ResultSet rs) throws SQLException {
							return handResult(pageNum, pageSize, rs);
						}
					}, pidsClob);
		}
		return page;
	}

	/***
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Page handResult(int pageNum, int pageSize, ResultSet rs)
			throws SQLException {

		Page page = new Page(pageNum);
		page.setPageSize(pageSize);
		int total = 0;
		JSONArray results = new JSONArray();
		while (rs.next()) {

			if (total == 0) {
				total = rs.getInt("TOTAL_RECORD_NUM_");
			}

			JSONObject json = new JSONObject();

			json.put("id", rs.getString("md5_code"));

			json.put("ruleid", rs.getString("ruleid"));

			json.put("situation", rs.getString("situation"));

			json.put("rank", rs.getInt("level_"));
			json.put("status", rs.getInt("state"));

			json.put("targets", rs.getString("targets"));

			json.put("information", rs.getString("information"));

			json.put("geometry",
					"(" + rs.getDouble("x") + "," + rs.getDouble("y") + ")");

			json.put("create_date", rs.getString("created"));
			json.put("update_date", rs.getString("updated"));

			json.put("worker", rs.getString("worker"));
			json.put(
					"qa_worker",
					rs.getString("qa_worker") == null ? "" : rs
							.getString("qa_worker"));
			json.put("qa_status", rs.getString("qa_status"));

			results.add(json);
		}
		page.setTotalCount(total);
		page.setResult(results);
		return page;

	}

	// ******************************
	/**
	 * @Title: listCheckResults
	 * @Description: 查询道路名检查结果
	 * @param params
	 * @param tips
	 * @param ruleCodes
	 * @return Page
	 * @throws
	 * @author zl zhangli5174@navinfo.com
	 * @date 2016年11月22日 下午8:55:38
	 */
	public Page listCheckResults(JSONObject params, JSONArray tips,
			JSONArray ruleCodes) throws SQLException {
		final int pageSize = params.getInt("pageSize");
		final int pageNum = params.getInt("pageNum");
		int subtaskId = params.getInt("subtaskId");// 获取subtaskid
		long pageStartNum = (pageNum - 1) * pageSize + 1;
		long pageEndNum = pageNum * pageSize;
		StringBuilder sql = new StringBuilder();
		// 获取ids
		String ids = "";
		String tmep = "";
		for (int i = 0; i < tips.size(); i++) {
			JSONObject tipsObj = tips.getJSONObject(i);
			ids += tmep;
			tmep = ",";
			ids += tipsObj.getString("id");
		}
		String rules = "";
		String tmep2 = "";
		for (int i = 0; i < ruleCodes.size(); i++) {
			JSONObject ruleObj = ruleCodes.getJSONObject(i);
			rules += tmep2;
			tmep2 = ",";
			rules += ruleObj.getString("ruleCode");
		}

		// sql.append(" select * from ( ");
		sql.append("with ");
		// **********************
		// 获取子任务范围内的所有 rdName 的nameId
		sql.append("q1 as ( ");
		sql.append("select rd.name_id from ( SELECT null tipid,r.* from rd_name r  where r.src_resume = '\"task\":"
				+ subtaskId + "' ");
		sql.append(" union all ");
		sql.append(" SELECT tt.*  FROM ( select substr(replace(t.src_resume,'\"',''),instr(replace(t.src_resume,'\"',''), ':') + 1,length(replace(src_resume,'\"',''))) as tipid,t.*  from rd_name t  where t.src_resume like '%tips%' ) tt ");
		sql.append(" where 1=1 ");
		// ********zl 2016.12.12 新增判断tips 是否有值****************
		if (ids != null && StringUtils.isNotEmpty(ids)) {
			sql.append(" and tt.tipid in (select column_value from table(clob_to_table('"
					+ ids + "'))) ");
		}
		sql.append(" ) rd ),");
		// **********************
		// 所有道路名的检查结果包含的 nameId 及其 val_exception_id
		/*
		 * select t.val_exception_id eid,REGEXP_SUBSTR(t.addition_info,
		 * '(\[NAME_ID,[0-9]+)', 1, LEVEL, 'i') nameid from (select
		 * a.val_exception_id,to_char(a.addition_info) addition_info from
		 * ni_val_exception a where a.ruleid in (select column_value from
		 * table(clob_to_table(
		 * 'COM01001,COM01003,COM20552,COM60104,GLM02115,GLM02129,GLM02130,GLM02131,GLM02132,GLM02137,GLM02138,GLM02139,GLM02142,GLM02145,GLM02150,GLM02154,GLM02156,GLM02157,GLM02166,GLM02167,GLM02170,GLM02173,GLM02183,GLM02187,GLM02191,GLM02197,GLM02198,GLM02209,GLM02213,GLM02214,GLM02215,GLM02216,GLM02223,GLM02224,GLM02227,GLM02228,GLM02230,GLM02233,GLM02234,GLM02235,GLM02236,GLM02248,GLM02254,GLM02260,GLM02261,GLM02262,GLM02269,GLM02270,GLM90216')))
		 * and a.addition_info like '%NAME_ID,%' ) t CONNECT BY LEVEL <=
		 * LENGTH(t.addition_info) - LENGTH(replace(t.addition_info,
		 * '[NAME_ID,', '[NAME_ID'))
		 */
		sql.append("q2 as ( ");
		sql.append(" SELECT  t.val_exception_id eid, replace(REGEXP_SUBSTR(t.addition_info,'(\\[NAME_ID,[0-9]+)', 1, LEVEL, 'i'),'[NAME_ID,','')  nameid  ");
		sql.append(" FROM ( ");
		sql.append(" select a.val_exception_id,to_char(a.addition_info) addition_info from ni_val_exception a  ");
		sql.append(" where a.ruleid in (select column_value from table(clob_to_table('"
				+ rules + "'))) ");
		sql.append(" ) t ");
		sql.append(" CONNECT BY LEVEL <= LENGTH(t.addition_info) - LENGTH(replace(t.addition_info, '[NAME_ID,', '[NAME_ID'))  ");
		sql.append(" ),");

		/*
		 * sql.append("q2 as ( "); sql.append(
		 * " SELECT distinct  to_char(replace(REGEXP_SUBSTR(t.addition_info,'NAME_ID,[0-9]+', 1, LEVEL, 'i'),'NAME_ID,',''))  nameid ,t.val_exception_id eid "
		 * ); sql.append(" FROM ( ");
		 * sql.append(" select a.* from ni_val_exception a  "); sql.append(
		 * " where a.ruleid in (select column_value from table(clob_to_table('"
		 * +rules+"'))) "); sql.append(" ) t "); sql.append(
		 * " CONNECT BY LEVEL <= LENGTH(t.addition_info) - LENGTH(REGEXP_REPLACE(t.addition_info, 'NAME_ID,', 'NAME_ID'))  "
		 * ); sql.append(" ),");
		 */
		// *********************
		sql.append("q3 as ( ");
		sql.append("select distinct b.eid from q2 b ,q1 c where b.nameid = c.name_id  ");
		sql.append(" ), ");
		// **********************
		sql.append("q4 as ( ");
		sql.append(" select NVL(d.md5_code,0) md5_code,NVL(d.ruleid,0) ruleid,NVL(d.situation,'') situation,\"LEVEL\" level_,"
				+ "NVL(to_char(d.addition_info),'') targets,"
				+ "NVL(d.information,'') information, "
				+ "NVL(d.location.sdo_point.x,0) x, "
				+ "NVL(d.location.sdo_point.y,0) y,"
				+ "d.created,NVL(d.worker,'') worker  "
				+ "from ni_val_exception d ,q3 e where d.val_exception_id = e.eid ");
		sql.append(") ");
		// ************************
		sql.append(" SELECT A.*,(SELECT COUNT(1) FROM q4) AS TOTAL_RECORD_NUM_  "
				+ "FROM " + "(SELECT T.*, ROWNUM AS ROWNO FROM q4 T ");
		sql.append(" WHERE ROWNUM <= " + pageEndNum + ") A "
				+ "WHERE A.ROWNO >= " + pageStartNum + " ");
		sql.append(" order by created desc,md5_code desc ");
		System.out.println("listCheckResults sql:  " + sql.toString());

		QueryRunner run = new QueryRunner();

		// ****************************************
		ResultSetHandler<Page> rsHandler3 = new ResultSetHandler<Page>() {
			public Page handle(ResultSet rs) throws SQLException {
				Page page = new Page();
				int total = 0;
				JSONArray results = new JSONArray();
				while (rs.next()) {
					if (total == 0) {
						total = rs.getInt("TOTAL_RECORD_NUM_");
					}

					JSONObject json = new JSONObject();

					json.put("id", rs.getString("md5_code"));

					json.put("ruleid", rs.getString("ruleid"));

					json.put("situation", rs.getString("situation"));

					json.put("rank", rs.getInt("level_"));

					json.put("targets", rs.getString("targets"));

					json.put("information", rs.getString("information"));

					json.put("geometry",
							"(" + rs.getDouble("x") + "," + rs.getDouble("y")
									+ ")");

					json.put("create_date", rs.getString("created"));

					json.put("worker", rs.getString("worker"));
					results.add(json);
				}
				page.setTotalCount(total);
				page.setResult(results);
				return page;
			}
		};

		Page p = run.query(conn, sql.toString(), rsHandler3);
		return p;
	}

	// ******************************

	public JSONArray loadByGrid(JSONArray grids, int pageSize, int page)
			throws Exception {

		JSONArray results = new JSONArray();

		Statement stmt = null;

		ResultSet rs = null;

		StringBuilder sql = new StringBuilder(
				"select * from (select tmp.*,rownum rn from (select a.md5_code,ruleid,situation,\"LEVEL\" level_,targets,information,a.location.sdo_point.x x,a.location.sdo_point.y y,created,worker from ni_val_exception a where exists(select 1 from ni_val_exception_grid b where a.md5_code=b.md5_code and b.grid_id in(");

		for (int i = 0; i < grids.size(); i++) {
			if (i > 0) {
				sql.append(",");

				sql.append(grids.getLong(i));
			} else {
				sql.append(grids.getLong(i));
			}
		}

		sql.append(")) order by created desc,md5_code desc ) tmp where rownum<=");

		sql.append(pageSize * page);

		sql.append(") where rn>");

		sql.append((page - 1) * pageSize);

		try {

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql.toString());

			while (rs.next()) {
				JSONObject json = new JSONObject();

				json.put("id", rs.getString("md5_code"));

				json.put("ruleid", rs.getString("ruleid"));

				json.put("situation", rs.getString("situation"));

				json.put("rank", rs.getInt("level_"));

				json.put("targets", rs.getString("targets"));

				json.put("information", rs.getString("information"));

				json.put("geometry",
						"(" + rs.getDouble("x") + "," + rs.getDouble("y") + ")");

				json.put("create_date", rs.getString("created"));

				json.put("worker", rs.getString("worker"));

				results.add(json);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return results;
	}

	public int loadCountByGrid(JSONArray grids) throws Exception {

		Statement stmt = null;

		ResultSet rs = null;

		StringBuilder sql = new StringBuilder(
				"select count(distinct(v.md5_code)) count from ni_val_exception v, ni_val_exception_grid g where v.md5_code=g.md5_code and g.grid_id in (");

		for (int i = 0; i < grids.size(); i++) {
			if (i > 0) {
				sql.append(",");

				sql.append(grids.getLong(i));
			} else {
				sql.append(grids.getLong(i));
			}
		}

		sql.append(")");

		try {

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql.toString());

			if (rs.next()) {
				return rs.getInt("count");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return 0;
	}

	public List loadByPid(int pid, List<String> ckRule) throws Exception {

		List ruleList = new ArrayList();

		Statement stmt = null;

		ResultSet rs = null;

		String ckRules = "(";

		for (String rule : ckRule) {
			ckRules += "'" + rule + "',";
		}
		ckRules = ckRules.substring(0, ckRules.length() - 1);

		ckRules += ")";

		StringBuilder sql = new StringBuilder(
				"SELECT n.ruleid FROM ck_result_object c,ni_val_exception n WHERE c.pid="
						+ pid + " AND c.md5_code=n.md5_code AND n.ruleid IN "
						+ ckRules);

		try {

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql.toString());

			while (rs.next()) {
				ruleList.add(rs.getString("ruleid"));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return ruleList;
	}


	/**
	 * @Title: poiCheckResultList
	 * @Description: 根据 pid 查询 exception
	 * @return
	 * @throws Exception
	 * @throws
	 * @author zl zhangli5174@navinfo.com
	 * @date 2017年2月13日 下午10:07:57
	 */
	public JSONArray poiCheckResultList(int pid) throws Exception {

		StringBuilder sql = new StringBuilder(
				"with q1 as( "
						+ "select a.md5_code,a.ruleid,a.\"LEVEL\" level_,a.targets,a.information,a.worker ,a.created,a.location.sdo_point.x x,a.location.sdo_point.y y,a.updated,a.qa_worker,a.qa_status from ni_val_exception a where  "
						+ " EXISTS ( SELECT 1 FROM CK_RESULT_OBJECT O WHERE (O.table_name like 'IX_POI\\_%' ESCAPE '\\' OR O.table_name ='IX_POI')  AND O.MD5_CODE=a.MD5_CODE "
						+ " and o.pid="+pid+"  "
						+ ") "
						+ " union all "
						+ "select c.md5_code,c.rule_id ruleid,c.status level_,c.targets,c.information,c.worker ,c.create_date created,(sdo_util.from_wktgeometry(c.geometry)).sdo_point.x x,(sdo_util.from_wktgeometry(c.geometry)).sdo_point.y y,c.update_date as updated,c.qa_worker,c.qa_status from ck_exception c where "
						+ " EXISTS ( SELECT 1 FROM CK_RESULT_OBJECT O WHERE (O.table_name like 'IX_POI\\_%' ESCAPE '\\' OR O.table_name ='IX_POI')  AND O.MD5_CODE=c.MD5_CODE "
						+ " and o.pid="+pid+" "
						+ ")  "
						+ " ), "
						+ "q2 as ( "
						+ "select distinct  e.md5_code,e.ruleid,e.level_,NVL(to_char(e.targets),'') targets,nvl(to_number(to_char(replace(REGEXP_SUBSTR(e.targets,'(\\[IX_POI,[0-9]+)', 1, LEVEL, 'i'),'[IX_POI,',''))),0)  pid,e.information,e.worker ,e.x,e.y,e.created,e.updated,e.qa_worker,e.qa_status "
						+ " from q1 e "
						+ " where 1=1 "
						+ " CONNECT BY LEVEL <= LENGTH(e.targets) - LENGTH(replace(e.targets, '[IX_POI,', '[IX_POI')) "
						+ "),"
						+ "q3 as ( "
						+ " select distinct md5_code,listagg(pid,' , ') within group (order by  md5_code) as pids  from q2 group by md5_code "
						+ ")");

		// b.md5_code,b.ruleid,b.pid,b.information,b.worker ,b.created
		sql.append(" select A.*,B.pids from q2 A ,q3 B where A.md5_code = B.md5_code  and A.pid = "
				+ pid + " order by A.created desc,A.md5_code desc ");

		System.out.println("poiCheckResultList:  " + sql);
		return new QueryRunner().query(conn, sql.toString(),
				new ResultSetHandler<JSONArray>() {

					@Override
					public JSONArray handle(ResultSet rs) throws SQLException {

						JSONArray results = new JSONArray();
						while (rs.next()) {

							JSONObject json = new JSONObject();

							json.put("id", rs.getString("md5_code"));

							json.put("ruleid", rs.getString("ruleid"));

							// json.put("situation", rs.getString("situation"));

							json.put("rank", rs.getInt("level_"));

							json.put("targets", rs.getString("targets"));

							json.put("information", rs.getString("information"));

							json.put("geometry", "(" + rs.getDouble("x") + ","
									+ rs.getDouble("y") + ")");

							json.put("create_date", rs.getString("created"));
							json.put("update_date", rs.getString("updated"));

							json.put("worker", rs.getString("worker"));
							json.put(
									"qa_worker",
									rs.getString("qa_worker") == null ? "" : rs
											.getString("qa_worker"));
							json.put("qa_status", rs.getString("qa_status"));

							JSONArray refFeaturesArr = new JSONArray();
							int refPoiCount = 0;

							String pids = rs.getString("pids");
							String[] pidsArr = pids.split(",");

							if (pidsArr != null && pidsArr.length > 1) {
								for (String pidStr : pidsArr) {
									if (pidStr != null
											&& StringUtils.isNotEmpty(pidStr)) {
										int refPid = Integer.parseInt(pidStr
												.replaceAll(" ", ""));
										System.out.println("refPid : "+refPid);
										System.out.println("rs.getInt(pid) : "+rs.getInt("pid"));
										
										if (refPid != rs.getInt("pid") && rs.getInt("pid") != 0) {
											// 存在关联poi
											JSONArray refFeatures = queryRefFeatures(refPid);
											if(refFeatures != null && refFeatures.size() >0){
												refFeaturesArr.addAll(refFeatures);
												refPoiCount += 1;
											}
										}

									}
								}
							}

							// 查询关联poi根据pid
							json.put("refFeatures", refFeaturesArr);
							// json.put("refCount", refPoiCount);

							results.add(json);
						}
						return results;
					}
				});
	}

	/**
	 * @Title: queryRefFeatures
	 * @Description: 根据pid 获取关联poi的数据
	 * @param pid
	 * @return JSONArray
	 * @throws SQLException 
	 * @throws
	 * @author zl zhangli5174@navinfo.com
	 * @date 2017年2月14日 下午8:02:27
	 */
	@SuppressWarnings("unchecked")
	public JSONArray queryRefFeatures(int pid) throws SQLException {
		StringBuilder sql = new StringBuilder(
				" select t.pid,t.kind_code,t.geometry,t.\"LEVEL\" level_,t.u_record,t.link_pid,t.poi_num fid,(select n.name from ix_poi_name n where n.poi_pid = t.pid  and n.name_type = 1 AND n.lang_code =  'CHI' and n.name_class = 1) name "
						+ "from ix_poi t  where t.pid =" + pid + " ");
		System.out.println("queryRefFeatures : "+sql);
		
		try {
			return new QueryRunner().query(conn, sql.toString(),
					new ResultSetHandler<JSONArray>() {

						@Override
						public JSONArray handle(ResultSet rs)
								throws SQLException {

							JSONArray results = new JSONArray();
							while (rs.next()) {
								JSONObject json = new JSONObject();

								json.put("name", rs.getString("name"));

								json.put("kindCode", rs.getString("kind_code"));

								json.put("fid", rs.getString("fid"));

								json.put("level", rs.getString("level_"));

								json.put("pid", rs.getInt("pid"));

								// json.put("refType", rs.getInt("ref_type"));

								int lifecycle = 0;
								switch (rs.getInt("u_record")) {
								case 0:
									lifecycle = 0;
									break;
								case 1:
									lifecycle = 3;
									break;
								case 2:
									lifecycle = 1;
									break;
								case 3:
									lifecycle = 2;
									break;
								}

								json.put("state", lifecycle);

								json.put("linkPid", rs.getInt("link_pid"));

								STRUCT struct = (STRUCT) rs
										.getObject("geometry");
								Geometry geometry = null;
								GeoTranslator trans = null;
								String geometryStr = null;
								try {
									geometry = GeoTranslator.struct2Jts(struct);
									trans = new GeoTranslator();
									geometryStr = trans.jts2Wkt(geometry, 1, 5);
								} catch (Exception e) {
									System.out.println("查询结果获取Geometry失败");
									e.printStackTrace();
								}
								json.put("geometry", geometryStr);
								results.add(json);
							}

							return results;
						}
					});
		} catch (SQLException e) {
			throw e;
		}
	}

	public Page listCheckResultsByJobId(JSONObject params, Integer jobId, String jobUuid, int subtaskId,
			JSONArray tips) throws SQLException {
		final int pageSize = params.getInt("pageSize");
		final int pageNum = params.getInt("pageNum");
		
		long pageStartNum = (pageNum - 1) * pageSize + 1;
		long pageEndNum = pageNum * pageSize;
		StringBuilder sql = new StringBuilder();
		// 获取ids
		String ids = "";
		String tmep = "";
		for (int i = 0; i < tips.size(); i++) {
			JSONObject tipsObj = tips.getJSONObject(i);
			ids += tmep;
			tmep = ",";
			ids += tipsObj.getString("id");
		}
		
		sql.append("with ");
		// **********************
		// 获取子任务范围内的所有 rdName 的nameId
		sql.append("q1 as ( ");
		sql.append("select '[NAME_ID,'||rd.name_id||']' nameId from ( SELECT null tipid,r.* from rd_name r  where r.src_resume = '\"task\":"
				+ subtaskId + "' ");
		sql.append(" union all ");
		sql.append(" SELECT tt.*  FROM ( select substr(replace(t.src_resume,'\"',''),instr(replace(t.src_resume,'\"',''), ':') + 1,length(replace(src_resume,'\"',''))) as tipid,t.*  from rd_name t  where t.src_resume like '%tips%' ) tt ");
		sql.append(" where 1=1 ");
		// ********zl 2016.12.12 新增判断tips 是否有值****************
		if (ids != null && StringUtils.isNotEmpty(ids)) {
			sql.append(" and tt.tipid in (select column_value from table(clob_to_table('"
					+ ids + "'))) ");
		}
		sql.append(" ) rd ),");
		// **********************
		sql.append("q2 as ( ");
		sql.append(" select NVL(d.md5_code,0) md5_code,NVL(d.ruleid,0) ruleid,NVL(d.situation,'') situation,\"LEVEL\" level_,"
				+ "NVL(to_char(d.addition_info),'') targets,"
				+ "NVL(d.information,'') information, "
				+ "NVL(d.location.sdo_point.x,0) x, "
				+ "NVL(d.location.sdo_point.y,0) y,"
				+ "d.created,NVL(d.worker,'') worker  "
				+ "from ni_val_exception d  where d.task_name = '"+jobUuid+"' ");
		sql.append("), ");
		sql.append("q3 as ( ");
		sql.append(" select e.* from q1 n ,q2 e where   e.targets like '%'||n.nameId||'%'  ");
		sql.append(") ");
		
		// ************************
		sql.append(" SELECT "+jobId+" jobId,A.*,(SELECT COUNT(1) FROM q3) AS TOTAL_RECORD_NUM_  "
				+ "FROM " + "(SELECT T.*, ROWNUM AS ROWNO FROM q3 T ");
		sql.append(" WHERE ROWNUM <= " + pageEndNum + ") A "
				+ "WHERE A.ROWNO >= " + pageStartNum + " ");
		//sql.append(" order by created desc,md5_code desc ");
		System.out.println("listCheckResultsByJobId sql:  " + sql.toString());

		QueryRunner run = new QueryRunner();

		// ****************************************
		ResultSetHandler<Page> rsHandler3 = new ResultSetHandler<Page>() {
			public Page handle(ResultSet rs) throws SQLException {
				Page page = new Page();
				int total = 0;
				JSONArray results = new JSONArray();
				while (rs.next()) {
					if (total == 0) {
						total = rs.getInt("TOTAL_RECORD_NUM_");
					}

					JSONObject json = new JSONObject();

					json.put("jobId", rs.getInt("jobId"));
					
					json.put("id", rs.getString("md5_code"));

					json.put("ruleid", rs.getString("ruleid"));

					json.put("situation", rs.getString("situation"));

					json.put("rank", rs.getInt("level_"));

					json.put("targets", rs.getString("targets"));

					json.put("information", rs.getString("information"));

					json.put("geometry",
							"(" + rs.getDouble("x") + "," + rs.getDouble("y")
									+ ")");

					json.put("create_date", rs.getString("created"));

					json.put("worker", rs.getString("worker"));
					results.add(json);
				}
				page.setTotalCount(total);
				page.setResult(results);
				return page;
			}
		};

		Page p = run.query(conn, sql.toString(), rsHandler3);
		return p;
	}

	public Page listCheckResultsByTaskName(JSONObject params, final Map<String, String> adminMap) throws SQLException {
		final int pageSize = params.getInt("pageSize");
		final int pageNum = params.getInt("pageNum");
		
		long pageStartNum = (pageNum - 1) * pageSize + 1;
		long pageEndNum = pageNum * pageSize;
		
		String taskName = params.getString("taskName");
		//int flag = params.getInt("flag");
		String nameIds = "";
		
		String sql_where_r = "";
		String sql_where_e = "";
		/*if(flag > 0){//查询选中数据的检查结果
			JSONArray dataArr = params.getJSONArray("data");
			if(dataArr != null &&  dataArr.size() > 0 ){
				nameIds = dataArr.join(",");
				sql_where_r = " and r.name_id in("+nameIds+")";
			}
		}else{*///根据查询条件查询检查结果
			JSONObject paramsObj = params.getJSONObject("params");
			if(paramsObj != null ){
				if(paramsObj.containsKey("name") && paramsObj.getString("name") != null && StringUtils.isNotEmpty(paramsObj.getString("name")) ){
					sql_where_r+=" and  r.name like '%"+paramsObj.getString("name")+"%' ";
				}
				if(paramsObj.containsKey("nameId") && paramsObj.getString("nameId") != null && StringUtils.isNotEmpty(paramsObj.getString("nameId")) ){
					sql_where_r+=" and  r.nameId = "+paramsObj.getString("nameId")+" ";
				}
				if(paramsObj.containsKey("adminId") && paramsObj.getString("adminId") != null && StringUtils.isNotEmpty(paramsObj.getString("adminId")) ){
					sql_where_r+=" and r.admin_id =  "+paramsObj.getString("adminId")+" ";
				}
				if(paramsObj.containsKey("namePhonetic") && paramsObj.getString("namePhonetic") != null && StringUtils.isNotEmpty(paramsObj.getString("namePhonetic")) ){
					sql_where_r+=" and  r.name_phonetic like '%"+paramsObj.getString("namePhonetic")+"%' ";
				}
				if(paramsObj.containsKey("ruleCode") && paramsObj.getString("ruleCode") != null && StringUtils.isNotEmpty(paramsObj.getString("ruleCode")) ){
					sql_where_e+=" and  d.ruleid like '%"+paramsObj.getString("ruleCode")+"%' ";
				}
				if(paramsObj.containsKey("information") && paramsObj.getString("information") != null && StringUtils.isNotEmpty(paramsObj.getString("information")) ){
					sql_where_e+=" and  d.information like '%"+paramsObj.getString("information")+"%' ";
				}
				
			}
			
//		}
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("with ");
		// **********************
		// 获取子任务范围内的所有 rdName 的nameId
		sql.append("q1 as ( ");
		sql.append("select '[NAME_ID,'||r.name_id||']' targets,r.* rd_name r where 1=1 ");
		
		sql.append(sql_where_r);
		
		sql.append(" ),");
		// **********************
		sql.append("q2 as ( ");
		sql.append(" select NVL(d.md5_code,0) md5_code,NVL(d.ruleid,0) ruleid,NVL(d.situation,'') situation,\"LEVEL\" level_,"
				+ "NVL(to_char(d.addition_info),'') targets,"
				+ "NVL(d.information,'') information, "
				+ "NVL(d.location.sdo_point.x,0) x, "
				+ "NVL(d.location.sdo_point.y,0) y,"
				+ "d.created,NVL(d.worker,'') worker  "
				+ "from ni_val_exception d  where d.task_name = '"+taskName+"' ");
		
		sql.append(sql_where_e);
		
		sql.append(" ), ");
		sql.append("q3 as ( ");
		sql.append(" select e.*,n.name_id,n.name,n.name_phonetic,n.road_type,n.admin_id from q1 n ,q2 e where   e.targets like '%'||n.targets||'%'  ");
		sql.append(") ");
		
		// ************************
		sql.append(" SELECT A.*,(SELECT COUNT(1) FROM q3) AS TOTAL_RECORD_NUM_  "
				+ "FROM " + "(SELECT T.*, ROWNUM AS ROWNO FROM q3 T ");
		sql.append(" WHERE ROWNUM <= " + pageEndNum + ") A "
				+ "WHERE A.ROWNO >= " + pageStartNum + " ");
		
		System.out.println("listCheckResultsBytaskName sql:  " + sql.toString());

		QueryRunner run = new QueryRunner();

		
		// ****************************************
		ResultSetHandler<Page> rsHandler3 = new ResultSetHandler<Page>() {
			public Page handle(ResultSet rs) throws SQLException {
				Page page = new Page();
				int total = 0;
				JSONArray results = new JSONArray();
				while (rs.next()) {
					if (total == 0) {
						total = rs.getInt("TOTAL_RECORD_NUM_");
					}

					JSONObject json = new JSONObject();

					//json.put("jobId", rs.getInt("jobId"));
					
					json.put("id", rs.getString("md5_code"));

					json.put("ruleid", rs.getString("ruleid"));

					json.put("situation", rs.getString("situation"));

					json.put("rank", rs.getInt("level_"));

//					json.put("targets", rs.getString("targets"));

					json.put("information", rs.getString("information"));

//					json.put("geometry",
//							"(" + rs.getDouble("x") + "," + rs.getDouble("y")
//									+ ")");

					json.put("create_date", rs.getString("created"));

//					json.put("worker", rs.getString("worker"));
					json.put("nameId", rs.getInt("name_id"));
					json.put("name", rs.getString("name"));
					json.put("namePhonetic", rs.getString("name_phonetic"));
					json.put("roadType", rs.getInt("road_type"));
					int adminId = rs.getInt("admin_id");
//					json.put("adminId", adminId);
					if(adminId == 214){
						json.put("adminName","全国");
					}else{
						if (!adminMap.isEmpty()) {
							if (adminMap.containsKey(String.valueOf(adminId))) {
								json.put("adminName", adminMap.get(String.valueOf(adminId)));
							} else {
								json.put("adminName","");
							}
						}
					}
					
					results.add(json);
				}
				page.setTotalCount(total);
				page.setResult(results);
				return page;
			}
		};
		Page p = run.query(conn, sql.toString(), rsHandler3);
		return p;
	}

	public JSONArray listCheckResultsRuleIds(JSONObject params) {
		JSONArray jobRuleObjs = null;
		try{
		
			String taskName = params.getString("taskName");
		
			QueryRunner run = new QueryRunner();
			
			String jobInfoSql = "select e.ruleid,count(1) numb  from ni_val_exception e  where e.task_name = '"+taskName+"' group by(ruleid) ";
			
			jobRuleObjs = run.query(conn, jobInfoSql, new ResultSetHandler<JSONArray>(){
				
				@Override
				public JSONArray handle(ResultSet rs) throws SQLException {
					JSONArray jobRuleArr = new JSONArray();
					if(rs.next()){
						JSONObject jobRuleObj = new JSONObject();
						jobRuleObj.put("ruleId", rs.getString("ruleid"));
//						jobRuleObj.put("count", rs.getString("numb"));
						jobRuleArr.add(jobRuleObj);
					}
					return jobRuleArr;
				}
				
			});
			return jobRuleObjs;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return jobRuleObjs;
		}
	}
}
