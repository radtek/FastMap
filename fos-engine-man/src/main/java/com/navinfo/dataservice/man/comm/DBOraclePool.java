package com.navinfo.dataservice.man.comm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

public class DBOraclePool {

	private static BasicDataSource dataSource = null;

	public static void init(JSONObject jsonConnMsg) throws Exception {

		String ip = jsonConnMsg.getString("ip");

		int port = jsonConnMsg.getInt("port");

		String serviceName = jsonConnMsg.getString("serviceName");

		String username = jsonConnMsg.getString("username");

		String password = jsonConnMsg.getString("password");

		Properties p = new Properties();
		p.setProperty("driverClassName", "oracle.jdbc.driver.OracleDriver");
		p.setProperty("url", "jdbc:oracle:thin:@" + ip + ":" + port + ":"
				+ serviceName);
		p.setProperty("password", username);// 连接数据库的密码
		p.setProperty("username", password);// 连接数据库的用户名
		p.setProperty("maxActive", "150");// 最大连接数
		p.setProperty("minIdle", "10"); // 最大空闲连接
		p.setProperty("maxIdle", "20"); // 最大空闲连接
		p.setProperty("initialSize", "30");// 超时等待时间以毫秒为单位
		p.setProperty("logAbandoned", "true");

		p.setProperty("removeAbandoned", "true");// 超时时间(以秒数为单位)
		p.setProperty("removeAbandonedTimeout", "10");
		p.setProperty("maxWait", "1000");

		p.setProperty("timeBetweenEvictionRunsMillis", "10000");
		p.setProperty("numTestsPerEvictionRun", "10");

		p.setProperty("minEvictableIdleTimeMillis", "10000");

		dataSource = (BasicDataSource) BasicDataSourceFactory
				.createDataSource(p);// 创建数据源。

	}

	public static synchronized Connection getConnection() throws SQLException {

		Connection conn = null;
		if (dataSource != null) {
			conn = dataSource.getConnection();
		}
		return conn;
	}

}
