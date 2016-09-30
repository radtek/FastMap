package com.navinfo.dataservice.engine.edit.operation.topo.depart.departrdnode;

import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;

import java.sql.Connection;
import java.util.List;

/**
 * 维护IxPoi对象
 * Created by chaixin on 2016/9/21 0021.
 */
public class OpRefRdSamelink {
    private Connection conn;

    public OpRefRdSamelink(Connection conn) {
        this.conn = conn;
    }

    public void updateRelation(Command command, List<RdLink> newLinks, Result result) throws Exception {
        com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.depart.Operation operation = new
                com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.depart.Operation(this.conn);
        operation.depart(command.getNodePid(), command.getRdLink(), newLinks, result, command.getRequester());
    }
}
