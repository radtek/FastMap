package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.Collection;
import java.util.List;

import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiChargingplot;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.common.CheckUtil;

/**
 * @ClassName FMYW20211
 * @author Han Shaoming
 * @date 2017年2月13日 下午3:26:04
 * @Description TODO
 * 检查条件：    lifecycle！=1
 * 检查原则：
 * 1、充电桩（分类为230227）的充电功率（chargingPole-power）大于100时，
 * 报log：充电桩充电功率大于100KW，请确认是否正确！
 */
public class FMYW20211 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if(obj.objName().equals(ObjectName.IX_POI)){
			IxPoiObj poiObj=(IxPoiObj) obj;
			IxPoi poi=(IxPoi) poiObj.getMainrow();
			String kindCode = poi.getKindCode();
			if(kindCode == null || !"230227".equals(kindCode)){return;}
			List<IxPoiChargingplot> ixPoiChargingPlots = poiObj.getIxPoiChargingplots();
			//错误数据
			if(ixPoiChargingPlots==null || ixPoiChargingPlots.isEmpty()){return;}
			for (IxPoiChargingplot ixPoiChargingPlot : ixPoiChargingPlots) {
				String plotPower = ixPoiChargingPlot.getPower();
				if(plotPower != null){
					if(CheckUtil.isNumber(plotPower)){
						Double power = Double.parseDouble(plotPower);
						if(power > 100){
							setCheckResult(poi.getGeometry(), poiObj,poi.getMeshId(), null);
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList) throws Exception {
		// TODO Auto-generated method stub

	}

}
