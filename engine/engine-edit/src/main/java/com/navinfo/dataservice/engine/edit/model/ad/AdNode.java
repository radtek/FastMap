package com.navinfo.dataservice.engine.edit.model.ad;

import java.util.List;
import java.util.Map;

import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.engine.edit.model.AbstractNode;
import com.navinfo.dataservice.engine.edit.model.BasicObj;
import com.navinfo.dataservice.engine.edit.model.BasicRow;

/** 
 * @ClassName: AdNode
 * @author xiaoxiaowen4127
 * @date 2016年8月18日
 * @Description: AdNode.java
 */
public class AdNode extends AbstractNode {

	@Override
	public String primaryKey() {
		return "NODE_PID";
	}

	@Override
	public Map<Class<? extends BasicRow>, List<BasicRow>> childRows() {
		return null;
	}

	@Override
	public Map<Class<? extends BasicObj>, List<BasicObj>> childObjs() {
		return null;
	}


	@Override
	public String tableName() {
		return "AD_NODE";
	}


	@Override
	public ObjType objType() {
		return ObjType.ADNODE;
	}

}
