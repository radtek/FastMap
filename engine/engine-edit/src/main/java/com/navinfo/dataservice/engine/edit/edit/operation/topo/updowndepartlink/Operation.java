package com.navinfo.dataservice.engine.edit.edit.operation.topo.updowndepartlink;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.StyledEditorKit.BoldAction;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.geom.JGeometryUtil;
import com.navinfo.dataservice.commons.util.JtsGeometryUtil;
import com.navinfo.dataservice.dao.glm.iface.IOperation;
import com.navinfo.dataservice.dao.glm.iface.IRow;
import com.navinfo.dataservice.dao.glm.iface.ObjStatus;
import com.navinfo.dataservice.dao.glm.iface.Result;
import com.navinfo.dataservice.dao.glm.model.ad.geo.AdLink;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLink;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkIntRtic;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkLimit;
import com.navinfo.dataservice.dao.glm.model.rd.link.RdLinkRtic;
import com.navinfo.dataservice.dao.glm.model.rd.node.RdNode;
import com.navinfo.dataservice.dao.glm.selector.rd.link.RdLinkSelector;
import com.navinfo.dataservice.dao.glm.selector.rd.node.RdNodeSelector;
import com.navinfo.dataservice.engine.edit.comm.util.operate.NodeOperateUtils;
import com.navinfo.dataservice.engine.edit.comm.util.operate.RdLinkOperateUtils;
import com.navinfo.navicommons.geo.computation.CompLineUtil;
import com.navinfo.navicommons.geo.computation.CompPolylineUtil;
import com.sun.research.ws.wadl.Link;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * @author zhaokk 上下线分离具体实现操作类
 */
public class Operation implements IOperation {
	protected Logger log = Logger.getLogger(this.getClass());
	private Command command;
	private Connection conn;

	public Operation(Command command, Connection conn) {

		this.command = command;
		this.conn = conn;
	}

	/*
	 * 上下线分离 执行类
	 */
	@Override
	public String run(Result result) throws Exception {
	
		// 1.创建分离后生成links
		this.createDepartLinks(result);
		// 2.删除soucelinks
		this.delSourceLinks(result);
		return null;
	}
	/*
	 * 创建上下分离RDLINk
	 */
	private void createDepartLinks(Result result) throws Exception {
		List<RdLink> links = command.getLinks();
		LineString[] lineStrings = new LineString[links.size()];
		// 组装LineString
		
		for (int i = 0; i < links.size(); i++) {
			lineStrings[i] = (JtsGeometryUtil.createLineString(GeoTranslator.transform(links
					.get(i).getGeometry(),0.00001,5).getCoordinates()));
		}
		// 调用分离后生成的上下线
		// 生成的线按照顺序存放在List<LineString> 前一半是右线 后一半是左线
		// 传入起点和终点Point
		Point sPoint = JtsGeometryUtil.createPoint(GeoTranslator.transform(this
				.getStartAndEndNode(links, 0).getGeometry(),0.00001,5).getCoordinate());
		RdNode snode = this.getStartAndEndNode(links, 0);
		RdNode eNode = this.getStartAndEndNode(links, 1);
		LineString[] lines = CompPolylineUtil.separate(sPoint, lineStrings,
				command.getDistance());

		// 生成分离后右线
		Map<Geometry, RdNode> map = new HashMap<Geometry, RdNode>();
		map.put(GeoTranslator.transform(snode.getGeometry(),0.00001,5), snode);
		map.put(GeoTranslator.transform(eNode.getGeometry(),0.00001,5), eNode);
		List<RdLink> upLists = new ArrayList<RdLink>();
		List<RdLink> downLists = new ArrayList<RdLink>();
		for (int i = 0; i < command.getLinkPids().size(); i++) {
			RdLink departLink = new RdLink();		
			departLink.setGeometry(lines[i]);
			RdLink currentLink = command.getLinks().get(i);
			RdLink nextLink = null;
			if (i == command.getLinkPids().size() - 1) {
				nextLink = currentLink;
			}else{
				nextLink = command.getLinks().get(i+1);
			}
			downLists.addAll(this.createDownRdLink(departLink, result,
					currentLink, nextLink, map));
			
		}// 生成分离后左线
		for (int i = lines.length-1; i >= command.getLinkPids().size(); i--) {
			RdLink departLink = new RdLink();
			departLink.setGeometry(lines[i]);
			RdLink currentLink = command.getLinks().get(lines.length-1-i);
			RdLink nextLink = null;
			if (i == command.getLinkPids().size()) {
				nextLink = currentLink;
			}else{
				nextLink = command.getLinks().get(lines.length-i);
			}
			upLists.addAll(this.createUpRdLink(departLink, result,
					currentLink, nextLink, map));
		}
		//维护挂接线信息
		this.updateAdjacentLines(lines,map,result);
		//创建内部生成线
		this.createInnerLines(lines,map,result);
		// 属性维护
		
		this.RelationLink(upLists, result, 0);
		this.RelationLink(downLists, result, 1);

	}
	/**
	 * 
	 * @param lines
	 * @param map
	 * @param result
	 * @throws Exception
	 */
	private void createInnerLines(LineString[] lines,
			Map<Geometry, RdNode> map, Result result) throws Exception {
		RdLinkSelector nodeSelector = new RdLinkSelector(conn);
		int currentPid = 0;
		Set<Boolean> flagBooleans =new HashSet<Boolean>();
		for(int i =0;i < command.getLinkPids().size() -1 ;i++){
			RdLink currentLink = command.getLinks().get(i);
			RdLink nextLink = command.getLinks().get(i+1);
			currentPid = this.getIntersectPid(currentLink, nextLink);
			List<RdLink> links = nodeSelector.loadByDepartNodePid(
					currentPid, currentLink.getPid(),
					nextLink.getPid(), true);
		    for(RdLink link :links){
		    	if(flagBooleans.size() > 1){
		    		break;
		    	}
		    	flagBooleans.add(CompPolylineUtil.isRightSide(JtsGeometryUtil.createLineString(currentLink.getGeometry().getCoordinates()), JtsGeometryUtil.createLineString(nextLink.getGeometry().getCoordinates()), JtsGeometryUtil.createLineString(link.getGeometry().getCoordinates())));
		    }
		   if (flagBooleans.size() >1 ){
			   this.createInnerLine(lines[i],lines[lines.length-i],map,result);
		   }
		}
		
	}
	/***
	 * 
	 * @param lineUpString
	 * @param lineDownString
	 * @param map
	 * @param result
	 * @throws Exception 
	 */

