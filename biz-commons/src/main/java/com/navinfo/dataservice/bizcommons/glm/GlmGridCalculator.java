package com.navinfo.dataservice.bizcommons.glm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.navinfo.dataservice.commons.database.MultiDataSourceFactory;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.navicommons.database.QueryRunner;
import com.navinfo.navicommons.geo.computation.JGeometryUtil;

import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;


/** 
* @ClassName: GlmGridCalculator 
* @author Xiao Xiaowen 
* @date 2016年4月13日 下午4:25:39 
* @Description: TODO
*/
public class GlmGridCalculator {

	protected Logger log = LoggerRepos.getLogger(this.getClass());
	public GlmGridCalculator(String gdbVersion,GlmGridCalculatorLock lock){
		this.gdbVersion=gdbVersion;
	}
	private String gdbVersion;
	private Map<String,GlmGridRefInfo> glmGridRefInfoMap = null;//key:表名，value：表配置信息
	private QueryRunner run = new QueryRunner();
	private Map<String,GlmGridRefInfo> getGlmGridRefInfoMap(){
		if(glmGridRefInfoMap==null){
			synchronized(this){
				if(glmGridRefInfoMap==null){
					glmGridRefInfoMap = loadGlmGridRefInfoMap();
				}
			}
		}
		return glmGridRefInfoMap;
	}
	private Map<String,GlmGridRefInfo> loadGlmGridRefInfoMap(){
		Connection conn = null;
		Map<String,GlmGridRefInfo> infoMap = null;
		try{
			conn = MultiDataSourceFactory.getInstance().getSysDataSource().getConnection();
			QueryRunner runner = new QueryRunner();
			String sql = "SELECT TABLE_NAME,REF_COL_NAME,REF_INFO,SINGLE_MESH FROM GLM_GRID_MAP WHERE GDB_VERSION = ?";
			infoMap = runner.query(conn, sql, new ResultSetHandler<Map<String,GlmGridRefInfo>>(){

				@Override
				public Map<String, GlmGridRefInfo> handle(ResultSet rs) throws SQLException {
					Map<String,GlmGridRefInfo> map = new HashMap<String,GlmGridRefInfo>();
					while(rs.next()){
						String tableName = rs.getString("TABLE_NAME");
						GlmGridRefInfo info = new GlmGridRefInfo(tableName);
						info.setGridRefCol(rs.getString("REF_COL_NAME"));
						info.setSingleMesh(rs.getInt("SINGLE_MESH")==1?true:false);
						String refInfo = rs.getString("REF_INFO");
						refInfo = com.navinfo.dataservice.commons.util.StringUtils.removeBlankChar(refInfo);
						if(StringUtils.isNotEmpty(refInfo)){
							String[] refArr = refInfo.split(",");
							List<String[]> refList = new ArrayList<String[]>();
							for(String ref:refArr){
								String[] arr = ref.split(":");
								if(arr.length==3){
									refList.add(arr);
								}else{
									log.warn("GLM_GRID_MAP配置表中关于表"+tableName+"的参考配置信息格式不正确，系统已经忽略，请手工检查");
									break;
								}
							}
							if(refList.size()>0){
								info.setRefInfo(refList);
								map.put(tableName, info);
							}else{
								//..忽略该表配置
							}
						}else{
							info.setRefInfo(null);
							map.put(tableName, info);
						}
					}
					return map;
				}
				
			}, gdbVersion);
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}finally{
			DbUtils.closeQuietly(conn);
		}
		return infoMap;
	}
	public GlmGridRefInfo getGlmGridRefInfo(String tableName){
		return getGlmGridRefInfoMap().get(tableName);
	}
	public String[] calc(String tableName,String pidColName,long pid,Connection dataConn)throws SQLException{
		String sql = assembleQueryGeoSql(tableName,pidColName,pid);
		String[] grids = run.query(dataConn, sql, new SingleRowGridHandler(tableName));
		return grids;
	}
	/**
	 * 给定表的row_id查询所属grid
	 * @param tableName
	 * @param rowIds
	 * @param dataConn：数据所在库的连接
	 * @return: key-value:key-rowId,value-grid号码字符串数组
	 * @throws Exception 
	 */
	public String[] calc(String tableName,String rowId,Connection dataConn)throws Exception{
		String sql = assembleQueryGeoSql(tableName,rowId);
		String[] grids = run.query(dataConn, sql, new SingleRowGridHandler(tableName));
		return grids;
	}
	/**
	 * 给定表的row_id查询所属grid
	 * @param tableName
	 * @param rowIds
	 * @param dataConn：数据所在库的连接
	 * @return:key-value:key-rowId,value-grid号码字符串数组
	 */
	public Map<String,String[]> calc(String tableName,Set<String> rowIds,Connection dataConn){
		//...
		return null;
	}
	/**
	 * 给定履历表的过滤条件，通过履历表查询grid号码
	 * 数据和履历在同一个库
	 * @param tableName
	 * @param logOpTypes:履历的OP_TP类型
	 * @param logConn：数据和履历所在库的连接
	 * @return key:log_detail.row_id,value:对应的记录所属的grid
	 */
	public Map<String,String[]> calc(String tableName,Integer[] logOpTypes,Connection logConn)throws SQLException{
		String sql = assembleQueryGeoSql(tableName,logOpTypes);
		Map<String,String[]> grids = run.query(logConn, sql, new MultiRowGridHandler(tableName));
		return grids;
	}
	/**
	 * 给定履历表的过滤条件，通过履历表查询grid号码
	 * 数据和履历不在同一个库，通过连接到履历查询
	 * 若remoteType=CROSS_USER，那么需要履历所在库和cross user库在同一台服务器上
	 * 若remoteType=DB_LINK,，那么前提是履历所在库已经存在指向参考的数据库的database link
	 * @param tableName
	 * @param logOpTypes：履历的操作状态过滤条件
	 * @param logConn:履历库所在库的连接
	 * @param remoteType：CROSS_USER/DB_LINK
	 * @param remoteParam:cross user name/database link name
	 * @return
	 * @throws SQLException
	 */
	public Map<String,String[]> calc(String tableName,Integer[] logOpTypes,Connection logConn,String remoteType,String remoteParam)throws SQLException{
		String sql = null;
		if("CROSS_USER".equals(remoteType)){
			sql = assembleQueryGeoSqlByCrossUser(tableName, logOpTypes, remoteParam);
		}else if("DB_LINK".equals(remoteType)){
			sql = this.assembleQueryGeoSql_Dblink(tableName, logOpTypes, remoteParam);
		}
		Map<String,String[]> grids = run.query(logConn, sql, new MultiRowGridHandler(tableName));
		return grids;
	}

