package com.navinfo.dataservice.engine.editplus.batchAndCheck.batch;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.druid.support.logging.Log;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.rule.BasicBatchRule;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.check.Check;
import com.navinfo.dataservice.engine.editplus.model.batchAndCheck.BatchRuleCommand;
import com.navinfo.dataservice.engine.editplus.model.batchAndCheck.BatchRule;

public class BatchExcuter {
	private static Logger log = Logger.getLogger(BatchExcuter.class);
	//删除数据,也要执行批处理的规则
	private static Set<String>  batchDelReules=new HashSet<String>(){{
		add("FM-BAT-D20-004");
		add("FM-BAT-D20-006");
		add("FM-BAT-20-187-1");
	}};
	public BatchExcuter() {
		// TODO Auto-generated constructor stub
	}
	
	public void exeRule(BatchRule batchRule,BatchRuleCommand batchRuleCommand) throws Exception{
		log.info("start run rule="+batchRule.getRuleId());
		if(batchRule.getAccessorType().equals("JAVA")){
			exeJavaRule(batchRule, batchRuleCommand);
		}
		log.info("end run rule="+batchRule.getRuleId());
	}
	
	public void exeJavaRule(BatchRule batchRule,BatchRuleCommand batchRuleCommand) throws Exception{
		BasicBatchRule ruleObj=(BasicBatchRule) batchRule.getAccessorClass().newInstance();
		ruleObj.setBatchRuleCommand(batchRuleCommand);
		ruleObj.setBatchRule(batchRule);
		if(batchDelReules.contains(batchRule.getRuleId())){
			ruleObj.setBatchDelData(true);
		}
		ruleObj.run();
	}

}
