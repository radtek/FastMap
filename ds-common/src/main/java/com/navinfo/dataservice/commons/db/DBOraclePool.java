package com.navinfo.dataservice.commons.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import net.sf.json.JSONObject;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.navinfo.dataservice.commons.constant.PropConstant;

public class DBOraclePool {

	private DataSource dataSource = null;

	private String ip;
	
	private int port;
	
	private String serviceName;
	
	private String username;
	
	private String password;

	public DBOraclePool(JSONObject jsonConnMsg) throws Exception {

		ip = jsonConnMsg.getString("ip");

		port = jsonConnMsg.getInt("port");

		serviceName = jsonConnMsg.getString("serviceName");

		username = jsonConnMsg.getString("username");

		password = jsonConnMsg.getString("password");

		this.init();
	}
	
	public DBOraclePool(String ip, int port, String serviceName, String username, String password) throws Exception{
		this.ip= ip;
		
		this.port = port;
		
		this.serviceName = serviceName;
		
		this.username = username;
		
		this.password = password;
		
		this.init();
	}

	private void init() throws Exception {

		
		Properties p = new Properties();
		p.setProperty("driverClassName", PropConstant.oracleDriver);
		p.setProperty("url", "jdbc:oracle:thin:@" + ip + ":" + port + ":"
				+ serviceName);
		p.setProperty("password", password);// 连接数据库的密码
		p.setProperty("username", username);// 连接数据库的用户名
//		p.setProperty("maxTotal", "150");// 最大连接数
//		p.setProperty("minIdle", "10"); // 最大空闲连接
//		p.setProperty("maxIdle", "20"); // 最大空闲连接
//		p.setProperty("initialSize", "30");// 超时等待时间以毫秒为单位
//		p.setProperty("logAbandoned", "true");
//
//		p.setProperty("removeAbandoned", "true");// 超时时间(以秒数为单位)
//		p.setProperty("removeAbandonedTimeout", "10");
//		p.setProperty("maxWaitMillis", "3000");
//
//		p.setProperty("timeBetweenEvictionRunsMillis", "10000");
//		p.setProperty("numTestsPerEvictionRun", "10");
//
//		p.setProperty("minEvictableIdleTimeMillis", "10000");

//		dataSource = (BasicDataSource) BasicDataSourceFactory
//				.createDataSource(p);// 创建数据源。
//		
//		dataSource.setInitialSize(25);
//		
//		dataSource.setMaxTotal(150);
//		
//		dataSource.setMaxWaitMillis(3500);
//		
//		dataSource.setRemoveAbandonedOnBorrow(true);
//		
//		dataSource.setRemoveAbandonedOnMaintenance(true);
		
		p.setProperty("initialSize", "5");
		p.setProperty("minIdle","5");
		p.setProperty("maxActive","150");
		p.setProperty("maxWait","3500");
		p.setProperty("timeBetweenEvictionRunsMillis","60000");
		p.setProperty("minEvictableIdleTimeMillis","300000");
		p.setProperty("filters","stat");
		p.setProperty("poolPreparedStatements","true");
		p.setProperty("maxPoolPreparedStatementPerConnectionSize","20");
		p.setProperty("testWhileIdle","true");
		p.setProperty("setValidationQuery","select 1 from dual");
		
		dataSource = DruidDataSourceFactory.createDataSource(p); 
		
	}

	public synchronized Connection getConnection() throws SQLException {

		Connection conn = null;
		if (dataSource != null) {
			conn = dataSource.getConnection();
		}
		return conn;
	}

}
