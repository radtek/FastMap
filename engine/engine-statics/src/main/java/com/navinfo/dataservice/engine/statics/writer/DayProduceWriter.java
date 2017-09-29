package com.navinfo.dataservice.engine.statics.writer;

import java.util.Iterator;
import java.util.regex.Pattern;
import org.bson.Document;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.navinfo.dataservice.engine.statics.tools.MongoDao;
import net.sf.json.JSONObject;

public class DayProduceWriter extends DefaultWriter{
	
	/**
	 * 统计信息写入mongo库
	 * @param messageJSON
	 * zl
	 */
	@Override
	public void write2Mongo(String timestamp,JSONObject identifyJson,JSONObject messageJSON){
		for(Object collectionNameTmp:messageJSON.keySet()){
			String collectionName=String.valueOf(collectionNameTmp);
			//初始化统计collection,删除当天的统计记录
			initMongoDbByDate(collectionName,timestamp);
			
			JSONObject jObj = messageJSON.getJSONObject(collectionName);
			
               //统计信息入库
    			Document resultDoc=new Document();
    			resultDoc.put("dpUpdateRoad",jObj.getDouble("dpUpdateRoad"));
    			resultDoc.put("dpAddRoad",jObj.getDouble("dpAddRoad"));
    			resultDoc.put("dpUpdatePoi",jObj.getInt("dpUpdatePoi"));
    			resultDoc.put("dpAddPoi",jObj.getInt("dpAddPoi"));
    			resultDoc.put("dpAverage",jObj.getJSONObject("dpAverage"));
    			resultDoc.put("timestamp",timestamp);
    			MongoDao md = new MongoDao(dbName);
    			md.insertOne(collectionName, resultDoc);
			
		}
		log.info("end write2Mongo");
	}

	
	/**
	 * @param collectionName
	 * @param timestamp
	 * @param 
	 */
	public void initMongoDbByDate(String collectionName,String timestamp) {
		log.info("init mongo "+collectionName);
		MongoDao mdao = new MongoDao(dbName);
		MongoDatabase md = mdao.getDatabase();
		// 初始化 col_name_grid
		Iterator<String> iter_grid = md.listCollectionNames().iterator();
		boolean flag_grid = true;
		while (iter_grid.hasNext()) {
			if (iter_grid.next().equalsIgnoreCase(collectionName)) {
				flag_grid = false;
				break;
			}
		}

		if (flag_grid) {
			md.createCollection(collectionName);
			md.getCollection(collectionName).createIndex(
					new BasicDBObject("timestamp", 1));
			createMongoSelfIndex(md, collectionName);
			log.info("-- -- create mongo collection " + collectionName + " ok");
			log.info("-- -- create mongo index on " + collectionName + "(timestamp) ok");
		}
		
		
		String dateStr = timestamp.substring(0,8); 
		// 删除当天的统计数据
		log.info("删除当天的统计数据 mongo "+collectionName+",timestamp="+timestamp+" ,dateStr="+dateStr);
		Pattern pattern = Pattern.compile("^"+dateStr);
		BasicDBObject query = new BasicDBObject("timestamp", pattern);
		
		mdao.deleteMany(collectionName, query);
	}
	
}