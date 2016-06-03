--
  --CREATE INDEX EXP_RD_68  ON  RD_NAME(NAME_GROUPID);

--dir:all
  --file:  exp-ad.xml
  --index:
     CREATE INDEX EXP_AD_01  ON  AD_FACE(MESH_ID);
     CREATE INDEX EXP_AD_02  ON  AD_LINK_MESH(MESH_ID,LINK_PID);
     CREATE INDEX EXP_AD_03  ON  AD_NODE_MESH(MESH_ID,NODE_PID);
     CREATE INDEX EXP_AD_04  ON  AD_FACE(REGION_ID);
     --CREATE INDEX EXP_AD_05  ON  AD_FACE(FACE_PID);
     CREATE INDEX EXP_AD_06  ON  AD_FACE_TOPO(FACE_PID,LINK_PID);
     --CREATE INDEX EXP_AD_07  ON  AD_LINK(LINK_PID);
     CREATE INDEX EXP_AD_08  ON  AD_LINK_MESH(LINK_PID);
     --CREATE INDEX EXP_AD_09  ON  AD_NODE(NODE_PID);
     CREATE INDEX EXP_AD_10  ON  AD_NODE_MESH(NODE_PID);
     CREATE INDEX EXP_AD_11  ON  AD_ADMIN_NAME(REGION_ID,NAME_ID);
     CREATE INDEX EXP_AD_12  ON  AD_ADMIN(ADMIN_ID);
     CREATE INDEX EXP_AD_13  ON  AD_ADMIN_GROUP(REGION_ID_UP);
     CREATE INDEX EXP_AD_14  ON  AD_ADMIN_PART(REGION_ID_DOWN);
     CREATE INDEX EXP_AD_15  ON  AD_ADMIN(EXTEND_ID);
     CREATE INDEX EXP_AD_16  ON  AD_ADMIN_NAME_TONE(NAME_ID);


  --file:  exp-ad_100W.xml
  --index:


  --file:  exp-ad_20W.xml
  --index:
    --CREATE INDEX EXP_AD_20  ON  AD_LINK_MESH_20W(MESH_ID);
    --CREATE INDEX EXP_AD_21  ON  AD_NODE_MESH_20W(MESH_ID,NODE_PID);
    --CREATE INDEX EXP_AD_22  ON  AD_LINK_20W(LINK_PID);
    --CREATE INDEX EXP_AD_23  ON  AD_LINK_MESH_20W(LINK_PID);
    --CREATE INDEX EXP_AD_24  ON  AD_NODE_20W(NODE_PID);
    --CREATE INDEX EXP_AD_25  ON  AD_NODE_20W(NODE_PID);


  --file:  exp-cm.xml
  --index:

    CREATE INDEX EXP_CM_01  ON  CMG_BUILDFACE(MESH_ID,BUILDING_PID);
    CREATE INDEX EXP_CM_02  ON  CMG_BUILDFACE_TOPO(FACE_PID,LINK_PID);
    CREATE INDEX EXP_CM_04  ON  CMG_BUILDLINK_MESH(MESH_ID,LINK_PID);
    CREATE INDEX EXP_CM_05  ON  CMG_BUILDING_3DICON(BUILDING_PID);
    CREATE INDEX EXP_CM_06  ON  CMG_BUILDING_3DMODEL(BUILDING_PID);
    CREATE INDEX EXP_CM_09  ON  CMG_BUILDNODE_MESH(NODE_PID);
    CREATE INDEX EXP_CM_10  ON  CMG_BUILDING_NAME(BUILDING_PID);
    CREATE INDEX EXP_CM_11  ON  CMG_BUILDING_POI(BUILDING_PID);



    CREATE INDEX EXP_CM_13  ON  CM_BUILDFACE(MESH_ID,BUILDING_PID);
    CREATE INDEX EXP_CM_14  ON  CM_BUILDFACE_TOPO(FACE_PID,LINK_PID);
    CREATE INDEX EXP_CM_15  ON  CM_BUILDNODE_MESH(NODE_PID);
    CREATE INDEX EXP_CM_16  ON  CM_BUILDLINK_MESH(MESH_ID,LINK_PID);

    CREATE INDEX EXP_CM_17  ON  CMG_BUILDFACE_TENANT(FACE_PID);
  --file:  exp-ix.xml
  --index:
    CREATE INDEX EXP_IX_01  ON  IX_POI(MESH_ID,LINK_PID);
    CREATE INDEX EXP_IX_02  ON  IX_POI_ICON(POI_PID);
    --CREATE INDEX EXP_IX_03  ON  IX_POI(PID);
    CREATE INDEX EXP_IX_04  ON  IX_POI_CONTACT(POI_PID);
    CREATE INDEX EXP_IX_06  ON  IX_POI_PARENT(PARENT_POI_PID);
    CREATE INDEX EXP_IX_07  ON  IX_POI_NAME(POI_PID);
    CREATE INDEX EXP_IX_08  ON  IX_POI_PHOTO(POI_PID);
    CREATE INDEX EXP_IX_11  ON  IX_POINTADDRESS(MESH_ID);
    CREATE INDEX EXP_IX_12  ON  IX_POINTADDRESS_NAME(PID);
    CREATE INDEX EXP_IX_10  ON  IX_POINTADDRESS_NAME_TONE(NAME_ID);
    CREATE INDEX EXP_IX_13  ON  IX_ANNOTATION(MESH_ID);
    CREATE INDEX EXP_IX_14  ON  IX_ANNOTATION_NAME(PID);
    CREATE INDEX EXP_IX_15  ON  IX_HAMLET(MESH_ID);
    CREATE INDEX EXP_IX_16  ON  IX_HAMLET_NAME(PID);
    CREATE INDEX EXP_IX_17  ON  IX_IC(MESH_ID);
    CREATE INDEX EXP_IX_18  ON  IX_CROSSPOINT(MESH_ID);
    CREATE INDEX EXP_IX_19  ON  IX_TOLLGATE(MESH_ID);
    CREATE INDEX EXP_IX_21  ON  IX_ROADNAME(MESH_ID);
    CREATE INDEX EXP_IX_22  ON  IX_POSTCODE(MESH_ID);
    --CREATE INDEX EXP_IX_24  ON  IX_POI_REFENGNAME(POI_PID);
    CREATE INDEX EXP_IX_26  ON  IX_POI_ADDRESS(POI_PID);
    CREATE INDEX EXP_IX_27  ON  IX_SAMEPOI_PART(POI_PID,GROUP_ID);

    CREATE INDEX EXP_IX_29  ON  IX_POINTADDRESS(GUIDE_LINK_PID);
    CREATE INDEX EXP_IX_30  ON  IX_POINTADDRESS(LOCATE_LINK_PID);

    CREATE INDEX EXP_IX_31  ON  IX_HAMLET(LINK_PID);
    CREATE INDEX EXP_IX_32   ON  IX_POI_CHILDREN(GROUP_ID,CHILD_POI_PID);
    CREATE INDEX EXP_IX_33   ON  IX_ANNOTATION_FLAG(PID);
    CREATE INDEX EXP_IX_34   ON  IX_POINTADDRESS_FLAG(PID);
    CREATE INDEX EXP_IX_35   ON  IX_POI_FLAG(POI_PID);
    CREATE INDEX EXP_IX_36   ON  IX_HAMLET_NAME_TONE(NAME_ID);
    CREATE INDEX EXP_IX_37   ON  IX_HAMLET_FLAG(PID);


    --   glm 1.7.0 new
    CREATE INDEX EXP_IX_38   ON  IX_POI_AUDIO(POI_PID);
    CREATE INDEX EXP_IX_39   ON  IX_POI_VIDEO(POI_PID);
    CREATE INDEX EXP_IX_40   ON  IX_POI_INTRODUCTION(POI_PID);
    CREATE INDEX EXP_IX_41   ON  IX_POI_ADVERTISEMENT(POI_PID);
    CREATE INDEX EXP_IX_42   ON  IX_POI_HOTEL(POI_PID);
    CREATE INDEX EXP_IX_43   ON  IX_POI_ATTRACTION(POI_PID);
    CREATE INDEX EXP_IX_44   ON  IX_POI_GASSTATION(POI_PID);
    CREATE INDEX EXP_IX_45   ON  IX_POI_RESTAURANT(POI_PID);
    CREATE INDEX EXP_IX_46   ON  IX_POI_CHARGINGSTATION(POI_PID);
    CREATE INDEX EXP_IX_47   ON  IX_POI_CHARGINGPLOT(POI_PID);
    CREATE INDEX EXP_IX_471   ON  IX_POI_CHARGINGPLOT_PH(POI_PID);
    CREATE INDEX EXP_IX_48   ON  IX_POINTADDRESS_CHILDREN(GROUP_ID,CHILD_PA_PID);
    CREATE INDEX EXP_IX_49   ON  IX_POI(KIND_CODE);
    CREATE INDEX EXP_IX_50   ON  AU_IX_POI(KIND_CODE);
    CREATE INDEX EXP_IX_51   ON  IX_POI_BUSINESSTIME(POI_PID);
    CREATE INDEX EXP_IX_1001  ON  IX_POI(POI_NUM);
    CREATE INDEX EXP_IX_1002   ON  IX_POI_NAME_FLAG(NAME_ID);
    CREATE INDEX EXP_IX_1003   ON  IX_POI_BUILDING(POI_PID);
    CREATE INDEX EXP_IX_1004   ON  IX_POI_CARRENTAL(POI_PID);

  --file:  exp-lc.xml
  --index:
    CREATE INDEX EXP_LC_01  ON  LC_FACE(MESH_ID);
    CREATE INDEX EXP_LC_02  ON  LC_LINK_MESH(MESH_ID,LINK_PID);
    CREATE INDEX EXP_LC_03  ON  LC_NODE_MESH(MESH_ID,NODE_PID);
    CREATE INDEX EXP_LC_04  ON  LC_FACE_TOPO(FACE_PID,LINK_PID);
    CREATE INDEX EXP_LC_06  ON  LC_LINK_KIND(LINK_PID);
    CREATE INDEX EXP_LC_08  ON  LC_FACE_NAME(FACE_PID);
    CREATE INDEX EXP_LC_9  ON  LC_FACE_NAME_20W(FACE_PID);




  --file:  exp-lc_100w.xml
  --index:
    CREATE INDEX EXP_LC_29  ON  LC_FACE_100W(MESH_ID);
    CREATE INDEX EXP_LC_10  ON  LC_LINK_MESH_100W(MESH_ID,LINK_PID);
    CREATE INDEX EXP_LC_11  ON  LC_NODE_MESH_100W(MESH_ID,NODE_PID);
    CREATE INDEX EXP_LC_12  ON  LC_FACE_TOPO_100W(FACE_PID);
    CREATE INDEX EXP_LC_13  ON  LC_LINK_KIND_100W(LINK_PID);


  --file:  exp-lc_20w.xml
  --index:
    CREATE INDEX EXP_LC_14  ON  LC_FACE_20W(MESH_ID);
    CREATE INDEX EXP_LC_15  ON  LC_LINK_MESH_20W(MESH_ID,LINK_PID);
    CREATE INDEX EXP_LC_16  ON  LC_NODE_MESH_20W(MESH_ID,NODE_PID);
    CREATE INDEX EXP_LC_17  ON  LC_FACE_TOPO_20W(FACE_PID,LINK_PID);
    CREATE INDEX EXP_LC_18  ON  LC_LINK_KIND_20W(LINK_PID);

  --file:  exp-lu.xml
  --index:
    CREATE INDEX EXP_LU_01  ON  LU_FACE(MESH_ID);
    CREATE INDEX EXP_LU_02  ON  LU_LINK_MESH(MESH_ID,LINK_PID);
    CREATE INDEX EXP_LU_03  ON  LU_NODE_MESH(MESH_ID,NODE_PID);
    CREATE INDEX EXP_LU_04  ON  LU_FACE_TOPO(FACE_PID);
    CREATE INDEX EXP_LU_05  ON  LU_LINK_KIND(LINK_PID);
    CREATE INDEX EXP_LU_06  ON  LU_FACE_NAME(FACE_PID);



  --file:  exp-pt-by-citycode.xml
  --index:


    CREATE INDEX EXP_PT_01  ON  PT_POI(CITY_CODE,LINK_PID);
    CREATE INDEX EXP_PT_02  ON  PT_LINE(CITY_CODE);
    CREATE INDEX EXP_PT_03  ON  PT_PLATFORM(CITY_CODE);
    CREATE INDEX EXP_PT_04  ON  PT_STRAND(CITY_CODE);
    CREATE INDEX EXP_PT_05  ON  PT_POI_NAME(POI_PID);

    CREATE INDEX EXP_PT_06  ON  PT_POI_NAME_TONE(NAME_ID);
    CREATE INDEX EXP_PT_07  ON  PT_POI_FLAG(POI_PID);
    CREATE INDEX EXP_PT_08  ON  PT_POI_PARENT(PARENT_POI_PID,GROUP_ID);
    CREATE INDEX EXP_PT_09  ON  PT_POI_CHILDREN(GROUP_ID);
    CREATE INDEX EXP_PT_10  ON  PT_PLATFORM_NAME(PID);

    CREATE INDEX EXP_PT_11  ON  PT_TRANSFER(POI_FIR,POI_SEC);
    CREATE INDEX EXP_PT_12  ON  PT_TRANSFER(PLATFORM_FIR,PLATFORM_SEC);
    CREATE INDEX EXP_PT_13  ON  PT_PLATFORM_ACCESS(PLATFORM_ID);
    CREATE INDEX EXP_PT_14  ON  PT_LINE_NAME(PID);
    CREATE INDEX EXP_PT_15  ON  PT_STRAND_NAME(PID);
    CREATE INDEX EXP_PT_16  ON  PT_STRAND_PLATFORM(STRAND_PID);
    CREATE INDEX EXP_PT_17  ON  PT_STRAND_SCHEDULE(STRAND_PID);
    CREATE INDEX EXP_PT_18  ON  PT_RUNTIME(STRAND_PID);

    CREATE INDEX EXP_PT_19  ON  PT_COMPANY(CITY_CODE);
    CREATE INDEX EXP_PT_20  ON  PT_SYSTEM(CITY_CODE);
    CREATE INDEX EXP_PT_21  ON  PT_ETA_COMPANY(COMPANY_ID);
    CREATE INDEX EXP_PT_22  ON  PT_ETA_SYSTEM(SYSTEM_ID);
    CREATE INDEX EXP_PT_23  ON  PT_ETA_ACCESS(POI_PID);
    CREATE INDEX EXP_PT_24  ON  PT_ETA_STOP(POI_PID);
    CREATE INDEX EXP_PT_25  ON  PT_ETA_LINE(PID);



  --file:  exp-rd.xml
  --index:
    CREATE INDEX EXP_RD_01  ON  RD_LINK(MESH_ID);
    CREATE INDEX EXP_RD_1001  ON  RD_LINK(S_NODE_PID,LINK_PID);
    CREATE INDEX EXP_RD_1002  ON  RD_LINK(E_NODE_PID,LINK_PID);
    CREATE INDEX EXP_RD_02  ON  RD_NODE_MESH(MESH_ID,NODE_PID);
    CREATE INDEX EXP_RD_03  ON  RD_INTER_NODE(PID,NODE_PID);
    CREATE INDEX EXP_RD_04  ON  RD_INTER_LINK(PID,LINK_PID);
    CREATE INDEX EXP_RD_05  ON  RD_ROAD_LINK(PID,LINK_PID);
    CREATE INDEX EXP_RD_06  ON  RD_OBJECT_NODE(PID,NODE_PID);
    CREATE INDEX EXP_RD_07  ON  RD_OBJECT_LINK(PID,LINK_PID);
    CREATE INDEX EXP_RD_08  ON  RD_OBJECT_INTER(PID,INTER_PID);
    CREATE INDEX EXP_RD_09  ON  RD_OBJECT_ROAD(PID,ROAD_PID);
    CREATE INDEX EXP_RD_10  ON  RD_OBJECT_NAME(PID);
    CREATE INDEX EXP_RD_11  ON  RD_CROSS_NODE(PID,NODE_PID);
    CREATE INDEX EXP_RD_12  ON  RD_CROSS_LINK(PID,LINK_PID);
    CREATE INDEX EXP_RD_13  ON  RD_CROSS_NAME(PID);
    CREATE INDEX EXP_RD_14  ON  RD_VIRCONNECT_TRANSIT(PID,FIR_NODE_PID,SEN_NODE_PID);
    CREATE INDEX EXP_RD_15  ON  RD_VIRCONNECT_NAME(PID);
    CREATE INDEX EXP_RD_16  ON  RD_CHAIN_LINK(PID,LINK_PID);
    CREATE INDEX EXP_RD_17  ON  RD_CHAIN_NAME(PID);
    CREATE INDEX EXP_RD_18  ON  RD_LANE_CONNEXITY(PID,IN_LINK_PID);
    CREATE INDEX EXP_RD_19  ON  RD_LANE_TOPOLOGY(CONNEXITY_PID);
    CREATE INDEX EXP_RD_20  ON  RD_LANE_VIA(TOPOLOGY_ID,LINK_PID);
    --CREATE INDEX EXP_RD_21  ON  RD_LANE_DETAIL(TOPOLOGY_ID);
    CREATE INDEX EXP_RD_22  ON  RD_GATE(IN_LINK_PID);
    CREATE INDEX EXP_RD_23  ON  RD_GATE_CONDITION(PID);
    CREATE INDEX EXP_RD_24  ON  RD_SE(IN_LINK_PID);
    CREATE INDEX EXP_RD_25  ON  RD_TOLLGATE(IN_LINK_PID);
    CREATE INDEX EXP_RD_26  ON  RD_TOLLGATE_NAME(PID);
    CREATE INDEX EXP_RD_27  ON  RD_TOLLGATE_PASSAGE(PID);
    CREATE INDEX EXP_RD_28  ON  RD_BRANCH(IN_LINK_PID,BRANCH_PID);
    CREATE INDEX EXP_RD_29  ON  RD_BRANCH_DETAIL(BRANCH_PID);
    CREATE INDEX EXP_RD_2001  ON  RD_BRANCH_SCHEMATIC(BRANCH_PID);
    CREATE INDEX EXP_RD_30  ON  RD_BRANCH_REALIMAGE(BRANCH_PID);
    CREATE INDEX EXP_RD_31  ON  RD_SERIESBRANCH(BRANCH_PID);
    CREATE INDEX EXP_RD_32  ON  RD_SIGNBOARD(BRANCH_PID,SIGNBOARD_ID);
    CREATE INDEX EXP_RD_33  ON  RD_BRANCH_VIA(BRANCH_PID,LINK_PID);
    CREATE INDEX EXP_RD_34  ON  RD_SIGNBOARD_NAME(SIGNBOARD_ID);
    CREATE INDEX EXP_RD_36  ON  RD_BRANCH_NAME(DETAIL_ID,NAME_ID);
    CREATE INDEX EXP_RD_37  ON  RD_VOICEGUIDE(IN_LINK_PID,PID);
    CREATE INDEX EXP_RD_38  ON  RD_VOICEGUIDE_DETAIL(VOICEGUIDE_PID,OUT_LINK_PID);
    CREATE INDEX EXP_RD_39  ON  RD_VOICEGUIDE_VIA(DETAIL_ID,LINK_PID);
    CREATE INDEX EXP_RD_40  ON  RD_RESTRICTION(IN_LINK_PID,PID);
    CREATE INDEX EXP_RD_41  ON  RD_RESTRICTION_DETAIL(RESTRIC_PID,OUT_LINK_PID);
    CREATE INDEX EXP_RD_42  ON  RD_RESTRICTION_VIA(DETAIL_ID,LINK_PID);
    CREATE INDEX EXP_RD_43  ON  RD_RESTRICTION_CONDITION(DETAIL_ID);
    CREATE INDEX EXP_RD_44  ON  RD_DIRECTROUTE(IN_LINK_PID);
    CREATE INDEX EXP_RD_45  ON  RD_DIRECTROUTE_VIA(PID,LINK_PID);
    CREATE INDEX EXP_RD_46  ON  RD_SAMELINK_PART(GROUP_ID,TABLE_NAME,LINK_PID);
    CREATE INDEX EXP_RD_47  ON  RD_SAMENODE_PART(GROUP_ID,TABLE_NAME,NODE_PID);
    CREATE INDEX EXP_RD_48  ON  RD_GSC_LINK(PID,TABLE_NAME,LINK_PID);


  --file:  exp-rd_link.xml
  --index:

    CREATE INDEX EXP_RD_50  ON  RD_TMCLOCATION_LINK(LINK_PID,GROUP_ID);
    CREATE INDEX EXP_RD_51  ON  RD_LINK_ZONE(LINK_PID,REGION_ID);
    CREATE INDEX EXP_RD_52  ON  RD_LINK_NAME(LINK_PID,NAME_GROUPID);

    CREATE INDEX EXP_RD_54  ON  RD_SIGNPOST(LINK_PID,PID);
    CREATE INDEX EXP_RD_55  ON  RD_CROSSWALK_INFO(NODE_PID,PID);
    CREATE INDEX EXP_RD_188  ON  RD_CROSSWALK_NODE(PID);
    CREATE INDEX EXP_RD_56  ON  RD_LANE(LINK_PID,LANE_PID);
    CREATE INDEX EXP_RD_57  ON  PT_POI(LINK_PID,PID);
    CREATE INDEX EXP_RD_58  ON  RD_SIGNPOST_PHOTO(SIGNPOST_PID,PHOTO_ID);
    CREATE INDEX EXP_RD_84  ON  RD_SIGNPOST_LINK(SIGNPOST_PID,LINK_PID);
    --CREATE INDEX EXP_RD_59  ON  RD_NAME_PHOTO(NAME_ID,PHOTO_ID);
    CREATE INDEX EXP_RD_60  ON  RD_LINK_FORM(LINK_PID);

    CREATE INDEX EXP_RD_63  ON  RD_LINK_SIDEWALK(LINK_PID);
    CREATE INDEX EXP_RD_64  ON  RD_LINK_WALKSTAIR(LINK_PID);
    CREATE INDEX EXP_RD_67  ON  RD_LINK_ADDRESS(LINK_PID);

    CREATE INDEX EXP_RD_69  ON  RD_SLOPE(LINK_PID);
    CREATE INDEX EXP_RD_691  ON  RD_SLOPE_VIA(SLOPE_PID,LINK_PID);
    CREATE INDEX EXP_RD_70  ON  RD_SPEEDLIMIT(LINK_PID);
    CREATE INDEX EXP_RD_701  ON  RD_SPEEDLIMIT(MESH_ID);
    CREATE INDEX EXP_RD_71  ON  RD_WARNINGINFO(LINK_PID);
    CREATE INDEX EXP_RD_72  ON  RD_TRAFFICSIGNAL(LINK_PID);
    CREATE INDEX EXP_RD_73  ON  RD_CROSSWALK_INFO(PID);
    CREATE INDEX EXP_RD_77  ON  RD_SIGNPOST_PHOTO(SIGNPOST_PID);
    CREATE INDEX EXP_RD_80  ON  RD_BRANCH_NAME_TONE(NAME_ID);
    CREATE INDEX EXP_RD_95  ON  ADAS_ITPLINK_GEOMETRY(LINK_PID);
	CREATE INDEX EXP_RD_96  ON  ADAS_RDLINK_GEOMETRY_DTM(LINK_PID);

  --file:  exp-rd_node.xml
  --index:
    CREATE INDEX EXP_RD_81  ON  RD_NODE_FORM(NODE_PID);
    CREATE INDEX EXP_RD_82  ON  RD_NODE_NAME(NODE_PID);
    CREATE INDEX EXP_RD_83  ON  RD_NODE_MESH(NODE_PID);

    CREATE INDEX EXP_RD_85  ON  RD_LINK_RTIC(LINK_PID);
	CREATE INDEX EXP_RD_99  ON  RD_LINK_INT_RTIC(LINK_PID);

	CREATE INDEX EXP_RD_97  ON  ADAS_RDNODE_SLOPE_DTM(NODE_PID);
	CREATE INDEX EXP_RD_98  ON  ADAS_RDNODE_INFO_DTM(NODE_PID);

    --glm 1.7.0 new and update
     CREATE INDEX EXP_RD_86  ON  RD_LANE_CONDITION(LANE_PID);
     CREATE INDEX EXP_RD_87  ON  RD_LANE_TOPO_VIA(TOPO_ID);
     CREATE INDEX EXP_RD_88  ON  RD_LANE_TOPO_DETAIL(IN_LANE_PID,OUT_LANE_PID,TOPO_ID);
     CREATE INDEX EXP_RD_89  ON  RD_TOLLGATE_COST(IN_TOLLGATE,OUT_TOLLGATE);
     CREATE INDEX EXP_RD_90  ON  RD_VARIABLE_SPEED(IN_LINK_PID);
     CREATE INDEX EXP_RD_91  ON  RD_MAINSIDE_LINK(LINK_PID,GROUP_ID);
     CREATE INDEX EXP_RD_92  ON  RD_MULTIDIGITIZED_LINK(LINK_PID,GROUP_ID);

    CREATE INDEX EXP_RD_53  ON  RD_ELECTRONICEYE(LINK_PID);
    CREATE INDEX EXP_RD_531  ON  RD_ELECTRONICEYE(MESH_ID);
    CREATE INDEX EXP_RD_532  ON  RD_ELECEYE_PART(GROUP_ID,ELECEYE_PID);
    
    CREATE INDEX EXP_RD_93  ON  RD_SPEEDBUMP(LINK_PID);
    CREATE INDEX EXP_RD_94  ON  RD_SPEEDBUMP(NODE_PID);
    CREATE INDEX EXP_RD_111  ON  RD_MILEAGEPILE(MESH_ID);
    CREATE INDEX EXP_RD_112  ON  RD_MILEAGEPILE(LINK_PID);



  --file:  exp-rw.xml
  --index:
    CREATE INDEX EXP_RW_01  ON  RW_LINK(MESH_ID);
    CREATE INDEX EXP_RW_02  ON  RW_NODE_MESH(MESH_ID,NODE_PID);
    CREATE INDEX EXP_RW_03  ON  RW_LINK(FEATURE_PID);
    CREATE INDEX EXP_RW_04  ON  RW_LINK_NAME(LINK_PID);
    --CREATE INDEX EXP_RW_05  ON  RW_NODE_NAME(NODE_PID);


  --file:  exp-rw_20W.xml
  --index:
    CREATE INDEX EXP_RW_06  ON  RW_LINK_20W(MESH_ID);
    CREATE INDEX EXP_RW_07  ON  RW_NODE_MESH_20W(MESH_ID,NODE_PID);
    CREATE INDEX EXP_RW_08  ON  RW_LINK_20W(FEATURE_PID);
    CREATE INDEX EXP_RW_09  ON  RW_LINK_NAME_20W(LINK_PID);
    --CREATE INDEX EXP_RW_10  ON  RW_NODE_NAME_20W(NODE_PID);



  --file:  exp-tmc.xml
  --index:

    CREATE INDEX EXP_TMC_04  ON  RD_TMCLOCATION_LINK(GROUP_ID);
    CREATE INDEX EXP_TMC_01  ON  TMC_POINT(LOCTABLE_ID);
    CREATE INDEX EXP_TMC_05  ON  TMC_POINT_TRANSLATENAME(TMC_ID);
    CREATE INDEX EXP_TMC_06  ON  TMC_LINE(LOCTABLE_ID);
    CREATE INDEX EXP_TMC_07  ON  TMC_LINE_TRANSLATENAME(TMC_ID);
    CREATE INDEX EXP_TMC_08  ON  TMC_AREA(LOCTABLE_ID);
    CREATE INDEX EXP_TMC_09  ON  TMC_AREA_TRANSLATENAME(TMC_ID);
    CREATE INDEX EXP_TMC_10  ON  TMC_VERSION(LOCTABLE_ID);


  --file:  exp-zone.xml
  --index:
    CREATE INDEX EXP_ZONE_01  ON  ZONE_FACE(MESH_ID);
    CREATE INDEX EXP_ZONE_08  ON  ZONE_FACE(REGION_ID);
    CREATE INDEX EXP_ZONE_02  ON  ZONE_LINK_MESH(MESH_ID,LINK_PID);
    CREATE INDEX EXP_ZONE_03  ON  ZONE_NODE_MESH(MESH_ID,NODE_PID);
    CREATE INDEX EXP_ZONE_05  ON  ZONE_FACE_TOPO(FACE_PID,LINK_PID);
    CREATE INDEX EXP_ZONE_06  ON  ZONE_LINK_MESH(LINK_PID);
    CREATE INDEX EXP_ZONE_07  ON  ZONE_NODE_MESH(NODE_PID);







