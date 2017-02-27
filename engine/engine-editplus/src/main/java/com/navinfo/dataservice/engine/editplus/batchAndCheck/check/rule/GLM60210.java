package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.dataservice.dao.plus.selector.custom.IxPoiSelector;

/**
 * @ClassName GLM60210
 * @author Han Shaoming
 * @date 2017年2月20日 下午8:51:24
 * @Description TODO
 * 检查条件： 非删除POI对象且存在同一关系
 * 检查原则：
 * 同一个POI不应制作多组同一关系，否则报log：同一个POI不应制作多组同一关系！
 */
public class GLM60210 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			long pid = poi.getPid();
			List<Long> pids = new ArrayList<Long>();
			pids.add(pid);
			List<Long> samePoiGroupIdsByPids = IxPoiSelector.getIxSamePoiGroupIdsByPids(this.getCheckRuleCommand().getConn(), pids);
			if(samePoiGroupIdsByPids.size() >1){
				setCheckResult(poi.getGeometry(), poiObj,poi.getMeshId(), null);
				return;
			}
		}
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList) throws Exception {
		// TODO Auto-generated method stub

	}

}