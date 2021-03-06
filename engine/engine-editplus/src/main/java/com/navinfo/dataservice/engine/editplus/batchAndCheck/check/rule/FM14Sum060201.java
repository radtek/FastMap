package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.navinfo.dataservice.api.metadata.iface.MetadataApi;
import com.navinfo.dataservice.api.metadata.model.ScSensitiveWordsObj;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiAddress;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.dataservice.dao.plus.selector.custom.IxPoiSelector;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.common.ScSensitiveWordsUtils;

/**
 * @ClassName FM14Sum060201
 * @author Han Shaoming
 * @date 2017年2月7日 下午2:23:14
 * @Description TODO
 * 检查条件：Lifecycle为“0（无）\1（删除）\4（验证）”不检查；
 * 检查原则： 地址（address）中包含敏感字。充电桩（230227）不参与检查。
 * 1、SC_SENSITIVE_WORDS表中type=1时，报log1；
 * 2、SC_SENSITIVE_WORDS表中type=2时，报log2；
 * 备注：
 * 1、配置表中两列关键字、分类和行政区划四个字段，需要同时满足有值列的条件（%表示通配符）
 * 2、配置表中有三个关键字为“三个及三个以上连续数字”需要做特殊处理（如：第一个关键字为“连续三位及三位以上数字”）
 * 3、第二关键字可能会存在“不为XX”的关键字，需要做特殊处理（如：<> %精%）
 */
public class FM14Sum060201 extends BasicCheckRule {

	private Map<Long, Long> adminMap=new HashMap<Long, Long>();
	
	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			//充电桩（230227）不参与检查
			String kindCode = poi.getKindCode();
			if(kindCode == null || "230227".equals(kindCode)){return;}
			IxPoiAddress ixPoiAddress=poiObj.getCHAddress();
			if(ixPoiAddress == null){return;}
			MetadataApi metadataApi=(MetadataApi) ApplicationContextUtil.getBean("metadataApi");
			String fullname = ixPoiAddress.getFullname();
			if(fullname==null){return;}
			Map<Integer, List<ScSensitiveWordsObj>> scSensitiveWordsMap = metadataApi.scSensitiveWordsMap();
			//地址（address）中包含敏感字,SC_SENSITIVE_WORDS表中type=1时
			List<ScSensitiveWordsObj> compareList1 = scSensitiveWordsMap.get(1);
			//type:1-POI 地址,2-POI 名称及其他
			int admin = 0;
			if(adminMap.containsKey(poi.getPid())){
				admin = Integer.valueOf(adminMap.get(poi.getPid()).toString());
			}
			List<ScSensitiveWordsObj> wordList1 = ScSensitiveWordsUtils.matchSensitiveWords(fullname, poi.getKindCode(), admin, compareList1,1);	
			if(wordList1!=null && !wordList1.isEmpty()){
				List<String> errMsgList1 = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				for (ScSensitiveWordsObj scSensitiveWordsObj : wordList1) {
					if(StringUtils.isNotEmpty(scSensitiveWordsObj.getSensitiveWord())){
						sb.append(scSensitiveWordsObj.getSensitiveWord());
					}
					if(StringUtils.isNotEmpty(scSensitiveWordsObj.getSensitiveWord2())){
						sb.append(",");
						sb.append(scSensitiveWordsObj.getSensitiveWord2());
					}
					errMsgList1.add(sb.toString());
				}
				setCheckResult(poi.getGeometry(), poiObj,poi.getMeshId(), "地址中存在绝对敏感字，请确认后修改地址或删除地址("+StringUtils.join(errMsgList1, ";")+")");
				return;
			}
			//地址（address）中包含敏感字,SC_SENSITIVE_WORDS表中type=2时
			List<ScSensitiveWordsObj> compareList2 = scSensitiveWordsMap.get(2);
			//type:1-POI 地址,2-POI 名称及其他
			List<ScSensitiveWordsObj> wordList2 = ScSensitiveWordsUtils.matchSensitiveWords(fullname, poi.getKindCode(), admin, compareList2,1);	
			if(wordList2!=null && !wordList2.isEmpty()){
				List<String> errMsgList2 = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				for (ScSensitiveWordsObj scSensitiveWordsObj : wordList2) {
					if(StringUtils.isNotEmpty(scSensitiveWordsObj.getSensitiveWord())){
						sb.append(scSensitiveWordsObj.getSensitiveWord());
					}
					if(StringUtils.isNotEmpty(scSensitiveWordsObj.getSensitiveWord2())){
						sb.append(",");
						sb.append(scSensitiveWordsObj.getSensitiveWord2());
					}
					errMsgList2.add(sb.toString());
				}
				setCheckResult(poi.getGeometry(), poiObj,poi.getMeshId(), "地址中存在需要确认的敏感字，请确认后修改地址或删除地址("+StringUtils.join(errMsgList2, ";")+")");
				return;
			}
		}
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList) throws Exception {
		Set<Long> pidList=new HashSet<Long>();
		for(BasicObj obj:batchDataList){
			pidList.add(obj.objPid());
		}
		adminMap = IxPoiSelector.getAdminIdByPids(getCheckRuleCommand().getConn(),pidList);
	}

}