	private void createInnerLine(LineString lineDownString, LineString lineUpString,
			Map<Geometry, RdNode> map, Result result) throws Exception {
		Coordinate[] coordinates = new Coordinate[2];
		Coordinate sCoordinate = lineDownString.getCoordinates()[lineDownString.getCoordinates().length-1];
		Coordinate eCoordinate = lineUpString.getCoordinates()[0];
		RdNode sNode = map.get(JtsGeometryUtil.createPoint(sCoordinate));
		RdNode eNode = map.get(JtsGeometryUtil.createPoint(eCoordinate));
		RdLink innerLink =  new RdLink();
		innerLink.setGeometry(JtsGeometryUtil.createLineString(coordinates));
		RdLinkOperateUtils.addRdLink(sNode, eNode, innerLink,
				innerLink, result);
		
	}

	/**
	 * 维护挂接的线
	 * @param lines 上下线分离后的线
	 * @param map   
	 * @param result
	 * @throws Exception
	 */
	private void updateAdjacentLines(LineString[] lines, Map<Geometry, RdNode> map,Result result ) throws Exception {
		RdLinkSelector nodeSelector = new RdLinkSelector(conn);
		Point currentPoint =null;
		int currentPid = 0;
		
		for(int i = 0 ; i < command.getLinkPids().size()-1;i++){
			RdLink currentLink = command.getLinks().get(i);
			RdLink nextLink = command.getLinks().get(i+1);
			currentPid = this.getIntersectPid(currentLink, nextLink);
			currentPoint = this.getIntersectPoint(currentLink, nextLink);
			List<RdLink> links = nodeSelector.loadByDepartNodePid(
					currentPid, currentLink.getPid(),
					nextLink.getPid(), true);
			for(RdLink link :links){
				LineString targetLine=null;
		
				if(CompPolylineUtil.isRightSide(JtsGeometryUtil.createLineString(currentLink.getGeometry().getCoordinates()), JtsGeometryUtil.createLineString(nextLink.getGeometry().getCoordinates()), JtsGeometryUtil.createLineString(link.getGeometry().getCoordinates()))){
					
					targetLine = CompPolylineUtil.cut(lines[i], lines[i+1],JtsGeometryUtil.createLineString(GeoTranslator.transform(link.getGeometry(), 0.00001, 5).getCoordinates()),currentPoint,true );
				}else{
					targetLine =CompPolylineUtil.cut(lines[lines.length-i], lines[lines.length-i-1],JtsGeometryUtil.createLineString(GeoTranslator.transform(link.getGeometry(), 0.00001, 5).getCoordinates()),currentPoint,false );
				}
				if (targetLine.getCoordinate() != null){
					
					this.updateadjacentLine(targetLine,link,currentPid,map,result);
					
				}
			}
			
		}
	}

