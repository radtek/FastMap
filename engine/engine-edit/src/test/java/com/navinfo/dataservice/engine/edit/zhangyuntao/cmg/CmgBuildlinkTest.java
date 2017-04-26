package com.navinfo.dataservice.engine.edit.zhangyuntao.cmg;

import com.navinfo.dataservice.engine.edit.InitApplication;
import com.navinfo.dataservice.engine.edit.zhangyuntao.eleceye.TestUtil;
import org.junit.Test;

/**
 * @Title: CmgBuildlinkTest
 * @Package: com.navinfo.dataservice.engine.edit.zhangyuntao.cmg
 * @Description: ${TODO}
 * @Author: Crayeres
 * @Date: 2017/4/13
 * @Version: V1.0
 */
public class CmgBuildlinkTest extends InitApplication {
    @Override
    public void init() {
        super.initContext();
    }

    @Test
    public void testCreateCmgBuildlink() {
        String requester = "{\"command\":\"CREATE\",\"type\":\"CMGBUILDLINK\",\"data\":{\"eNodePid\":0,\"sNodePid\":0," +
                "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[127.0669363439083,33.9616218843058],[127.06732124090196," +
                "33.96170864585347]]},\"catchLinks\":[]},\"dbId\":84}";
        TestUtil.run(requester);
    }

    @Test
    public void testDeleteCmgBuildlink() {
        String requester = "{\"command\":\"DELETE\",\"dbId\":13,\"type\":\"CMGBUILDLINK\",\"objId\":505000007}";
        TestUtil.run(requester);
    }

    @Test
    public void testRepairCmgBuildlink() {
        String requester = "{\"command\":\"REPAIR\",\"type\":\"CMGBUILDLINK\",\"objId\":401000007,\"dbId\":13,\"subtaskId\":1," +
                "\"data\":{\"type\":\"CMGBUILDLINK\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.52590990066528," +
                "39.74020261499875],[116.52642,39.73992],[116.52611,39.73939],[116.52585,39.7399]]}," +
                "\"catchInfos\":[{\"nodePid\":510000009,\"longitude\":116.52590990066528,\"latitude\":39.74020261499875}," +
                "{\"nodePid\":510000009,\"longitude\":116.52585,\"latitude\":39.7399}]}}";
        TestUtil.run(requester);
    }
}