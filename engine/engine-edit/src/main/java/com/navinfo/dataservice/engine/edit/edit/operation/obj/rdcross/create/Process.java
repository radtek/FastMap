package com.navinfo.dataservice.engine.edit.edit.operation.obj.rdcross.create;

import com.navinfo.dataservice.engine.edit.edit.operation.AbstractCommand;
import com.navinfo.dataservice.engine.edit.edit.operation.AbstractProcess;

public class Process extends AbstractProcess<Command> {

//	private Command command;
//
//	private Result result;
//
//	private Connection conn;
//
//	private String postCheckMsg;
	
	private Check check = new Check();

	public Process(AbstractCommand command) throws Exception {
		super(command);
//		this.command = (Command) command;
//
//		this.result = new Result();
//
//		this.conn = GlmDbPoolManager.getInstance().getConnection(this.command
//				.getProjectId());

	}

//	@Override
//	public ICommand getCommand() {
//
//		return command;
//	}
//
//	@Override
//	public Result getResult() {
//
//		return result;
//	}

	@Override
	public boolean prepareData() throws Exception {

		return false;
	}
	
	@Override
	public String exeOperation() throws Exception {
		// TODO Auto-generated method stub
		return new Operation(this.getCommand(),this.getConn()).run(this.getResult());
	}

//	@Override
//	public String preCheck() throws Exception {
//		
//		check.checkSideNode(conn, command.getNodePids());
//		
//		check.checkNodeForm(conn, command.getNodePids());
//		
//		check.checkNodeInCross(conn, command.getNodePids());
//		
//		return null;
//	}

//	@Override
//	public String run() throws Exception {
//		String msg;
//		try {
//			conn.setAutoCommit(false);
//
//			this.prepareData();
//
//			String preCheckMsg = this.preCheck();
//
//			if (preCheckMsg != null) {
//				throw new Exception(preCheckMsg);
//			}
//
//			IOperation operation = new Operation(command, conn);
//
//			msg = operation.run(result);
//
//			this.recordData();
//
//			this.postCheck();
//
//			conn.commit();
//
//		} catch (Exception e) {
//
//			conn.rollback();
//
//			throw e;
//		} finally {
//			try {
//				conn.close();
//			} catch (Exception e) {
//
//			}
//		}
//
//		return msg;
//	}
//
//	@Override
//	public void postCheck() throws Exception {
//	}
//
//	@Override
//	public String getPostCheck() throws Exception {
//
//		return postCheckMsg;
//	}

//	@Override
//	public boolean recordData() throws Exception {
//		
//		LogWriter lw = new LogWriter(conn, this.command.getDbId());
//		
//		lw.generateLog(command, result);
//		
//		OperatorFactory.recordData(conn, result);
//
//		lw.recordLog(command, result);
//
//		return true;
//	}
//	
}
