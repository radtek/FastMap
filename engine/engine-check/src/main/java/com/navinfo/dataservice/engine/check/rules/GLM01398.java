package com.navinfo.dataservice.engine.check.rules;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkLimit;
import com.navinfo.dataservice.engine.check.core.baseRule;

/**
 * Created by Crayeres on 2017/2/8.
 */
public class GLM01398 extends baseRule {
    @Override
    public void preCheck(CheckCommand checkCommand) throws Exception {

    }

    @Override
    public void postCheck(CheckCommand checkCommand) throws Exception {
        for (IRow row : checkCommand.getGlmList()) {
            if (row instanceof RdLinkLimit && row.status() != ObjStatus.DELETE) {
                RdLinkLimit limit = (RdLinkLimit) row;

                int type = limit.getType();
                if (limit.changedFields().containsKey("type"))
                    type = Integer.valueOf(limit.changedFields().get("type").toString());

                if (type == 6) {
                    int tollType = limit.getTollType();
                    if (limit.changedFields().containsKey("tollType"))
                        tollType = Integer.valueOf(limit.changedFields().get("tollType").toString());

                    if (tollType != 2 && tollType != 3) {
                        setCheckResult("", "[RD_LINK," + limit.getLinkPid() + "]", 0);
                    }
                }
            }
        }
    }
}