	/**
	 * 
	 * @param type:rowid/log
	 * @return
	 * @throws Exception 
	 */
	private String assembleQueryGeoSql(String tableName,String rowId) throws Exception{
		try {
			StringBuilder sb = new StringBuilder();
			GlmGridRefInfo refInfo = getGlmGridRefInfoMap().get(tableName);
			sb.append(refInfo.getEditQuerySql());
			sb.append(" AND P.ROW_ID = HEXTORAW('");
			sb.append(rowId);
			sb.append("')");
			return sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception("表:"+tableName+"未配置GLM_GRID_MAP信息");
		}
	}

	/**
	 * 
	 * @param type:rowid/log
	 * @return
	 */
	private String assembleQueryGeoSql(String tableName,String pidColName,long pid){
		StringBuilder sb = new StringBuilder();
		GlmGridRefInfo refInfo = getGlmGridRefInfoMap().get(tableName);
		sb.append(refInfo.getEditQuerySql());
		sb.append(" AND P.");
		sb.append(pidColName);
		sb.append(" = "+pid);
		return sb.toString();
		
	}
	private String assembleQueryGeoSql(String tableName,Integer[] logOpTypes){
		StringBuilder sb = new StringBuilder();
		GlmGridRefInfo refInfo = getGlmGridRefInfoMap().get(tableName);
		sb.append(refInfo.getDiffQuerySql());
		sb.append(" AND L.OP_TP IN ("+StringUtils.join(logOpTypes,",")+")");
		return sb.toString();
	}
	private String assembleQueryGeoSql_Dblink(String tableName,Integer[] logOpTypes,String dbLinkName){
		StringBuilder sb = new StringBuilder();
		GlmGridRefInfo refInfo = getGlmGridRefInfoMap().get(tableName);
		sb.append(refInfo.replaceDiffSqlByDbLink(dbLinkName));
		sb.append(" AND L.OP_TP IN ("+StringUtils.join(logOpTypes,",")+")");
		return sb.toString();
	}
	private String assembleQueryGeoSqlByCrossUser(String tableName,Integer[] logOpTypes,String crossUserName){
		StringBuilder sb = new StringBuilder();
		GlmGridRefInfo refInfo = getGlmGridRefInfoMap().get(tableName);
		sb.append(refInfo.replaceDiffSqlByCrossUser(crossUserName));
		sb.append(" AND L.OP_TP IN ("+StringUtils.join(logOpTypes,",")+")");
		return sb.toString();
	}
	class SingleRowGridHandler implements ResultSetHandler<String[]>{
		
