package com.navinfo.dataservice.datahub.chooser.strategy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.navinfo.dataservice.api.datahub.model.DbServer;
import com.navinfo.dataservice.commons.database.MultiDataSourceFactory;
import com.navinfo.dataservice.datahub.exception.DataHubException;
import com.navinfo.navicommons.database.QueryRunner;

/** 
 * @ClassName: UseRefDbStrategy 
 * @author Xiao Xiaowen 
 * @date 2015-12-8 下午1:48:43 
 * @Description: TODO
 */
public class UseRefDbStrategy extends DbServerStrategy{
	protected Logger log = Logger.getLogger(this.getClass());

	public UseRefDbStrategy(AbstractStrategyLock strategyLock){
		super(strategyLock);
	}
	/* (non-Javadoc)
	 * @see com.navinfo.dataservice.datahub.chooser.strategy.DbServerStrategy#getPriorDbServer(java.util.List, java.util.Map)
	 */
	@Override
	public DbServer getPriorDbServer(List<DbServer> dbServers,
			Map<String, Object> params) throws DataHubException {
		if(dbServers.size()>1){
			return null;
		}else{
			return dbServers.get(0);
		}
	}
	/* (non-Javadoc)
	 * @see com.navinfo.dataservice.datahub.chooser.strategy.DbServerStrategy#getPriorDbServer(java.util.Map)
	 */
	@Override
	public DbServer getPriorDbServer(String bizType,Map<String, Object> params)
			throws DataHubException {
		if(params==null
				||params.get("refDbId")==null){
			throw new DataHubException("必须传入参考库Id，否则无法选择服务器。");
		}
		Connection conn = null;
		try{
			String sql = "SELECT s.server_id,s.SERVER_IP,s.server_port,s.server_type,S.SERVICE_NAME FROM db_server s,db_hub d WHERE s.server_id=d.SERVER_ID and s.biz_type like ? and d.db_id=?";
			
			QueryRunner run = new QueryRunner();
			conn = MultiDataSourceFactory.getInstance().getSysDataSource().getConnection();
			DbServer db = run.query(conn, sql,new ResultSetHandler<DbServer>(){

				@Override
				public DbServer handle(ResultSet rs) throws SQLException {
					DbServer inDb = null;
					if(rs.next()){
						String ip = rs.getString("SERVER_IP");
						int port = rs.getInt("SERVER_PORT");
						String type = rs.getString("SERVER_TYPE");
						inDb = new DbServer(type,ip,port);
						inDb.setSid(rs.getInt("SERVER_ID"));
						inDb.setServiceName(rs.getString("SERVICE_NAME"));
					}
					return inDb;
				}
				
			}, "%"+bizType+"%", params.get("refDbId"));
			return db;
		}catch (Exception e) {
			DbUtils.rollbackAndCloseQuietly(conn);
			log.error(e.getMessage(), e);
			throw new DataHubException("查询省份和服务器的映射表出现错误，原因："+e.getMessage(),e);
		} finally {
			DbUtils.commitAndCloseQuietly(conn);
		}
	}

}
