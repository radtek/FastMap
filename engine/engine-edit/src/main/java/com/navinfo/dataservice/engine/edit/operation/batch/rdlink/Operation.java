package com.navinfo.dataservice.engine.edit.operation.batch.rdlink;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.util.StringUtils;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.ObjType;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdAdmin;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdFace;
import com.navinfo.dataservice.dao.glm.model.lu.LuFace;
import com.navinfo.dataservice.dao.glm.model.lu.LuFaceTopo;
import com.navinfo.dataservice.dao.glm.model.lu.LuLink;
import com.navinfo.dataservice.dao.glm.model.lu.LuLinkKind;
import com.navinfo.dataservice.dao.glm.model.poi.index.IxPoi;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.same.RdSameNode;
import com.navinfo.dataservice.dao.glm.model.rd.same.RdSameNodePart;
import com.navinfo.dataservice.dao.glm.selector.ReflectionAttrUtils;
import com.navinfo.dataservice.dao.glm.selector.ad.geo.AdAdminSelector;
import com.navinfo.dataservice.dao.glm.selector.ad.geo.AdFaceSelector;
import com.navinfo.dataservice.dao.glm.selector.lu.LuFaceSelector;
import com.navinfo.dataservice.dao.glm.selector.lu.LuLinkSelector;
import com.navinfo.dataservice.dao.glm.selector.poi.index.IxPoiSelector;
import com.navinfo.dataservice.dao.glm.selector.rd.link.RdLinkSelector;
import com.navinfo.dataservice.dao.glm.selector.rd.same.RdSameNodeSelector;
import com.navinfo.dataservice.engine.edit.operation.batch.BatchRuleType;
import com.navinfo.dataservice.engine.edit.utils.GeoRelationUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * @Title: Operation.java
 * @Description: RdLink 批处理操作类
 * @author 赵凯凯
 * @date 2016年8月15日 下午2:31:50
 * @version V1.0
 */
public class Operation implements IOperation {

	private Command command;
	private Connection conn;

	// 任务范围
	Geometry subTaskGeo;

	public Operation(Command command, Connection conn) {
		this.command = command;
		this.conn = conn;

	}

	@Override
	public String run(Result result) throws Exception {
		this.batchRdLink(result);
		return null;
	}

	private void batchRdLink(Result result) throws Exception {
		if (StringUtils.isNotEmpty(this.command.getRuleId())) {
			BatchRuleType ruleType = Enum.valueOf(BatchRuleType.class,
					this.command.getRuleId());
			switch (ruleType) {
			case BATCHUBAN:
				this.batchUrbanLink(result);
				break;
			case BATCHDELURBAN:
				this.batchDelUrbanLink(result);
				break;
			case BATCHREGIONIDRDLINK:
				this.batchRegionIdRdLink(result);
				break;
			case BATCHREGIONIDPOI:
				this.batchRegionIdPoi(result);
				break;
			default:
				break;
			}
		} else {
			throw new Exception("规则号不能为空");
		}
	}

	private void batchUrbanLink(Result result) throws Exception {
		// 通过face查找符合的link
		List<RdLink> links = filterUrbanLinks();

		for (RdLink link : links) {

			if (link.getUrban() == 1) {

				continue;
			}

			link.changedFields().put("urban", 1);

			result.insertObject(link, ObjStatus.UPDATE, link.getPid());
		}

	}

	private void batchDelUrbanLink(Result result) throws Exception {

		List<RdLink> updateLinks = filterUrbanLinks();

		for (RdLink link : updateLinks) {

			if (link.getUrban() == 0) {

				continue;
			}

			link.changedFields().put("urban", 0);

			result.insertObject(link, ObjStatus.UPDATE, link.getPid());
		}

	}