	/**
	 * 更新挂接线信息
	 * @param targetLine 挂接线分离后几何
	 * @param link
	 * @param currentPid
	 * @param map
	 * @param result
	 * @throws Exception
	 */
	private void updateadjacentLine(LineString targetLine, RdLink link,
			int currentPid, Map<Geometry, RdNode> map, Result result) throws Exception {
		JSONObject updateContent = new JSONObject();
		updateContent.put("geometry", GeoTranslator.jts2Geojson(targetLine));
		link.fillChangeFields(updateContent);
		Point point =null;
		if(map.containsKey(JtsGeometryUtil.createPoint(targetLine.getCoordinates()[0]))){
			point = JtsGeometryUtil.createPoint(targetLine.getCoordinates()[0]);
		}else{
			point = JtsGeometryUtil.createPoint(targetLine.getCoordinates()[targetLine.getCoordinates().length-1]);
		}
		if(currentPid == link.getsNodePid()){
			updateContent.put("sNodePid",map.get(point).getPid());
		}else{
			updateContent.put("eNodePid",map.get(point).getPid());
		}
		link.fillChangeFields(updateContent);
		result.insertObject(link, ObjStatus.UPDATE, link.getPid());
		 
		
	}

	// 上下线属性维护
	// upDownFlag 新生成线标志0上线(左线) 1下线(右线)
	private void RelationLink(List<RdLink> links, Result result, int upDownFlag) {

		for (RdLink link : links) {
			// 1.属性维护
			this.relationNatureForlink(link);
			// 2.限制信息
			this.relationLimitForLink(link);
			// 3.限速信息
			// 上下线分离后的新link都清空原先link的速度限制值
			link.getSpeedlimits().clear();
			// 4同一线关系维护
			// 5同一点关系维护
			// 6 对RTIC信息的维护
			this.relationRticForLink(link, upDownFlag);
			// 7对TMC的维护
			// 8对线门牌处理
			// 9对详细车道信息的维护
			// 10关联link的维护
			// 11上下线分离目标link
			// 12邮编索引的维护
			// 13上下线分离后对点限速关联link的维护
			// 14上下线分离后对电子眼关联link的维护
			// 15对点-线-线关系的维护
			// 16 对点线关系的维护
			// 17对线点线关系（车信，交限，分歧，语音引导，顺行）信息的维护
			// 18.逆方向
			if (upDownFlag == 0) {
				link.setDirect(3);
			} else {
				link.setDirect(2);
			}
			
			result.insertObject(link, ObjStatus.INSERT, link.getPid());
		}
	}

	// 速度限制值、行政区划值、人行便道、阶梯、总车道数，左车道数、右车道数、车道等级初始化，上下线分离属性
	private void relationNatureForlink(RdLink link) {
		link.setMultiDigitized(1);
		link.setLaneNum(2);
		link.setLaneLeft(0);
		link.setLaneRight(0);
		link.setLaneClass(2);
		link.setWalkstairFlag(0);
		link.setSidewalkFlag(0);
	}

	// link的限制类型为单行限制、穿行限制、车辆限制时，上下线分离后新link自动删除对应限制类型下的道路限制信息子表
	private void relationLimitForLink(RdLink link) {
		for (IRow row : link.getLimits()) {
			RdLinkLimit limit = (RdLinkLimit) row;
			if (limit.getType() == 1 || limit.getType() == 2
					|| limit.getType() == 3) {
				link.getLimits().remove(limit);
			}
		}
	}

