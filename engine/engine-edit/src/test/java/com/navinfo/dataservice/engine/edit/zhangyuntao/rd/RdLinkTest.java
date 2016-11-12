package com.navinfo.dataservice.engine.edit.zhangyuntao.rd;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.selector.rd.link.RdLinkSelector;
import com.navinfo.dataservice.engine.edit.InitApplication;
import com.navinfo.dataservice.engine.edit.utils.batch.SpeedLimitUtils;
import com.navinfo.dataservice.engine.edit.zhangyuntao.eleceye.TestUtil;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.sql.Connection;

/**
 * @author zhangyt
 * @Title: RdLinkTest.java
 * @Description: TODO
 * @date: 2016年8月3日 上午10:32:29
 * @version: v1.0
 */
public class RdLinkTest extends InitApplication {

    public RdLinkTest() {
    }

    @Override
    public void init() {
        super.initContext();
    }

    @Test
    public void testDelete() {
        String parameter = "{\"command\":\"UPDATE\",\"dbId\":17,\"type\":\"RDLINK\",\"objId\":308002568,\"data\":{\"feeStd\":66.99,\"pid\":308002568,\"objStatus\":\"UPDATE\"}}";
        TestUtil.run(parameter);
    }

    @Test
    public void update() {
        String parameter = "{\"command\":\"BREAK\",\"dbId\":42,\"objId\":100008435,\"data\":{\"longitude\":116.4702206995429,\"latitude\":40.08258242178863},\"type\":\"RDLINK\"}";
        parameter = "{\"command\":\"UPDATE\",\"dbId\":42,\"type\":\"RDLINK\",\"objId\":589615,\"data\":{\"kind\":3,\"pid\":589615,\"objStatus\":\"UPDATE\"}}";
        parameter = "{\"command\":\"UPDATE\",\"dbId\":42,\"type\":\"RDLINK\",\"objId\":589615,\"data\":{\"laneNum\":4,\"pid\":589615,\"objStatus\":\"UPDATE\"}}";

        try {
            Connection conn = DBConnector.getInstance().getConnectionById(42);
            RdLinkSelector selector = new RdLinkSelector(conn);
            RdLink link = (RdLink) selector.loadById(589615, false, null);
            JSONObject json = JSONObject.fromObject("{\"urban\":1,\"pid\":589615,\"objStatus\":\"UPDATE\"}");
            SpeedLimitUtils.updateRdLink(link, json, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TestUtil.run(parameter);
    }

    @Test
    public void repair() {
        String parameter = "{\"command\":\"REPAIR\",\"dbId\":42,\"objId\":100008849,\"data\":{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46721,40.083],[116.46730363368988,40.082890151613405],[116.46738,40.08272]]},\"interLinks\":[],\"interNodes\":[]},\"type\":\"RDLINK\"}";
        parameter = "{\"command\":\"CREATE\",\"dbId\":17,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.44574403762817,39.91689151500802],[116.44597470760347,39.91648007577787]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
        TestUtil.run(parameter);
    }

    @Test
    public void create() {
        String parameter = "{\"command\":\"CREATE\",\"dbId\":42,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.46699,40.08309],[116.46714,40.08249]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
        parameter = "{\"command\":\"CREATE\",\"dbId\":17,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.44574403762817,39.91689151500802],[116.44597470760347,39.91648007577787]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
//        parameter = "{\"command\":\"CREATE\",\"dbId\":17,\"data\":{\"eNodePid\":0,\"sNodePid\":0,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[116.44590497016907,39.91660762220357],[116.44618928432465,39.91627846965277]]},\"catchLinks\":[]},\"type\":\"RDLINK\"}";
        TestUtil.run(parameter);
    }

    @Test
    public void depart() {
        String parameter = "{\"command\":\"UPDOWNDEPART\",\"type\":\"RDLINK\",\"dbId\":17,\"distance\":\"13.4\",\"data\":{\"linkPids\":[575024,567326,567327,665041]}}";
        parameter = "{\"command\":\"UPDOWNDEPART\",\"type\":\"RDLINK\",\"dbId\":17,\"distance\":\"8.3\",\"data\":{\"linkPids\":[86366612,87757818,87757819,86366614,663904,663906,663908,677530,87758006,87758007,87729181]}}";
        parameter = "{\"command\":\"UPDOWNDEPART\",\"type\":\"RDLINK\",\"dbId\":17,\"distance\":\"6.6\",\"data\":{\"linkPids\":[520000327,403000339]}}";
        TestUtil.run(parameter);
    }

    @Test
    public void search() {
        String parameter = "http://192.168.4.188:8000/service/render/obj/getByTileWithGap?parameter={\"dbId\":17,\"gap\":80,\"types\":[\"RDHGWGLIMIT\"],\"z\":18,\"x\":215889,\"y\":99231}";
        try {
            TestSearch.testSearchGap(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updownDepart() {
        String parameter = "{\"command\":\"UPDOWNDEPART\",\"type\":\"RDLINK\",\"dbId\":17,\"distance\":\"6.6\",\"data\":{\"linkPids\":[209000217]}}";
        parameter = "{\"command\":\"UPDOWNDEPART\",\"type\":\"RDLINK\",\"dbId\":17,\"distance\":\"30.4\",\"data\":{\"linkPids\":[209000217]}}";
        parameter = "{\"command\":\"UPDOWNDEPART\",\"type\":\"RDLINK\",\"dbId\":17,\"distance\":\"13.4\",\"data\":{\"linkPids\":[323048,323046,319664,319663,319467,319466,319465]}}";
        TestUtil.run(parameter);
    }

    @Test
    public void move() {
        String parameter = "{\"command\":\"MOVE\",\"dbId\":17,\"objId\":304000034,\"data\":{\"longitude\":116.37564,\"latitude\":38.51548},\"type\":\"ZONENODE\"}";
        TestUtil.run(parameter);
    }
}
