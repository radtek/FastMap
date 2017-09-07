package com.navinfo.dataservice.scripts;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.config.SystemConfigFactory;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.dao.plus.log.LogDetail;
import com.navinfo.dataservice.dao.plus.log.ObjHisLogParser;
import com.navinfo.dataservice.dao.plus.log.PoiLogDetailStat;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.obj.BasicObj;
import com.navinfo.dataservice.dao.plus.operation.OperationResult;
import com.navinfo.dataservice.dao.plus.operation.OperationSegment;
import com.navinfo.dataservice.dao.plus.selector.ObjBatchSelector;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.Batch;
import com.navinfo.dataservice.engine.editplus.batchAndCheck.batch.BatchCommand;
import net.sf.json.JSONObject;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Title: BatchTranslate
 * @Package: com.navinfo.dataservice.scripts
 * @Description: ${TODO}
 * @Author: Crayeres
 * @Date: 9/5/2017
 * @Version: V1.0
 */
public class BatchTranslate {

    private static final Logger logger = LoggerRepos.getLogger(BatchTranslate.class);

    private BatchTranslate(){
    }

    private static ThreadLocal<BatchTranslate> threadLocal = new ThreadLocal<>();

    private volatile Map<String, Integer> successMesh;

    private volatile Map<String, Integer> failureMesh;

    private volatile Map<String, Integer> runningMesh;

    public static BatchTranslate getInstance() {
        BatchTranslate translate = threadLocal.get();
        if (null == translate) {
            translate = new BatchTranslate();
            threadLocal.set(translate);
        }
        return translate;
    }

    private Integer dbId;

    private void init(JSONObject request) {
        successMesh = new HashMap<>();
        failureMesh = new HashMap<>();
        runningMesh = new HashMap<>();
        dbId = request.optInt("dbId", Integer.MIN_VALUE);
    }

    public JSONObject execute(JSONObject request) throws InterruptedException {
        logger.info("batch translate start...");
        Long time = System.currentTimeMillis();

        JSONObject response = new JSONObject();

        init(request);

        Map<Integer, List<BasicObj>> map = loadData(request);
        if (map.isEmpty()) {
            return response;
        }
        logger.info(String.format("translate meshes with [%s]", Arrays.toString(map.keySet().toArray(new Integer[]{}))));

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(map.size() + 1), new ThreadPoolExecutor.DiscardOldestPolicy());

        for (final Map.Entry<Integer, List<BasicObj>> entry: map.entrySet()) {
            Task task = new Task(entry.getKey(), entry.getValue());
            executor.execute(task);
        }

        executor.shutdown();

        while (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
            logger.info(String.format("running mesh is [%s]", JSONObject.fromObject(runningMesh).toString()));
            logger.info(String.format("executor.getPoolSize()：%d，executor.getQueue().size()：%d，executor.getCompletedTaskCo" +
                            "unt()：%d", executor.getPoolSize(), executor.getQueue().size(), executor.getCompletedTaskCount()));
        }

        response.put("successMesh", JSONObject.fromObject(successMesh).toString());
        response.put("failureMesh", JSONObject.fromObject(failureMesh).toString());
        response.put("speedTime", (System.currentTimeMillis() - time) >> 10);

        logger.info("batch translate end...");