	// 如果双方向道路变上下线分离，则将上行方向的RTIC信息作为“上行”赋到分离后通行方向与上行方向相同的link上；
	// 将下行方向的RTIC信息作为“上行”的RTIC信息赋到分离后通行方向与原RTIC上行方向相反的link上；
	// RTIC方向值由程序根据制作RTIC时的方向与划线方向的关系自动计算，其余信息继承原link；
	// 单方向道路变上下线分离，将单方向道路上的RTIC信息赋值给与该单方向道路通行方向相同的一侧道路上。
	private void relationRticForLink(RdLink link, int upDownFlag) {
		// 道路:LINK 与 RTIC 关系表（车导客户用）
		for (IRow row : link.getRtics()) {
			RdLinkRtic linkRtic = (RdLinkRtic) row;
			if (link.getDirect() == 1) {
				if (upDownFlag == 0) {
					if (linkRtic.getUpdownFlag() == 0) {
						link.getRtics().remove(linkRtic);
					}
					linkRtic.setRticDir(2);
				} else {
					if (linkRtic.getUpdownFlag() == 1) {
						link.getRtics().remove(linkRtic);
					}
					linkRtic.setRticDir(3);

				}
			}
			if (upDownFlag == 0) {
				if (link.getDirect() == 2 && link.getDirect() == 3) {
					link.getRtics().clear();
				}
			}
		}

		// 道路:LINK 与 RTIC 关系表（互联网客户用）
		for (IRow row : link.getIntRtics()) {
			RdLinkIntRtic linkRtic = (RdLinkIntRtic) row;
			if (link.getDirect() == 1) {
				if (upDownFlag == 0) {
					if (linkRtic.getUpdownFlag() == 0) {
						link.getRtics().remove(linkRtic);
					}
					linkRtic.setRticDir(2);
				} else {
					if (linkRtic.getUpdownFlag() == 1) {
						link.getRtics().remove(linkRtic);
					}
					linkRtic.setRticDir(3);

				}
			}
			if (upDownFlag == 0) {
				if (link.getDirect() == 2 && link.getDirect() == 3) {
					link.getRtics().clear();
				}
			}
		}

	}
	/*
	 * @param departLink 分离后生成的link
	 * @param result    
	 * @param sourceLink 分离前对应原始link
	 * @param sourceNextLink 分离前对应原始link下一条link
	 * @param map
	 * @return  返回生成上(左边)对应links
	 * @throws Exception
	 */
	private List<RdLink> createUpRdLink(RdLink departLink, Result result,
			RdLink sourceLink, RdLink sourceNextLink, Map<Geometry, RdNode> map)
			throws Exception {
		RdLinkSelector linkSelector = new RdLinkSelector(conn);
		// 查找分离前link起始点上挂接的link
		RdNode sNode = null;
		RdNode eNode = null;
		if(sourceLink.getPid() == sourceNextLink.getPid()){
			return this.createEndRdLink(departLink, result, sourceLink, sourceNextLink, map);
		}
		List<RdLink> links = linkSelector.loadByDepartNodePid
				(this.getIntersectPid(sourceLink, sourceNextLink), sourceLink.getPid(), sourceNextLink.getPid(), true);
		// 如果对应起点没有挂接的link
		//对于上(左)线需要生成新的node
		if (links.size() <= 0) {
			sNode = this.getNodeByDepartGeo(departLink, 1,map ,result);
			eNode = this.getNodeByDepartGeo(departLink, 0, map, result);
		}
		//如果对应起点有挂接的link
		//且没有下线挂接的link 需要修改原有node的属性
		//如果至少有一条下挂的link对于上(左)线需要生成新的node
		else {
			sNode = this.getDepartRdlinkNode(links, departLink, sourceLink, sourceNextLink, 1,1, map, result);
			eNode = this.getDepartRdlinkNode(links, departLink, sourceLink, sourceNextLink,0,1, map, result);
		}
		return RdLinkOperateUtils.addRdLink(sNode, eNode, departLink,
				sourceLink, result);
	}
	/*
	 * @param departLink 分离后生成的link
	 * @param result    
	 * @param sourceLink 分离前对应原始link
	 * @param sourceNextLink 分离前对应原始link下一条link
	 * @param map
	 * @return  返回生成下(右边)对应links
	 * @throws Exception
	 */
	private List<RdLink> createDownRdLink(RdLink departLink, Result result,
			RdLink sourceLink, RdLink sourceNextLink, Map<Geometry, RdNode> map)
			throws Exception {
		RdLinkSelector linkSelector = new RdLinkSelector(conn);

		// 查找分离前link起始点上挂接的link
		RdNode sNode = null;
		RdNode eNode = null;
		if(sourceLink.getPid() == sourceNextLink.getPid()){
			return this.createEndRdLink(departLink, result, sourceLink, sourceNextLink, map);
		}
		List<RdLink> links = linkSelector.loadByDepartNodePid
				(this.getIntersectPid(sourceLink, sourceNextLink), sourceLink.getPid(), sourceNextLink.getPid(), true);
		// 如果对应起点上
		if (links.size() <= 0) {
			sNode = this.updateAdNodeForTrack(departLink,
					sourceLink.getsNodePid(),map, result, 1);
			eNode = this.updateAdNodeForTrack(departLink,
					sourceLink.geteNodePid(),map, result, 0);

		} else {
			sNode = this.getDepartRdlinkNode(links, departLink, sourceLink, sourceNextLink, 1, 0, map, result);
			eNode=  this.getDepartRdlinkNode(links, departLink, sourceLink, sourceNextLink, 0, 0, map, result);
		}
		return RdLinkOperateUtils.addRdLink(sNode, eNode, departLink,
				sourceLink, result);
	}
	/**
	 * 
	 * @param departLink
	 * @param result
	 * @param sourceLink
	 * @param sourceNextLink
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private List<RdLink> createEndRdLink(RdLink departLink, Result result,
			RdLink sourceLink, RdLink sourceNextLink, Map<Geometry, RdNode> map) throws Exception{
		RdNode sNode = null;
		RdNode eNode = null;
			sNode = map.get(JtsGeometryUtil.createPoint(departLink.getGeometry()
					.getCoordinates()[0]));
			eNode = map.get(JtsGeometryUtil.createPoint(departLink.getGeometry()
					.getCoordinates()[departLink.getGeometry()
					.getCoordinates().length - 1]));
			return RdLinkOperateUtils.addRdLink(sNode, eNode, departLink,
					sourceLink, result);
	}
	/*
	 * @param links 当前sourceLink 起点或终点挂接的link
	 * @param departLink 分离后的link
	 * @param sourceLink 分立前对应的link
	 * @param sourceNextLink 分离前对应link的下一条link
	 * @param flag 生成node按照线几何起始和终点node 1 起点  0 终点
	 * @param flagUpDown 生成上(左)下(右)线标志 1上 0下
	 * @param map   存放已经生成的adnode 
	 * @param result 
	 * @return 返回新生成的AdNode
	 * @throws Exception
	 */
	private RdNode getDepartRdlinkNode(List<RdLink> links,RdLink departLink,RdLink sourceLink,RdLink sourceNextLink,int flag,int flagUpDown,Map<Geometry, RdNode> map,Result result) throws Exception{
		List<Boolean> flagBooleans = new ArrayList<Boolean>();
		RdNode node = null;
		int currentPid = this.getIntersectPid(sourceLink, sourceNextLink);
		for (RdLink link : links) {
			flagBooleans.add(this.isRightSide(sourceLink, sourceNextLink, link));
		}
		if (flagBooleans.contains(true)){
			if(flagUpDown == 1){
				node =this.getNodeByDepartGeo(departLink, flag,map, result); 
			}else{
				node = this.updateAdNodeForTrack(departLink,
						currentPid, map,result, 1);
			}

		}
		if (!flagBooleans.contains(true)){
			if(flagUpDown == 1){
				node = this.updateAdNodeForTrack(departLink,
						currentPid,map, result, 1);
			}else{
				node =this.getNodeByDepartGeo(departLink, flag,map, result);
			}
		}

		return node;
	}
	/*
	 * 根据分离后的几何属性生成新的node
	 * @param departLink 分离后的link
	 * @param map 已经生成的node
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private RdNode getNodeByDepartGeo(RdLink departLink,int flag,Map<Geometry, RdNode> map,Result result) throws Exception{
		RdNode node = null;
		Geometry geometry =null;
		Coordinate coordinate = null;
		if(flag == 1){
			coordinate = departLink.getGeometry().getCoordinates()[0];
		}else{
			coordinate = departLink.getGeometry().getCoordinates()[ departLink.getGeometry().getCoordinates().length-1];
		}
		geometry = JtsGeometryUtil.createPoint(coordinate);
		if (map.containsKey(geometry)) {
			node = map.get(geometry);
		} else {
			node = NodeOperateUtils.createNode(coordinate.x,coordinate.y);
			map.put(GeoTranslator.transform(node.getGeometry(),0.00001,5), node);
			result.insertObject(node, ObjStatus.INSERT, node.getPid());
		}
		return node;
	}

	// 更新分离后要移动node的几何属性
	//flag ==1 根据分离后线几何属性的最后点属性更新node的几何属性
	//flag ==0根据分离后线几何属性的开始点属性更新node的几何属性
	private RdNode updateAdNodeForTrack(RdLink link, int nodePid, Map<Geometry, RdNode> map,Result result, int flag) throws Exception {
		JSONObject updateContent = new JSONObject();
		RdNodeSelector nodeSelector = new RdNodeSelector(conn);
		RdNode node = (RdNode) nodeSelector.loadById(nodePid, true);
		Geometry geometry = null;
		if (flag == 1) {
			geometry = JtsGeometryUtil.createPoint(link.getGeometry()
					.getCoordinates()[0]);
			if(map.containsKey(geometry)){
				return map.get(geometry);
			}
			updateContent.put("geometry", GeoTranslator.jts2Geojson(
					geometry));
		} else {
			geometry = JtsGeometryUtil.createPoint(link.getGeometry()
					.getCoordinates()[link.getGeometry()
					.getCoordinates().length - 1]);
			if(map.containsKey(geometry)){
				return map.get(geometry);
			}
			updateContent.put("geometry", GeoTranslator.jts2Geojson(geometry));
		}
		if(!map.containsKey(geometry)){
			map.put(geometry,  node);
		}
		node.fillChangeFields(updateContent);
		result.insertObject(node, ObjStatus.UPDATE, node.getPid());
		return node;
	}

	// 获取联通线的起点和终点
	// 0 起点  1 终点
	// 根据联通线的第一条link和第二条link算出起点Node
	// 根据联通线最后一条link和倒数第二条link算出终点Node
	private RdNode getStartAndEndNode(List<RdLink> links, int flag)
			throws Exception {
		RdLink fristLink = null;
		RdLink secondLink = null;
		RdNode node = null;
		if (flag == 0) {
			fristLink = links.get(0);
			secondLink = links.get(1);
		}
		if (flag == 1) {
			fristLink = links.get(links.size() - 1);
			secondLink = links.get(links.size() - 2);
		}
		List<Integer> nodes = new ArrayList<Integer>();
		nodes.add(secondLink.getsNodePid());
		nodes.add(secondLink.geteNodePid());
		if (nodes.contains(fristLink.getsNodePid())) {
			IRow row = new RdNodeSelector(conn).loadById(
					fristLink.geteNodePid(), true);
			node = (RdNode) row;
		}
		if (nodes.contains(fristLink.geteNodePid())) {
			IRow row = new RdNodeSelector(conn).loadById(
					fristLink.getsNodePid(), true);
			node = (RdNode) row;

		}
		return node;
	}
	/**
	 * 删除上下线分离原始线
	 * @param result
	 */
	private void delSourceLinks(Result result){
		for(RdLink link:command.getLinks()){
			result.insertObject(link, ObjStatus.DELETE, link.pid());
		}
	}
	/**
	 * @param startLine
	 * @param endLine
	 * @param adjacentLine
	 * @return
	 * @throws Exception
	 */
	private boolean isRightSide(RdLink startLine, RdLink endLine,
			RdLink adjacentLine) throws Exception {
		return CompPolylineUtil.isRightSide(JtsGeometryUtil.createLineString(startLine.getGeometry().getCoordinates()), JtsGeometryUtil.createLineString(endLine.getGeometry().getCoordinates()), 
	JtsGeometryUtil.createLineString(startLine.getGeometry().getCoordinates()));
	}
	/**
	 *  获取两个link相交的pid
	 * @param fristLink
	 * @param secondLink
	 * @return
	 */
	private int getIntersectPid (RdLink fristLink,RdLink secondLink){
		if(fristLink.getsNodePid()  == secondLink.getsNodePid()||fristLink.getsNodePid()  == secondLink.geteNodePid()){
			return fristLink.getsNodePid();
		}else{
			return fristLink.geteNodePid();
		}
		
	}
	/**
	 * 获取两个link相交的Poit
	 * @param fristLink
	 * @param secondLink
	 * @return
	 */
	private Point getIntersectPoint (RdLink fristLink,RdLink secondLink){
		if(fristLink.getsNodePid()  == secondLink.getsNodePid()||fristLink.getsNodePid()  == secondLink.geteNodePid()){
			return JtsGeometryUtil.createPoint(GeoTranslator.transform(fristLink.getGeometry(), 0.00001, 5).getCoordinates()[0]);
		}else{
			return JtsGeometryUtil.createPoint(GeoTranslator.transform(fristLink.getGeometry(), 0.00001, 5).getCoordinates()[GeoTranslator.transform(fristLink.getGeometry(), 0.00001, 5).getCoordinates().length-1]);
		}
		
	}
	

}
