package com.navinfo.dataservice.api.man.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/** 
* @ClassName:  Task 
* @author code generator
* @date 2016-06-06 06:12:30 
* @Description: TODO
*/
public class Task implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int taskId ;
	private String name;
	private int blockId=0 ;
	private String blockName;
	private int regionId=0;
	private int programId=0;
	private String programName;
	private String version;
	private int createUserId ;
	private String createUserName;
	private Timestamp createDate ;
	private int status ;
	private String descp ;
	private Timestamp planStartDate ;
	private Timestamp planEndDate ;
	private int type;
	private int programType;
	private Timestamp producePlanStartDate ;
	private Timestamp producePlanEndDate ;
	private int lot ;
	private int groupId;
	private String groupName;
	private int roadPlanTotal ;
	private int poiPlanTotal ;
	private int latest ;
	private int groupLeader =0; 
	private String workProperty ;
	
	private String workKind;
	private String overdueReason;
	private String overdueOtherReason;

	public String getWorkKind() {
		return workKind;
	}
	
	public List<Integer> getWorkKindList(){
		String[] kindList = this.workKind.split("\\|");
		List<Integer> result=new ArrayList<Integer>();
		for(int i=0;i<kindList.length;i++){
			if(kindList[i].equals("1")){
				result.add(i+1);
			}
		}
		return result;
	}


	public void setWorkKind(String workKind) {
		this.workKind = workKind;
	}
	/**
	 * 将JSONArray转成workKind，例如workKindArray=[1，2，3]，workKind=1|1|1|0
	 * @param workKindArray
	 */
	public void setWorkKind(JSONArray workKindArray){
		if(workKindArray==null||workKindArray.size()==0){
			this.workKind="0|0|0|0";
			return;
		}
		String result = "0|0|0|0";
		for(Object kind:workKindArray){
			int t=Integer.valueOf(kind.toString());
			result=result.substring(0,(t-1)*2)+"1"+result.substring((t-1)*2+1,result.length());
		}
		this.workKind= result;
	}
	
	/**
	 * workKind=0|1|0|0,num=2,则返回1，num=1，则返回0
	 * 1外业采集，2众包，3情报矢量，4多源
	 * @param num
	 * @return
	 */
	public int getSubWorkKind(int num){
		if(this.workKind==null||this.workKind.equals("")){return 0;}
		return Integer.valueOf(this.workKind.substring((num-1)*2, (num-1)*2+1));
	}

	private JSONObject geometry;
	private Map<Integer,Integer> gridIds;
	
	private String method;
	private String adminName;
	private int inforStage;
	
	public Task (){
	}

	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDescp() {
		if(null==descp){return "";}
		return descp;
	}
	public void setDescp(String descp) {
		if(null==descp){this.descp="";}
		this.descp = descp;
	}
	public Timestamp getPlanEndDate() {
		return planEndDate;
	}
	public void setPlanEndDate(Timestamp collectPlanEndDate) {
		this.planEndDate = collectPlanEndDate;
	}
	public int getLatest() {
		return latest;
	}
	public void setLatest(int latest) {
		this.latest = latest;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
//	@Override
//	public String toString() {
//		return "Task [taskId=" + taskId +",cityId="+cityId+",createUserId="+createUserId+",createDate="+createDate+",status="+taskStatus+",descp="+taskDescp+",planStartDate="+planStartDate+",planEndDate="+planEndDate+",monthEditPlanStartDate="+monthEditPlanStartDate+",monthEditPlanEndDate="+monthEditPlanEndDate+",monthEditGroupId="+monthEditGroupId+",latest="+latest+"]";
//	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
//		result = prime * result + ((cityId == null) ? 0 : cityId.hashCode());
//		result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
//		result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
//		result = prime * result + ((taskStatus == null) ? 0 : taskStatus.hashCode());
//		result = prime * result + ((taskDescp == null) ? 0 : taskDescp.hashCode());
//		result = prime * result + ((planStartDate == null) ? 0 : planStartDate.hashCode());
//		result = prime * result + ((planEndDate == null) ? 0 : planEndDate.hashCode());
//		result = prime * result + ((monthEditPlanStartDate == null) ? 0 : monthEditPlanStartDate.hashCode());
//		result = prime * result + ((monthEditPlanEndDate == null) ? 0 : monthEditPlanEndDate.hashCode());
//		result = prime * result + ((monthEditGroupId == null) ? 0 : monthEditGroupId.hashCode());
//		result = prime * result + ((latest == null) ? 0 : latest.hashCode());
//		return result;
//	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Task other = (Task) obj;
//		if (taskId == null) {
//			if (other.taskId != null)
//				return false;
//		} else if (!taskId.equals(other.taskId))
//			return false;
//		if (cityId == null) {
//			if (other.cityId != null)
//				return false;
//		} else if (!cityId.equals(other.cityId))
//			return false;
//		if (createUserId == null) {
//			if (other.createUserId != null)
//				return false;
//		} else if (!createUserId.equals(other.createUserId))
//			return false;
//		if (createDate == null) {
//			if (other.createDate != null)
//				return false;
//		} else if (!createDate.equals(other.createDate))
//			return false;
//		if (taskStatus == null) {
//			if (other.taskStatus != null)
//				return false;
//		} else if (!taskStatus.equals(other.taskStatus))
//			return false;
//		if (taskDescp == null) {
//			if (other.taskDescp != null)
//				return false;
//		} else if (!taskDescp.equals(other.taskDescp))
//			return false;
//		if (planStartDate == null) {
//			if (other.planStartDate != null)
//				return false;
//		} else if (!planStartDate.equals(other.planStartDate))
//			return false;
//		if (planEndDate == null) {
//			if (other.planEndDate != null)
//				return false;
//		} else if (!planEndDate.equals(other.planEndDate))
//			return false;
//		
//		if (monthEditPlanStartDate == null) {
//			if (other.monthEditPlanStartDate != null)
//				return false;
//		} else if (!monthEditPlanStartDate.equals(other.monthEditPlanStartDate))
//			return false;
//		if (monthEditPlanEndDate == null) {
//			if (other.monthEditPlanEndDate != null)
//				return false;
//		} else if (!monthEditPlanEndDate.equals(other.monthEditPlanEndDate))
//			return false;
//		if (monthEditGroupId == null) {
//			if (other.monthEditGroupId != null)
//				return false;
//		} else if (!monthEditGroupId.equals(other.monthEditGroupId))
//			return false;
//		if (latest == null) {
//			if (other.latest != null)
//				return false;
//		} else if (!latest.equals(other.latest))
//			return false;
//		return true;
//	}
	public String getName() {
		if(null==name){return "";}
		return name;
	}
	public void setName(String name) {
		if(null==name){this.name="";}
		this.name = name;
	}
	public Timestamp getPlanStartDate() {
		return planStartDate;
	}
	public void setPlanStartDate(Timestamp planStartDate) {
		this.planStartDate = planStartDate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}


	/**
	 * @return the blockId
	 */
	public int getBlockId() {
		return blockId;
	}


	/**
	 * @param blockId the blockId to set
	 */
	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}


	/**
	 * @return the regionId
	 */
	public int getRegionId() {
		return regionId;
	}


	/**
	 * @param regionId the regionId to set
	 */
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}


	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}


	/**
	 * @return the producePlanStartDate
	 */
	public Timestamp getProducePlanStartDate() {
		return producePlanStartDate;
	}


	/**
	 * @param producePlanStartDate the producePlanStartDate to set
	 */
	public void setProducePlanStartDate(Timestamp producePlanStartDate) {
		this.producePlanStartDate = producePlanStartDate;
	}


	/**
	 * @return the producePlanEndDate
	 */
	public Timestamp getProducePlanEndDate() {
		return producePlanEndDate;
	}


	/**
	 * @param producePlanEndDate the producePlanEndDate to set
	 */
	public void setProducePlanEndDate(Timestamp producePlanEndDate) {
		this.producePlanEndDate = producePlanEndDate;
	}


	/**
	 * @return the lot
	 */
	public int getLot() {
		return lot;
	}


	/**
	 * @param lot the lot to set
	 */
	public void setLot(int lot) {
		this.lot = lot;
	}


	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}


	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}


	/**
	 * @return the roadPlanTotal
	 */
	public int getRoadPlanTotal() {
		return roadPlanTotal;
	}


	/**
	 * @param roadPlanTotal the roadPlanTotal to set
	 */
	public void setRoadPlanTotal(int roadPlanTotal) {
		this.roadPlanTotal = roadPlanTotal;
	}


	/**
	 * @return the poiPlanTotal
	 */
	public int getPoiPlanTotal() {
		return poiPlanTotal;
	}


	/**
	 * @param poiPlanTotal the poiPlanTotal to set
	 */
	public void setPoiPlanTotal(int poiPlanTotal) {
		this.poiPlanTotal = poiPlanTotal;
	}


	/**
	 * @return the programId
	 */
	public int getProgramId() {
		return programId;
	}


	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(int programId) {
		this.programId = programId;
	}


	/**
	 * @return the groupLeader
	 */
	public int getGroupLeader() {
		return groupLeader;
	}


	/**
	 * @param groupLeader the groupLeader to set
	 */
	public void setGroupLeader(int groupLeader) {
		this.groupLeader = groupLeader;
	}


	/**
	 * @return the blockName
	 */
	public String getBlockName() {
		if(blockName==null){
			return "";
		}
		return blockName;
	}


	/**
	 * @param blockName the blockName to set
	 */
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}


	/**
	 * @return the programName
	 */
	public String getProgramName() {
		if(programName==null){
			return "";
		}
		return programName;
	}


	/**
	 * @param programName the programName to set
	 */
	public void setProgramName(String programName) {
		this.programName = programName;
	}


	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}


	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	/**
	 * @return the workProperty
	 */
	public String getWorkProperty() {
		return workProperty;
	}


	/**
	 * @param workProperty the workProperty to set
	 */
	public void setWorkProperty(String workProperty) {
		this.workProperty = workProperty;
	}


	/**
	 * @return the programType
	 */
	public int getProgramType() {
		return programType;
	}


	/**
	 * @param programType the programType to set
	 */
	public void setProgramType(int programType) {
		this.programType = programType;
	}


	/**
	 * @return the geometry
	 */
	public JSONObject getGeometry() {
		return geometry;
	}


	/**
	 * @param geometry the geometry to set
	 */
	public void setGeometry(JSONObject geometry) {
		this.geometry = geometry;
	}


	/**
	 * @return the gridIds
	 */
	public Map<Integer,Integer> getGridIds() {
		return gridIds;
	}


	/**
	 * @param gridIds the gridIds to set
	 */
	public void setGridIds(Map<Integer,Integer> gridIds) {
		this.gridIds = gridIds;
	}


	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getAdminName() {
		return adminName;
	}


	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getOverdueReason() {
		return overdueReason;
	}

	public void setOverdueReason(String overdueReason) {
		this.overdueReason = overdueReason;
	}

	public String getOverdueOtherReason() {
		return overdueOtherReason;
	}

	public void setOverdueOtherReason(String overdueOtherReason) {
		this.overdueOtherReason = overdueOtherReason;
	}

	public int getInforStage() {
		return inforStage;
	}

	public void setInforStage(int inforStage) {
		this.inforStage = inforStage;
	}

}
