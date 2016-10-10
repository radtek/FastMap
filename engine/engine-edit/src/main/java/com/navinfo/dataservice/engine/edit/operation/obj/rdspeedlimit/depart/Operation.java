package com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.depart;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.navinfo.dataservice.dao.glm.model.rd.speedlimit.RdSpeedlimit;
import com.navinfo.dataservice.dao.glm.selector.rd.speedlimit.RdSpeedlimitSelector;
import com.navinfo.navicommons.geo.computation.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于维护节点分离对点限速的影响
 * Created by chaixin on 2016/9/20 0020.
 */
public class Operation {

    private Connection conn;

    public Operation(Connection conn) {
        this.conn = conn;
    }

    public void depart(int nodePid, RdLink oldLink, List<RdLink> newLinks, Result result) throws Exception {
        // 加载RdSpeedlimitSelector
        RdSpeedlimitSelector selector = new RdSpeedlimitSelector(this.conn);
        // 获取分离link上挂接的RdSpeedlimit
        List<RdSpeedlimit> speedlimits = selector.loadSpeedlimitByLinkPid(oldLink.pid(), true);
        // 分离后没产生跨图幅打断
        if (newLinks.size() == 1) {
            Geometry linkGeo = newLinks.get(0).getGeometry();
            for (RdSpeedlimit speedlimit : speedlimits) {
                // 判断点限速所处段几何是否发生变化
                Geometry nochangeGeo = GeoTranslator.transform(oldLink.getGeometry(), 0.00001, 5).intersection(linkGeo);
                if (GeoTranslator.transform(speedlimit.getGeometry(), 0.00001, 5).intersects(nochangeGeo)) continue;

                // 计算speedlimit几何与移动后link几何最近的点
                Coordinate coor = GeometryUtils.GetNearestPointOnLine(speedlimit.getGeometry().getCoordinate(), linkGeo);
                if (null != coor) {
                    speedlimit.changedFields().put("geometry", GeoTranslator.jts2Geojson(GeoTranslator.point2Jts(coor.x, coor.y)));
                    result.insertObject(speedlimit, ObjStatus.UPDATE, speedlimit.pid());
                }
            }
        } else if (newLinks.size() > 1) {
            // 跨图幅打断时计算speedlimit与每条RdLink的距离,取距离最小的link为关联link
            for (RdSpeedlimit speedlimit : speedlimits) {
                Coordinate minCoor = null;
                int minLinkPid = 0;
                double minLength = 0;
                Geometry limitGeo = speedlimit.getGeometry();
                // 判断点限速所处段几何是否发生变化
                List<Geometry> geometries = new ArrayList<Geometry>();
                for (RdLink link : newLinks) {
                    geometries.add(link.getGeometry());
                }
                Geometry nochangeGeo = GeoTranslator.transform(oldLink.getGeometry(), 0.00001, 5).intersection(GeoTranslator.geojson2Jts(GeometryUtils.connectLinks(geometries)));
                if (GeoTranslator.transform(speedlimit.getGeometry(), 0.00001, 5).intersects(nochangeGeo)) continue;

                for (RdLink link : newLinks) {
                    Geometry linkGeo = link.getGeometry();
                    Coordinate tmpCoor = GeometryUtils.GetNearestPointOnLine(limitGeo.getCoordinate(), linkGeo);
                    if (null != tmpCoor) {
                        double length = GeometryUtils.getDistance(limitGeo.getCoordinate(), tmpCoor);
                        if (minLength == 0 || length < minLength) {
                            minLength = length;
                            minCoor = tmpCoor;
                            minLinkPid = link.pid();
                        }
                    }
                }
                if (null != minCoor) {
                    speedlimit.changedFields().put("geometry", GeoTranslator.jts2Geojson(GeoTranslator.point2Jts(minCoor.x, minCoor.y)));
                    speedlimit.changedFields().put("linkPid", minLinkPid);
                    result.insertObject(speedlimit, ObjStatus.UPDATE, speedlimit.pid());
                }
            }
        }
    }

    // 用于维护上下线分离对点限速的影响
    public String updownDepart(RdNode sNode, List<RdLink> links, Map<Integer, RdLink> leftLinks, Map<Integer, RdLink> rightLinks, Result result) throws Exception {
        // 查找上下线分离对影响到的点限速
        List<Integer> linkPids = new ArrayList<Integer>();
        linkPids.addAll(leftLinks.keySet());
        RdSpeedlimitSelector rdSpeedlimitSelector = new RdSpeedlimitSelector(conn);
        List<RdSpeedlimit> rdSpeedlimits = rdSpeedlimitSelector.loadSpeedlimitByLinkPids(linkPids, true);
        // 点限速数量为零则不需要维护
        if (rdSpeedlimits.size() == 0) {
            return "";
        }
        // 构建RdLinkPid-电子眼的对应集合
        Map<Integer, List<RdSpeedlimit>> rdSpeedlimitMap = new HashMap<Integer, List<RdSpeedlimit>>();
        for (RdSpeedlimit rdSpeedlimit : rdSpeedlimits) {
            List<RdSpeedlimit> list = rdSpeedlimitMap.get(rdSpeedlimit.getLinkPid());
            if (null != list) {
                list.add(rdSpeedlimit);
            } else {
                list = new ArrayList<RdSpeedlimit>();
                list.add(rdSpeedlimit);
                rdSpeedlimitMap.put(rdSpeedlimit.getLinkPid(), list);
            }
        }
        for (RdLink link : links) {
            RdLink leftLink = leftLinks.get(link.pid());
            RdLink rightLink = rightLinks.get(link.pid());
            if (rdSpeedlimitMap.containsKey(link.getPid())) {
                List<RdSpeedlimit> rdSpeedlimitList = rdSpeedlimitMap.get(link.getPid());
                for (RdSpeedlimit rdSpeedlimit : rdSpeedlimitList) {
                    int direct = rdSpeedlimit.getDirect();
                    // 点限速为顺方向则关联link为左线
                    if (direct == leftLink.getDirect()) {
                        updateRdSpeedlimit(leftLink, rdSpeedlimit, result);
                        // 点限速为逆方向则关联link为右线
                    } else if (direct == rightLink.getDirect()) {
                        updateRdSpeedlimit(rightLink, rdSpeedlimit, result);
                    }
                }
            }
        }
        return "";
    }

    // 更新点限速信息
    private void updateRdSpeedlimit(RdLink link, RdSpeedlimit rdSpeedlimit, Result result) throws Exception {
        // 计算原点限速坐标到分离后link的垂足点
        Coordinate targetPoint = GeometryUtils.GetNearestPointOnLine(rdSpeedlimit.getGeometry().getCoordinate(), link.getGeometry());
        JSONObject geoPoint = new JSONObject();
        geoPoint.put("type", "Point");
        geoPoint.put("coordinates", new double[]{targetPoint.x, targetPoint.y});
        Geometry tmpGeo = GeoTranslator.geojson2Jts(geoPoint);
        geoPoint = GeoTranslator.jts2Geojson(tmpGeo, 0.00001, 5);
        rdSpeedlimit.changedFields().put("geometry", geoPoint);
        rdSpeedlimit.changedFields().put("linkPid", link.getPid());
        // 更新点限速坐标以及挂接线
        result.insertObject(rdSpeedlimit, ObjStatus.UPDATE, rdSpeedlimit.pid());
    }
}
