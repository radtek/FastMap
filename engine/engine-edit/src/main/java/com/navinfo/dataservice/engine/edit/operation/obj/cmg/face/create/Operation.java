package com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.create;

import com.navinfo.dataservice.bizcommons.service.PidUtil;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildface;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildlink;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildlinkMesh;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildnode;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildnodeMesh;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;
import com.navinfo.dataservice.dao.glm.selector.cmg.CmgBuildfaceSelector;
import com.navinfo.dataservice.dao.glm.selector.cmg.CmgBuildlinkSelector;
import com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.CmgfaceUtil;
import com.navinfo.dataservice.engine.edit.operation.obj.cmg.link.CmglinkUtil;
import com.navinfo.dataservice.engine.edit.utils.CmgLinkOperateUtils;
import com.navinfo.dataservice.engine.edit.utils.Constant;
import com.navinfo.dataservice.engine.edit.utils.NodeOperateUtils;
import com.navinfo.navicommons.geo.computation.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Title: Operation
 * @Package: com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.create
 * @Description: 创建CMG-FACE具体操作
 * @Author: Crayeres
 * @Date: 2017/4/11
 * @Version: V1.0
 */
public class Operation implements IOperation {

    /**
     * 日志记录
     */
    private static Logger logger = Logger.getLogger(Operation.class);

    /**
     * 参数
     */
    private Command command;

    /**
     * 数据库链接
     */
    private Connection conn;

    public Operation(Command command, Connection conn) {
        this.command = command;
        this.conn = conn;
    }

    /**
     * 执行操作
     *
     * @param result 操作结果
     * @return 操作后的对象
     * @throws Exception
     */
    @Override
    public String run(Result result) throws Exception {
        if (CollectionUtils.isEmpty(command.getLinkPids())) {
            Geometry geometry = GeoTranslator.geojson2Jts(command.getGeometry(), 1, Constant.BASE_PRECISION);
            int cmgfaceMeshId = CmgfaceUtil.calcFaceMeshId(geometry);
            // 创建CMG-NODE
            Coordinate firstNode = geometry.getCoordinates()[0];
            CmgBuildnode cmgnode = NodeOperateUtils.createCmgBuildnode(firstNode.x, firstNode.y);
            result.insertObject(cmgnode, ObjStatus.INSERT, cmgnode.pid());
            // 创建CMG-NODE子表MESH
            cmgnode.getMeshes().clear();
            createCmgnodeMesh(result, cmgfaceMeshId, cmgnode);
            // 创建CMG-LINK
            CmgBuildlink cmglink = CmgLinkOperateUtils.createCmglink(geometry, cmgnode.pid(), cmgnode.pid(), result, false);
            // 创建CMG-LINK子表MESH
            createCmglinkMesh(result, cmgfaceMeshId, cmglink);
            // 创建CMG-FACE
            CmgBuildface cmgface = createCmgface(result, GeoTranslator.getPolygonToPoints(geometry.getCoordinates()), cmgfaceMeshId);
            // 创建CMG-FACE子表TOPO
            CmgfaceUtil.createCmgfaceTopo(result, cmglink.pid(), cmgface.pid(), 1);
        } else {
            if (command.getLinkType().equals(ObjType.RDLINK.toString())) {
                // 依据RdLink几何生成CmgLink几何
                createCmglinkRefRdlink(result);
            }
            // 重新计算线构面LINK顺序
            Map<Integer, Geometry> map = CmgfaceUtil.calcCmglinkSequence(command.getCmglinks());
            // 更新参数LINK
            command.setLinkPids(new ArrayList<>(map.keySet()));
            // 连接所有坐标点
            Coordinate[] coordinates = GeoTranslator.getCalLineToPython(new ArrayList<>(map.values())).getCoordinates();
            // 判断是否逆序
            if (!GeometryUtils.IsCCW(coordinates)) {
                List<Coordinate> array = Arrays.asList(coordinates);
                Collections.reverse(array);
                coordinates = array.toArray(new Coordinate[array.size()]);
                Collections.reverse(command.getLinkPids());
            }
            // 通过坐标点构成面
            Geometry geometry = GeoTranslator.getPolygonToPoints(coordinates);
            // 计算CMG-FACE的图幅号
            int cmgfaceMeshId = CmgfaceUtil.calcFaceMeshId(GeoTranslator.transform(geometry, Constant.BASE_SHRINK, Constant.BASE_PRECISION));
            // 创建CMG-FACE
            CmgBuildface cmgface = createCmgface(result, geometry, cmgfaceMeshId);
            // 创建CMG-FACE-TOPO
            for (int seq = 0; seq < command.getLinkPids().size();) {
                CmgfaceUtil.createCmgfaceTopo(result, command.getLinkPids().get(seq), cmgface.pid(), ++seq);
            }
            // 初始化CMG-NODE-SELECTOR
            AbstractSelector cmgnodeSelector = new AbstractSelector(CmgBuildnode.class, conn);
            // 用于避免CMG-NODE重复处理
            Set<Integer> nodes = new HashSet<>();
            // 重新计算CMG-LINK-MESH信息、CMG-NODE-MESH信息
            for (IRow row : command.getCmglinks()) {
                CmgBuildlink cmglink = (CmgBuildlink) row;
                // 重新计算CMG-LINK-MESH信息
                calcCmglinkMesh(result, cmgfaceMeshId, cmglink);
                // 重新计算START点的MESH信息
                CmgBuildnode cmgnode = null;
                if (command.getLinkType().equals(ObjType.RDLINK.toString())) {
                    for (IRow r : result.getAddObjects()) {
                        if (r instanceof CmgBuildnode && ((CmgBuildnode)r).pid() == cmglink.getsNodePid()) {
                            cmgnode = (CmgBuildnode) r;
                        }
                    }
                } else {
                    cmgnode = (CmgBuildnode) cmgnodeSelector.loadById(cmglink.getsNodePid(), false);
                }
                if (!nodes.contains(cmgnode.pid())) {
                    nodes.add(cmgnode.pid());
                    calcCmgnodeMesh(result, cmgfaceMeshId, cmglink, cmgnode);
                }
                // 重新计算END点的MESH信息
                if (command.getLinkType().equals(ObjType.RDLINK.toString())) {
                    for (IRow r : result.getAddObjects()) {
                        if (r instanceof CmgBuildnode && ((CmgBuildnode)r).pid() == cmglink.geteNodePid()) {
                            cmgnode = (CmgBuildnode) r;
                        }
                    }
                } else {
                    cmgnode = (CmgBuildnode) cmgnodeSelector.loadById(cmglink.geteNodePid(), false);
                }
                if (!nodes.contains(cmgnode.pid())) {
                    nodes.add(cmgnode.pid());
                    calcCmgnodeMesh(result, cmgfaceMeshId, cmglink, cmgnode);
                }
            }
        }
        return null;
    }

