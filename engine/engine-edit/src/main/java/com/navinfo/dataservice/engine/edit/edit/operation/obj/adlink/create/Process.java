package com.navinfo.dataservice.engine.edit.edit.operation.obj.adlink.create;

import java.sql.Connection;

import com.navinfo.dataservice.commons.db.DBOraclePoolManager;
import com.navinfo.dataservice.dao.glm.iface.ICommand;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.IProcess;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.log.LogWriter;
import com.navinfo.dataservice.engine.edit.edit.operation.OperatorFactory;

public class Process implements IProcess {

	private Command command;

	private Result result;

	private Connection conn;
	
	private String postCheckMsg;
	
	private Check check = new Check();
	
	public Process(ICommand command) throws Exception {
		this.command = (Command) command;

		this.result = new Result();

		this.conn = DBOraclePoolManager.getConnection(this.command
				.getProjectId());

	}
	
	@Override
	public ICommand getCommand() {
		
		return command;
	}

	@Override
	public Result getResult() {
		
		return result;
	}

	@Override
	public boolean prepareData() throws Exception {
		
		return false;
	}

	@Override
	public String preCheck() throws Exception {
		
		check.checkDupilicateNode(command.getGeometry());
		
		check.checkGLM04002(conn, command.geteNodePid(), command.getsNodePid());
		
		check.checkGLM13002(conn, command.geteNodePid(), command.getsNodePid());
		
		return null;
	}

	@Override
	public String run() throws Exception {
		String msg;
		try {
			conn.setAutoCommit(false);

			this.prepareData();

			String preCheckMsg = this.preCheck();

			if (preCheckMsg != null) {
				throw new Exception(preCheckMsg);
			}

			Operation operation = new Operation(command, check, conn);

			msg = operation.run(result);

			this.recordData();
			
			operation.breakLine();

			this.postCheck();

			conn.commit();

		} catch (Exception e) {
			
			conn.rollback();

			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				
			}
		}

		return msg;
	}

	@Override
	public void postCheck() throws Exception {
		
		check.postCheck(conn, result);
	}

	@Override
	public String getPostCheck() throws Exception {
		
		return postCheckMsg;
	}

	@Override
	public boolean recordData() throws Exception {
		
		OperatorFactory.recordData(conn, result);

		LogWriter lw = new LogWriter(conn);

		lw.recordLog(command, result);

		return true;
	}

}
