package com.navinfo.dataservice.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.api.man.model.RegionMesh;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.engine.man.region.RegionService;
import com.navinfo.navicommons.database.QueryRunner;
import com.navinfo.navicommons.database.sql.DBUtils;

import net.sf.json.JSONObject;

public class HbasePoiInfo {
	private static Logger log = LoggerRepos.getLogger(HbasePoiInfo.class);

	private QueryRunner run = new QueryRunner();

	private Map<String, List<PoiInfo>> poiCollectionByMesh = new HashMap<>();

	private Set<Integer> notFindPoi = new HashSet<>();

	public Map<String, List<PoiInfo>> getPoiCollectionByMesh() {
		return this.poiCollectionByMesh;
	}

	/**
	 * 
	 * 读取hadoop库生成的txt，分批遍历，取到hadoop库中的poi数据并插入大区日库
	 * 
	 * @param filePath
	 * @param outPath
	 * @throws Exception
	 */
	public void getHBaseDataInfo(String filePath, String outPath) throws Exception {
		File file = new File(filePath);

		InputStreamReader read = new InputStreamReader(new FileInputStream(file));

		BufferedReader reader = new BufferedReader(read);

		String line;

		int cache = 0;

		PrintWriter pw = new PrintWriter(outPath);

		while ((line = reader.readLine()) != null) {
			JSONObject poiObj = JSONObject.fromObject(line);

			PoiInfo poiInfo = resolveResultInfo(poiObj);

			String meshId = poiInfo.getMeshId();

			log.info("获取poi信息：" + poiInfo);

			if (poiCollectionByMesh.containsKey(meshId)) {
				poiCollectionByMesh.get(meshId).add(poiInfo);
			} else {
				List<PoiInfo> poiCollection = new ArrayList<>();
				poiCollection.add(poiInfo);
				poiCollectionByMesh.put(meshId, poiCollection);
			}

			cache++;

			if (cache > 200000) {
				// insert operation
				createMapBetweenPoiAndDbId();

				// print not find poi
				for (Integer pid : notFindPoi) {
					pw.println(pid);
				}

				cache = 0;
				this.clearCollection();
			}
		}

		if (cache > 0) {
			createMapBetweenPoiAndDbId();

			for (Integer pid : notFindPoi) {
				pw.println(pid);
			}
		}

		pw.flush();

		pw.close();

		reader.close();
	}

	/**
	 * hadoop一条数据转换成PoiInfo对象
	 * 
	 * @param poiObj
	 * @return
	 */
	private PoiInfo resolveResultInfo(JSONObject poiObj) {
		PoiInfo poiInfo = new PoiInfo();

		poiInfo.setPid(poiObj.getInt("pid"));
		poiInfo.setFieldVerification(poiObj.getInt("fieldVerification"));
		poiInfo.setVerifyRecord(GetVerRecord(poiObj.getString("verifyFlag")));
		poiInfo.setSourceRecord(GetSrcRecord(poiObj.getString("sourceFlag")));
		poiInfo.setMeshId(poiObj.getString("meshid"));

		return poiInfo;
	}

	/**
	 * Hadoop库：verifyFlags.record到poi_flag中VER_RECORD的转换
	 * 
	 * @param verifyRecord
	 * @return
	 */
	private int GetVerRecord(String verifyRecord) {
		int verRecord = 0;
		switch (verifyRecord) {
		case "010000020001":
			verRecord = 1;
			break;
		case "010000060001":
			verRecord = 2;
			break;
		case "010000040001":
		case "010000050001":
			verRecord = 3;
			break;
		case "010000010001":
			verRecord = 4;
			break;
		case "010000030001":
			verRecord = 5;
			break;
		default:
			break;
		}
		return verRecord;
	}

	/**
	 * Hadoop库：sourceFlags.record到poi_flag中SRC_RECORD的转换
	 * 
	 * @param sourceRecord
	 * @return
	 */
	private int GetSrcRecord(String sourceRecord) {
		if (sourceRecord == null || sourceRecord.isEmpty()) {
			return 0;
		}

		int srcRecord = 1;
		switch (sourceRecord) {
		case "001000010000":
			srcRecord = 1;
			break;
		case "001000020000":
		case "001000030000":
		case "001000030001":
		case "001000030002":
		case "001000030003":
		case "001000030004":
			srcRecord = 3;
			break;
		case "001000130000":
			srcRecord = 2;
			break;
		case "001000040000":
			srcRecord = 4;
			break;
		case "001000140000":
			srcRecord = 5;
			break;
		default:
			break;
		}
		
		return srcRecord;
	}

	public void clearCollection() {
		poiCollectionByMesh.clear();
		notFindPoi.clear();
	}