--dir:au
  --file:  exp-au-by-mesh-main.xml
  --index:
    CREATE INDEX EXP_AU_01  ON  AU_MARK(MESH_ID);
    --CREATE INDEX EXP_AU_02  ON  AU_GPSTRACK(MESH_ID);
    CREATE INDEX EXP_AU_03  ON  AU_MARK_PHOTO(MARK_ID,PHOTO_ID);
    CREATE INDEX EXP_AU_04  ON  AU_MARK_AUDIO(MARK_ID,AUDIO_ID);
    CREATE INDEX EXP_AU_05  ON  AU_MARK_VIDEO(MARK_ID,VIDEO_ID);
    CREATE INDEX EXP_AU_06  ON  AU_GPSTRACK_PHOTO(GPSTRACK_ID,PHOTO_GUID);
    CREATE INDEX EXP_AU_06_1  ON  AU_GPSTRACK_GROUP_VIDEO(GROUP_ID,VIDEO_ID);
    CREATE INDEX EXP_AU_07  ON  AU_GPSRECORD(MARK_ID);
    CREATE INDEX EXP_AU_08  ON  AU_DRAFT(MARK_ID);
    CREATE INDEX EXP_AU_09  ON  AU_SERIESPHOTO(MESH_ID);
    
    CREATE INDEX EXP_AU_12  ON  AU_SERIESPHOTO(TASK_ID);
    CREATE INDEX EXP_AU_14  ON  AU_ADAS_GPSTRACK(TASK_ID,FIELD_TASK_ID);
    
    
    CREATE INDEX EXP_AU_15  ON  AU_IX_POI(GEO_TASK_ID);
    CREATE INDEX EXP_AU_16  ON  AU_IX_POI(ATT_TASK_ID);
    
    CREATE INDEX EXP_AU_17  ON  AU_IX_POINTADDRESS(GEO_TASK_ID);
    CREATE INDEX EXP_AU_18  ON  AU_IX_POINTADDRESS(ATT_TASK_ID);
    
   
    CREATE INDEX EXP_AU_19  ON  AU_IX_ANNOTATION(GEO_TASK_ID);
    CREATE INDEX EXP_AU_20  ON  AU_IX_ANNOTATION(ATT_TASK_ID);
    
    CREATE INDEX EXP_AU_21  ON  AU_PT_POI(GEO_TASK_ID);
    CREATE INDEX EXP_AU_22  ON  AU_PT_POI(ATT_TASK_ID);
    
    CREATE INDEX EXP_AU_23  ON  AU_PT_PLATFORM(GEO_TASK_ID);
    CREATE INDEX EXP_AU_24  ON  AU_PT_PLATFORM(ATT_TASK_ID);
    
    CREATE INDEX EXP_AU_25  ON  AU_PT_STRAND(GEO_TASK_ID);
    CREATE INDEX EXP_AU_26  ON  AU_PT_STRAND(ATT_TASK_ID);
    
    CREATE INDEX EXP_AU_27  ON  AU_PT_LINE(ATT_TASK_ID);
    CREATE INDEX EXP_AU_28  ON  AU_PT_COMPANY(ATT_TASK_ID);
    CREATE INDEX EXP_AU_29  ON  AU_PT_SYSTEM(ATT_TASK_ID);
    
    CREATE INDEX EXP_AU_30  ON  AU_MARK(TASK_ID,FIELD_TASK_ID);
    CREATE INDEX EXP_AU_31  ON  AU_GPSTRACK(MESH_ID,GROUP_ID);
    CREATE INDEX EXP_AU_31_2  ON  AU_GPSTRACK_GROUP(FIELD_TASK_ID);
    CREATE INDEX EXP_AU_31_3  ON  AU_GPSTRACK_GROUP(GROUP_ID,FIELD_TASK_ID);
    CREATE INDEX EXP_AU_31_4  ON  AU_GPSTRACK_GROUP(TASK_ID,FIELD_TASK_ID);
    CREATE INDEX EXP_AU_32  ON  AU_ADAS_MARK(TASK_ID,FIELD_TASK_ID);
    CREATE INDEX EXP_AU_33  ON  AU_MARK(FIELD_TASK_ID);
    CREATE INDEX EXP_AU_34  ON  AU_IX_POI(FIELD_TASK_ID);
    CREATE INDEX EXP_AU_35  ON  AU_IX_POINTADDRESS(FIELD_TASK_ID);
    CREATE INDEX EXP_AU_36  ON  AU_IX_ANNOTATION(FIELD_TASK_ID);
    
    
    CREATE INDEX EXP_AU_37  ON  AU_IX_SAMEPOI_PART(AUDATA_ID,SAMEPOI_AUDATA_ID);
 

    --dir:all
  --file:  exp-hw.xml
  --index:



    CREATE INDEX EXP_HW_05  ON  HW_ROUTE(LINK_PID,ROUTE_PID);
    CREATE INDEX EXP_HW_06  ON  HW_ROUTE(NODE_PID,ROUTE_PID);
    CREATE INDEX EXP_HW_09  ON  HW_ESTAB_CONTAIN(ESTAB_PID,GROUP_ID);
    CREATE INDEX EXP_HW_11  ON  HW_ESTAB_MAIN(ESTAB_PID,GROUP_ID);
    CREATE INDEX EXP_HW_1001  ON  HW_ESTAB_NAME(ESTAB_PID);
    CREATE INDEX EXP_HW_1002  ON  HW_ESTAB_SA(ESTAB_PID);
    

    
	CREATE INDEX EXP_HW_1003  ON  HW_POSITION(LINK_PID,POSITION_PID);
    CREATE INDEX EXP_HW_1004  ON  HW_POSITION(NODE_PID,POSITION_PID);
    CREATE INDEX EXP_HW_1005  ON  HW_ESTAB_JCT(S_ESTAB_PID);
    CREATE INDEX EXP_HW_1006  ON  HW_ESTAB_JCT(E_ESTAB_PID);
    CREATE INDEX EXP_HW_1007  ON  HW_ESTAB_ROUTE_POS(ESTAB_PID);
    CREATE INDEX EXP_HW_1008  ON  HW_ESTAB_ROUTE_POS(ROUTE_PID);
    CREATE INDEX EXP_HW_1009  ON  HW_ESTAB_ROUTE_POS(POSITION_PID);


        --dir:all
  --file:  exp-adas.xml
  --index:

    CREATE INDEX EXP_ADAS_01  ON  ADAS_LINK(MESH_ID);
    --CREATE INDEX EXP_ADAS_02  ON  ADAS_NODE(MESH_ID);
    CREATE INDEX EXP_ADAS_03  ON  ADAS_LINK_GEOMETRY(LINK_PID);
    CREATE INDEX EXP_ADAS_04  ON  ADAS_SLOPE(LINK_PID);
    CREATE INDEX EXP_ADAS_05  ON  ADAS_NODE_INFO(NODE_PID,IN_LINK_PID);
    CREATE INDEX EXP_ADAS_06  ON  ADAS_NODE_INFO(NODE_PID,OUT_LINK_PID);
    CREATE INDEX EXP_ADAS_07  ON  ADAS_LINK(LINK_PID,S_NODE_PID);
    CREATE INDEX EXP_ADAS_08  ON  ADAS_LINK(LINK_PID,E_NODE_PID);
    
    


  CREATE INDEX EXP_RD_101  ON  RD_BRANCH(OUT_LINK_PID,BRANCH_PID);
  CREATE INDEX EXP_RD_102  ON  RD_GATE(OUT_LINK_PID,PID);
  CREATE INDEX EXP_RD_103  ON  RD_TOLLGATE(OUT_LINK_PID,PID);
  CREATE INDEX EXP_RD_104  ON  RD_SE(OUT_LINK_PID,PID);
  CREATE INDEX EXP_RD_105  ON  RD_DIRECTROUTE(OUT_LINK_PID,PID);
  CREATE INDEX EXP_RD_106  ON  RD_VARIABLE_SPEED(OUT_LINK_PID,VSPEED_PID);
  CREATE INDEX EXP_RD_107  ON  RD_VARIABLE_SPEED(IN_LINK_PID,VSPEED_PID);
  --CREATE INDEX EXP_RD_108  ON  RD_NAME_TONE(NAME_ID);
  CREATE INDEX EXP_RD_109  ON  RD_LINK_SPEEDLIMIT(LINK_PID);
  CREATE INDEX EXP_RD_110  ON  RD_LINK_LIMIT(LINK_PID);



