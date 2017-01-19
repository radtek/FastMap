package com.navinfo.dataservice.engine.check.rules;

import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneFace;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneNode;
import com.navinfo.dataservice.dao.glm.selector.ad.zone.ZoneFaceSelector;
import com.navinfo.dataservice.engine.check.core.baseRule;

import java.util.List;

/**
 * Created by chaixin on 2017/1/19 0019.
 */
public class PermitModificatePolygonEndpoint extends baseRule {
    @Override
    public void preCheck(CheckCommand checkCommand) throws Exception {
        for (IRow nodeRow : checkCommand.getGlmList()) {
            if (nodeRow instanceof ZoneNode) {
                ZoneNode node = (ZoneNode) nodeRow;
                if (!node.changedFields().containsKey("geometry")) continue;

                ZoneFaceSelector selector = new ZoneFaceSelector(getConn());
                List<ZoneFace> faces = selector.loadZoneFaceByNodeId(node.pid(), false);
                if (faces.size() > 0)
                    setCheckResult("", "", 0);
            }
        }
    }

    @Override
    public void postCheck(CheckCommand checkCommand) throws Exception {

    }
}