    /**
     * 依据RdLink几何生成CmgLink
     * @param result 结果集
     * @throws Exception 线构面出错
     */
    private void createCmglinkRefRdlink(Result result) throws Exception {
        List<IRow> cmglinks = new ArrayList<>();
        Map<Coordinate, Integer> map = new HashMap<>();
        for (IRow row : command.getCmglinks()) {
            Geometry geometry = GeoTranslator.transform(((RdLink)row).getGeometry(), Constant.BASE_SHRINK, Constant.BASE_PRECISION);
            int sNodePid = 0;
            int eNodePid = 0;
            Coordinate firstCoor = geometry.getCoordinates()[0];
            Coordinate lastCoor = geometry.getCoordinates()[geometry.getCoordinates().length - 1];
            for (Map.Entry<Coordinate, Integer> entry : map.entrySet()) {
                if (entry.getKey().equals(firstCoor)) {
                    sNodePid = entry.getValue();
                }
                if (entry.getKey().equals(lastCoor)) {
                    eNodePid = entry.getValue();
                }
            }
            JSONObject json = CmgLinkOperateUtils.createCmglinkEndpoint(geometry, sNodePid, eNodePid, result);
            if (0 == sNodePid) {
                sNodePid = json.getInt("s");
                map.put(firstCoor, sNodePid);
            }
            if (0 == eNodePid) {
                eNodePid = json.getInt("e");
                map.put(lastCoor, eNodePid);
            }


            CmgBuildlink cmglink = CmgLinkOperateUtils.createCmglink(geometry, sNodePid, eNodePid, result, false);
            cmglinks.add(cmglink);
        }
        command.setCmglinks(cmglinks);
    }

    /**
     * 创建CMG-NODE-MESH
     * @param result 结果集
     * @param cmgfaceMeshId 关联面的图幅号
     * @param cmgnode CMG-NODE
     */
    private void createCmgnodeMesh(Result result, int cmgfaceMeshId, CmgBuildnode cmgnode) {
        CmgBuildnodeMesh cmgnodeMesh = new CmgBuildnodeMesh();
        cmgnodeMesh.setNodePid(cmgnode.pid());
        cmgnodeMesh.setMeshId(cmgfaceMeshId);
        result.insertObject(cmgnodeMesh, ObjStatus.INSERT, cmgnode.pid());
    }

