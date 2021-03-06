package com.navinfo.dataservice.engine.limit.operation.meta.scplateresgroup.relation;

import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.engine.limit.glm.iface.DbType;
import com.navinfo.dataservice.engine.limit.glm.iface.LimitObjType;
import com.navinfo.dataservice.engine.limit.glm.model.meta.ScPlateresGroup;
import com.navinfo.dataservice.engine.limit.operation.AbstractCommand;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Command extends AbstractCommand {

    private String requester;

    private List<String>groupIds=new ArrayList<>();

    private List<ScPlateresGroup> groups = new ArrayList<>();

    public List<String> getGroupIds() {
        return groupIds;
    }

    public List<ScPlateresGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ScPlateresGroup> groups) {
        this.groups = groups;
    }

    private String infoIntelId="";

    public String getInfoIntelId() {
        return infoIntelId;
    }

    private boolean isCheckInfect = false;

    public boolean isCheckInfect() {
        return isCheckInfect;
    }

    public Command(JSONObject json, String requester) {

        this.requester = requester;

        infoIntelId = json.getString("infoIntelId");

        groupIds = new ArrayList<>(JSONArray.toCollection(json.getJSONArray("objIds")));
    }

    @Override
    public OperType getOperType() {
        return OperType.RELATION;
    }

    @Override
    public DbType getDbType() {
        return DbType.METADB;
    }

    @Override
    public String getRequester() {
        return requester;
    }

    @Override
    public LimitObjType getObjType() {
        return LimitObjType.SCPLATERESGROUP;
    }

}
