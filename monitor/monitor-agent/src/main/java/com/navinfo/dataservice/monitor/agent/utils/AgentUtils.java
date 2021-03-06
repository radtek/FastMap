package com.navinfo.dataservice.monitor.agent.utils;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.util.ServiceInvokeUtil;
import com.navinfo.dataservice.monitor.agent.model.StatInfo;
import com.navinfo.dataservice.monitor.agent.starter.ConfigFileHandle;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AgentUtils {
	
	protected static Logger log = LoggerRepos.getLogger(AgentUtils.class);

	/**
	 * [
	 * 	{
	 * 		"endpoint": "test-endpoint",
	 * 		"metric": "test-metric2",
	 * 		"timestamp": 1456286040,
	 * 		"step": 60,
	 * 		"value": 2,
	 * 		"counterType": "GAUGE",
	 * 		"tags": "idc=lg,loc=beijing",
	 * 	}
	 * ]
	 * 
	 * metric: 最核心的字段，代表这个采集项具体度量的是什么, 比如是cpu_idle呢，还是memory_free, 还是qps
	 * endpoint: 标明Metric的主体(属主)，比如metric是cpu_idle，那么Endpoint就表示这是哪台机器的cpu_idle
	 * timestamp: 表示汇报该数据时的unix时间戳，注意是整数，代表的是秒
	 * value: 代表该metric在当前时间点的值，float64
	 * step: 表示该数据采集项的汇报周期，这对于后续的配置监控策略很重要，必须明确指定。
	 * counterType: 只能是COUNTER或者GAUGE二选一，前者表示该数据采集项为计时器类型，后者表示其为原值 (注意大小写)
	 * 		GAUGE：即用户上传什么样的值，就原封不动的存储
	 * 		COUNTER：指标在存储和展现的时候，会被计算为speed，即（当前值 - 上次值）/ 时间间隔
	 * tags: 一组逗号分割的键值对, 对metric进一步描述和细化, 可以是空字符串. 比如idc=lg，比如service=xbox等，多个tag之间用逗号分割
	 * 说明：这7个字段都是必须指定
	 * 
	 * @author Han Shaoming
	 * @param jsa
	 * @throws Exception 
	 */
	public static String pushData(List<StatInfo> datas) throws Exception{
		Client client = null;
		String result = null;
		try {
			JSONArray jsa = new JSONArray();
			for (StatInfo statInfo : datas) {
				JSONObject jso = JSONObject.fromObject(statInfo);
				jsa.add(jso);
			}
			log.debug("监控系统推送的数据:"+jsa.toString());
			client = ClientBuilder.newClient();
			Entity<String> payload = Entity.json(jsa.toString());
			//获取推送地址
			Map<String, String> agentServer = ConfigFileHandle.getAgentServer();
			String host = agentServer.get("host");
			String port = agentServer.get("port");
//			String url = "http://192.168.4.110:1988/v1/push";
			String url = "http://"+host+":"+port+"/v1/push";
			log.info("监控系统推送:"+url);
			Response response = client.target(url).request(MediaType.APPLICATION_JSON_TYPE).post(payload);
			
//			System.out.println("status: " + response.getStatus());
//			System.out.println("headers: " + response.getHeaders());
//			System.out.println("body:" + response.readEntity(String.class));
			int status = response.getStatus();
			String body = response.readEntity(String.class);
			if(status==200 && "success".equals(body)){
				result = "调用服务成功！";
			}else{
				result = "调用服务失败！";
			}
		} catch (Exception e) {
			throw new Exception("调用服务失败，原因为"+e.getMessage());
		} finally{
			if(client != null){
				client.close();
			}
		}
		return result;
	}
	
	/**
	 * 判断tomcat是否启动
	 * @author Han Shaoming
	 * @param host
	 * @param port
	 * @return
	 * @throws Exception 
	 */
	public static boolean tomcatRunSuccess(String host,String port) throws Exception{
		try {
			String url = "http://"+host+":"+port+"/";
			int responseTime = 2000;
			ServiceInvokeUtil.invokeByGet(url, null, responseTime);
//			Client client = ClientBuilder.newClient();
//			Response response = client.target(url).request(MediaType.APPLICATION_JSON_TYPE).get();
//			int status = response.getStatus();
//			if(status==200){
//			}else{
//				return false;
//			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	
}