	private List<RdLink> filterUrbanLinks() throws Exception {

		List<RdLink> updateLinks = new ArrayList<RdLink>();

		LuFaceSelector luFaceSelector = new LuFaceSelector(conn);

		LuFace face = (LuFace) luFaceSelector.loadById(this.command.getPid(),
				true);

		if (face.getKind() != 21) {

			return updateLinks;
		}

		// 假想线
		List<LuLink> meshLinks = new ArrayList<LuLink>();

		// 组成面的lulink
		List<LuLink> luLinks = new ArrayList<LuLink>();

		getLuLinkInfo(face, meshLinks, luLinks);

		RdLinkSelector rdLinkSelector = new RdLinkSelector(this.conn);

		List<RdLink> rdLinks = rdLinkSelector.loadLinkByFaceGeo(face.getPid(),
				face.tableName().toUpperCase(), true);

		LineString linkGeometry = null;

		Geometry faceGeometry = GeoTranslator.geojson2Jts(
				GeoTranslator.jts2Geojson(face.getGeometry()), 0.00001, 5);

		for (RdLink link : rdLinks) {

			linkGeometry = (LineString) GeoTranslator.geojson2Jts(
					GeoTranslator.jts2Geojson(link.getGeometry()), 0.00001, 5);

			IntersectionMatrix intersectionMatrix = linkGeometry
					.relate(faceGeometry);

			// Link完全在Polygon内
			if (GeoRelationUtils.Interior(intersectionMatrix)) {

				updateLinks.add(link);

				continue;
			}

			// Link的两个端点在Polygon的边界上，除端点外其他部分完全在Polygon内部
			if (GeoRelationUtils.InteriorAnd2Intersection(intersectionMatrix)) {

				// 起点在假想线上
				boolean intersectsSNode = false;
				// 终点在假想线上
				boolean intersectsENode = false;

				for (LuLink lulink : meshLinks) {

					if (linkGeometry.getStartPoint().intersects(
							lulink.getGeometry())) {

						intersectsSNode = true;
					}

					if (linkGeometry.getEndPoint().intersects(
							lulink.getGeometry())) {

						intersectsENode = true;
					}

					if (intersectsSNode && intersectsENode) {
						break;
					}
				}

				// link的端点均在假想线上
				if (intersectsSNode && intersectsENode) {

					updateLinks.add(link);

					continue;
				}

				boolean sNodeHaveSameNode = haveSameNodeByLU(
						link.getsNodePid(), luLinks);

				// 起点没有制作同一点
				if (!sNodeHaveSameNode) {
					continue;
				}

				boolean eNodeHaveSameNode = haveSameNodeByLU(
						link.geteNodePid(), luLinks);

				// 两个端点均与此Polygon的边界点制作了同一Node
				if (sNodeHaveSameNode && eNodeHaveSameNode) {

					updateLinks.add(link);
				}

				continue;
			}

			if (GeoRelationUtils.InteriorAnd1Intersection(intersectionMatrix)) {

				// 交点pid
				int intersectionNodePid = link.getsNodePid();

				// 交点几何
				Point intersectionNodeGeo = linkGeometry.getStartPoint();

				if (linkGeometry.getEndPoint().intersects(faceGeometry)) {

					intersectionNodePid = link.geteNodePid();

					intersectionNodeGeo = linkGeometry.getEndPoint();
				}

				boolean intersectsMeshLink = false;

				for (LuLink lulink : meshLinks) {

					if (intersectionNodeGeo.intersects(lulink.getGeometry())) {

						intersectsMeshLink = true;

						break;
					}
				}

				// 在假想线上
				if (intersectsMeshLink) {

					updateLinks.add(link);

					continue;
				}

				boolean haveSameNode = haveSameNodeByLU(intersectionNodePid,
						luLinks);

				// 交点制作了同一Node
				if (haveSameNode) {

					updateLinks.add(link);
				}
			}
		}

		return updateLinks;

	}

	/**
	 * 获取土地利用面的lulink信息
	 * 
	 * @param face
	 * @param meshLinks
	 *            假想线组
	 * @param luLinks
	 *            组成面的lulink组
	 * @throws Exception
	 */
	private void getLuLinkInfo(LuFace face, List<LuLink> meshLinks,
			List<LuLink> luLinks) throws Exception {
		List<Integer> linkPids = new ArrayList<Integer>();

		for (IRow row : face.getFaceTopos()) {
			LuFaceTopo luFaceTopo = (LuFaceTopo) row;

			linkPids.add(luFaceTopo.getLinkPid());
		}

		LuLinkSelector luLinkSelector = new LuLinkSelector(this.conn);

		List<IRow> luLinkRows = luLinkSelector.loadByIds(linkPids, false, true);

		for (IRow rowLuLink : luLinkRows) {

			LuLink lulink = (LuLink) rowLuLink;

			luLinks.add(lulink);

			for (IRow rowKind : lulink.getLinkKinds()) {

				LuLinkKind kind = (LuLinkKind) rowKind;

				if (kind.getKind() == 8) {

					meshLinks.add(lulink);

					break;
				}
			}
		}
	}

