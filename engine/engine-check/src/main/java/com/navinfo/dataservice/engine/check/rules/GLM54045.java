/**
 * 
 */
package com.navinfo.dataservice.engine.check.rules;

import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdTmclocation;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdTmclocationLink;
import com.navinfo.dataservice.dao.glm.selector.rd.link.RdLinkSelector;
import com.navinfo.dataservice.engine.check.core.baseRule;

/**
 * @ClassName: GLM54045
 * @author Zhang Xiaolong
 * @date 2016年11月25日 下午4:10:27
 * @Description: TMC匹配信息中的“位置方向”不能是“初始值”
 */
public class GLM54045 extends baseRule {

	@Override
	public void preCheck(CheckCommand checkCommand) throws Exception {
	}

	@Override
	public void postCheck(CheckCommand checkCommand) throws Exception {
		RdLinkSelector selector = new RdLinkSelector(getConn());
		for (IRow obj : checkCommand.getGlmList()) {
			//新增rdtmclocation对象，检查子表数据
			if (obj instanceof RdTmclocation) {
				RdTmclocation tmclocation = (RdTmclocation) obj;
				List<IRow> tmcLocationLinks = tmclocation.getLinks();
				for (IRow row : tmcLocationLinks) {
					setCheckResult(row,selector);
				}
			}
			//TMC位置方向编辑
			else if(obj instanceof RdTmclocationLink){
				RdTmclocationLink rdTmclocationLink = (RdTmclocationLink) obj;
				this.checkRdTmclocationLink(rdTmclocationLink);
			}
		}
	}
	
	/**
	 * @author Han Shaoming
	 * @param rdTmclocationLink
	 * @throws Exception 
	 */
	private void checkRdTmclocationLink(RdTmclocationLink rdTmclocationLink) throws Exception {
		if(ObjStatus.UPDATE.equals(rdTmclocationLink.status())){
			Map<String, Object> changedFields = rdTmclocationLink.changedFields();
			if(changedFields != null && !changedFields.isEmpty()){
				if(changedFields.containsKey("locDirect")){
					int  locDirect = (int) changedFields.get("locDirect");
					if(locDirect == 0){
						String target = "[RD_TMCLOCATION," + rdTmclocationLink.getGroupId() + "]";
						this.setCheckResult("", target, 0);
					}
				}
			}
		}
		
	}
	
	/**
	 * 设置检查结果
	 * @param row rdtmclocationlink子表对象
	 * @param selector
	 * @throws Exception
	 */
	private void setCheckResult(IRow row,RdLinkSelector selector) throws Exception
	{
		RdTmclocationLink link = (RdTmclocationLink) row;
		
		//如果新增的rdtmclocationlink的位置方向为0，则报log
		if(link.getLocDirect() == 0)
		{
			RdLink rdLink = (RdLink) selector.loadByIdOnlyRdLink(link.getLinkPid(), true);
			
			this.setCheckResult(rdLink.getGeometry(), "[RD_TMCLOCATION,"+link.getGroupId()+"]", rdLink.getMeshId());
			return;
		}
	}
}