    /**
     * 创建CMG-LINK-MESH
     * @param result 结果集
     * @param cmgfaceMeshId 关联面的图幅号
     * @param cmglink CMG-LINK
     */
    private void createCmglinkMesh(Result result, int cmgfaceMeshId, CmgBuildlink cmglink) {
        CmgBuildlinkMesh cmglinkMesh = new CmgBuildlinkMesh();
        cmglinkMesh.setLinkPid(cmglink.pid());
        cmglinkMesh.setMeshId(cmgfaceMeshId);
        result.insertObject(cmglinkMesh, ObjStatus.INSERT, cmglink.pid());
    }

    /**
     * 创建CMG-FACE
     * @param result 结果集
     * @param geometry CMG-FACE几何
     * @param cmgfaceMeshId 根据几何计算出的图幅
     * @return 创建的CMG-FACE对象
     * @throws Exception 创建出错
     */
    private CmgBuildface createCmgface(Result result, Geometry geometry, int cmgfaceMeshId) throws Exception {
        CmgBuildface cmgface = new CmgBuildface();
        cmgface.setPid(PidUtil.getInstance().applyCmgBuildfacePid());
        cmgface.setGeometry(geometry);
        cmgface.setPerimeter(GeometryUtils.getLinkLength(geometry));
        cmgface.setArea(GeometryUtils.getCalculateArea(geometry));
        cmgface.setMeshId(cmgfaceMeshId);
        result.insertObject(cmgface, ObjStatus.INSERT, cmgface.pid());
        return cmgface;
    }


    /**
     *  重新计算CMG-LINK-MESH, 清除原不匹配图幅，没有新图幅号时重新生成
     * @param result 结果集
     * @param cmgfaceMeshId 关联面的图幅号
     * @param cmglink CMG-LINK
     */
    private void calcCmglinkMesh(Result result, int cmgfaceMeshId, CmgBuildlink cmglink) {
        CmgBuildfaceSelector selector = new CmgBuildfaceSelector(conn);
        try {
            List<CmgBuildface> cmgfaces = selector.listTheAssociatedFaceOfTheLink(cmglink.pid(), false);
            if (CollectionUtils.isEmpty(cmgfaces)) {
                Iterator<IRow> iterator = cmglink.getMeshes().iterator();
                while (iterator.hasNext()) {
                    CmgBuildlinkMesh cmglinkMesh = (CmgBuildlinkMesh) iterator.next();
                    if (cmgfaceMeshId != cmglinkMesh.getMeshId()) {
                        iterator.remove();
                        result.insertObject(cmglinkMesh, ObjStatus.DELETE, cmglink.pid());
                    }
                }
                if (CollectionUtils.isEmpty(cmglink.getMeshes())) {
                    createCmglinkMesh(result, cmgfaceMeshId, cmglink);
                }
            } else {
                createCmglinkMesh(result, cmgfaceMeshId, cmglink);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     *
     * @param result 结果集
     * @param cmgfaceMeshId 关联面的图幅号
     * @param cmglink CMG-LINK
     * @param cmgnode CMG-NODE
     */
    private void calcCmgnodeMesh(Result result, int cmgfaceMeshId, CmgBuildlink cmglink, CmgBuildnode cmgnode) {
        CmgBuildfaceSelector selector = new CmgBuildfaceSelector(conn);
        try {
            List<CmgBuildface> cmgfaces = selector.listTheAssociatedFaceOfTheNode(cmgnode.pid(), false);
            if (CollectionUtils.isEmpty(cmgfaces)) {
                Iterator<IRow> iterator = cmgnode.getMeshes().iterator();
                while (iterator.hasNext()) {
                    CmgBuildnodeMesh cmgnodeMesh = (CmgBuildnodeMesh) iterator.next();
                    if (cmgfaceMeshId != cmgnodeMesh.getMeshId()) {
                        iterator.remove();
                        result.insertObject(cmgnodeMesh, ObjStatus.DELETE, cmgnode.pid());
                    }
                }
                if (CollectionUtils.isEmpty(cmgnode.getMeshes())) {
                    createCmgnodeMesh(result, cmgfaceMeshId, cmglink, cmgnode);
                }
            } else {
                createCmgnodeMesh(result, cmgfaceMeshId, cmglink, cmgnode);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 创建CMG-LINK-MESH
     * @param result 结果集
     * @param cmgfaceMeshId 关联面的图幅号
     * @param cmglink CMG-LINK
     * @param cmgnode CMG-NODE
     */
    private void createCmgnodeMesh(Result result, int cmgfaceMeshId, CmgBuildlink cmglink, CmgBuildnode cmgnode) {
        CmgBuildnodeMesh cmgnodeMesh = new CmgBuildnodeMesh();
        cmgnodeMesh.setNodePid(cmgnode.pid());
        cmgnodeMesh.setMeshId(cmgfaceMeshId);
        result.insertObject(cmgnodeMesh, ObjStatus.INSERT, cmglink.pid());
    }


}