	/**
	 * 读取hadoop库数据，建立Poi与dbId的映射关系，按照dbId存储相应的poi信息
	 * 
	 * 并插入相应的regionDB库
	 * 
	 * @throws Exception
	 */
	private void createMapBetweenPoiAndDbId() throws Exception {
		Map<Integer, List<PoiInfo>> poiDataByDailyDbId = new HashMap<>();

		ManApi manApi = (ManApi) ApplicationContextUtil.getBean("manApi");
		List<RegionMesh> regions = manApi.queryRegionWithMeshes(this.getPoiCollectionByMesh().keySet());

		if (regions == null || regions.size() == 0) {
			log.error(String.format("根据图幅%s未查询到所属大区库信息",StringUtils.join(this.getPoiCollectionByMesh().keySet(),",")));
			return;
		}

		// 以图幅为主键的数据，更新为以dbId为主键
		for (Map.Entry<String, List<PoiInfo>> entry : this.getPoiCollectionByMesh().entrySet()) {
			for (RegionMesh region : regions) {
				if (!region.getMeshes().contains(entry.getKey())) {
					continue;
				}

				//日库
				if (poiDataByDailyDbId.containsKey(region.getDailyDbId())) {
					poiDataByDailyDbId.get(region.getDailyDbId()).addAll(entry.getValue());
				} else {
					poiDataByDailyDbId.put(region.getDailyDbId(), entry.getValue());
				}
			} // for
		} // for

		//日库
		for (Map.Entry<Integer, List<PoiInfo>> entry : poiDataByDailyDbId.entrySet()) {
			createPoiFlagTable(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 大区日库创建POI_FLAG表，并插入数据
	 * 
	 * @param dailyDbId
	 * @param values
	 * @throws Exception
	 */
	private void createPoiFlagTable(int dailyDbId, List<PoiInfo> values) throws Exception {
		Connection dailyConn = null;
		try {
			dailyConn = DBConnector.getInstance().getConnectionById(dailyDbId);
			String existPoiFlag = String.format("SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = 'IX_POI_FLAG_METHOD'");

			int PoiFlagCount = run.queryForInt(dailyConn, existPoiFlag);
			if (PoiFlagCount == 1) {
				log.info(String.format("大区库<%d>已存在IX_POI_FLAG_METHOD子表", dailyDbId));
			} else {
				String createPoiFlag = createTable;
				run.execute(dailyConn, createPoiFlag);
				log.info(String.format("大区库<%d>创建IX_POI_FLAG_METHOD:成功", dailyDbId));
			}

			log.info("IX_POI_FLAG_METHOD插入数据：");
			for (PoiInfo poiInfo : values) {
				String isExistPoi = String.format("SELECT COUNT(*) FROM IX_POI WHERE PID = %d", poiInfo.getPid());
				int count = run.queryForInt(dailyConn, isExistPoi);

				if (count == 0) {
					log.info("IX_POI表中不存在pid = " + poiInfo.getPid() + "索引，不插入IX_POI_FLAG_METHOD表中");
					notFindPoi.add(poiInfo.getPid());
					continue;
				}

				String isExistPoiFlag = String.format("SELECT COUNT(*) FROM IX_POI_FLAG_METHOD WHERE POI_PID = %d",
						poiInfo.getPid());
				int poiFlagCount = run.queryForInt(dailyConn, isExistPoiFlag);

				String insertItemToPoiFlag = "";
				if (poiFlagCount == 0) {
					insertItemToPoiFlag = String.format(
							"INSERT INTO IX_POI_FLAG_METHOD VALUES(%d, %d, %d, 0, 0, 0, 0, 0, 0, %d, 0, null, 0, null, null, '%s')",
							poiInfo.getPid(), poiInfo.getVerifyRecord(), poiInfo.getSourceRecord(),
							poiInfo.getFieldVerification(),UuidUtils.genUuid());
				} else {
					insertItemToPoiFlag = String.format(
							"UPDATE IX_POI_FLAG_METHOD SET VER_RECORD = %d,SRC_RECORD = %d, FIELD_VERIFIED = %d WHERE POI_PID = %d",
							poiInfo.getVerifyRecord(), poiInfo.getSourceRecord(), poiInfo.getFieldVerification(),
							poiInfo.getPid());
				}
				run.execute(dailyConn, insertItemToPoiFlag);
			}
			dailyConn.commit();
		} catch (Exception e) {
			DBUtils.rollBack(dailyConn);
			throw e;
		} finally {
			if (dailyConn != null) {
				DBUtils.closeConnection(dailyConn);
			}
		}
	}// end
	
	private String createTable = "CREATE TABLE IX_POI_FLAG_METHOD(POI_PID NUMBER(10),VER_RECORD NUMBER(1),SRC_RECORD NUMBER(1),SRC_NAME_CH NUMBER(1),"
			+ "SRC_ADDRESS NUMBER(1),SRC_TELEPHONE NUMBER(1),SRC_COORDINATE NUMBER(1),SRC_NAME_ENG NUMBER(1),SRC_NAME_POR NUMBER(1),FIELD_VERIFIED NUMBER(1),"
			+ "REFRESH_CYCLE NUMBER(3),REFRESH_DATE VARCHAR2(14),U_RECORD NUMBER(3),U_FIELDS VARCHAR(200),U_DATA VARCHAR(14),ROW_ID RAW(16))";
}