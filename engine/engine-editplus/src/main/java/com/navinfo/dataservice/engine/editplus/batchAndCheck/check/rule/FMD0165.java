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
 * 	FM-D01-65	官方标准英文字符集检查		DHM
	 检查条件：
	   非删除POI对象；
	检查原则：
	不允许出现官方标准英文仅为“_-/:;~'".^,?!*()<>$%&+@#及半角空格”组成的名称，否则报出log：不允许出现英文仅为“_-/:;~'".^,?!*()<>$%&+@#及半角空格”组成的名称！
	检查名称：官方标准英文名称（name_type=1，name_class=1，langCode=ENG）
 * @author sunjiawei
 */
public class FMD0165 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			List<IxPoiName> names = poiObj.getIxPoiNames();
			if(names==null||names.size()==0){return;}
			for(IxPoiName nameTmp:names){
				if(nameTmp.isEng()&&nameTmp.getNameClass()==1&&nameTmp.getNameType()==1){
					String nameStr = nameTmp.getName();
					if(nameStr==null||nameStr.isEmpty()){continue;}
					Pattern p = Pattern.compile("^[_\\-/:;~'\".^,?!*()<>$%&+@# ]+$");
					Matcher m = p.matcher(nameStr);
					if(m.matches()){
						setCheckResult(poi.getGeometry(), poiObj, poi.getMeshId(), null);}
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
