package com.navinfo.dataservice.job.statics.manJob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.api.man.iface.ManApi;
import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.constant.PropConstant;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.engine.statics.tools.MongoDao;
import com.navinfo.dataservice.job.statics.AbstractStatJob;
import com.navinfo.dataservice.jobframework.exception.JobException;

import net.sf.json.JSONObject;

public class PersonJob extends AbstractStatJob {
	
	private static final String db_name = SystemConfigFactory.getSystemConfig().getValue(PropConstant.fmStat);
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	public PersonJob(JobInfo jobInfo) {
		super(jobInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String stat() throws JobException {
		PersonJobRequest statReq = (PersonJobRequest)request;
		MongoDao md = new MongoDao(db_name);
		try {
			ManApi manApi = (ManApi)ApplicationContextUtil.getBean("manApi");
			String timestamp = statReq.getTimestamp();
			String timeForOrical = timestamp.substring(0, 8);
			log.info("timestamp:" + timestamp);
			List<Map<String, Object>> personList = manApi.staticsPersionJob(timeForOrical);
			//从mango库中查询数据
			Map<Integer, Map<String, Object>> tasks = queryTaskData(timestamp, md);
			Map<Integer, Object> personFcc = queryPersonFcc(timestamp, md);
			Map<Integer, Object> subTasks = queryDataFromMongo(md, timestamp);
			
			Map<String, Object> result = new HashMap<>();
			List<Map<String, Object>> keyMaps = new ArrayList<>();
			for(Map<String, Object> map : personList){
				int taskId =  Integer.parseInt(map.get("taskId").toString());
				int userId = Integer.parseInt(map.get("userId").toString());
				String key = userId + "_" + taskId + "_" + timestamp;
				Set<Long> subtaskSet = (Set<Long>) map.get("subtaskIds");
				String cityName = map.get("cityName").toString();
				String userName = map.get("userName").toString();
				String taskName = map.get("taskName").toString();
				String leaderName = map.get("leaderName").toString();
				double linkAllLen = 0d;
				double link27AllLen = 0d;
				double tipsAddLen = 0d;
				double tipsAllLen = 0d;
				double fccUpdateLen = 0d;
				int poiAllNum = 0;
				int poiUploadNum = 0;
				int poiFreshNum = 0;
				int poiFinishNum = 0;
				int deleteCount = 0;
	    		int alterCount = 0;
	    		int increaseCount = 0;
	    		
				String startDate = "";
				String endDate = "";
				String workTime = "";
				if(tasks.containsKey(taskId)){
					Map<String, Object> taskMap = tasks.get(taskId);
					linkAllLen =  (double) taskMap.get("linkAllLen");
					link27AllLen =  (double) taskMap.get("link27AllLen");
					poiAllNum = (int) taskMap.get("poiAllNum");
				}
				if(personFcc.containsKey(taskId)){
					Map<String, Object> fccMap = (Map<String, Object>) personFcc.get(taskId);
					startDate = fccMap.get("startDate").toString();
					endDate = fccMap.get("endDate").toString();
					workTime = fccMap.get("workTime").toString();
					fccUpdateLen = Double.valueOf(fccMap.get("fccUpdateLen").toString());
				}
				for(Entry<Integer, Object> entry : subTasks.entrySet()){
					long subtaskId = entry.getKey();
					if(subtaskSet.contains(subtaskId)){
						Map<String, Object> subData = (Map<String, Object>) entry.getValue();
						poiUploadNum += (int)subData.get("poiUploadNum");
						poiFreshNum += (int)subData.get("poiFreshNum");
						poiFinishNum += (int)subData.get("poiFinishNum");
						tipsAddLen += (double)subData.get("tipsAddLen");
						tipsAllLen += (double)subData.get("tipsAllLen");
						deleteCount += (int)subData.get("deleteCount");
						alterCount += (int)subData.get("alterCount");
						increaseCount += (int)subData.get("increaseCount");
					}
				}
				//汇总数据放入map中
				Map<String, Object> dataMap = new HashMap<>();
				dataMap.put("key", key);
				dataMap.put("taskId", taskId);
				dataMap.put("userId", userId);
				dataMap.put("cityName", cityName);
				dataMap.put("userName", userName);
				dataMap.put("taskName", taskName);
				dataMap.put("leaderName", leaderName);
				dataMap.put("linkAllLen", linkAllLen);
				dataMap.put("link27AllLen", link27AllLen);
				dataMap.put("tipsAddLen", tipsAddLen);
				dataMap.put("tipsAllLen", tipsAllLen);
				dataMap.put("poiAllNum", poiAllNum);
				dataMap.put("poiUploadNum", poiUploadNum);
				dataMap.put("poiFreshNum", poiFreshNum);
				dataMap.put("poiFinishNum", poiFinishNum);
				dataMap.put("deleteCount", deleteCount);
				dataMap.put("alterCount", alterCount);
				dataMap.put("increaseCount", increaseCount);
				
				dataMap.put("startDate", startDate);
				dataMap.put("endDate", endDate);
				dataMap.put("workTime", workTime);
				dataMap.put("fccUpdateLen", fccUpdateLen);
				dataMap.put("date", timeForOrical);
				dataMap.put("version", SystemConfigFactory.getSystemConfig().getValue(PropConstant.seasonVersion));
				keyMaps.add(dataMap);
			}
			result.put("person", keyMaps);
			log.info("result:" + result);
			
			return JSONObject.fromObject(result).toString();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobException(e.getMessage(),e);
		}
	}
	

	/**
	 * 从mango库中查询统计数据
	 * @param String
	 * @param Map<String, Object>
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public Map<Integer, Object> queryDataFromMongo(MongoDao md, String timestamp){
		
		//从mongo库差personTips和personDay和task的数据
		Map<Integer, Object> personTips = queryPersonTips(timestamp, md);
		Map<Integer, Object> personDay = queryPersonDay(timestamp, md);
		
		//personTips和personDay的数据放在一个map中
		for(Entry<Integer, Object> entry : personTips.entrySet()){
			int subtaskId = entry.getKey();
			Map<String, Object> subMap = (Map<String, Object>) entry.getValue();
			int poiUploadNum = 0;
			int poiFreshNum = 0;
			int poiFinishNum = 0;
			double tipsAddLen = (double) subMap.get("tipsAddLen");
			double tipsAllLen = (double) subMap.get("tipsAllLen");
			
    		int deleteCount = 0;
    		int alterCount = 0;
    		int increaseCount = 0;
			if(personDay.containsKey(subtaskId)){
				Map<String, Integer> daySubMap = (Map<String, Integer>) personDay.get(subtaskId);
				poiUploadNum = daySubMap.get("poiUploadNum");
				poiFreshNum = daySubMap.get("poiFreshNum");
				poiFinishNum = daySubMap.get("poiFinishNum");
				deleteCount = daySubMap.get("deleteCount");
				alterCount = daySubMap.get("alterCount");
				increaseCount = daySubMap.get("increaseCount");
			}
			subMap.put("poiUploadNum", poiUploadNum);
			subMap.put("poiFreshNum", poiFreshNum);
			subMap.put("poiFinishNum", poiFinishNum);
			subMap.put("tipsAddLen", tipsAddLen);
			subMap.put("tipsAllLen", tipsAllLen);
			subMap.put("deleteCount", deleteCount);
			subMap.put("alterCount", alterCount);
			subMap.put("increaseCount", increaseCount);
			personTips.put(subtaskId, subMap);
		}
		return personTips;
	}
	
	/**
	 * 查询personTips中的数据放入对应map中
	 * @param String
	 * @param MongoDao
	 * @return  Map<Integer, Object>
	 * 
	 * */
	public Map<Integer, Object> queryPersonTips(String timestamp, MongoDao md){
		Map<Integer, Object> result = new HashMap<>();
		String personTipsName = "person_tips";
		BasicDBObject query = new BasicDBObject();
		query.put("timestamp", timestamp);		
		MongoCursor<Document> personTips = md.find(personTipsName, query).iterator();
		//统计一个任务下所有子任务的personTips
		while(personTips.hasNext()){
			JSONObject tipsJson = JSONObject.fromObject(personTips.next());
			double tipsAddLen = 0;
			double tipsAllLen = 0;
			int subtaskId = 0;
			Map<String, Object> map = new HashMap<>();
			subtaskId = Integer.parseInt(tipsJson.get("subtaskId").toString());
			tipsAddLen = Double.valueOf(tipsJson.get("tipsAddLen").toString());
			tipsAllLen = Double.valueOf(tipsJson.get("tipsAllLen").toString());
			map.put("tipsAddLen", tipsAddLen);
			map.put("tipsAllLen", tipsAllLen);
		    result.put(subtaskId, map);
		}
		return result;
	}
	
	/**
	 * 查询personDay中的数据放入对应map中
	 * @param String
	 * @param MongoDao
	 * @return  Map<Integer, Object>
	 * 
	 */
	public Map<Integer, Object> queryPersonDay(String timestamp, MongoDao md){
		String personTableName = "person_day";
		BasicDBObject query = new BasicDBObject();
		query.put("timestamp", timestamp);		
		MongoCursor<Document> person = md.find(personTableName, query).iterator();
		
		Map<Integer, Object> subtaskData = new HashMap<>();
		//统计所有的子任务的数据
		while(person.hasNext()){
			JSONObject json = JSONObject.fromObject(person.next());
			
			Map<String, Integer> map = new HashMap<>();
			int subtaskId = (int) json.get("subtaskId");
			int poiUploadNum = (int) json.get("uploadNum");
			int poiFreshNum = (int) json.get("freshNum");
			int poiFinishNum = (int) json.get("finishNum");
    		int deleteCount = (int) json.get("deleteCount");
    		int alterCount = (int) json.get("alterCount");
    		int increaseCount = (int) json.get("increaseCount");
			
			map.put("poiUploadNum", poiUploadNum);
			map.put("poiFreshNum", poiFreshNum);
			map.put("poiFinishNum", poiFinishNum);
			map.put("deleteCount", deleteCount);
			map.put("alterCount", alterCount);
			map.put("increaseCount", increaseCount);
			subtaskData.put(subtaskId, map);
		}
		return subtaskData;
	}
	
	/**
	 * 从mango库中查询任务统计数据
	 * @param String
	 * @param Map<Integer, Object>
	 * 
	 * */
	public Map<Integer, Map<String, Object>> queryTaskData(String timestamp, MongoDao md){
		
		Map<Integer, Map<String, Object>> tasks = new HashMap<>();

		String planTableName = "task_day_plan";
		MongoCursor<Document> plan = md.find(planTableName, null).iterator();
		while (plan.hasNext()) {
			
			JSONObject json = JSONObject.fromObject(plan.next());
			int taskId = json.getInt("taskId");
			
			Map<String, Object> taskData = new HashMap<>();
			double linkAllLen = Double.valueOf(json.getString("linkAllLen"));
			double link27AllLen = Double.valueOf(json.getString("link27AllLen"));
			int poiAllNum = Integer.parseInt(json.getString("poiAllNum"));
			taskData.put("linkAllLen", linkAllLen);
			taskData.put("link27AllLen", link27AllLen);
			taskData.put("poiAllNum", poiAllNum);
			
			tasks.put(taskId, taskData);
		}
		return tasks;
	}
	
	/**
	 * 查询queryPersonFcc中的数据放入对应map中
	 * @param String
	 * @param MongoDao
	 * @return  Map<Integer, Object>
	 * 
	 * */
	public Map<Integer, Object> queryPersonFcc(String timestamp, MongoDao md){
		Map<Integer, Object> result = new HashMap<>();
		String personFccName = "person_fcc";
		BasicDBObject query = new BasicDBObject();
		query.put("timestamp", timestamp);		
		MongoCursor<Document> personFcc = md.find(personFccName, query).iterator();
		while(personFcc.hasNext()){
			JSONObject fccJson = JSONObject.fromObject(personFcc.next());
			String startCollectTime = "";
			String endCollectTime = "";
			int taskId = Integer.parseInt(fccJson.get("taskId").toString());
			double fccUpdateLen = Double.valueOf(fccJson.get("linkLen").toString());
			Map<String, Object> map = new HashMap<>();
			startCollectTime = (StringUtils.isBlank(fccJson.get("startCollectTime").toString()) ? df.format(new Date()) : fccJson.get("startCollectTime").toString());
			endCollectTime = (StringUtils.isBlank(fccJson.get("endCollectTime").toString()) ? df.format(new Date()) : fccJson.get("endCollectTime").toString());
			String workTime = "";
			try {
				Date begin = df.parse(startCollectTime.replace("/", "-"));
				Date end = df.parse(endCollectTime.replace("/", "-")); 
				long between = (end.getTime() - begin.getTime())/1000;//除以1000是为了转换成秒   
				int day = (int) (between/(24*3600));   
				int hour = (int) (between%(24*3600)/3600);   
				int minute = (int) (between%3600/60);   
				int second = (int) (between%60);
				workTime = day+"天"+hour+"小时"+minute+"分"+second+"秒";
			}catch(ParseException e){
				e.printStackTrace();
			}
			
			map.put("startDate", startCollectTime);
			map.put("endDate", endCollectTime);
			map.put("workTime", workTime);
			map.put("fccUpdateLen", fccUpdateLen);
		    result.put(taskId, map);
		}
		return result;
	}
	
}
