package com.navinfo.dataservice.engine.edit.operation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.exception.DataNotChangeException;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.dao.check.CheckCommand;
import com.navinfo.dataservice.dao.glm.iface.AlertObject;
import com.navinfo.dataservice.dao.glm.iface.IObj;
import com.navinfo.dataservice.dao.glm.iface.IProcess;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.OperType;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.log.LogWriter;
import com.navinfo.dataservice.engine.check.CheckEngine;

/**
 * @ClassName: Abstractprocess
 * @author MaYunFei
 * @date 上午10:54:43
 * @Description: Abstractprocess.java
 */
public abstract class AbstractProcess<T extends AbstractCommand> implements
		IProcess {
	private T command;
	private Result result;
	private Connection conn;
	protected CheckCommand checkCommand = new CheckCommand();
	protected CheckEngine checkEngine = null;
	public static Logger log = Logger.getLogger(AbstractProcess.class);

	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}

	public void setCommand(T command) {
		this.command = command;

	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	private String postCheckMsg;

	public AbstractProcess() {
		this.log = LoggerRepos.getLogger(this.log);
	}

	public AbstractProcess(AbstractCommand command, Result result,
			Connection conn) throws Exception {
		this.command = (T) command;
		if (conn != null) {
			this.conn = conn;
		}
		if (result != null) {
			this.result = result;
		} else {
			result = new Result();
		}
		// 初始化检查参数
		this.initCheckCommand();
	}

	public AbstractProcess(AbstractCommand command) throws Exception {
		this.command = (T) command;
		this.result = new Result();
		if (!command.isHasConn()) {
			this.conn = DBConnector.getInstance().getConnectionById(
					this.command.getDbId());
		}
		// 初始化检查参数
		this.initCheckCommand();
	}

	// 初始化检查参数
	public void initCheckCommand() throws Exception {
		this.checkCommand.setObjType(this.command.getObjType());
		this.checkCommand.setOperType(this.command.getOperType());
		// this.checkCommand.setGlmList(this.command.getGlmList());
		this.checkEngine = new CheckEngine(checkCommand, this.conn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.navinfo.dataservice.dao.glm.iface.IProcess#getCommand()
	 */
	@Override
	public T getCommand() {
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.navinfo.dataservice.dao.glm.iface.IProcess#getResult()
	 */
	@Override
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.navinfo.dataservice.dao.glm.iface.IProcess#prepareData()
	 */
	@Override
	public boolean prepareData() throws Exception {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.navinfo.dataservice.dao.glm.iface.IProcess#preCheck()
	 */
	@Override
	public String preCheck() throws Exception {
		// TODO Auto-generated method stub
		// createPreCheckGlmList();
		List<IRow> glmList = new ArrayList<IRow>();
		glmList.addAll(this.getResult().getAddObjects());
		glmList.addAll(this.getResult().getUpdateObjects());
		glmList.addAll(this.getResult().getDelObjects());
		this.checkCommand.setGlmList(glmList);
		String msg = checkEngine.preCheck();
		return msg;
	}

	// 构造前检查参数。前检查，如果command中的构造不满足前检查参数需求，则需重写该方法，具体可参考createPostCheckGlmList
	public void createPreCheckGlmList() {
		List<IRow> resultList = new ArrayList<IRow>();
		Result resultObj = this.getResult();
		if (resultObj.getAddObjects().size() > 0) {
			resultList.addAll(resultObj.getAddObjects());
		}
		if (resultObj.getUpdateObjects().size() > 0) {
			resultList.addAll(resultObj.getUpdateObjects());
		}
		if (resultObj.getDelObjects().size() > 0) {
			resultList.addAll(resultObj.getDelObjects());
		}
		this.checkCommand.setGlmList(resultList);
	}

	public abstract String exeOperation() throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.navinfo.dataservice.dao.glm.iface.IProcess#run()
	 */
	@Override
	public String run() throws Exception {
		String msg;
		try {
			conn.setAutoCommit(false);

			this.prepareData();

			long startPostCheckTime = System.currentTimeMillis();

			msg = exeOperation();

			long endPostCheckTime = System.currentTimeMillis();

			log.info("exeOperation use time   "
					+ String.valueOf(endPostCheckTime - startPostCheckTime));

			checkResult();

			if (!this.getCommand().getOperType().equals(OperType.DELETE)
					&& !this.getCommand().getObjType().equals(ObjType.RDBRANCH)
					&& !this.getCommand().getObjType()
							.equals(ObjType.RDELECEYEPAIR)
					&& !this.getCommand().getObjType().equals(ObjType.LUFACE)
					&& !this.getCommand().getObjType().equals(ObjType.LCFACE)) {
			}
			handleResult(this.getCommand().getObjType(), this.getCommand()
					.getOperType(), result);

			log.info("BEGIN  PRECHECK ");
			String preCheckMsg = this.preCheck();
			if (preCheckMsg != null) {
				throw new Exception(preCheckMsg);
			}

			log.info("END  PRECHECK ");
			// 维护车道信息
			this.updateRdLane();
			// 处理提示信息
			if (this.command.getInfect() == 1) {
				return this.delPrompt(this.getResult(), this.getCommand());
			}

			startPostCheckTime = System.currentTimeMillis();

			this.recordData();

			endPostCheckTime = System.currentTimeMillis();

			log.info("recordData use time   "
					+ String.valueOf(endPostCheckTime - startPostCheckTime));

			startPostCheckTime = System.currentTimeMillis();

			log.info("BEGIN  POSTCHECK ");

			this.postCheck();

			endPostCheckTime = System.currentTimeMillis();

			log.info("BEGIN  POSTCHECK ");

			log.info("post check use time   "
					+ String.valueOf(endPostCheckTime - startPostCheckTime));

			conn.commit();
			log.info("操作成功");

		} catch (Exception e) {

			conn.rollback();

			throw e;
		} finally {
			try {
				conn.close();

				System.out.print("结束\r\n");
			} catch (Exception e) {

			}
		}

		return msg;
	}

	/**
	 * 操作結果排序
	 * 
	 * @param objs
	 * @param status
	 * @return
	 */
	private Map<String, List<AlertObject>> sort(List<IObj> objs,
			ObjStatus status) {
		Map<String, List<AlertObject>> tm = new HashMap<String, List<AlertObject>>();
		for (IObj obj : objs) {

			if (tm.containsKey(ObjStatus.getCHIName(status).concat(
					obj.objType().toString()))) {

				AlertObject object = new AlertObject(obj.objType(), obj.pid(),
						status);

				List<AlertObject> list = tm.get(ObjStatus.getCHIName(status)
						.concat(obj.objType().toString()));
				list.add(object);
			} else {
				AlertObject object = new AlertObject(obj.objType(), obj.pid(),
						status);
				List<AlertObject> tem = new ArrayList<AlertObject>();
				tem.add(object);
				tm.put(ObjStatus.getCHIName(status).concat(
						obj.objType().toString()), tem);
			}

		}
		return tm;
	}

	/**
	 * 对象转化
	 * 
	 * @param rows
	 * @param objs
	 */
	private void convertObj(List<IRow> rows, List<IObj> objs) {
		for (IRow row : rows) {
			if (row instanceof IObj) {
				IObj obj = (IObj) row;
				objs.add(obj);
			}
		}
	}

	/**
	 * 删除要素提示 zhaokk
	 * 
	 * @param result
	 * @param command
	 */
	private String delPrompt(Result result, T command) {
		Map<String, List<AlertObject>> infects = new HashMap<String, List<AlertObject>>();
		List<IRow> addList = result.getAddObjects();
		List<IRow> delList = result.getDelObjects();
		List<IRow> updateList = result.getUpdateObjects();
		List<IObj> addObj = new ArrayList<IObj>();
		List<IObj> delObj = new ArrayList<IObj>();
		List<IObj> updateObj = new ArrayList<IObj>();
		// 添加对新增要素的影响
		this.convertObj(addList, addObj);
		this.convertObj(updateList, updateObj);
		this.convertObj(delList, delObj);
		infects.putAll(this.sort(addObj, ObjStatus.INSERT));
		infects.putAll(this.sort(updateObj, ObjStatus.UPDATE));
		infects.putAll(this.sort(delObj, ObjStatus.DELETE));
		log.info("删除影响：" + JSONObject.fromObject(infects).toString());
		return JSONObject.fromObject(infects).toString();
	}

	public String innerRun() throws Exception {
		String msg;
		try {

			this.prepareData();

			msg = exeOperation();

			checkResult();

			if (!this.getCommand().getOperType().equals(OperType.DELETE)
					&& !this.getCommand().getObjType().equals(ObjType.RDBRANCH)
					&& !this.getCommand().getObjType()
							.equals(ObjType.RDELECEYEPAIR)
					&& !this.getCommand().getObjType().equals(ObjType.LUFACE)
					&& !this.getCommand().getObjType().equals(ObjType.LCFACE)) {
				handleResult(this.getCommand().getObjType(), this.getCommand()
						.getOperType(), result);
			}

			String preCheckMsg = this.preCheck();

			if (preCheckMsg != null) {
				throw new Exception(preCheckMsg);
			}
			this.updateRdLane();
			// 处理提示信息
			if (this.command.getInfect() == 1) {
				return this.delPrompt(this.getResult(), this.getCommand());
			}
			this.recordData();

			this.postCheck();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				System.out.print("结束\r\n");
			} catch (Exception e) {

			}
		}

		return msg;
	}

	/**
	 * 被动维护详细车道
	 * 
	 * @throws Exception
	 */
	public void updateRdLane() throws Exception {
		com.navinfo.dataservice.engine.edit.operation.obj.rdlane.update.OpRefRelationObj operation = new com.navinfo.dataservice.engine.edit.operation.obj.rdlane.update.OpRefRelationObj(
				this.getConn(), getResult());

		operation.updateRdLane(getCommand().getObjType());
	}

	/**
	 * 检查请求是否执行了某些操作
	 * 
	 * @throws Exception
	 */
	private void checkResult() throws Exception {
		if (this.getCommand().getObjType().equals(ObjType.IXPOI)) {
			return;
		} else if (CollectionUtils.isEmpty(result.getAddObjects())
				&& CollectionUtils.isEmpty(result.getUpdateObjects())
				&& CollectionUtils.isEmpty(result.getDelObjects())) {
			throw new DataNotChangeException("属性值未发生变化");
		}
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
		// glmList.addAll(this.getResult().getDelObjects());
		this.checkCommand.setGlmList(glmList);
		this.checkEngine.postCheck();

	}

	// 构造后检查参数
	public void createPostCheckGlmList() {
		List<IRow> resultList = new ArrayList<IRow>();
		Result resultObj = this.getResult();
		if (resultObj.getAddObjects().size() > 0) {
			resultList.addAll(resultObj.getAddObjects());
		}
		if (resultObj.getUpdateObjects().size() > 0) {
			resultList.addAll(resultObj.getUpdateObjects());
		}
		this.checkCommand.setGlmList(resultList);
	}

	@Override
	public String getPostCheck() throws Exception {
		return postCheckMsg;
	}

	@Override
	public boolean recordData() throws Exception {
		LogWriter lw = new LogWriter(conn);
		lw.setUserId(command.getUserId());
		lw.setTaskId(command.getTaskId());
		lw.generateLog(command, result);
		OperatorFactory.recordData(conn, result);
		lw.recordLog(command, result);
		try {
			PoiMsgPublisher.publish(result);
		} catch (Exception e) {
			log.error(e, e);
		}
		return true;
	}

	public CheckCommand getCheckCommand() {
		return checkCommand;
	}

	public void setCheckCommand(CheckCommand checkCommand) {
		this.checkCommand = checkCommand;
	}

	public void handleResult(ObjType objType, OperType operType, Result result) {
		switch (operType) {
		case CREATE:
		case CREATESIDEROAD:
		case BREAK:
			List<Integer> addObjPidList = result.getListAddIRowObPid();
			for (int i = 0; i < result.getAddObjects().size(); i++) {
				IRow row = result.getAddObjects().get(i);
				if (objType.equals(row.objType())) {
					if (addObjPidList.get(i) != null) {
						result.setPrimaryPid(addObjPidList.get(i));
						break;
					}
				}
			}
			break;
		case UPDATE:
			for (IRow row : result.getAddObjects()) {
				result.setPrimaryPid(row.parentPKValue());
				return;
			}
			for (IRow row : result.getUpdateObjects()) {
				result.setPrimaryPid(row.parentPKValue());
				return;
			}
			for (IRow row : result.getDelObjects()) {
				result.setPrimaryPid(row.parentPKValue());
				return;
			}
			break;
		case REPAIR:
		case MOVE:
			List<Integer> allObjPidList = new ArrayList<>();
			List<IRow> allIRows = new ArrayList<>();
			allIRows.addAll(result.getUpdateObjects());
			allObjPidList.addAll(result.getListUpdateIRowObPid());
			if (CollectionUtils.isEmpty(allObjPidList)) {
				allIRows.addAll(result.getAddObjects());
				allObjPidList.addAll(result.getListAddIRowObPid());
			}
			for (int i = 0; i < allIRows.size(); i++) {
				IRow row = allIRows.get(i);
				if (objType.equals(row.objType())) {
					if (allObjPidList.get(i) != null) {
						result.setPrimaryPid(allObjPidList.get(i));
						break;
					}
				}
			}
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		if (ObjStatus.INSERT.equals(ObjStatus.INSERT)) {
			System.out.println(12423);
		}

	}
}
