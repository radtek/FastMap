package com.navinfo.dataservice.engine.check.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkSpeedlimit;
import com.navinfo.dataservice.engine.check.core.baseRule;
import com.navinfo.dataservice.engine.check.helper.DatabaseOperator;

/**
 * @ClassName GLM56034
 * @author Han Shaoming
 * @date 2017年1月5日 下午5:53:11
 * @Description TODO
 * 限速条件为时间限制/季节时段，时间段必须有值，否则报log
 * 条件线限速时间段	服务端后检查
 * 条件线限速限速条件	服务端后检查
 */
public class GLM56034 extends baseRule {

	@Override
	public void preCheck(CheckCommand checkCommand) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postCheck(CheckCommand checkCommand) throws Exception {
		// TODO Auto-generated method stub
		for(IRow row:checkCommand.getGlmList()){
			//条件线限速时间段,条件线限速限速条件
			if (row instanceof RdLinkSpeedlimit){
				RdLinkSpeedlimit rdLinkSpeedlimit = (RdLinkSpeedlimit) row;
				this.checkRdLinkSpeedlimit(rdLinkSpeedlimit);
			}
		}
	}

	/**
	 * @author Han Shaoming
	 * @param rdSpeedlimit
	 * @throws Exception 
	 */
	private void checkRdLinkSpeedlimit(RdLinkSpeedlimit rdLinkSpeedlimit) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> changedFields = rdLinkSpeedlimit.changedFields();
		if(!changedFields.isEmpty()){
			//条件线限速时间段
			if(changedFields.containsKey("timeDomain")){
				String timeDomain = (String) changedFields.get("timeDomain");
				if("[]".equals(timeDomain) || timeDomain == null){
					boolean check = this.check(rdLinkSpeedlimit.getLinkPid());
					
					if(check){
						String target = "[RD_LINK," + rdLinkSpeedlimit.getLinkPid() + "]";
						this.setCheckResult("", target, 0);
					}
				}
			}
			//条件线限速限速条件
			if(changedFields.containsKey("speedDependent")){
				int speedDependent = (int) changedFields.get("speedDependent");
				if(speedDependent == 10 || speedDependent == 12){
					boolean check = this.check(rdLinkSpeedlimit.getLinkPid());
					
					if(check){
						String target = "[RD_LINK," + rdLinkSpeedlimit.getLinkPid() + "]";
						this.setCheckResult("", target, 0);
					}
				}
			}
		}
	}
	
	/**
	 * @author Han Shaoming
	 * @param rdNodeForm
	 * @throws Exception 
	 */
	private boolean check(int pid) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		StringBuilder sb = new StringBuilder();
		   
		sb.append("SELECT DISTINCT RLS.LINK_PID FROM RD_LINK_SPEEDLIMIT RLS");
		sb.append(" WHERE RLS.LINK_PID = "+pid);
		sb.append(" AND RLS.U_RECORD <>2 AND RLS.SPEED_DEPENDENT IN (10,12)");
		sb.append(" AND (RLS.TIME_DOMAIN ='[]' OR RLS.TIME_DOMAIN IS NULL)");
		String sql = sb.toString();
		log.info("后检查GLM56034--sql:" + sql);
		
		DatabaseOperator getObj = new DatabaseOperator();
		List<Object> resultList = new ArrayList<Object>();
		resultList = getObj.exeSelect(this.getConn(), sql);
		
		if(!resultList.isEmpty()){
			flag = true;
		}
		return flag;
	}
}
