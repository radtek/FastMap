package com.navinfo.dataservice.engine.edit.operation.obj.rdlink.update;

import com.navinfo.dataservice.dao.glm.iface.AlertObject;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkForm;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkIntRtic;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkName;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkRtic;
import com.navinfo.dataservice.dao.glm.selector.rd.link.RdLinkSelector;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.AbstractProcess;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Process extends AbstractProcess<Command> {

    public Process(AbstractCommand command) throws Exception {
        super(command);
    }

    public Process(AbstractCommand command, Result result, Connection conn) throws Exception {
        super(command, result, conn);
    }

    private RdLink updateLink;

    @Override
    public boolean prepareData() throws Exception {
        RdLinkSelector linkSelector = new RdLinkSelector(this.getConn());
        if (null == getCommand().getLinkPids())
            this.updateLink = (RdLink) linkSelector.loadById(this.getCommand().getLinkPid(), true);
        else {
            for (IRow row : linkSelector.loadByIds(getCommand().getLinkPids(), true, true))
                getCommand().getLinks().add((RdLink) row);
        }
        return false;
    }

    @Override
    public String run() throws Exception {
        if (!this.getCommand().isInfect()) {

            prepareData();

            Operation operation = new Operation(this.getCommand(), updateLink, this.getConn());
            operation.run(this.getResult());

            String preCheckMsg = this.preCheck();
            if (preCheckMsg != null) {
                throw new Exception(preCheckMsg);
            }

            updateRdLane();

            //postCheck();

        } else {
            String preCheckMsg = this.preCheck();
            if (preCheckMsg != null) {
                throw new Exception(preCheckMsg);
            }
            prepareData();
            Map<String, List<AlertObject>> infects = new HashMap<>();

            //修改link方向
            Operation operation = new Operation();
            List<AlertObject> updateLinkDataList = operation.getUpdateRdLinkAlertData(updateLink, this.getCommand().getUpdateContent());
            if (CollectionUtils.isNotEmpty(updateLinkDataList)) {
                infects.put("修改link方向", updateLinkDataList);
            }
            com.navinfo.dataservice.engine.edit.operation.obj.trafficsignal.update.Operation trafficOperation = new com.navinfo
                    .dataservice.engine.edit.operation.obj.trafficsignal.update.Operation(this.getConn());
            List<AlertObject> deleteTrafficAlertDataList = trafficOperation.getUpdateLinkDirectInfectData(updateLink, this.getCommand()
                    .getUpdateContent());
            if (CollectionUtils.isNotEmpty(deleteTrafficAlertDataList)) {
                infects.put("修改link方向删除link上原有信号灯", deleteTrafficAlertDataList);
            }

            List<AlertObject> updateCrossAlertDataList = trafficOperation.getUpdateLinkDirectInfectCross(updateLink, this.getCommand()
                    .getUpdateContent());

            if (CollectionUtils.isNotEmpty(updateCrossAlertDataList)) {
                infects.put("请注意，修改道路方向，可能需要对下列路口维护信号灯信息", updateCrossAlertDataList);
            }

            return JSONObject.fromObject(infects).toString();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.navinfo.dataservice.dao.glm.iface.IProcess#postCheck()
     */
    @Override
    public void postCheck() throws Exception {
        // TODO Auto-generated method stub
        // this.createPostCheckGlmList();
        List<IRow> glmList = new ArrayList<IRow>();
        glmList.addAll(this.getResult().getAddObjects());
        glmList.addAll(this.getResult().getUpdateObjects());
        for (IRow irow : this.getResult().getDelObjects()) {
            if (irow instanceof RdLinkRtic) {
                glmList.add(irow);
            } else if (irow instanceof RdLinkIntRtic) {
                glmList.add(irow);
            } else if (irow instanceof RdLinkForm) {
                glmList.add(irow);
            } else if (irow instanceof RdLinkName) {
                glmList.add(irow);
            }
        }
        this.checkCommand.setGlmList(glmList);
        this.checkEngine.postCheck();

    }

    @Override
    public String exeOperation() throws Exception {
        return null;
    }

    public String innerRun() throws Exception {
        String msg;
        this.prepareData();

        IOperation operation = new Operation(this.getCommand(), this.updateLink, getConn());

        msg = operation.run(this.getResult());

        String preCheckMsg = this.preCheck();

        if (preCheckMsg != null) {
            throw new Exception(preCheckMsg);
        }

        /**
         * InnerRun中些履历会导致重复履历出现
         *
         * 由于后检查中需要查询数据最新状态, 批量修改时需要分开提交
         * this.recordData();
         * 执行重写后的后检查, 将删除状态的信息放入待检查LIST中
         * this.postCheck();
         *
         */

        return msg;
    }
}
