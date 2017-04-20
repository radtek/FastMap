package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
/**
 *  GLM60439 		数字别名与中文名称对照检查		DHM
	检查对象：
	非删除POI对象
	检查原则：
	官方别名中文名称(name_type=1,name_class=3,lang_code=CHI或CHT)中如果包含三个及三个以上连续的阿拉伯数字“０到９”，
	需要将“０到９(全角)”转化成中文数字“〇（零）到九”，和官方标准化中文名称对比，
	不匹配报LOG：数字别名与官方标准化中文不匹配！
	此处曾用名不检查；
 * @author sunjiawei
 */
public class GLM60439 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			List<IxPoiName> names = poiObj.getIxPoiNames();
			if(names==null||names.size()==0){return;}
			for(IxPoiName nameTmp:names){
				if(nameTmp.isCH()){
					String name=nameTmp.getName();
					IxPoiName aliaPoiName = poiObj.getAliasCHIName(0);
					if(name==null||name.isEmpty()){continue;}
					if(nameTmp.isUsedName()){continue;}
					Pattern p = Pattern.compile(".*[\uFF10-\uFF19]{3,}.*"); //三个及三个以上连续的阿拉伯数字“０到９”（全角）
					String convertNumber = "";
					String aliaName = aliaPoiName.getName();
					if(p.matcher(aliaName).matches()){
						Pattern p1 = Pattern.compile("[^\uFF10-\uFF19]{3,}");
						String beforeConvertNumber = p1.matcher(aliaName).replaceAll("").trim();
						convertNumber = SBC2Chinese(beforeConvertNumber);
					}
					if(nameTmp.isOfficeName()&&nameTmp.isStandardName()){
						if(!name.contains(convertNumber)){
							setCheckResult(poi.getGeometry(), poiObj, poi.getMeshId(), null);
							return;
						}
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

	/**
	 * 全角转中文
	 * @param str
	 * @return
	 */
	public String SBC2Chinese(String str){
		char[] array = str.toCharArray();
		String convertStr = "";
		for (char c : array) {
			switch(c)
			{
				case '１':convertStr+="一";break;
				case '２':convertStr+="二";break;
				case '３':convertStr+="三";break;
				case '４':convertStr+="四";break;
				case '５':convertStr+="五";break;
				case '６':convertStr+="六";break;
				case '７':convertStr+="七";break;
				case '８':convertStr+="八";break;
				case '９':convertStr+="九";break;
				case '０':convertStr+="〇";break;
			}
		}
		return convertStr;
	}
	
	
}
