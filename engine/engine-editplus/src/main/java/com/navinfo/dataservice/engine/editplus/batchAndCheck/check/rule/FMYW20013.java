package com.navinfo.dataservice.engine.editplus.batchAndCheck.check.rule;

import java.util.Collection;
import java.util.List;

import com.navinfo.dataservice.dao.plus.model.basic.OperationType;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.obj.IxPoiObj;
import com.navinfo.dataservice.dao.plus.obj.ObjectName;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.common.CheckUtil;

/**
 * FM-YW-20-013	中文即是英文作业	DHM	
 * 检查条件：
 *     以下条件(1)、(2)、(3)之一，且与(4)同时满足时，需要进行检查：
 *     (1)存在官方原始中文名称IX_POI_NAME的新增；
 *     (2)存在官方原始中文名称IX_POI_NAME的修改；
 *     (3)存在IX_POI修改且存在KIND_CODE修改；
 *     (4)LANG_CODE="CHI"时，NAME只包含{0-9、a-b、A-B、符号}；
 * 检查原则：
 *     满足条件的POI全部报出。
 *     提示：中文即是英文作业
 * @author zhangxiaoyi
 */
public class FMYW20013 extends BasicCheckRule {

	@Override
	public void runCheck(BasicObj obj) throws Exception {
		if (obj.objName().equals(ObjectName.IX_POI)) {
			IxPoiObj poiObj = (IxPoiObj) obj;
			if (!isCheck(poiObj)) {
				return;
			}
			IxPoi poi = (IxPoi) poiObj.getMainrow();
			List<IxPoiName> names = poiObj.getIxPoiNames();
			if (names == null || names.size() == 0) {
				return;
			}
			for (IxPoiName nameTmp : names) {
				if ("CHI".equals(nameTmp.getLangCode())) {
					String nameStr = nameTmp.getName();
					if (nameStr == null || nameStr.isEmpty()) {
						continue;
					}
					boolean hasChinese = false;
					for (char c : nameStr.toCharArray()) {
						if (CheckUtil.isChinese(String.valueOf(c))) {
							hasChinese = true;
							break;
						}
					}
					if (!hasChinese) {
						setCheckResult(poi.getGeometry(), poiObj, poi.getMeshId(), null);
						return;
					}
				}
			}
		}
	}

	/**
	 * 以下条件(1)、(2)、(3)之一，且与(4)同时满足时，需要进行检查：
	 *     (1)存在官方原始中文名称IX_POI_NAME的新增；
	 *     (2)存在官方原始中文名称IX_POI_NAME的修改；
	 *     (3)存在IX_POI修改且存在KIND_CODE修改；
	 * @param poiObj
	 * @return
	 * @throws Exception
	 */
	private boolean isCheck(IxPoiObj poiObj) throws Exception {
		IxPoi poi = (IxPoi) poiObj.getMainrow();
		String newKindCode = poi.getKindCode();
		if (poi.hisOldValueContains(IxPoi.KIND_CODE)) {
			String oldKindCode = "";
			if (poi.getHisOldValue(IxPoi.KIND_CODE) != null) {
				oldKindCode = (String) poi.getHisOldValue(IxPoi.KIND_CODE);
			}
			if (!newKindCode.equals(oldKindCode)) {
				return true;
			}
		}

		// (1)存在官方原始中文名称IX_POI_NAME的新增；(2)存在官方原始中文名称IX_POI_NAME的修改；
		IxPoiName officeOriginCHIName = poiObj.getOfficeOriginCHIName();

		if (officeOriginCHIName.getHisOpType().equals(OperationType.INSERT)) {
			return true;
		}
		if (officeOriginCHIName.getHisOpType().equals(OperationType.UPDATE) && officeOriginCHIName.hisOldValueContains(IxPoiName.NAME)) {
			String oldName = (String) officeOriginCHIName.getHisOldValue(IxPoiName.NAME);
			String newName = officeOriginCHIName.getName();
			if (!newName.equals(oldName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void loadReferDatas(Collection<BasicObj> batchDataList) throws Exception {
		// TODO Auto-generated method stub
	}

}
