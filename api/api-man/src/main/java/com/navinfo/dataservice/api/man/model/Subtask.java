package com.navinfo.dataservice.api.man.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.navinfo.dataservice.api.man.model.Task;
import com.navinfo.dataservice.api.man.model.Block;
import com.navinfo.dataservice.api.man.model.BlockMan;


/** 
* @ClassName:  Subtask 
* @author code generator
* @date 2016-06-06 07:40:15 
* @Description: TODO
*/
public class Subtask implements Serializable  {
	private Integer subtaskId ;
	private String name;
	private Integer blockId ;
	private Integer taskId ;
	private String geometry ;
	private Integer stage ;
	private Integer type ;
	private Integer createUserId ;
	private Timestamp createDate ;
	private Integer exeUserId ;
	private Integer status ;
	private Timestamp planStartDate ;
	private Timestamp planEndDate ;
	private String descp ;
	private Block block;
	private Task task;
	private BlockMan blockMan;
	private String[] gridIds;
	private Integer dbId ;
	private Integer groupId;
	
	public Subtask (){
	}
	
	public Subtask (Integer subtaskId ,
			String name,
			Integer blockId,
			Integer taskId,
			String geometry,
			Integer stage,
			Integer type,
			Integer createUserId,
			Timestamp createDate,
			Integer exeUserId,
			Integer status,
			Timestamp planStartDate,
			Timestamp planEndDate,
			String descp,
			Block block,
			Task task,
			BlockMan blockMan,
			String[] gridIds,
			Integer dbId,
			Integer groupId){
		this.subtaskId=subtaskId ;
		this.name = name;
		this.blockId=blockId ;
		this.taskId=taskId ;
		this.geometry=geometry ;
		this.stage=stage ;
		this.type=type ;
		this.createUserId=createUserId ;
		this.createDate=createDate ;
		this.exeUserId=exeUserId ;
		this.status=status ;
		this.planStartDate=planStartDate ;
		this.planEndDate=planEndDate ;
		this.descp=descp ;
		this.block=block ;
		this.task=task ;
		this.blockMan=blockMan ;
		this.gridIds = gridIds;
		this.dbId = dbId;
		this.groupId = groupId;
	}
	public int getGroupId(){
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getName(){
		if(null==name){return "";}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDbId(){
		return dbId;
	}
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	public String[] getGridIds(){
		return gridIds;
	}
	public void setGridIds(String[] gridIds) {
		this.gridIds = gridIds;
	}
	public Block getBlock(){
		return block;
	}
	public void setBlock(Block block2) {
		this.block = block2;
	}
	public BlockMan getBlockMan(){
		return blockMan;
	}
	public void setBlockMan(BlockMan blockMan) {
		this.blockMan = blockMan;
	}
	public Task getTask(){
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public Integer getSubtaskId() {
		if(null==subtaskId){return 0;}
		return subtaskId;
	}
	public void setSubtaskId(Integer subtaskId) {
		this.subtaskId = subtaskId;
	}
	public Integer getBlockId() {
		if(null==blockId){return 0;}
		return blockId;
	}
	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
	}
	public Integer getTaskId() {
		if(null==taskId){return 0;}
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public String getGeometry() {
		if(null==geometry){return "";}
		return geometry;
	}
	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
	public Integer getStage() {
		if(null==stage){return 0;}
		return stage;
	}
	public void setStage(Integer stage) {
		this.stage = stage;
	}
	public Integer getType() {
		if(null==type){return 0;}
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getCreateUserId() {
		if(null==createUserId){return 0;}
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public Integer getExeUserId() {
		if(null==exeUserId){return 0;}
		return exeUserId;
	}
	public void setExeUserId(Integer exeUserId) {
		this.exeUserId = exeUserId;
	}
	public Integer getStatus() {
		if(null==status){return 0;}
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Timestamp getPlanStartDate() {
		return planStartDate;
	}
	public void setPlanStartDate(Timestamp planStartDate) {
		this.planStartDate = planStartDate;
	}
	public Timestamp getPlanEndDate() {
		return planEndDate;
	}
	public void setPlanEndDate(Timestamp planEndDate) {
		this.planEndDate = planEndDate;
	}
	public String getDescp() {
		return descp;
	}
	public void setDescp(String descp) {
		this.descp = descp;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subtask ["
				+ "subtaskId=" + subtaskId
				+ ",name=" + this.getName() 
				+",blockId="+this.getBlockId()
				+",taskId="+this.getTaskId()
				+",geometry="+geometry
				+",stage="+stage
				+",type="+type
				+",createUserId="+createUserId
				+",createDate="+createDate
				+",exeUserId="+exeUserId
				+",status="+status
				//+",planStartDate="+planStartDate
				//+",planEndDate="+planEndDate
//				+",startDate="+startDate
//				+",endDate="+endDate
				//+ ",block=" + block.toString()
				//+ ",blockMan=" + blockMan.toString()
				//+ ",task=" + task.toString()
				+ ",dbId=" + dbId
				+ ",groupId=" + groupId
				//+ ",gridIds=" + gridIds
				+",descp="+descp+"]";
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subtaskId == null) ? 0 : subtaskId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((blockId == null) ? 0 : blockId.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
		result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result + ((exeUserId == null) ? 0 : exeUserId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((planStartDate == null) ? 0 : planStartDate.hashCode());
		result = prime * result + ((planEndDate == null) ? 0 : planEndDate.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((gridIds == null) ? 0 : gridIds.hashCode());
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		result = prime * result + ((blockMan == null) ? 0 : blockMan.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		result = prime * result + ((descp == null) ? 0 : descp.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subtask other = (Subtask) obj;
		if (subtaskId == null) {
			if (other.subtaskId != null)
				return false;
		} else if (!subtaskId.equals(other.subtaskId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (blockId == null) {
			if (other.blockId != null)
				return false;
		} else if (!blockId.equals(other.blockId))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (geometry == null) {
			if (other.geometry != null)
				return false;
		} else if (!geometry.equals(other.geometry))
			return false;
		if (stage == null) {
			if (other.stage != null)
				return false;
		} else if (!stage.equals(other.stage))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (createUserId == null) {
			if (other.createUserId != null)
				return false;
		} else if (!createUserId.equals(other.createUserId))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (exeUserId == null) {
			if (other.exeUserId != null)
				return false;
		} else if (!exeUserId.equals(other.exeUserId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (planStartDate == null) {
			if (other.planStartDate != null)
				return false;
		} else if (!planStartDate.equals(other.planStartDate))
			return false;
		if (planEndDate == null) {
			if (other.planEndDate != null)
				return false;
		} else if (!planEndDate.equals(other.planEndDate))
			return false;
		if (dbId == null) {
			if (other.dbId != null)
				return false;
		} else if (!dbId.equals(other.dbId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (gridIds == null) {
			if (other.gridIds != null)
				return false;
		} else if (!gridIds.equals(other.gridIds))
			return false;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		if (blockMan == null) {
			if (other.blockMan != null)
				return false;
		} else if (!blockMan.equals(other.blockMan))
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		if (descp == null) {
			if (other.descp != null)
				return false;
		} else if (!descp.equals(other.descp))
			return false;
		return true;
	}
	
	
	
}
