package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.Collection;
import java.util.List;

import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
/**
 * FM-YW-20-050	NAME非空检查	DHM	
 * 检查条件：该POI发生变更(新增或修改主子表、删除子表)；
 * 检查原则：当LANG_CODE=""ENG""时，NAME不能为空，否则报出：**名称不能为空
 * 备注：**是NAME_TYPE+NAME_CLASS+LANG_CODE的中文描述（如：标准化官方英文，标准化简称中文等等）
 * @author zhangxiaoyi
 */
public class FMYW20050 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			List<IxPoiName> names = poiObj.getIxPoiNames();
			if(names==null||names.size()==0){return;}
			for(IxPoiName nameTmp:names){
				if(nameTmp.isEng()){
					String name=nameTmp.getName();
					if(name==null||name.isEmpty()){
						setCheckResult(poi.getGeometry(), poiObj, poi.getMeshId(), 
								nameTmp.getTypeName()+nameTmp.getClassName()+"英文名称不能为空");
						return;
					}
				}
			}
		}
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
