package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
/**
 * FM-GLM60178	英葡文拼音为空检查	DHM
 * 检查条件：该POI发生变更(新增或修改主子表、删除子表)；
 * 检查原则：POI语言代码不是CHI和CHT的名称，相应的拼音应为空，否则报出：非中文名称的拼音不为空
 * @author zhangxiaoyi
 */
public class FMGLM60178 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			List<IxPoiName> names = poiObj.getIxPoiNames();
			if(names==null||names.size()==0){return;}
			for(IxPoiName nameTmp:names){
				if(!nameTmp.isCH()){
					String nameStr=nameTmp.getNamePhonetic();
					if(!(nameStr==null||nameStr.isEmpty())){
						setCheckResult(poi.getGeometry(), poiObj, poi.getMeshId(),null);
                        return;}
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