        return response;
    }

    private Map<Integer, List<BasicObj>> loadData(JSONObject request) {
        Map<Integer, List<BasicObj>> map = new HashMap<>();
        Connection conn = null;

        try {
            if (dbId != Integer.MIN_VALUE) {
                conn = DBConnector.getInstance().getConnectionById(dbId);
            } else {
                conn = DBConnector.getInstance().getMkConnection();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT *");
            sb.append("  FROM IX_POI IP");
            sb.append(" WHERE IP.ROW_ID IN");
            sb.append("       (SELECT LG.TB_ROW_ID FROM LOG_DETAIL LG WHERE LG.OP_TP <> 2)");

            String meshes = request.optString("meshes", "");

            if (StringUtils.isNotEmpty(meshes)) {
                sb.append("   AND IP.MESH_ID IN (");
                sb.append(org.apache.commons.lang.StringUtils.join(meshes.split(","), ","));
                sb.append(")");
            }
            List<Long> pids = new QueryRunner().query(conn, sb.toString(), new TranslateHandler());

            logger.info(String.format("load data number is %d", pids.size()));

            Set<String> tabNames = new HashSet<>();
            tabNames.add("IX_POI_NAME");
            Map<Long,BasicObj> objs =  ObjBatchSelector.selectByPids(conn, "IX_POI", tabNames,false, pids, true, true);
            Map<Long, List<LogDetail>> logs = PoiLogDetailStat.loadByRowEditStatus(conn, pids);
            ObjHisLogParser.parse(objs, logs);

            IxPoi ixPoi;
            Integer meshId;
            for (BasicObj basicObj : objs.values()) {
                ixPoi = (IxPoi) basicObj.getMainrow();
                meshId = ixPoi.getMeshId();
                if (map.containsKey(meshId)) {
                    map.get(meshId).add(basicObj);
                } else {
                    List<BasicObj> list = new ArrayList<>();
                    list.add(basicObj);
                    map.put(meshId, list);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(conn);
        }

        return map;
    }


    private void batchTranslate(Integer dbId, List<BasicObj> list) throws Exception{
        Connection conn = null;
        try {
            if (dbId != Integer.MIN_VALUE) {
                conn = DBConnector.getInstance().getConnectionById(dbId);
            } else {
                conn = DBConnector.getInstance().getMkConnection();
            }
            conn.setAutoCommit(false);

            OperationResult operationResult=new OperationResult();
            operationResult.putAll(list);

            // 执行批处理FM-BAT-20-115
            BatchCommand batchCommand=new BatchCommand();
            batchCommand.setRuleId("FM-BAT-20-115");
            Batch batch=new Batch(conn,operationResult);
            batch.operate(batchCommand);
            persistBatch(batch);
            conn.commit();
        } catch (Exception e) {
            DbUtils.rollback(conn);
            logger.error("run fm-bat-20-115 is error...", e.fillInStackTrace());
            throw e;
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void persistBatch(Batch batch) throws Exception {
        batch.persistChangeLog(OperationSegment.SG_COLUMN, 0);//FIXME:修改默认的用户
    }


    private class TranslateHandler implements ResultSetHandler<List<Long>> {
        @Override
        public List<Long> handle(ResultSet rs) throws SQLException {
            List<Long> pids = new ArrayList<>();
            rs.setFetchSize(2000);
            while (rs.next()) {
                pids.add(rs.getLong("PID"));
            }
            return pids;
        }
    }


    class Task implements Runnable {

        private Integer meshId;

        private List<BasicObj> list;

        protected Task(Integer meshId, List<BasicObj> list) {
            this.meshId = meshId;
            this.list = list;
        }

        @Override
        public void run() {
            BatchTranslate translate = BatchTranslate.getInstance();
            String key = String.valueOf(meshId);
            try {

                runningMesh.put(key, list.size());
                translate.batchTranslate(dbId, list);
                runningMesh.remove(key);

                successMesh.put(key, list.size());
            } catch (Exception e) {
                logger.error(String.format("mesh %d translate error..", meshId));
                failureMesh.put(key, list.size());
                e.fillInStackTrace();
            }

            logger.info(String.format("mesh %d translate is over..", meshId));
        }

    }

    public static void main(String[] args) {
        try{
            Map<String,String> map = new HashMap<String,String>();
            if(args.length%2!=0){
                System.out.println("ERROR:need args:-irequest xxx");
                return;
            }
            for(int i=0; i<args.length;i+=2){
                map.put(args[i], args[i+1]);
            }
            String irequest = map.get("-irequest");
            if(StringUtils.isEmpty(irequest)){
                System.out.println("ERROR:need args:-irequest xxx");
                return;
            }
            JSONObject request=null;
            JSONObject response = null;
            String dir = SystemConfigFactory.getSystemConfig().getValue("scripts.dir");
            //String dir = "D:/";
            //初始化context
            JobScriptsInterface.initContext();
            //
            request = ToolScriptsInterface.readJson(dir+"request"+ File.separator+irequest);

            BatchTranslate translate = new BatchTranslate();
            response = translate.execute(request);

            ToolScriptsInterface.writeJson(response,dir+"response"+File.separator+irequest);
            logger.debug(response);
            logger.debug("Over.");
            System.exit(0);
        }catch(Exception e){
            System.out.println("Oops, something wrong...");
            e.printStackTrace();
        }
    }
}