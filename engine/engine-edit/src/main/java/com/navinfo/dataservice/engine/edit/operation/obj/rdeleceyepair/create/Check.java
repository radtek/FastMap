package com.navinfo.dataservice.engine.edit.operation.obj.rdeleceyepair.create;

import com.navinfo.dataservice.dao.glm.model.rd.eleceye.RdElectroniceye;

public class Check {

	public void checkRdEleceyePair(Command command) throws Exception {
		RdElectroniceye startEleceye = command.getEntryEleceye();
		RdElectroniceye endEleceye = command.getEntryEleceye();

		if (null == startEleceye || null == endEleceye)
			throw new Exception("匹配限速配对电子眼必须选择两个电子眼");

		int startKind = startEleceye.getKind();
		int endKind = endEleceye.getKind();

		if (Command.ENTRY_KIND != startKind || Command.EXIT_KIND != endKind)
			throw new Exception("匹配限速配对电子眼所选电子眼类型不对");
	}
}
