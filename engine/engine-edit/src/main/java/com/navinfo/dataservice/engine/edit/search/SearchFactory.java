package com.navinfo.dataservice.engine.edit.search;

import java.sql.Connection;

import com.navinfo.dataservice.dao.glm.iface.ISearch;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.search.*;

/**
 * 查询工厂
 */
public class SearchFactory {

	private Connection conn;

	public SearchFactory(Connection conn) {
		this.conn = conn;
	}

	private int dbId;

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	/**
	 * 创建查询类
	 * 
	 * @param ot
	 *            对象类型
	 * @return
	 */
	public ISearch createSearch(ObjType ot) {

		switch (ot) {
		case RDLINK:
			return new RdLinkSearch(conn);
		case RDRESTRICTION:
			return new RdRestrictionSearch(conn);
		case RDCROSS:
			return new RdCrossSearch(conn);
		case RDNODE:
			RdNodeSearch rdNodeSearch = new RdNodeSearch(conn);
			rdNodeSearch.setDbId(this.getDbId());
			return rdNodeSearch;
		case RDLANECONNEXITY:
			return new RdLaneConnexitySearch(conn);
		case RDSPEEDLIMIT:
			return new RdSpeedlimitSearch(conn);
		case RDSPEEDLIMIT_DEPENDENT:
			return new RdSpeedlimitSearch(conn, "DEPENDENT");
		case RDLINKSPEEDLIMIT:
			return new RdLinkSpeedLimitSearch(conn);
		case RDLINKSPEEDLIMIT_DEPENDENT:
			return new RdLinkSpeedLimitSearch(conn, "DEPENDENT");
		case RDBRANCH:
			return new RdBranchSearch(conn);
		case RDLINKINTRTIC:
			return new RdLinkIntRticSearch(conn);
		case RDLINKRTIC:
			return new RdLinkRticSearch(conn);
		case RDGSC:
			return new RdGscSearch(conn);
		case ADLINK:
			return new AdLinkSearch(conn);
		case ADFACE:
			return new AdFaceSearch(conn);
		case ADNODE:
			return new AdNodeSearch(conn);
		case RWLINK:
			return new RwLinkSearch(conn);
		case RWNODE:
			return new RwNodeSearch(conn);
		case ADADMIN:
			return new AdAdminSearch(conn);
		case IXPOI:
			return new IxPoiSearch(conn);
		case ZONENODE:
			return new ZoneNodeSearch(conn);
		case ZONELINK:
			return new ZoneLinkSearch(conn);
		case ZONEFACE:
			return new ZoneFaceSearch(conn);
		case LUNODE:
			return new LuNodeSearch(conn);
		case LULINK:
			return new LuLinkSearch(conn);
		case LUFACE:
			return new LuFaceSearch(conn);
		case RDTRAFFICSIGNAL:
			return new RdTrafficsignalSearch(conn);
		case RDELECTRONICEYE:
			return new RdElectroniceyeSearch(conn);
		case RDWARNINGINFO:
			return new RdWarninginfoSearch(conn);
		case RDLINKWARNING:
			return new RdLinkWarningSearch(conn);
		case RDSLOPE:
			return new RdSlopeSearch(conn);
		case RDGATE:
			return new RdGateSearch(conn);
		case RDINTER:
			RdInterSearch rdInterSearch = new RdInterSearch(conn);
			rdInterSearch.setDbId(this.dbId);

			return rdInterSearch;
		case LCNODE:
			return new LcNodeSearch(conn);
		case LCLINK:
			return new LcLinkSearch(conn);
		case LCFACE:
			return new LcFaceSearch(conn);
		case RDSE:
			return new RdSeSearch(conn);
		case RDSPEEDBUMP:
			return new RdSpeedbumpSearch(conn);
		case RDSAMENODE:
			return new RdSameNodeSearch(conn);
		case RDSAMELINK:
			return new RdSameLinkSearch(conn);
		case RDDIRECTROUTE:
			return new RdDirectrouteSearch(conn);
		case RDTOLLGATE:
			return new RdTollgateSearch(conn);
		case RDOBJECT:
			RdObjectSearch objectSearch = new RdObjectSearch(conn);
			objectSearch.setDbId(this.dbId);
			return objectSearch;
		case RDROAD:
			RdRoadSearch rdRoadSearch = new RdRoadSearch(conn);
			rdRoadSearch.setDbId(this.dbId);
			return new RdRoadSearch(conn);
		case RDVOICEGUIDE:
			return new RdVoiceguideSearch(conn);
		case RDVARIABLESPEED:
			return new RdVariableSpeedSearch(conn);
		case RDLANE:
			return new RdLaneSearch(conn);
		case IXSAMEPOI:
			return new IxSamepoiSearch(conn);
		case RDHGWGLIMIT:
			return new RdHgwgLimitSearch(conn);
		case RDMILEAGEPILE:
			return new RdMileagepileSearch(conn);
		case TMCPOINT:
			return new TmcPointSearch(conn);
		case RDTMCLOCATION:
			return new RdTmcLocationSearch(conn);
		case CMGBUILDNODE:
			return new CmgBuildnodeSearch(conn);
		case CMGBUILDLINK:
			return new CmgBuildlinkSearch(conn);
		case CMGBUILDFACE:
			return new CmgBuildfaceSearch(conn);
		case CMGBUILDING:
			return new CmgBuildingSearch(conn);
			// *** zl 2017.06.27 GPS_RECORD , VECTOR_TAB ,VECTOR_TAB_SUSPECT **
		case AUGPSRECORD:
			return new GpsRecordSearch(conn);
		case VECTORTAB:
			return new VectorTabSearch(conn);
		case VECTORTABSUSPECT:
			return new VectorTabSuspectSearch(conn);
		case MISSROADDIDI:
			return new MissRoadDiDiSearch(conn);
		case MISSROADTENGXUN:
			return new MissRoadTengXunSearch(conn);

		default:
			return null;
		}
	}
}
