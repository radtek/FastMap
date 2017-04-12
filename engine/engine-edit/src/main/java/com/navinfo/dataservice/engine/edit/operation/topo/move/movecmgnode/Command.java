package com.navinfo.dataservice.engine.edit.operation.topo.move.movecmgnode;

import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildface;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildlink;
import com.navinfo.dataservice.dao.glm.model.cmg.CmgBuildnode;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.obj.cmg.node.CmgnodeUtil;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by crayeres on 2017/4/12.
 */
public class Command extends AbstractCommand {

    /**
     * 参数
     */
    private String requester;

    /**
     * 待修改对象
     */
    private CmgBuildnode cmgnode;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 受影响CMG-LINK
     */
    private List<CmgBuildlink> cmglinks;

    /**
     * 受影响CMG-FACE
     */
    private List<CmgBuildface> cmgfaces;

    @Override
    public OperType getOperType() {
        return OperType.MOVE;
    }

    @Override
    public String getRequester() {
        return this.requester;
    }

    @Override
    public ObjType getObjType() {
        return ObjType.CMGBUILDNODE;
    }

    public Command(JSONObject json, String requester) {
        this.requester = requester;
        this.setDbId(json.getInt("dbId"));

        cmgnode.setPid(json.getInt("objId"));
        longitude = CmgnodeUtil.reviseItude(json.getDouble("longitude"));
        latitude = CmgnodeUtil.reviseItude(json.getDouble("latitude"));
    }

    public CmgBuildnode getCmgnode() {
        return cmgnode;
    }

    public void setCmgnode(CmgBuildnode cmgnode) {
        this.cmgnode = cmgnode;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public List<CmgBuildlink> getCmglinks() {
        return cmglinks;
    }

    public void setCmglinks(List<CmgBuildlink> cmglinks) {
        this.cmglinks = cmglinks;
    }

    public List<CmgBuildface> getCmgfaces() {
        return cmgfaces;
    }

    public void setCmgfaces(List<CmgBuildface> cmgfaces) {
        this.cmgfaces = cmgfaces;
    }
}
