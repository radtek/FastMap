package com.navinfo.dataservice.engine.check.rules;

import java.util.ArrayList;
import java.util.List;
import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.model.rd.directroute.RdDirectroute;
import com.navinfo.dataservice.dao.glm.model.rd.voiceguide.RdVoiceguide;
import com.navinfo.dataservice.engine.check.core.baseRule;
import com.navinfo.dataservice.engine.check.helper.DatabaseOperator;

/**
 * @ClassName GLM18005
 * @author Han Shaoming
 * @date 2017年1月4日 下午3:14:05
 * @Description TODO
 * 路口的限制信息数组中不能同时存在顺行和路口语音引导
 * 关系类型编辑（语音引导详细信息表）服务端后检查
 * 新增语音引导服务端后检查
 */
public class GLM18005 extends baseRule {

	@Override
	public void preCheck(CheckCommand checkCommand) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postCheck(CheckCommand checkCommand) throws Exception {
		// TODO Auto-generated method stub
		for(IRow row:checkCommand.getGlmList()){
			//新增顺行
			if (row instanceof RdDirectroute){
				RdDirectroute rdDirectroute = (RdDirectroute) row;
				this.checkRdDirectroute(rdDirectroute);
			}
			//新增语音引导
			else if (row instanceof RdVoiceguide){
				RdVoiceguide rdVoiceguide = (RdVoiceguide) row;
				this.checkRdVoiceguide(rdVoiceguide);
			}
		}
	}

	/**
	 * @author Han Shaoming
	 * @param rdVoiceguide
	 * @throws Exception 
	 */
	private void checkRdVoiceguide(RdVoiceguide rdVoiceguide) throws Exception {
		// TODO Auto-generated method stub
		//判断新增
		if(ObjStatus.INSERT.equals(rdVoiceguide.status())){
			boolean check = this.check(rdVoiceguide.getPid());
			
			if(check){
				String target = "[RD_VOICEGUIDE," + rdVoiceguide.getPid() + "]";
				this.setCheckResult("", target, 0);
			}
			
		}
	}

	/**
	 * @author Han Shaoming
	 * @param rdDirectroute
	 * @throws Exception 
	 */
	private void checkRdDirectroute(RdDirectroute rdDirectroute) throws Exception {
		// TODO Auto-generated method stub
		if(ObjStatus.INSERT.equals(rdDirectroute.status())){
			StringBuilder sb = new StringBuilder();
			
			sb.append("SELECT RD.PID FROM RD_VOICEGUIDE RV, RD_VOICEGUIDE_DETAIL RVD, RD_DIRECTROUTE RD");
			sb.append(" WHERE RD.PID = "+rdDirectroute.getPid());
			//sb.append(" AND RD.RELATIONSHIP_TYPE = 1 AND RVD.RELATIONSHIP_TYPE = 1");
			sb.append(" AND RV.PID = RVD.VOICEGUIDE_PID");
			sb.append(" AND RV.IN_LINK_PID = RD.IN_LINK_PID AND RV.NODE_PID = RD.NODE_PID");
			sb.append(" AND RVD.OUT_LINK_PID = RD.OUT_LINK_PID AND RV.U_RECORD <> 2");
			sb.append(" AND RVD.U_RECORD <> 2 AND RD.U_RECORD <> 2");
			String sql = sb.toString();
			log.info("后检查GLM18005--sql:" + sql);
			
			DatabaseOperator getObj = new DatabaseOperator();
			List<Object> resultList = new ArrayList<Object>();
			resultList = getObj.exeSelect(this.getConn(), sql);
			
			if(!resultList.isEmpty()){
				String target = "[RD_DIRECTROUTE," + rdDirectroute.getPid() + "]";
				this.setCheckResult("", target, 0);
			}
		}
	}
	
	/**
	 * @author Han Shaoming
	 * @param rdNodeForm
	 * @throws Exception 
	 */
	private boolean check(int voiceguidePid) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT RV.PID FROM RD_VOICEGUIDE RV, RD_VOICEGUIDE_DETAIL RVD, RD_DIRECTROUTE RD");
		sb.append(" WHERE RV.PID = "+voiceguidePid);
		//sb.append(" AND RD.RELATIONSHIP_TYPE = 1 AND RVD.RELATIONSHIP_TYPE = 1");
		sb.append(" AND RV.PID = RVD.VOICEGUIDE_PID");
		sb.append(" AND RV.IN_LINK_PID = RD.IN_LINK_PID AND RV.NODE_PID = RD.NODE_PID");
		sb.append(" AND RVD.OUT_LINK_PID = RD.OUT_LINK_PID AND RV.U_RECORD <> 2");
		sb.append(" AND RVD.U_RECORD <> 2 AND RD.U_RECORD <> 2");
		String sql = sb.toString();
		log.info("后检查GLM18005--sql:" + sql);
		
		DatabaseOperator getObj = new DatabaseOperator();
		List<Object> resultList = new ArrayList<Object>();
		resultList = getObj.exeSelect(this.getConn(), sql);
		
		if(!resultList.isEmpty()){
			flag = true;
		}
		return flag;
	}

}
