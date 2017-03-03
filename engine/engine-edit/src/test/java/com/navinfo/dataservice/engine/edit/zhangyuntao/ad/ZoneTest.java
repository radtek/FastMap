package com.navinfo.dataservice.engine.edit.zhangyuntao.ad;

import com.navinfo.dataservice.engine.edit.InitApplication;
import com.navinfo.dataservice.engine.edit.zhangyuntao.eleceye.TestUtil;
import org.junit.Test;

/**
 * Created by chaixin on 2016/9/30 0030.
 */
public class ZoneTest extends InitApplication {
    @Override
    public void init() {
        super.initContext();
    }

    @Test
    public void move() {
        String paramenter = "{\"command\":\"MOVE\",\"dbId\":17,\"objId\":208000047," +
                "\"data\":{\"longitude\":116.875079870224,\"latitude\":40.419205785712236},\"type\":\"ZONENODE\"}";
        //paramenter = "{\"command\":\"MOVE\",\"dbId\":17,\"objId\":208000047,
        // \"data\":{\"longitude\":116.87521398067473,\"latitude\":40.41819701924083},\"type\":\"ZONENODE\"}";
        TestUtil.run(paramenter);
    }

    @Test
    public void remove() {
        String parameter = "{\"command\":\"DELETE\",\"type\":\"ZONEFACE\",\"dbId\":17,\"objId\":309000042}";
        TestUtil.run(parameter);
    }

    @Test
    public void breakL() {
        String parameter = "{\"command\":\"CREATE\",\"dbId\":17,\"objId\":302000561," +
                "\"data\":{\"longitude\":116.61131470075341,\"latitude\":40.25247994052458},\"type\":\"ZONENODE\"}";
        TestUtil.run(parameter);
    }

    @Test
    public void create() {
        String parameter = "{\"command\":\"CREATE\",\"type\":\"ZONEFACE\",\"dbId\":84," +
                "\"data\":{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.48312330245972," +
                "39.94966293471222],[116.48246884346007,39.94844565039495],[116.4836597442627,39.948519675059835]," +
                "[116.48312330245972,39.94966293471222]]}}}";
        TestUtil.run(parameter);
    }
}
