package com.navinfo.dataservice.engine.check.rules;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.engine.check.core.baseRule;

/**
 * Created by Crayeres on 2017/2/17.
 */
public class GLM01357 extends baseRule {
    @Override
    public void preCheck(CheckCommand checkCommand) throws Exception {

    }

    @Override
    public void postCheck(CheckCommand checkCommand) throws Exception {
        for (IRow row : checkCommand.getGlmList()) {
            if (row instanceof RdLink && row.status() == ObjStatus.UPDATE) {
                RdLink link = (RdLink) row;

                int direct = link.getDirect();
                if (link.changedFields().containsKey("direct"))
                    direct = Integer.valueOf(link.changedFields().get("direct").toString());

                if (direct == 2 || direct == 3) {
                    int laneLeft = link.getLaneLeft();
                    if (link.changedFields().containsKey("laneLeft"))
                        laneLeft = Integer.valueOf(link.changedFields().get("laneLeft").toString());

                    int laneRight = link.getLaneRight();
                    if (link.changedFields().containsKey("laneRight"))
                        laneRight = Integer.valueOf(link.changedFields().get("laneRight").toString());

                    if (laneLeft != 0 || laneRight != 0) {
                        setCheckResult(link.getGeometry(), "[RD_LINK," + link.pid() + "]", link.mesh());
                    }
                }
            }
        }
    }
}