--file:  exp-au_ix.xml
  --index:

    CREATE INDEX EXP_AU_IX_01  ON  AU_IX_POI_PARENT(AUDATA_ID,GROUP_ID);
    CREATE INDEX EXP_AU_IX_02  ON  AU_IX_POI_CHILDREN(AUDATA_ID,GROUP_ID);
    CREATE INDEX EXP_AU_IX_03  ON  AU_IX_POI_CONTACT(AUDATA_ID);
    CREATE INDEX EXP_AU_IX_04  ON  AU_IX_POI_NAME(AUDATA_ID,AUNAME_ID);
    CREATE INDEX EXP_AU_IX_05  ON  AU_IX_POI_ADDRESS(AUDATA_ID);
    CREATE INDEX EXP_AU_IX_06  ON  AU_IX_POI_RESTAURANT(AUDATA_ID);
    CREATE INDEX EXP_AU_IX_07  ON  AU_IX_ANNOTATION_NAME(AUDATA_ID);
    CREATE INDEX EXP_AU_IX_08  ON  AU_IX_POINTADDRESS_PARENT(AUDATA_ID,GROUP_ID);
    CREATE INDEX EXP_AU_IX_09  ON  AU_IX_POINTADDRESS_CHILDREN(AUDATA_ID,GROUP_ID);
    CREATE INDEX EXP_AU_IX_10  ON  AU_IX_POINTADDRESS_NAME(AUDATA_ID);
    CREATE INDEX EXP_AU_IX_11  ON  AU_ADAS_GPSRECORD(MARK_ID);
    CREATE INDEX EXP_AU_IX_12  ON  AU_IX_POI_FLAG(AUDATA_ID);
    CREATE INDEX EXP_AU_IX_13  ON  AU_IX_POI_NAME_FLAG(AUNAME_ID);
    CREATE INDEX EXP_AU_IX_14  ON  AU_IX_POI_RP00(MESH_ID);



    CREATE INDEX EXP_AU_PT_01  ON  AU_PT_POI_NAME(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_02  ON  AU_PT_POI_PARENT(AUDATA_ID,GROUP_ID);
    CREATE INDEX EXP_AU_PT_03  ON  AU_PT_POI_CHILDREN(GROUP_ID);
    CREATE INDEX EXP_AU_PT_04  ON  AU_PT_ETA_ACCESS(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_05  ON  AU_PT_ETA_STOP(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_06  ON  AU_PT_PLATFORM_NAME(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_07  ON  AU_PT_POI(FIELD_TASK_ID,AUDATA_ID);
    CREATE INDEX EXP_AU_PT_08  ON  AU_PT_TRANSFER(FIELD_TASK_ID,POI_FIR,POI_SEC);
    CREATE INDEX EXP_AU_PT_09  ON  AU_PT_TRANSFER(FIELD_TASK_ID,PLATFORM_FIR,PLATFORM_SEC);
    CREATE INDEX EXP_AU_PT_10  ON  AU_PT_PLATFORM(FIELD_TASK_ID,AUDATA_ID);
    CREATE INDEX EXP_AU_PT_11  ON  AU_PT_PLATFORM_ACCESS(PLATFORM_ID);
    CREATE INDEX EXP_AU_PT_12  ON  AU_PT_LINE_NAME(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_13  ON  AU_PT_ETA_LINE(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_14  ON  AU_PT_STRAND_NAME(AUDATA_ID);
    CREATE INDEX EXP_AU_PT_15  ON  AU_PT_STRAND_PLATFORM(AUDATA_ID);
    
    
    create index EXP_RD_D01 on RD_LINK (KIND, S_NODE_PID, E_NODE_PID);
    create index EXP_RD_D02 on RD_LINK (KIND, LINK_PID);
    create index EXP_RD_D03 on RD_LINK (LINK_PID, LENGTH);
    create index EXP_IX_D01 on IX_POI (CHAIN, TYPE);
    create index EXP_IX_D02 on IX_POI_NAME (LANG_CODE, NAME_TYPE, NAME_CLASS);
    create bitmap index EXP_RD_D05 on RD_LINK (DICI_TYPE);
    create index EXP_RD_D04 on RD_LINK_FORM (LINK_PID, FORM_OF_WAY);
    create bitmap index EXP_RD_D06 on RD_LINK (PAVE_STATUS);
    create index EXP_RD_D07 on RD_LINK_LIMIT (TYPE, LINK_PID);
    create bitmap index EXP_RD_D09 on RD_LINK (LANE_CLASS);
    create bitmap index EXP_RD_D10 on RD_LINK (URBAN);
    create bitmap index EXP_RD_D11 on RD_LINK (DIGITAL_LEVEL);
    create bitmap index EXP_RD_D12 on RD_LINK (DEVELOP_STATE);
    create bitmap index EXP_RD_D13 on RD_LINK (TOLL_INFO);
    create bitmap index EXP_RD_D14 on RD_LINK (DIRECT);
    create bitmap index EXP_RD_D15 on RD_LINK (MULTI_DIGITIZED);
    create bitmap index EXP_RD_D16 on RD_LINK (IMI_CODE);
    create bitmap index EXP_RD_D17 on RD_LINK (ROUTE_ADOPT);
    create bitmap index EXP_RD_D18 on RD_LINK (APP_INFO);
    create bitmap index EXP_RD_D19 on RD_LINK (FUNCTION_CLASS);
    create index EXP_RD_D20 on RD_BRANCH (NODE_PID, BRANCH_PID, RELATIONSHIP_TYPE);
    create index EXP_RD_D22 on RD_BRANCH_DETAIL (BRANCH_TYPE, BRANCH_PID, DETAIL_ID);
    create bitmap index EXP_RD_D23 on RD_LINK (SIDEWALK_FLAG);
    create bitmap index EXP_RD_D24 on RD_LINK (IS_VIADUCT);
    create bitmap index EXP_RD_D25 on RD_LINK (WALK_FLAG);
    create bitmap index EXP_IX_D03 on IX_ANNOTATION (KIND_CODE);
    create bitmap index EXP_IX_D04 on IX_ANNOTATION (RANK);
    create index PK_CK_EXCEPTION_01 on CK_EXCEPTION (RULE_ID,INFORMATION);

	-- fm added
	CREATE INDEX EXP_RW_05  ON  RW_LINK_NAME(NAME_GROUPID);
    
    

