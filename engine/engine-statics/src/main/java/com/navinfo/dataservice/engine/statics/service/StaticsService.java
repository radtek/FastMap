package com.navinfo.dataservice.engine.statics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.navinfo.dataservice.api.statics.model.BlockExpectStatInfo;
import com.navinfo.dataservice.api.statics.model.GridChangeStatInfo;
import com.navinfo.dataservice.api.statics.model.GridStatInfo;
import com.navinfo.dataservice.engine.statics.StatMain;
import com.navinfo.dataservice.engine.statics.expect.ExpectStatusMain;
import com.navinfo.dataservice.engine.statics.expect.PoiCollectExpectMain;
import com.navinfo.dataservice.engine.statics.expect.PoiDailyExpectMain;
import com.navinfo.dataservice.engine.statics.expect.RoadCollectExpectMain;
import com.navinfo.dataservice.engine.statics.expect.RoadDailyExpectMain;
import com.navinfo.dataservice.engine.statics.poicollect.PoiCollectMain;
import com.navinfo.dataservice.engine.statics.poidaily.PoiDailyMain;
import com.navinfo.dataservice.engine.statics.roadcollect.RoadCollectMain;
import com.navinfo.dataservice.engine.statics.roaddaily.RoadDailyMain;
import com.navinfo.dataservice.engine.statics.tools.MongoDao;

public class StaticsService {
	private StaticsService() {
	}

	private static class SingletonHolder {
		private static final StaticsService INSTANCE = new StaticsService();
	}

	public static StaticsService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 查询grid的最新一天的统计信息
	 * 
	 * @param grids
	 * @param poiColName
	 * @param roadColName
	 * @return
	 */
	public List<GridStatInfo> getLatestStatByGrids(List<String> grids,
			String poiColName, String roadColName) {

		MongoDao md = new MongoDao(StatMain.db_name);

		List<GridStatInfo> list = new ArrayList<GridStatInfo>();

		Map<String, GridStatInfo> map = new HashMap<String, GridStatInfo>();

		for (String grid : grids) {
			GridStatInfo info = new GridStatInfo();

			info.setGridId(grid);

			map.put(grid, info);
		}

		int total = grids.size();

		MongoCursor<Document> iter = md
				.find(poiColName, Filters.in("grid_id", grids))
				.sort(Sorts.descending("stat_date")).batchSize(total)
				.iterator();

		int count = 0;
		while (iter.hasNext()) {

			JSONObject json = JSONObject.fromObject(iter.next());

			String gridId = json.getString("grid_id");

			JSONObject poi = json.getJSONObject("poi");

			GridStatInfo info = map.get(gridId);

			if (info == null) {
				info = new GridStatInfo();

				info.setGridId(gridId);
			}

			info.setFinishPoi(poi.getInt("finish"));

			info.setPercentPoi((int)poi.getDouble("percent"));

			info.setTotalPoi(poi.getInt("total"));

			map.put(gridId, info);

			count++;

			if (count >= total) {
				break;
			}
		}

		iter = md.find(roadColName, Filters.in("grid_id", grids))
				.sort(Sorts.descending("stat_date")).batchSize(grids.size())
				.iterator();

		count = 0;
		while (iter.hasNext()) {
			JSONObject json = JSONObject.fromObject(iter.next());

			String gridId = json.getString("grid_id");

			JSONObject road = json.getJSONObject("road");

			GridStatInfo info = map.get(gridId);

			if (info == null) {
				info = new GridStatInfo();

				info.setGridId(gridId);
			}

			info.setFinishRoad(road.getDouble("finish"));

			info.setPercentRoad((int)road.getDouble("percent"));

			info.setTotalRoad(road.getDouble("total"));

			map.put(gridId, info);

			count++;

			if (count >= total) {
				break;
			}
		}

		for (Map.Entry<String, GridStatInfo> e : map.entrySet()) {
			list.add(e.getValue());
		}

		return list;
	}

	/**
	 * 
	 * 查询grid的变迁图
	 * 
	 * @param grids
	 * @param stage
	 * @param type
	 * @param date
	 * @return
	 */
	public List<GridChangeStatInfo> getChangeStatByGrids(Set<String> grids,
			int stage, int type, String date) {

		String colName;
		if (stage == 0) {// 采集
			if (type == 0) {
				colName = PoiCollectMain.col_name_grid;
			} else {
				colName = RoadCollectMain.col_name_grid;
			}
		} else if (stage == 1) {// 日编
			if (type == 0) {
				colName = PoiDailyMain.col_name_grid;
			} else {
				colName = RoadDailyMain.col_name_grid;
			}
		} else { // 月编
			if (type == 0) {
				colName = PoiDailyMain.col_name_grid;
			} else {
				colName = RoadDailyMain.col_name_grid;
			}
		}
		MongoDao md = new MongoDao(StatMain.db_name);

		List<GridChangeStatInfo> list = new ArrayList<GridChangeStatInfo>();

		Map<String, GridChangeStatInfo> map = new HashMap<String, GridChangeStatInfo>();

		for (String grid : grids) {
			GridChangeStatInfo info = new GridChangeStatInfo();

			info.setGridId(grid);

			map.put(grid, info);
		}

		int total = grids.size();

		MongoCursor<Document> iter = md
				.find(colName,
						Filters.and(Filters.in("grid_id", grids),
								Filters.eq("stat_date", date)))
				.batchSize(total).iterator();

		int count = 0;
		while (iter.hasNext()) {

			JSONObject json = JSONObject.fromObject(iter.next());

			String gridId = json.getString("grid_id");

			GridChangeStatInfo info = map.get(gridId);

			if (info == null) {
				info = new GridChangeStatInfo();

				info.setGridId(gridId);
			}

			if (type == 0) {
				JSONObject poi = json.getJSONObject("poi");

				info.setPercent((int) poi.getDouble("percent"));
			} else {
				JSONObject road = json.getJSONObject("road");

				if(stage==0){
					info.setPercent((int) road.getDouble("percent"));
				}
				else{
					info.setPercent((int) road.getDouble("all_percent"));
				}
			}

			map.put(gridId, info);

			count++;

			if (count >= total) {
				break;
			}
		}

		for (Map.Entry<String, GridChangeStatInfo> e : map.entrySet()) {
			list.add(e.getValue());
		}

		return list;
	}

