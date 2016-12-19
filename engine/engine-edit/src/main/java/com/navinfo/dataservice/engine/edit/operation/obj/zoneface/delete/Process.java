package com.navinfo.dataservice.engine.edit.operation.obj.zoneface.delete;

import com.navinfo.dataservice.dao.glm.iface.IProcess;
import com.navinfo.dataservice.dao.glm.model.ad.zone.ZoneFace;
import com.navinfo.dataservice.dao.glm.selector.ad.geo.AdAdminSelector;
import com.navinfo.dataservice.dao.glm.selector.ad.zone.ZoneFaceSelector;
import com.navinfo.dataservice.engine.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.operation.AbstractProcess;

public class Process extends AbstractProcess<Command> implements IProcess {

    private Check check = new Check();

    /**
     * @param command
     * @throws Exception
     */
    public Process(AbstractCommand command) throws Exception {
        super(command);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void postCheck() throws Exception {
        check.postCheck(this.getConn(), this.getResult(), this.getCommand().getDbId());
        super.postCheck();
    }

    @Override
    public boolean prepareData() throws Exception {

        ZoneFace zoneFace = (ZoneFace) new ZoneFaceSelector(getConn()).loadById(getCommand().getFaceId(), true);
        getCommand().setZoneFace(zoneFace);
        try {
            AdAdminSelector selector = new AdAdminSelector(getConn());
            getCommand().setAdAdmin(selector.loadByAdminId(zoneFace.getRegionId(), true));
        } catch (Exception e) {
        }
        return super.prepareData();
    }

    /* (non-Javadoc)
         * @see com.navinfo.dataservice.engine.edit.edit.operation.AbstractProcess#createOperation()
         */
    @Override
    public String exeOperation() throws Exception {
        return new Operation(this.getCommand(), check, this.getConn()).run(this.getResult());
    }


}
