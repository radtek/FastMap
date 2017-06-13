package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.navinfo.dataservice.api.metadata.iface.MetadataApi;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxSamepoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxSamepoiPart;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxSamePoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;

/**
 * @ClassName GLM60291
 * @author Han Shaoming
 * @date 2017年2月21日 下午6:42:24
 * @Description TODO
 * 检查条件： 非删除POI对象；
 * 检查原则：
 * 同一关系中关系类型为“多分类同属性(多义性)”（RELATION_TYPE=1）的一组POI（如：KIND=1或KIND=2），
 * 其两方分类必须在SC_POINT_KIND_NEW中TYPE=5的POIKIND和R_KIND中存在，即：KIND=1或KIND=2必须存在于任意同一行的一组
 * POIKIND或R_KIND中,如果不存在，则报Log：XXXX与XXXX分类之间不可制作同一关系
 */
public class GLM60291 extends BasicCheckRule {
	
	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_SAMEPOI)){
			IxSamePoiObj poiObj=(IxSamePoiObj) obj;
			IxSamepoi poi=(IxSamepoi) poiObj.getMainrow();
			if(poi.getRelationType()!=1){return;}
			List<IxSamepoiPart> parts = poiObj.getIxSamepoiParts();
			if(parts==null||parts.size()<2){return;}
			
			BasicObj obj1 = myReferDataMap.get(ObjectName.IX_POI).get(parts.get(0).getPoiPid());
			IxPoi poi1 = (IxPoi) obj1.getMainrow();
			String kind1=poi1.getKindCode();
			
			BasicObj obj2 = myReferDataMap.get(ObjectName.IX_POI).get(parts.get(1).getPoiPid());
			IxPoi poi2 = (IxPoi) obj2.getMainrow();
			String kind2=poi2.getKindCode();
			
			//SC_POINT_KIND_NEW表的TYPE=5
			MetadataApi metadataApi = (MetadataApi) ApplicationContextUtil.getBean("metadataApi");
			List<Map<String, String>> scPointKindNew5List = metadataApi.scPointKindNewChainKind5Map();
			boolean check = true;
			for (Map<String, String> scPointKindNew5Map : scPointKindNew5List) {
				for(Map.Entry<String, String> entry : scPointKindNew5Map.entrySet()){
					String key = entry.getKey();
					String value = entry.getValue();
					if((kind1.equals(key)&&kind2.equals(value))||(kind1.equals(value)&&kind2.equals(key))){
						check = false;
						break;
					}
				}
				if(!check){break;}
			}
			Map<String, String> kindNameByKindCode = metadataApi.getKindNameByKindCode();
			if(check){
				String targets = "[IX_POI,"+poi1.getPid()+"];[IX_POI,"+poi2.getPid()+"]";
				setCheckResult(poi1.getGeometry(), targets,poi1.getMeshId(), kindNameByKindCode.get(kind1)+"与"+kindNameByKindCode.get(kind2)+"分类之间不可制作同一关系");
				return;
			}
		}
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList) throws Exception {
		Set<Long> pidList=new HashSet<Long>();
		for(BasicObj obj:batchDataList){
			IxSamePoiObj poiObj=(IxSamePoiObj) obj;
			IxSamepoi poi=(IxSamepoi) poiObj.getMainrow();
			if(poi.getRelationType()!=1){continue;}
			List<IxSamepoiPart> parts = poiObj.getIxSamepoiParts();
			for(IxSamepoiPart tmp:parts){
				pidList.add(tmp.getPoiPid());
			}
		}
		Map<Long, BasicObj> result = getCheckRuleCommand().loadReferObjs(pidList, ObjectName.IX_POI, null, false);
		myReferDataMap.put(ObjectName.IX_POI, result);
	}

}
