package com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.depart;

import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.navinfo.dataservice.dao.glm.model.rd.trafficsignal.RdTrafficsignal;
import com.navinfo.dataservice.dao.glm.selector.rd.trafficsignal.RdTrafficsignalSelector;
import com.navinfo.dataservice.engine.edit.utils.CalLinkOperateUtils;

import java.sql.Connection;
import java.util.*;

/**
 * Created by chaixin on 2016/10/9 0009.
 */
public class Operation {

    private Connection conn;

    public Operation(Connection conn) {
        this.conn = conn;
    }

    /**
     * 维护上下线分离对信号灯的影响
     *
     * @param links      分离线
     * @param leftLinks  分离后左线
     * @param rightLinks 分离后右线
     * @param result     结果集
     * @return
     * @throws Exception
     */
    public String updownDepart(List<RdLink> links, Map<Integer, RdLink> leftLinks, Map<Integer, RdLink> rightLinks, RdNode sNode, RdNode eNode, Result result) throws Exception {
        RdTrafficsignalSelector selector = new RdTrafficsignalSelector(conn);
        Integer[] linkPids = leftLinks.keySet().toArray(new Integer[]{});
        int length = linkPids.length;
        // 1.信号灯进入点为分离线的经过点时删除信号灯
        List<Integer> nodePids = CalLinkOperateUtils.calNodePids(links);
        List<RdTrafficsignal> trafficsignals = null;
        if (!nodePids.isEmpty()) {
            trafficsignals = selector.loadByNodePids(nodePids, true);
            List<String> isDeleted = new ArrayList<>();

            for (RdTrafficsignal trafficsignal : trafficsignals) {
                // 处理复合路口被删除时红绿灯未删除问题（红绿灯必须依托路口存在）
                List<RdTrafficsignal> rdTrafficsignals = selector.loadByTrafficPid(false, trafficsignal.pid());
                for (RdTrafficsignal iRow : rdTrafficsignals) {
                    if (isDeleted.contains(iRow.rowId())) {
                        continue;
                    } else {
                        isDeleted.add(iRow.rowId());
                        result.insertObject(iRow, ObjStatus.DELETE, iRow.pid());
                    }
                }
            }
        }
        RdLink link = null;
        // 2.信号灯进入点为分离线的起点时更新信号灯进入线
        trafficsignals = selector.loadByNodeId(true, sNode.pid());
        if (!trafficsignals.isEmpty()) {
            for (RdTrafficsignal trafficsignal : trafficsignals) {
                for (RdLink l : links) {
                    if (trafficsignal.getLinkPid() == l.pid()) {
                        trafficsignal.changedFields().put("linkPid", leftLinks.get(l.pid()).pid());
                        result.insertObject(trafficsignal, ObjStatus.UPDATE, trafficsignal.pid());
                        break;
                    }
                }
            }
        }
        // 3.信号灯进入点为分离线的终点时更新信号灯进入线
        trafficsignals = selector.loadByNodeId(true, eNode.pid());
        if (!trafficsignals.isEmpty()) {
            for (RdTrafficsignal trafficsignal : trafficsignals) {
                for (RdLink l : links) {
                    if (trafficsignal.getLinkPid() == l.pid()) {
                        trafficsignal.changedFields().put("linkPid", rightLinks.get(l.pid()).pid());
                        result.insertObject(trafficsignal, ObjStatus.UPDATE, trafficsignal.pid());
                        break;
                    }
                }
            }
        }
        return "";
    }
}
