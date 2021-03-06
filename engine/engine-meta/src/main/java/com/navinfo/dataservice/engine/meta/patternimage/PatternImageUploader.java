package com.navinfo.dataservice.engine.meta.patternimage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Date;
import java.util.Scanner;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;

import net.sf.json.JSONObject;

public class PatternImageUploader {

	public void run(String fileName) throws Exception {

		String sql = "insert into SC_MODEL_MATCH_G values (?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,sysdate)";

		PreparedStatement pstmt = null;

		Connection conn = null;

		Scanner scanner = null;

		try {
			conn = DBConnector.getInstance().getMetaConnection();

			pstmt = conn.prepareStatement(sql);

			int counter = 0;

			scanner = new Scanner(new FileInputStream(fileName));

			while (scanner.hasNextLine()) {

				String line = scanner.nextLine();

				JSONObject json = JSONObject.fromObject(line);

				pstmt.setNull(1, Types.INTEGER);

				pstmt.setString(2, "");

				pstmt.setString(3, "");

				pstmt.setString(4, "");

				pstmt.setString(5, "");

				pstmt.setString(6, json.getString("bType"));

				pstmt.setString(7, json.getString("mType"));

				pstmt.setString(8, "");

				pstmt.setString(9, json.getString("name"));

				pstmt.setString(10, "");

				pstmt.setString(11, json.getString("format"));

				pstmt.setString(12, "");

				pstmt.setString(13, "");

				pstmt.setString(14, "");

				pstmt.setString(15, "");

				ByteArrayInputStream stream = new ByteArrayInputStream(json
						.getString("content").getBytes());

				pstmt.setBlob(16, stream);

				pstmt.setInt(17, 0);

				pstmt.executeUpdate();

				counter++;

				if (counter % 100 == 0) {
					conn.commit();
				}

			}

			conn.commit();

		} catch (Exception e) {

			throw e;

		} finally {

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {

				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}

			if (scanner != null) {
				scanner.close();
			}
		}
	}
}
