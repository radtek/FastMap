/**
 * 
 */
package com.navinfo.dataservice.engine.check.rules;

import java.util.ArrayList;
import java.util.List;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdTmclocation;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdTmclocationLink;
import com.navinfo.dataservice.engine.check.core.NiValException;
import com.navinfo.dataservice.engine.check.core.baseRule;
import com.navinfo.dataservice.engine.check.helper.DatabaseOperator;

/**
 * (删除)
 * @ClassName: PERMIT_CHECK_TMC_NUM
 * @author Zhang Xiaolong
 * @date 2016年11月25日 下午4:10:27
 * @Description: 同一个TMC点和同一个link组成的两组匹配信息中，“位置方向”只允许一个为P，一个为N
 */
public class CheckTmcpointDirectPn extends baseRule {

	@Override
	public void preCheck(CheckCommand checkCommand) throws Exception {
	}

	@Override
	public void postCheck(CheckCommand checkCommand) throws Exception {
		for (IRow obj : checkCommand.getGlmList()) {
			if (obj instanceof RdTmclocation) {
				RdTmclocation tmclocation = (RdTmclocation) obj;

				List<IRow> tmcLocationLinks = tmclocation.getLinks();

				for (IRow row : tmcLocationLinks) {
					checkTmcLocDirect(row);
				}
			} else if (obj instanceof RdTmclocationLink) {
				checkTmcLocDirect(obj);
			}
		}
	}

	private void checkTmcLocDirect(IRow row) throws Exception {
		RdTmclocationLink link = (RdTmclocationLink) row;
		if (link.changedFields().containsKey("locDirect")) {
			int locDirect = (int) link.changedFields().get("locDirect");

			if (locDirect == 3 || locDirect == 4) {
				StringBuilder sb = new StringBuilder();

				sb.append(
						"with tmp1 as(     select tmc_id,GROUP_ID from RD_TMCLOCATION where GROUP_ID = "+link.getGroupId()+" and U_RECORD !=2 ),tmp2 as (     select a.GROUP_ID,b.link_pid  from     tmp1 a,RD_TMCLOCATION_LINK b     WHERE a.GROUP_ID = b.GROUP_ID AND 	b.U_RECORD !=2 	 AND 	b.LINK_PID = ");
				sb.append(link.getLinkPid());
				sb.append(" and b.LOC_DIRECT =");
				sb.append(locDirect);
				sb.append(
						" GROUP BY b.LINK_PID,a.TMC_ID,a.GROUP_ID HAVING COUNT(1) >0 ) select /*+index(c link_pid)*/ c.GEOMETRY, '[RD_TMCLOCATION,' || tmp2.group_id || ']' TARGET, c.MESH_ID FROM tmp2,RD_LINK c WHERE tmp2.link_pid = c.LINK_PID  and c.U_RECORD !=2");
				String sql = sb.toString();

				DatabaseOperator getObj = new DatabaseOperator();
				List<NiValException> resultList = new ArrayList<NiValException>();
				resultList = getObj.getNiValExceptionFromSql(this.getConn(), sql);

				if (resultList.size() > 0) {
					for (NiValException niValException : resultList) {
						// 设置好ruleId和log
						niValException.setRuleId(this.getRuleCode());

						niValException.setInformation(this.getRuleLog());

						this.setCheckResult(niValException);
					}
					return;
				}
			}
		}
	}
}
