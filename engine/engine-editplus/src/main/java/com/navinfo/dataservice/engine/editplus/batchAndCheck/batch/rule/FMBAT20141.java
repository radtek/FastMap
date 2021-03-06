package com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.navinfo.dataservice.api.metadata.iface.MetadataApi;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.commons.util.ExcelReader;
import com.navinfo.dataservice.dao.plus.model.basic.OperationType;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.selector.custom.IxPoiSelector;
/**
 * 当标准化简体中文名称（names-langCode="CHI",names-type=1）更新NAME时，
 * 将标准化简体中文NAME字段转拼音并赋值到对应的NAME_PHONETIC中
 * @author gaopengrong
 */
public class FMBAT20141 extends BasicBatchRule {
	private Map<Long,Long> pidAdminId;
	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList)
			throws Exception {
		Set<Long> pidList=new HashSet<Long>();
		for(BasicObj obj:batchDataList){
			pidList.add(obj.objPid());
		}
		pidAdminId = IxPoiSelector.getAdminIdByPids(getBatchRuleCommand().getConn(), pidList);
	}

	@Override
	public void runBatch(BasicObj obj) throws Exception {
		IxPoiObj poiObj=(IxPoiObj) obj;
		String adminCode=null;
		if(pidAdminId!=null&&pidAdminId.containsKey(poiObj.getMainrow().getObjPid())){
			adminCode=pidAdminId.get(poiObj.getMainrow().getObjPid()).toString();
		}
		//存在IX_POI_NAME标准化中文名称，新增或者修改履历
		List<IxPoiName> names = poiObj.getStandardCHIName();
		MetadataApi metadataApi=(MetadataApi) ApplicationContextUtil.getBean("metadataApi");
		for (IxPoiName name:names) {
			if(name.getHisOpType().equals(OperationType.INSERT)){
				String newName=name.getName();
				//批拼音
				String fullName = ExcelReader.h2f(newName);
				name.setNamePhonetic(metadataApi.pyConvert(fullName,adminCode,null));
				
			} else if (name.getHisOpType().equals(OperationType.UPDATE)&&name.hisOldValueContains(IxPoiName.NAME)) {
				String oldName=(String) name.getHisOldValue(IxPoiName.NAME);
				String newName=name.getName();
				if(!newName.equals(oldName)){
					//批拼音
					name.setNamePhonetic(metadataApi.pyConvert(newName,adminCode,null));
				}
			}
		}
	}

}
