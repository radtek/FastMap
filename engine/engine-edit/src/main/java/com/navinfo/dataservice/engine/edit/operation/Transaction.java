package com.navinfo.dataservice.engine.edit.operation;

import com.google.common.base.CaseFormat;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.util.JsonUtils;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.dao.glm.iface.*;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFace;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneFace;
import com.navinfo.dataservice.dao.glm.model.lc.LcFace;
import com.navinfo.dataservice.dao.glm.model.lc.LcLink;
import com.navinfo.dataservice.dao.glm.model.lu.LuFace;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.navinfo.dataservice.dao.glm.selector.AbstractSelector;
import com.navinfo.dataservice.dao.glm.selector.SelectorUtils;
import com.navinfo.dataservice.dao.log.LogWriter;
import com.navinfo.dataservice.engine.edit.utils.Constant;
import com.navinfo.dataservice.engine.edit.utils.DbMeshInfoUtil;
import com.navinfo.dataservice.engine.edit.utils.GeometryUtils;
import com.navinfo.navicommons.database.sql.DBUtils;
import com.vividsolutions.jts.geom.Geometry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;

/**
 * 操作控制器
 */
public class Transaction {

    private static Logger logger = Logger.getLogger(Transaction.class);

    /**
     * 请求参数
     */
    private String requester;

    /**
     * 操作类型
     */
    private OperType operType;

    /**
     * 对象类型
     */
    private ObjType objType;

    /**
     * 数据库链接
     */
    private Connection conn;

    /**
     * 用户Id
     */
    private long userId;

    /**
     * 子任务Id
     */
    private int subTaskId;

    /**
     * 数据库类型
     */
    private int dbType;

    /**
     * 主要操作
     */
    private AbstractProcess process;

    /**
     * 删除标识
     * 1：提示，0：删除
     */
    private int infect = 0;

    public Transaction(String requester) {
        this.requester = requester;
    }

    public Transaction(String requester, Connection conn) {
        this.requester = requester;
        this.conn = conn;
    }

    /**
     * Getter method for property <tt>requester</tt>.
     *
     * @return property value of requester
     */
    public String getRequester() {
        return requester;
    }

    /**
     * Setter method for property <tt>requester</tt>.
     *
     * @param requester value to be assigned to property requester
     */
    public void setRequester(String requester) {
        this.requester = requester;
    }

    /**
     * Getter method for property <tt>operType</tt>.
     *
     * @return property value of operType
     */
    public OperType getOperType() {
        return operType;
    }

    /**
     * Getter method for property <tt>objType</tt>.
     *
     * @return property value of objType
     */
    public ObjType getObjType() {
        return objType;
    }

    /**
     * Setter method for property <tt>objType</tt>.
     *
     * @param objType value to be assigned to property objType
     */
    public void setObjType(ObjType objType) {
        this.objType = objType;
    }

    /**
     * Getter method for property <tt>conn</tt>.
     *
     * @return property value of conn
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * Setter method for property <tt>conn</tt>.
     *
     * @param conn value to be assigned to property conn
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * Getter method for property <tt>userId</tt>.
     *
     * @return property value of userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Setter method for property <tt>userId</tt>.
     *
     * @param userId value to be assigned to property userId
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Getter method for property <tt>subTaskId</tt>.
     *
     * @return property value of subTaskId
     */
    public int getSubTaskId() {
        return subTaskId;
    }

    /**
     * Setter method for property <tt>subTaskId</tt>.
     *
     * @param subTaskId value to be assigned to property subTaskId
     */
    public void setSubTaskId(int subTaskId) {
        this.subTaskId = subTaskId;
    }

    /**
     * Setter method for property <tt>dbType</tt>.
     *
     * @param dbType value to be assigned to property dbType
     */
    public void setDbType(int dbType) {
        this.dbType = dbType;
    }

    /**
     * Getter method for property <tt>infect</tt>.
     *
     * @return property value of infect
     */
    public int getInfect() {
        return infect;
    }

    /**
     * Setter method for property <tt>infect</tt>.
     *
     * @param infect value to be assigned to property infect
     */
    public void setInfect(int infect) {
        this.infect = infect;
    }

