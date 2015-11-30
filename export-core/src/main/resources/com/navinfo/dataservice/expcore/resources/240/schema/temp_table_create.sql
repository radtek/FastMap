CREATE TABLE TEMP_AD_ADMIN_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AD_ADMIN_RG_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AD_FACE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AD_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AD_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_SC_MODEL_MATCH_G_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_SPECIALCASE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CMG_BUILDFACE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CMG_BUILDING_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CMG_BUILDLINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CMG_BUILDNODE_${suffix} (PID  NUMBER(10)) NOLOGGING;

CREATE TABLE TEMP_CM_BUILDFACE_${suffix} (PID  NUMBER(10)) NOLOGGING;
--CREATE TABLE TEMP_CM_BUILDING_${suffix} (PID  NUMBER(10)) NOLOGGING;
--CREATE TABLE TEMP_CM_BUILDLINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
--CREATE TABLE TEMP_CM_BUILDNODE_${suffix} (PID  NUMBER(10)) NOLOGGING;


CREATE TABLE TEMP_CM_BLOCKLINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_BLOCKNODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_BLOCK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_BUILDING_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_BUILDLINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_BUILDNODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_ROADLINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_ROADNODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_CM_ROAD_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_RD_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_RD_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_RW_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_RW_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
--CREATE TABLE TEMP_FILTER_TMC_POINT_${suffix} (LOC_CODE  NUMBER(5),LOCTABLE_ID VARCHAR2(2)) NOLOGGING;
CREATE TABLE TEMP_IX_POI_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_IX_POI_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LC_FACE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LC_FEATURE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LC_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LC_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LU_FACE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LU_FEATURE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LU_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_LU_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_LINE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_POI_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_PT_POI_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_POI_GROUP_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_PLATFORM_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_STRAND_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_BRANCH_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_BRANCH_DETAIL_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_CHAIN_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_CROSSWALK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_CROSS_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_DIRECTROUTE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_GATE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_GSC_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_INTER2_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_INTER_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_RD_LANE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LANE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LANE_CONNEXITY_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LANE_TOPOLOGY_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LINK_NAMEID_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LINK_RELATEID_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LINK_TMC_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_OBJECT_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_RESTRICTION_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_RESTRICTION_DETAIL_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_ROAD2_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_ROAD_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_SE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_SIGNBOARD_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_SIGNPOST_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_TOLLGATE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_VIRCONNECT_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_VOICEGUIDE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_SPEEDLIMIT_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_ELECTRONICEYE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RTIC_INFO_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RW_FEATURE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RW_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_SAME_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_SAME_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_TMC_POINT_${suffix} (LOC_CODE  NUMBER(5),LOCTABLE_ID VARCHAR2(2)) NOLOGGING;
CREATE TABLE TEMP_ZONE_FACE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_ZONE_FEATURE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_ZONE_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_ZONE_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;

CREATE TABLE TEMP_HW_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_HW_ESTAB_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_HW_MESH_${suffix} (PID  NUMBER(10)) NOLOGGING;


CREATE TABLE TEMP_IX_HAMLET_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_POINTADDRESS_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_POI_GROUP_${suffix} (PID  NUMBER(10)) NOLOGGING;


CREATE TABLE TEMP_HW_POSITION_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_HW_ROUTE_${suffix} (PID  NUMBER(10)) NOLOGGING;

CREATE TABLE TEMP_RD_LANE_TOPO_DETAIL_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_POINTADDRESS_GROUP_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_IX_POINTADDRESS_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_MARK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_GPSTRACK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_PHOTO_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_AUDIO_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_VIDEO_${suffix} (PID  NUMBER(10)) NOLOGGING;


CREATE TABLE TEMP_RD_VARIABLE_SPEED_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_SAMEPOI_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_LINK_NAMEGID_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_SERIESPHOTO_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_ADAS_MARK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_ADAS_GPSTRACK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_ANNOTATION_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_COMPANY_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_SYSTEM_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_ADAS_LINK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_ADAS_NODE_${suffix} (PID  NUMBER(10)) NOLOGGING;

CREATE TABLE TEMP_PT_ETA_ACCESS_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_ETA_STOP_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_ETA_LINE_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_PT_TRANSFER_${suffix} (PID  NUMBER(10)) NOLOGGING;


CREATE TABLE TEMP_IX_POI_POINUM_${suffix} (POI_NUM  varchar2(50)) NOLOGGING;

CREATE TABLE TEMP_EXP_PARAMTERS_${suffix} (param_value varchar2(50),area SDO_GEOMETRY,param_name varchar2(20)) NOLOGGING;

--NEW 2012/9/3
CREATE TABLE TEMP_AD_ADMIN_GROUP_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_RD_SLOPE_${suffix} (PID  NUMBER(10)) NOLOGGING;

--NEW 200模型升级
CREATE TABLE TEMP_RD_NATGUD_JUN_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_HWY_JUNCTION_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_HWY_JCT_${suffix} (PID  NUMBER(10)) NOLOGGING;
--CREATE TABLE TEMP_RD_NODE_INFO_ADAS_${suffix} (PID  NUMBER(10)) NOLOGGING;--210模型删除
CREATE TABLE TEMP_IX_NATGUD_${suffix} (PID  NUMBER(10)) NOLOGGING;





CREATE TABLE TEMP_PARENT_CHILD_${suffix} (CHILD_PID NUMBER(10),PARENT_PID NUMBER(10)) NOLOGGING;

CREATE TABLE TEMP_PARENT_CHILD_PC_${suffix} (CHILD_PID NUMBER(10),PARENT_PID NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_POI_RP00_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_FILTER_IX_POI_NK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_IX_POI_NK_${suffix} (PID  NUMBER(10)) NOLOGGING;


CREATE TABLE TEMP_AU_GPSTRACK_GROUP_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_POI_FIELD_TASK_${suffix} (PID  NUMBER(10)) NOLOGGING;
CREATE TABLE TEMP_AU_PA_FIELD_TASK_${suffix} (PID  NUMBER(10)) NOLOGGING;

--NEW 240模型升级
CREATE TABLE TEMP_ELECEYE_GROUP_${suffix} (PID  NUMBER(10)) NOLOGGING;