	private boolean haveSameNodeByLU(int rdNodePid, List<LuLink> luLinks)
			throws Exception {

		String tableName = ReflectionAttrUtils
				.getTableNameByObjType(ObjType.RDNODE);

		RdSameNodeSelector rdSameNodeSelector = new RdSameNodeSelector(
				this.conn);

		List<RdSameNode> sameNodes = rdSameNodeSelector.loadSameNodeByNodePids(
				String.valueOf(rdNodePid), tableName, false);

		// 无同一关系
		if (sameNodes.size() == 0) {
			return false;
		}

		String luNodeTableName = ReflectionAttrUtils
				.getTableNameByObjType(ObjType.LUNODE);

		Set<Integer> sameluNodes = new HashSet<Integer>();

		for (IRow rowPart : sameNodes.get(0).getParts()) {

			RdSameNodePart sameNodePart = (RdSameNodePart) rowPart;

			if (sameNodePart.getTableName().equals(luNodeTableName)) {

				sameluNodes.add(sameNodePart.getNodePid());
			}
		}

		// 无与lunode组成的同一关系
		if (sameluNodes.size() == 0) {

			return false;
		}

		for (LuLink luLink : luLinks) {
			if (sameluNodes.contains(luLink.getsNodePid())
					|| sameluNodes.contains(luLink.geteNodePid())) {

				return true;
			}
		}

		return false;
	}

	private AdFace filterAdFace() throws Exception {

		AdFaceSelector adFaceSelector = new AdFaceSelector(conn);

		AdFace face = (AdFace) adFaceSelector.loadById(this.command.getPid(),
				true, true);

		if (face.getRegionId() == 0) {

			return null;
		}

		AdAdminSelector adAdminSelector = new AdAdminSelector(conn);

		AdAdmin admin = (AdAdmin) adAdminSelector.loadById(face.getRegionId(),
				true, true);

		if (admin.getAdminType() < 0 && admin.getAdminType() > 7) {

			return null;
		}

		return face;
	}

	private void batchRegionIdRdLink(Result result) throws Exception {

		AdFace face = filterAdFace();

		if (face == null) {

			return;
		}

		RdLinkSelector rdLinkSelector = new RdLinkSelector(conn);

		List<RdLink> rdLinks = rdLinkSelector.loadLinkByFaceGeo(face.getPid(),
				face.tableName().toUpperCase(), true);

		Geometry linkGeometry = null;

		// 获取AdFace的regionId
		Integer regionId = face.getRegionId();

		Geometry faceGeometry = GeoTranslator.geojson2Jts(
				GeoTranslator.jts2Geojson(face.getGeometry()), 0.00001, 5);

		for (RdLink link : rdLinks) {

			linkGeometry = (LineString) GeoTranslator.geojson2Jts(
					GeoTranslator.jts2Geojson(link.getGeometry()), 0.00001, 5);

			IntersectionMatrix intersectionMatrix = linkGeometry
					.relate(faceGeometry);

			if (GeoRelationUtils.Interior(intersectionMatrix)
					|| GeoRelationUtils
							.InteriorAnd2Intersection(intersectionMatrix)
					|| GeoRelationUtils
							.InteriorAnd1Intersection(intersectionMatrix)) {

				link.changedFields().put("leftRegionId", face.getRegionId());

				if (link.getRightRegionId() != regionId) {
					link.changedFields().put("rightRegionId", regionId);
				}

				if (link.getLeftRegionId() != regionId) {
					link.changedFields().put("leftRegionId", regionId);
				}

			} else if (GeoRelationUtils.Boundary(intersectionMatrix)) {
				if (GeoRelationUtils.IsLinkOnLeftOfRing(linkGeometry,
						faceGeometry)) {
					if (link.getLeftRegionId() != regionId)
						link.changedFields().put("leftRegionId", regionId);
				} else {
					if (link.getRightRegionId() != regionId)
						link.changedFields().put("rightRegionId", regionId);
				}
			} else {
				continue;
			}

			result.insertObject(link, ObjStatus.UPDATE, link.getPid());
		}

	}

	private void batchRegionIdPoi(Result result) throws Exception {

		AdFace face = filterAdFace();

		if (face == null) {
			return;
		}

		IxPoiSelector ixPoiSelector = new IxPoiSelector(conn);

		List<IxPoi> pois = ixPoiSelector.loadByAdFace(face.getPid(), true);

		// 获取AdFace的regionId
		Integer regionId = face.getRegionId();

		for (IxPoi poi : pois) {

			if (poi.getRegionId() != regionId) {

				poi.changedFields().put("regionId", regionId);

				result.insertObject(poi, ObjStatus.UPDATE, poi.getPid());
			}
		}
	}

}
