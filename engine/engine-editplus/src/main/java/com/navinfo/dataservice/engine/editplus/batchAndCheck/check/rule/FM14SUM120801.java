package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.common.CheckUtil;
/**
 * FM-14Sum-12-08-01	名称中空格错误	DHM	
 * 检查条件：Lifecycle为“0（无）\1（删除）\4（验证）”不检查；
 * 检查原则：
 * 1、名称（name）字段内容中存在空格，且空格前后若为以下组合，将Err的情况，程序报出；---见空格规则表
 * 2、前后空格检查：不能以空格开头或结尾；
 * 3、多个空格检查：不能出现连续空格；
 * 4、回车符检查：不能包含回车符；
 * 5、Tab符检查：不能包含Tab符号；
 * 以上查出的问题有几种情况报几个log。
 * 排除：空格前后的字或词一样时，不用报Log。
 * @author zhangxiaoyi
 *
 */
public class FM14SUM120801 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			IxPoiName nameObj = poiObj.getOfficeOriginCHName();
			if(nameObj==null){return;}
			String nameStr = nameObj.getName();
			if(nameStr==null||nameStr.isEmpty()){return;}
			String nameTmp=CheckUtil.strQ2B(nameStr);
			List<String> errorList=new ArrayList<String>();
			errorList=CheckUtil.checkIllegalBlank(nameTmp);
			if(!CheckUtil.blankRuleTable(nameTmp)){
				errorList.add("非法空格");
			}
			if(errorList.size()>0){
				setCheckResult(poi.getGeometry(), poiObj, poi.getMeshId(),
						"名称空格错误："+errorList.toString().replace("[", "").replace("]", ""));
				return;
			}
		}
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList)
			throws Exception {
	}

}