	/**
	 * 查询Block是否达到预期的状态，0未达到，1已达到
	 * @param blocks
	 * @return
	 */
	public Map<Integer, Integer> getExpectStatusByBlocks(Set<Integer> blocks) {

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		for(Integer block : blocks){
			map.put(block, 0);
		}

		MongoDao md = new MongoDao(StatMain.db_name);

		MongoCursor<Document> iter = md
				.find(ExpectStatusMain.col_name_block,Filters.in("block_id", blocks)
								)
				.batchSize(blocks.size()).iterator();

		while (iter.hasNext()) {
			
			Document doc = iter.next();
			
			int status = doc.getInteger("status");
			
			int blockId = doc.getInteger("block_id");
			
			map.put(blockId, status);
			
		}
		
		return map;
	}
	
	/**
	 * 查询城市的是否达到预期的状态， 0未达到，1达到
	 * @param citys
	 * @return
	 */
	public Map<Integer, Integer> getExpectStatusByCitys(Set<Integer> citys) {

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		for (Integer city : citys) {
			map.put(city, city%2);
		}

		return map;
	}

	/**
	 * 
	 * 查询Block的预期统计信息
	 * 
	 * @param blockId
	 * @param stage
	 * @param type
	 * @return
	 */
	public List<BlockExpectStatInfo> getExpectStatByBlock(int blockId,
			int stage, int type) {

		String colName;

		if (stage == 0) {// 采集
			if (type == 0) {
				colName = PoiCollectExpectMain.col_name_block;
			} else {
				colName = RoadCollectExpectMain.col_name_block;
			}
		} else if (stage == 1) {// 日编
			if (type == 0) {
				colName = PoiDailyExpectMain.col_name_block;
			} else {
				colName = RoadDailyExpectMain.col_name_block;
			}
		} else { // 月编
			if (type == 0) {
				colName = PoiDailyExpectMain.col_name_block;
			} else {
				colName = RoadDailyExpectMain.col_name_block;
			}
		}

		List<BlockExpectStatInfo> list = new ArrayList<BlockExpectStatInfo>();

		MongoDao md = new MongoDao(StatMain.db_name);

		MongoCursor<Document> iter = md
				.find(colName, Filters.eq("block_id", blockId))
				.sort(Sorts.ascending("stat_date")).iterator();

		while (iter.hasNext()) {

			Document doc = iter.next();

			BlockExpectStatInfo info = new BlockExpectStatInfo();

			info.setDate(doc.getString("stat_date"));

			info.setExpect(doc.getDouble("expect"));

			info.setFinish(doc.getDouble("finish"));

			info.setPercent(doc.getInteger("percent"));

			list.add(info);
		}

		return list;

	}
	
	public JSONObject getStatBySubtask(int subtaskId){
		
		JSONObject data = new JSONObject();
		
		data.put("subtaskId", subtaskId);
		
		data.put("percent", 20);
		
		JSONObject poi = new JSONObject();
		
		poi.put("total", 2000);
		
		poi.put("finish", 400);
		
		poi.put("working", 1600);
		
		data.put("poi", poi);
		
		JSONObject road = new JSONObject();
		
		road.put("total", 2000);
		
		road.put("finish", 400);
		
		road.put("working", 1600);
		
		data.put("road", road);
		
		return data;
	}
	
	public static void main(String[] args) throws Exception {
//		String wkt = "POLYGON ((116.55736132939865 40.37309069499443, 116.88314510913636 40.37309069499443, 116.88314510913636 40.25788148053289, 116.55736132939865 40.25788148053289, 116.55736132939865 40.37309069499443))";
//		
//		WKTReader r = new WKTReader();
//		Geometry geo = r.read(wkt);
//		Set<String> grids = CompGeometryUtil.geo2GridsWithoutBreak(geo);
		
		List<String> grids = new ArrayList<String>();
		grids.add("60563600");
		StaticsService.getInstance().getLatestStatByGrids(grids, PoiDailyMain.col_name_grid, RoadDailyMain.col_name_grid);
		
//		StaticsService.getInstance().getChangeStatByGrids(grids, 0, 2, "20160620");
	}
}
