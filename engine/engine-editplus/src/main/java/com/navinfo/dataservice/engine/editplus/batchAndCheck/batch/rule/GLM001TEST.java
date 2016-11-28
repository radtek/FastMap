package com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.rule;

import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.dao.plus.model.basic.BasicRow;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;

public class GLM001TEST extends BasicBatchRule {

	public GLM001TEST() {
	}

	@Override
	public void runBatch(String objName, BasicObj obj) throws Exception {
		if(objName.equals("IX_POI")){
			IxPoi poiObj=(IxPoi) obj.getMainrow();
			Map<String, Object> oldValueMap=poiObj.getOldValues();
			if(!oldValueMap.containsKey("KIND_CODE")){return;}
			poiObj.setKindCode("test124");
			List<BasicRow> subRows=obj.getRowsByName("IX_POI_NAME");
			for(BasicRow br:subRows){
				if(br.getObjType().equals("UPDATE")){
				}
			}
			IxPoiObj ixpoiObj = (IxPoiObj)obj;
			IxPoiName name = ixpoiObj.createIxPoiName();
			name.setLangCode("CHI");
		}else if(objName.equals("IX_POI_NAME")){}
	}

}