    /**
     * 创建操作命令
     *
     * @return 命令
     */
    private AbstractCommand createCommand(String requester) throws Exception {
        // 修改net.sf.JSONObject的bug：string转json对象损失精度问题（解决方案目前有两种，一种替换新的jar包以及依赖的包，第二种先转fastjson后再转net.sf）
        com.alibaba.fastjson.JSONObject fastJson = com.alibaba.fastjson.JSONObject.parseObject(requester);
        JSONObject json = JsonUtils.fastJson2netJson(fastJson);

        operType = Enum.valueOf(OperType.class, json.getString("command"));
        objType = Enum.valueOf(ObjType.class, json.getString("type"));
        if (json.containsKey("infect")) {
            infect = json.getInt("infect");
        }

        switch (objType) {
            case RDLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterdlink.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrdpoint.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairrdlink.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departrdnode.Command(json, requester);
                    case UPDOWNDEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.updowndepartlink.Command(json, requester);
                    case CREATESIDEROAD:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.sideRoad.create.Command(json, requester);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlink.Command(json, requester);
                    case BATCHDELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.delete.rdlink.Command(json, requester);
                }
            case FACE:
                switch (operType) {
                    case ONLINEBATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.batch.rdlink.Command(json, requester);
                }
            case RDNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrdpoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdnode.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterdnode.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.moverdnode.Command(json, requester);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdnode.Command(json, requester);
                    case BATCHDELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.delete.rdnode.Command(json, requester);
                }
            case RDRESTRICTION:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdrestriction.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdrestriction.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdrestriction.delete.Command(json, requester);
                }
            case RDCROSS:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdcross.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdcross.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletecross.Command(json, requester);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdcross.Command(json, requester);
                }
            case RDBRANCH:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdbranch.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdbranch.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdbranch.delete.Command(json, requester);
                }
            case RDLANECONNEXITY:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlaneconnexity.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlaneconnexity.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlaneconnexity.delete.Command(json, requester);
                }
            case RDSPEEDLIMIT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.delete.Command(json, requester);
                }
            case RDLINKSPEEDLIMIT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.rdlinkspeedlimit.create.Command(json,
                                requester);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlinkspeedlimit.Command(json, requester);
                }
            case ADADMIN:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.delete.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.move.Command(json, requester);
                    case RELATION:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.relation.Command(json, requester);
                }
            case RDGSC:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgsc.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgsc.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgsc.delete.Command(json, requester);
                }
            case ADNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakadpoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adnode.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.moveadnode.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleteadnode.Command(json, requester);
                }
            case ADLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adlink.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adlink.update.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakadpoint.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleteadlink.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairadlink.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departadnode.Command(json, requester);
                }
            case ADFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adface.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adface.delete.Command(json, requester);
                }
            case ADADMINGROUP:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmingroup.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmingroup.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmingroup.delete.Command(json, requester);
                }
            case IXPOI:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.create.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.move.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.delete.Command(json, requester);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.batch.poi.Command(json, requester);
                }
            case IXPOIPARENT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poiparent.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poiparent.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poiparent.delete.Command(json, requester);
                }
            case RWNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrwpoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rwnode.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterwnode.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.moverwnode.Command(json, requester);
                }
            case RWLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rwlink.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rwlink.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterwlink.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairrwlink.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrwpoint.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departrwnode.Command(json, requester);
                }
            case ZONENODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakzonepoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zonenode.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movezonenode.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletezonenode.Command(json, requester);
                }
            case ZONELINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zonelink.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zonelink.update.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakzonepoint.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletezonelink.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairzonelink.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departzonenode.Command(json, requester);
                }
            case ZONEFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zoneface.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zoneface.delete.Command(json, requester);
                }
            case LUNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklupoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lunode.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movelunode.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelunode.Command(json, requester);
                }
            case LULINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lulink.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lulink.update.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklupoint.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelulink.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairlulink.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departlunode.Command(json, requester);
                }
            case LUFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.luface.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.luface.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.luface.delete.Command(json, requester);
                }
            case LCNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklcpoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcnode.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movelcnode.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelcnode.Command(json, requester);
                }
            case LCLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lclink.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lclink.update.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklcpoint.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelclink.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairlclink.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departlcnode.Command(json, requester);
                }
            case LCFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcface.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcface.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcface.delete.Command(json, requester);
                }
            case RDELECTRONICEYE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.move.Command(json, requester);
                }
            case RDELECEYEPAIR:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceyepair.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceyepair.delete.Command(json, requester);
                }
            case RDTRAFFICSIGNAL:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.update.Command(json, requester);
                }
            case RDWARNINGINFO:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.update.Command(json, requester);
                }
            case RDSLOPE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdslope.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdslope.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdslope.update.Command(json, requester);
                }
            case RDGATE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgate.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgate.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgate.update.Command(json, requester);
                }

            case RDDIRECTROUTE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rddirectroute.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rddirectroute.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rddirectroute.update.Command(json, requester);
                }
            case RDINTER:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdinter.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdinter.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdinter.update.Command(json, requester);
                }
            case RDOBJECT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdobject.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdobject.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdobject.update.Command(json, requester);
                }
            case RDVARIABLESPEED:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvariablespeed.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvariablespeed.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvariablespeed.update.Command(json, requester);
                }
            case RDSE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdse.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdse.delete.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdse.update.Command(json, requester);
                }
            case RDSPEEDBUMP:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedbump.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedbump.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedbump.delete.Command(json, requester);
                }
            case RDSAMENODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamenode.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamenode.delete.Command(json, requester);
                }
            case RDTOLLGATE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdtollgate.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdtollgate.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdtollgate.delete.Command(json, requester);
                }
            case RDVOICEGUIDE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvoiceguide.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvoiceguide.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvoiceguide.delete.Command(json, requester);
                }
            case RDROAD:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdroad.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdroad.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdroad.delete.Command(json, requester);
                }
            case RDSAMELINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.create.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.delete.Command(json, requester);
                }
            case RDLANE:
                switch (operType) {
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlane.delete.Command(json, requester);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlane.Command(json, requester);
                }
            case RDLANETOPODETAIL:
                switch (operType) {
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlanetopodetail.Command(json, requester);
                }
            case IXSAMEPOI:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.samepoi.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.samepoi.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.samepoi.delete.Command(json, requester);
                }
            case IXPOIUPLOAD:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.upload.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.upload.delete.Command(json, requester);
                }
            case RDHGWGLIMIT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.move.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.delete.Command(json, requester);
                }
            case RDMILEAGEPILE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.move.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.delete.Command(json, requester);
                }
            case RDTMCLOCATION:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.tmc.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.tmc.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.tmc.delete.Command(json, requester);
                }
            case CMGBUILDNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakcmgpoint.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.node.update.Command(json, requester);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movecmgnode.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletecmgnode.Command(json, requester);
                }
            case CMGBUILDLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.link.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.link.update.Command(json, requester);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakcmgpoint.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletecmglink.Command(json, requester);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repaircmglink.Command(json, requester);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departcmgnode.Command(json, requester);
                }
            case CMGBUILDFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.delete.Command(json, requester);
                }
            case CMGBUILDING:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.building.create.Command(json, requester);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.building.update.Command(json, requester);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.building.delete.Command(json, requester);
                }
        }
        throw new Exception("不支持的操作类型");
    }

    /**
     * 创建操作进程
     *
     * @param command 操作命令
     * @return 操作进程
     * @throws Exception
     */
    private AbstractProcess createProcess(AbstractCommand command) throws Exception {
        switch (objType) {
            case RDLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterdlink.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrdpoint.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departrdnode.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairrdlink.Process(command);
                    case UPDOWNDEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.updowndepartlink.Process(command);
                    case CREATESIDEROAD:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.sideRoad.create.Process(command);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlink.Process(command);
                    case BATCHDELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.delete.rdlink.Process(command);
                }
            case FACE:
                switch (operType) {
                    case ONLINEBATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.batch.rdlink.Process(command);
                }
            case RDNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrdpoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdnode.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterdnode.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.moverdnode.Process(command);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdnode.Process(command);
                    case BATCHDELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.delete.rdnode.Process(command);
                }
            case RDRESTRICTION:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdrestriction.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdrestriction.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdrestriction.delete.Process(command);
                }
            case RDCROSS:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdcross.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdcross.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletecross.Process(command);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdcross.Process(command);
                }
            case RDBRANCH:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdbranch.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdbranch.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdbranch.delete.Process(command);
                }
            case RDLANECONNEXITY:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlaneconnexity.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlaneconnexity.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlaneconnexity.delete.Process(command);
                }
            case RDSPEEDLIMIT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedlimit.delete.Process(command);
                }
            case RDLINKSPEEDLIMIT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdlink.rdlinkspeedlimit.create.Process(command);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlinkspeedlimit.Process(command);
                }
            case ADADMIN:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.delete.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.move.Process(command);
                    case RELATION:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmin.relation.Process(command);
                }
            case RDGSC:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgsc.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgsc.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgsc.delete.Process(command);
                }
            case ADNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakadpoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adnode.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.moveadnode.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleteadnode.Process(command);
                }
            case ADLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adlink.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adlink.update.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakadpoint.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleteadlink.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairadlink.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departadnode.Process(command);
                }
            case ADFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adface.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adface.delete.Process(command);
                }
            case ADADMINGROUP:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmingroup.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmingroup.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.adadmingroup.delete.Process(command);
                }
            case IXPOI:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.create.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.move.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.delete.Process(command);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.batch.poi.Process(command);
                }
            case IXPOIPARENT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poiparent.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poiparent.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poiparent.delete.Process(command);
                }
            case RWNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrwpoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rwnode.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterwnode.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.moverwnode.Process(command);
                }
            case RWLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rwlink.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rwlink.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deleterwlink.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairrwlink.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakrwpoint.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departrwnode.Process(command);
                }

            case ZONENODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakzonepoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zonenode.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movezonenode.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletezonenode.Process(command);
                }
            case ZONELINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zonelink.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zonelink.update.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakzonepoint.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletezonelink.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairzonelink.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departzonenode.Process(command);
                }
            case ZONEFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zoneface.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.zoneface.delete.Process(command);
                }
            case LUNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklupoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lunode.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movelunode.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelunode.Process(command);
                }
            case LULINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lulink.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lulink.update.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklupoint.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelulink.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairlulink.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departlunode.Process(command);
                }
            case LUFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.luface.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.luface.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.luface.delete.Process(command);
                }
            case RDELECTRONICEYE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceye.move.Process(command);
                }
            case RDELECEYEPAIR:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceyepair.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdeleceyepair.delete.Process(command);
                }
            case RDTRAFFICSIGNAL:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.update.Process(command);
                }
            case RDWARNINGINFO:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdwarninginfo.update.Process(command);
                }
            case RDSLOPE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdslope.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdslope.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdslope.update.Process(command);
                }
            case RDGATE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgate.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgate.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdgate.update.Process(command);
                }
            case RDDIRECTROUTE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rddirectroute.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rddirectroute.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rddirectroute.update.Process(command);
                }
            case RDINTER:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdinter.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdinter.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdinter.update.Process(command);
                }
            case RDOBJECT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdobject.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdobject.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdobject.update.Process(command);
                }
            case RDVARIABLESPEED:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvariablespeed.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvariablespeed.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvariablespeed.update.Process(command);
                }
            case RDSE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdse.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdse.delete.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdse.update.Process(command);
                }
            case RDSPEEDBUMP:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedbump.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedbump.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdspeedbump.delete.Process(command);
                }
            case LCNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklcpoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcnode.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movelcnode.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelcnode.Process(command);
                }
            case LCLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lclink.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lclink.update.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breaklcpoint.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletelclink.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repairlclink.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departlcnode.Process(command);
                }
            case LCFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcface.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcface.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.lcface.delete.Process(command);
                }
            case RDSAMENODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamenode.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamenode.delete.Process(command);
                }
            case RDTOLLGATE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdtollgate.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdtollgate.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdtollgate.delete.Process(command);
                }
            case RDVOICEGUIDE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvoiceguide.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvoiceguide.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdvoiceguide.delete.Process(command);
                }
            case RDROAD:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdroad.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdroad.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdroad.delete.Process(command);
                }
            case RDSAMELINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.create.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.rdsamelink.delete.Process(command);
                }
            case RDLANE:
                switch (operType) {
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlane.delete.Process(command);
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlane.Process(command);
                }
            case RDLANETOPODETAIL:
                switch (operType) {
                    case BATCH:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.batch.batchrdlanetopodetail.Process(command);
                }
            case IXSAMEPOI:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.samepoi.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.samepoi.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.samepoi.delete.Process(command);
                }
            case IXPOIUPLOAD:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.upload.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.poi.upload.delete.Process(command);
                }
            case RDHGWGLIMIT:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.move.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.hgwg.delete.Process(command);
                }
            case RDMILEAGEPILE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.move.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.mileagepile.delete.Process(command);
                }
            case RDTMCLOCATION:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.tmc.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.tmc.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.tmc.delete.Process(command);
                }
            case CMGBUILDNODE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakcmgpoint.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.node.update.Process(command);
                    case MOVE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.move.movecmgnode.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletecmgnode.Process(command);
                }
            case CMGBUILDLINK:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.link.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.link.update.Process(command);
                    case BREAK:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.breakin.breakcmgpoint.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.delete.deletecmglink.Process(command);
                    case REPAIR:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.repair.repaircmglink.Process(command);
                    case DEPART:
                        return new com.navinfo.dataservice.engine.edit.operation.topo.depart.departcmgnode.Process(command);
                }
            case CMGBUILDFACE:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.face.delete.Process(command);
                }
            case CMGBUILDING:
                switch (operType) {
                    case CREATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.building.create.Process(command);
                    case UPDATE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.building.update.Process(command);
                    case DELETE:
                        return new com.navinfo.dataservice.engine.edit.operation.obj.cmg.building.delete.Process(command);
                }
        }
        throw new Exception("不支持的操作类型");

    }

    /**
     * 根据结果集计算是否为跨大区作业
     * @param commands
     * @param processes
     * @param result
     * @throws Exception
     */
    private void calcDbIdsRefResult(List<AbstractCommand> commands, List<AbstractProcess> processes, Result result) throws Exception {
        List<String> requests = new ArrayList<>();

        Set<Integer> dbIds = new HashSet<>();

        // 计算新增数据
        calcDbIdRefResultList(result.getAddObjects(), dbIds);
        // 计算修改数据
        calcDbIdRefResultList(result.getUpdateObjects(), dbIds);
        // 计算删除数据
        calcDbIdRefResultList(result.getDelObjects(), dbIds);

        logger.info(String.format("本次跨大区操作涉及数据库ID:[%s]", Arrays.toString(dbIds.toArray())));

        JSONObject json = JSONObject.fromObject(requester);
        dbIds.remove(Integer.valueOf(process.getCommand().getDbId()));

        for (int dbId : dbIds) {
            String request = json.discard("dbIds").element("dbId", dbId).toString();
            requests.add(request);
        }

        for (String request : requests) {
            AbstractCommand command = initCommand(request);
            commands.add(command);

            AbstractProcess process = createProcess(command);
            process.getConn().setAutoCommit(false);
            processes.add(process);
        }
    }

    /**
     * 计算一组数据对应的大区库
     *
     * @param rows
     * @param dbIds
     */
    private void calcDbIdRefResultList(List<IRow> rows, Set<Integer> dbIds) {
        for (IRow row : rows) {
            if (row instanceof IObj) {
                if (Constant.RDLINK_REF_OBJECT.contains(objType)) {
                    try {
                        row = new AbstractSelector(RdLink.class, process.getConn()).loadById(
                                Integer.parseInt(loadFieldValue(row, "LinkPid").toString()), false);
                    } catch (Exception e) {
                        logger.error(String.format("获取关联RDLINK要素失败[row.table_name: %s, row.row_id: %s]", row.tableName(), row.rowId()), e);
                    }
                }

                Geometry geometry = GeometryUtils.loadGeometry(row);
                if (row.changedFields().containsKey("geometry")) {
                    try {
                        geometry = GeoTranslator.geojson2Jts((JSONObject) row.changedFields().get("geometry"));
                    } catch (JSONException e) {
                        logger.error(String.format("获取更新后几何出错[row.table_name: %s, row.row_id: %s]", row.tableName(), row.rowId()), e);
                    }
                }
                dbIds.addAll(DbMeshInfoUtil.calcDbIds(geometry));
            } else {
                calcDbIdRefParentObj(dbIds, row);
            }
        }
    }

    /**
     * 根据子表计算对应主表是否跨大区
     *
     * @param dbIds
     * @param row
     */
    private void calcDbIdRefParentObj(Set<Integer> dbIds, IRow row) {
        if (StringUtils.startsWithIgnoreCase(row.tableName(), row.parentTableName() + "_")) {
            try {
                String className = String.format("%s.%s", row.getClass().getPackage().getName(), CaseFormat.UPPER_UNDERSCORE.to
                        (CaseFormat.UPPER_CAMEL, row.parentTableName()));
                IRow partent = new AbstractSelector(Class.forName(className), process.getConn()).loadById(row.parentPKValue(), false);
                dbIds.addAll(DbMeshInfoUtil.calcDbIds(GeometryUtils.loadGeometry(partent)));
            } catch (Exception e) {
                logger.error(String.format("未找到对应主表信息 [row.table_name: %s, row.row_id: %s]", row.tableName(), row.rowId()), e);
            }
        }
    }

    /**
     * 处理跨大区数据
     * @param conn
     * @param command
     * @param sourceResult
     * @return
     * @throws Exception
     */
    private Result handle(Connection conn, AbstractCommand command, Result sourceResult) throws Exception {
        Result result = null;

        if (Constant.RDLINK_REF_OBJECT.contains(objType)) {
            result = sourceResult;
        } else if (OperType.CREATE.equals(operType)) {
            result = sourceResult;

            List<IRow> addObjects = sourceResult.getAddObjects();
            Map<String, IRow> inserts = new HashMap<>();

            if (Constant.LINK_TYPES.contains(objType)) {
                for (IRow row : addObjects) {
                    if (Constant.LINK_TYPES.contains(row.objType())) {
                        addMissNode(addObjects, Integer.parseInt(loadFieldValue(row, "sNodePid").toString()), inserts, row, conn);
                        addMissNode(addObjects, Integer.parseInt(loadFieldValue(row, "eNodePid").toString()), inserts, row, conn);
                    }
                }
            }
            if (Constant.FACE_TYPES.contains(objType)) {
                for (IRow row : addObjects) {
                    if (row instanceof LcFace) {
                        LcFace face = (LcFace) row;
                        for (IRow topo : face.getTopos()) {
                            addMissLink(addObjects, inserts, topo, conn);
                        }
                    }
                    if (row instanceof LuFace) {
                        LuFace face = (LuFace) row;
                        for (IRow topo : face.getFaceTopos()) {
                            addMissLink(addObjects, inserts, topo, conn);
                        }
                    }
                    if (row instanceof AdFace) {
                        AdFace face = (AdFace) row;
                        for (IRow topo : face.getFaceTopos()) {
                            addMissLink(addObjects, inserts, topo, conn);
                        }
                    }
                    if (row instanceof ZoneFace) {
                        ZoneFace face = (ZoneFace) row;
                        for (IRow topo : face.getFaceTopos()) {
                            addMissLink(addObjects, inserts, topo, conn);
                        }
                    }
                }
            }

            for (IRow row : inserts.values()) {
                result.insertObject(row, ObjStatus.INSERT, row.parentPKValue());
            }
        } else if (OperType.UPDATE.equals(operType) || OperType.DELETE.equals(operType) || OperType.BREAK.equals(operType)) {
            result = sourceResult;
        } else if (OperType.DEPART.equals(operType) || OperType.REPAIR.equals(operType) || OperType.MOVE.equals(operType)) {
            assertErrorOperation(sourceResult);
            result = sourceResult;
        }

        return result;
    }

    /**
     * 判断是否包含非法操作(将大区内数据移动到大区接边线上)
     * @param sourceResult
     * @throws Exception
     */
    private void assertErrorOperation(Result sourceResult) throws Exception {
        for (IRow row : sourceResult.getUpdateObjects()) {
            if (Constant.NODE_TYPES.contains(row.objType()) && row.changedFields().containsKey("geometry")) {
                Geometry oldGeometry = GeoTranslator.transform(GeometryUtils.loadGeometry(row), Constant.BASE_SHRINK, Constant.BASE_PRECISION);
                Geometry newGeometry = GeoTranslator.geojson2Jts((JSONObject) row.changedFields().get("geometry"), Constant.BASE_SHRINK,
                        Constant.BASE_PRECISION);
                if (DbMeshInfoUtil.calcDbIds(oldGeometry).size() == 1 && DbMeshInfoUtil.calcDbIds(newGeometry).size() > 1) {
                    throw new Exception("不支持此操作");
                }
            }
            if (Constant.LINK_TYPES.contains(row.objType()) && row.changedFields().containsKey("geometry")) {
                Geometry oldGeometry = GeoTranslator.transform(GeometryUtils.loadGeometry(row), Constant.BASE_SHRINK, Constant.BASE_PRECISION);
                Geometry newGeometry = GeoTranslator.geojson2Jts((JSONObject) row.changedFields().get("geometry"), Constant.BASE_SHRINK,
                        Constant.BASE_PRECISION);
                if (DbMeshInfoUtil.calcDbIds(oldGeometry).size() == 1 && DbMeshInfoUtil.calcDbIds(newGeometry).size() > 1) {
                    throw new Exception("不支持此操作");
                }
            }
        }
    }

    /**
     * 根据属性名获取值
     *
     * @param row
     * @param fieldName
     * @return
     */
    private Object loadFieldValue(IRow row, String fieldName) throws Exception{
        Class clazz = row.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (StringUtils.equalsIgnoreCase(method.getName(), "get" + fieldName)) {
                try {
                    return method.invoke(row);
                } catch (IllegalAccessException e) {
                    logger.error("根据属性名获取IRow的属性值", e);
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    logger.error("根据属性名获取IRow的属性值", e);
                    e.printStackTrace();
                }
            }
        }
        throw new Exception();
    }

    /**
     * 补全缺失NODE
     *
     * @param addObjects
     * @param nodePid
     * @param inserts
     * @param row
     * @param conn
     * @throws Exception
     */
    private void addMissNode(List<IRow> addObjects, int nodePid, Map<String, IRow> inserts, IRow row, Connection conn) throws Exception {
        if (!Constant.OBJ_TYPE_CLASS_MAP.containsKey(row.objType())) {
            return;
        }
        Class clazz = Constant.OBJ_TYPE_CLASS_MAP.get(row.objType());

        for (IRow n : addObjects) {
            if (StringUtils.equalsIgnoreCase(clazz.getSimpleName(), n.objType().toString()) && n.parentPKValue() == nodePid) {
                return;
            }
        }

        try {
            new AbstractSelector(clazz, conn).loadById(nodePid, false);
        } catch (Exception e) {
            IRow node = new AbstractSelector(clazz, process.getConn()).loadById(nodePid, false);
            inserts.put(node.objType().toString() + node.parentPKValue(), node);
        }
    }

    /**
     * 补全缺失Node
     *
     * @param addObjects
     * @param inserts
     * @param row
     * @param conn
     * @throws Exception
     */
    private void addMissLink(List<IRow> addObjects, Map<String, IRow> inserts, IRow row, Connection conn) throws Exception {
        if (!Constant.OBJ_TYPE_CLASS_MAP.containsKey(row.objType())) {
            return;
        }
        Class clazz = Constant.OBJ_TYPE_CLASS_MAP.get(row.objType());

        int linkPid = Integer.parseInt(loadFieldValue(row, "LinkPid").toString());
        for (IRow n : addObjects) {
            if (StringUtils.equalsIgnoreCase(clazz.getSimpleName(), n.objType().toString()) && n.parentPKValue() == linkPid) {
                return;
            }
        }

        try {
            new AbstractSelector(LcLink.class, conn).loadById(linkPid, false);
        } catch (Exception e) {
            inserts.put(row.objType().toString() + row.parentPKValue(), row);
            IRow link = new AbstractSelector(LcLink.class, process.getConn()).loadById(linkPid, false);
            inserts.put(link.objType().toString() + link.parentPKValue(), link);
            addMissNode(addObjects, Integer.parseInt(loadFieldValue(link, "sNodePid").toString()), inserts, link, conn);
            addMissNode(addObjects, Integer.parseInt(loadFieldValue(link, "eNodePid").toString()), inserts, link, conn);
        }
    }

    /**
     * 执行操作
     *
     * @return
     * @throws Exception
     */
    public String run() throws Exception {
        List<AbstractCommand> commands = new ArrayList<>();
        List<AbstractProcess> processes = new ArrayList<>();
        commands.add(initCommand(requester));
        processes.add(createProcess(commands.iterator().next()));

        String msg = "";
        try {
            for (AbstractProcess process : processes) {
                process.getConn().setAutoCommit(false);
            }

            process = processes.iterator().next();

            msg = process.run();

            Result result = process.getResult();

            // 初始化新增数据RowId，保证接边库数据一致
            initRowid(result.getAddObjects());

            recordData(process, result);

            // 先判断该操作是否会产生接边影响
            // WEB端传入参数为非接边作业时，检查操作结果是否产生接边影响
            calcDbIdsRefResult(commands, processes, result);

            for (int i = 1; i < processes.size(); i++) {
                AbstractProcess pro = processes.get(i);
                Result res = handle(pro.getConn(), pro.getCommand(), result);
                if (null == res) {
                    continue;
                }
                recordData(pro, res);
            }

            for (AbstractProcess process : processes) {
                process.getConn().commit();
            }
        } catch (Exception e) {
            logger.error(String.format("%s操作失败，数据库进行回滚，requester: %s", objType, requester), e);
            for (AbstractProcess process : processes) {
                DBUtils.rollBack(process.getConn());
            }
            throw e;
        } finally {
            for (AbstractProcess process : processes) {
                DBUtils.closeConnection(process.getConn());
            }
        }
        return msg;
    }

    /**
     * 执行操作
     *
     * @return
     * @throws Exception
     */
    public String innerRun() throws Exception {
        List<AbstractCommand> commands = new ArrayList<>();
        List<AbstractProcess> processes = new ArrayList<>();
        commands.add(initCommand(requester));
        processes.add(createProcess(commands.iterator().next()));

        String msg = "";
        try {
            for (AbstractProcess process : processes) {
                process.getConn().setAutoCommit(false);
            }

            process = processes.iterator().next();

            msg = process.innerRun();

            Result result = process.getResult();

            // 初始化新增数据RowId，保证接边库数据一致
            initRowid(result.getAddObjects());

            recordData(process, result);

            // 先判断该操作是否会产生接边影响
            // WEB端传入参数为非接边作业时，检查操作结果是否产生接边影响
            calcDbIdsRefResult(commands, processes, result);

            for (int i = 1; i < processes.size(); i++) {
                AbstractProcess pro = processes.get(i);
                Result res = handle(pro.getConn(), pro.getCommand(), result);
                recordData(pro, res);
            }

            for (AbstractProcess process : processes) {
                process.getConn().commit();
            }
        } catch (Exception e) {
            logger.error(String.format("%s操作失败，数据库进行回滚，requester: %s", objType, requester), e);
            for (AbstractProcess process : processes) {
                DBUtils.rollBack(process.getConn());
            }
        } finally {
            for (AbstractProcess process : processes) {
                DBUtils.closeConnection(process.getConn());
            }
        }
        return msg;
    }

    /**
     * 生成履历，写入数据
     * @param process
     * @param result
     * @return
     * @throws Exception
     */
    public boolean recordData(AbstractProcess process, Result result) throws Exception {
        AbstractCommand command = process.getCommand();
        LogWriter lw = new LogWriter(process.getConn());
        lw.setUserId(command.getUserId());
        lw.setTaskId(command.getTaskId());
        lw.generateLog(command, result);
        OperatorFactory.recordData(process.getConn(), result);
        lw.recordLog(command, result);
        try {
            PoiMsgPublisher.publish(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    /**
     * 填充AddObject数据的RowId，防止跨库数据不一致
     *
     * @param rows
     */
    private void initRowid(List<IRow> rows) {
        for (IRow row : rows) {
            String tableName = SelectorUtils.getObjTableName(row);
            if (StringUtils.isNotEmpty(row.rowId())) {
                if (!tableName.equals("IX_POI")) {
                    row.setRowId(UuidUtils.genUuid());
                }
            } else {
                row.setRowId(UuidUtils.genUuid());
            }
            if (CollectionUtils.isNotEmpty(row.children())) {
                for (List<IRow> list : row.children()) {
                    initRowid(list);
                }
            }
        }
    }

    /**
     * 初始化Command信息
     * @param request
     * @return
     * @throws Exception
     */
    private AbstractCommand initCommand(String request) throws Exception {
        AbstractCommand command = createCommand(request);
        command.setUserId(userId);
        command.setTaskId(subTaskId);
        command.setDbType(dbType);
        command.setInfect(infect);
        if (null != conn) {
            command.setHasConn(true);
        }
        return command;
    }

    /**
     * @return 操作简要日志信息
     */
    public String getLogs() {
        return process.getResult().getLogs();
    }

    public JSONArray getCheckLog() {
        return process.getResult().getCheckResults();

    }

    public int getPid() {
        return process.getResult().getPrimaryPid();
    }
}
