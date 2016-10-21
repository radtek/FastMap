package com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.depart;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.warninginfo.RdWarninginfo;
import com.navinfo.dataservice.dao.glm.selector.rd.warninginfo.RdWarninginfoSelector;
import com.navinfo.navicommons.geo.GeoUtils;
import com.navinfo.navicommons.geo.computation.GeometryUtils;

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

    public String updownDepart(List<RdLink> links, Map<Integer, RdLink> leftLinks, Map<Integer, RdLink> rightLinks, Map<Integer, RdLink> notargetLinkMap, Result result) throws Exception {
        RdWarninginfoSelector selector = new RdWarninginfoSelector(conn);
        // 1.警示信息link为上下分离的目标link,删除警示信息
        List<Integer> linkPids = new ArrayList<>();
        linkPids.addAll(leftLinks.keySet());
        List<RdWarninginfo> warninginfos = selector.loadByLinks(linkPids, true);
        for (RdWarninginfo warninginfo : warninginfos) {
            result.insertObject(warninginfo, ObjStatus.DELETE, warninginfo.pid());
        }
        // 2.警示信息进入点为目标link的经过点
        Set<Integer> tmpNodePids = new HashSet<>();
        for (RdLink link : links) {
            tmpNodePids.add(link.getsNodePid());
            tmpNodePids.add(link.geteNodePid());
        }
        List<Integer> nodePids = Arrays.asList(tmpNodePids.toArray(new Integer[]{})).subList(1, tmpNodePids.size() - 1);
        List<RdWarninginfo> list = selector.loadByNodePids(nodePids, true);
        list.removeAll(warninginfos);
        for (RdWarninginfo info : list) {
            Integer linkPid = info.getLinkPid();
            RdLink link = notargetLinkMap.get(linkPid);
            Object sNodePid = link.changedFields().get("sNodePid");
            Object eNodePid = link.changedFields().get("eNodePid");
            if (null != sNodePid) {
                if (info.getNodePid() == link.getsNodePid())
                    info.changedFields().put("nodePid", (Integer) sNodePid);
            } else if (null != eNodePid) {
                if (info.getNodePid() == link.geteNodePid())
                    info.changedFields().put("nodePid", (Integer) eNodePid);
            }
            result.insertObject(info, ObjStatus.UPDATE, info.pid());
        }

        return "";
    }
}