		private String tableName;
		public SingleRowGridHandler(String tableName){
			this.tableName=tableName;
		}

		@Override
		public String[] handle(ResultSet rs) throws SQLException {
			if("CK_EXCEPTION".equals(tableName)){
				WKT wkt = new WKT();
				while(rs.next()){
					String rowId = null;
					try{
						rowId = rs.getString("ROW_ID");
						JGeometry geo = wkt.toJGeometry(rs.getBytes("GEOMETRY"));
						Set<String> rowGrids = null;
						int meshId = rs.getInt("MESH_ID");
						if(meshId>0){
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, String.valueOf(meshId));
						}else{
							String[] meshes = JGeometryUtil.geo2MeshIds(geo);
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, meshes);
						}
						return rowGrids.toArray(new String[0]);
					}catch(Exception e){
						throw new SQLException("查询的geometry可能格式错误，无法转换为object。row_id:"+rowId,e);
					}
				}
			}else{
				while(rs.next()){
					String rowId = null;
					try{
						rowId = rs.getString("ROW_ID");
						JGeometry geo = null;
						geo = JGeometry.load(rs.getBytes("GEOMETRY"));
						Set<String> rowGrids = null;
						int meshId = rs.getInt("MESH_ID");
						if(meshId>0){
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, String.valueOf(meshId));
						}else{
							String[] meshes = JGeometryUtil.geo2MeshIds(geo);
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, meshes);
						}
						return rowGrids.toArray(new String[0]);
					}catch(Exception e){
					    log.error(e.getMessage());
						throw new SQLException("查询的geometry可能格式错误，无法转换为object。row_id:"+rowId,e);
					}
				}
			}
			return null;
		}
		
	}
	class MultiRowGridHandler implements ResultSetHandler<Map<String,String[]>>{
		private String tableName;
		public MultiRowGridHandler(String tableName){
			this.tableName=tableName;
		}

		@Override
		public Map<String, String[]> handle(ResultSet rs) throws SQLException {
			Map<String,String[]> gs = new HashMap<String,String[]>();
			if("CK_EXCEPTION".equals(tableName)){
				WKT wkt = new WKT();
				while(rs.next()){
					String rowId = null;
					try{
						rowId = rs.getString("ROW_ID");
						JGeometry geo = wkt.toJGeometry(rs.getBytes("GEOMETRY"));
						Set<String> rowGrids = null;
						int meshId = rs.getInt("MESH_ID");
						if(meshId>0){
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, String.valueOf(meshId));
						}else{
							String[] meshes = JGeometryUtil.geo2MeshIds(geo);
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, meshes);
						}
						gs.put(rowId, rowGrids.toArray(new String[0]));
					}catch(Exception e){
						throw new SQLException("查询的geometry可能格式错误，无法转换为object。row_id:"+rowId,e);
					}
				}
			}else{
				while(rs.next()){
					String rowId = null;
					try{
						rowId = rs.getString("ROW_ID");
						JGeometry geo = null;
						geo = JGeometry.load(rs.getBytes("GEOMETRY"));
						Set<String> rowGrids = null;
						int meshId = rs.getInt("MESH_ID");
						if(meshId>0){
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, String.valueOf(meshId));
						}else{
							String[] meshes = JGeometryUtil.geo2MeshIds(geo);
							rowGrids = JGeometryUtil.intersectGeometryGrid(geo, meshes);
						}
						gs.put(rowId, rowGrids.toArray(new String[0]));
					}catch(Exception e){
						log.error(e.getMessage());
						throw new SQLException("查询的geometry可能格式错误，无法转换为object。row_id:"+rowId,e);
					}
				}
			}
			return gs;
		}
		
	}
	
	public static void main(String[] args){
	}
	
}
