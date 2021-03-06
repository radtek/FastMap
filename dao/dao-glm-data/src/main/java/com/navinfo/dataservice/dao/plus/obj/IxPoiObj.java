package com.navinfo.dataservice.dao.plus.obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

import com.navinfo.dataservice.dao.plus.model.basic.BasicRow;
import com.navinfo.dataservice.dao.plus.model.basic.OperationType;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiAddress;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiAdvertisement;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiAttraction;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiAudio;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiBuilding;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiBusinesstime;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiCarrental;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiChargingplot;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiChargingplotPh;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiChargingstation;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiChildren;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiContact;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiDetail;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiEntryimage;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiEvent;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiFlag;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiFlagMethod;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiGasstation;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiHotel;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiIcon;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiIntroduction;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiName;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiNameFlag;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiNameTone;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiParent;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiParking;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiPhoto;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiRestaurant;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiTourroute;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxPoiVideo;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxSamepoi;
import com.navinfo.dataservice.dao.plus.model.ixpoi.IxSamepoiPart;

/**
 * @ClassName: IxPoi
 * @author xiaoxiaowen4127
 * @date 2016年11月8日
 * @Description: IxPoi.java
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IxPoiObj extends AbstractIxObj {

	protected String parentFid;
	protected List<Map<Long, Object>> childFids;
	protected long adminId = 0L;

	public long getAdminId() {
		return adminId;
	}

	public void setAdminId(long adminId) {
		this.adminId = adminId;
	}

	public String getParentFid() {
		return parentFid;
	}

	public void setParentFid(String parentFid) {
		this.parentFid = parentFid;
	}

	public List<Map<Long, Object>> getChildFids() {
		return childFids;
	}

	public void setChildFid(List<Map<Long, Object>> childFids) {
		this.childFids = childFids;
	}

	public IxPoiObj(BasicRow mainrow) {
		super(mainrow);
	}

	public List<IxPoiName> getIxPoiNames() {
		return (List) subrows.get("IX_POI_NAME");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiName对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiName对象的外键
	 */
	public IxPoiName createIxPoiName() throws Exception {
		IxPoiName ixPoiName = (IxPoiName) (ObjFactory.getInstance().createRow(
				"IX_POI_NAME", this.objPid()));
		if (subrows.containsKey("IX_POI_NAME")) {
			subrows.get("IX_POI_NAME").add(ixPoiName);
		} else {
			List<BasicRow> ixPoiNameList = new ArrayList<BasicRow>();
			ixPoiNameList.add(ixPoiName);
			subrows.put("IX_POI_NAME", ixPoiNameList);
		}
		return ixPoiName;
	}

	public List<IxPoiAddress> getIxPoiAddresses() {
		return (List) subrows.get("IX_POI_ADDRESS");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建IxPoiAddress
	 *             创建一个IxPoiAddress对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiAddress对象的外键
	 */
	public IxPoiAddress createIxPoiAddress() throws Exception {
		IxPoiAddress ixPoiAddress = (IxPoiAddress) (ObjFactory.getInstance()
				.createRow("IX_POI_ADDRESS", this.objPid()));
		if (subrows.containsKey("IX_POI_ADDRESS")) {
			subrows.get("IX_POI_ADDRESS").add(ixPoiAddress);
		} else {
			List<BasicRow> ixPoiAddressList = new ArrayList<BasicRow>();
			ixPoiAddressList.add(ixPoiAddress);
			subrows.put("IX_POI_ADDRESS", ixPoiAddressList);
		}
		return ixPoiAddress;
	}

	public List<IxPoiFlagMethod> getIxPoiFlagMethods() {
		return (List) subrows.get("IX_POI_FLAG_METHOD");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建IxPoiFlagMethod
	 *             创建一个IxPoiFlagMethod对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中
	 *             。
	 */
	public IxPoiFlagMethod createIxPoiFlagMethod() throws Exception {
		IxPoiFlagMethod ixPoiFlagMethod = (IxPoiFlagMethod) (ObjFactory
				.getInstance().createRow("IX_POI_FLAG_METHOD", this.objPid()));
		if (subrows.containsKey("IX_POI_FLAG_METHOD")) {
			subrows.get("IX_POI_FLAG_METHOD").add(ixPoiFlagMethod);
		} else {
			List<BasicRow> ixPoiFlagMethodList = new ArrayList<BasicRow>();
			ixPoiFlagMethodList.add(ixPoiFlagMethod);
			subrows.put("IX_POI_FLAG_METHOD", ixPoiFlagMethodList);
		}
		return ixPoiFlagMethod;
	}

	public List<IxPoiContact> getIxPoiContacts() {
		return (List) subrows.get("IX_POI_CONTACT");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiContact对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中
	 *             。 暂时没有维护IxPoiContact对象的外键
	 */
	public IxPoiContact createIxPoiContact() throws Exception {
		IxPoiContact ixPoiContact = (IxPoiContact) (ObjFactory.getInstance()
				.createRow("IX_POI_CONTACT", this.objPid()));
		if (subrows.containsKey("IX_POI_CONTACT")) {
			subrows.get("IX_POI_CONTACT").add(ixPoiContact);
		} else {
			List<BasicRow> ixPoiContactList = new ArrayList<BasicRow>();
			ixPoiContactList.add(ixPoiContact);
			subrows.put("IX_POI_CONTACT", ixPoiContactList);
		}
		return ixPoiContact;
	}

	public List<IxPoiRestaurant> getIxPoiRestaurants() {
		return (List) subrows.get("IX_POI_RESTAURANT");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiRestaurant对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiRestaurant对象的外键
	 */
	public IxPoiRestaurant createIxPoiRestaurant() throws Exception {
		IxPoiRestaurant ixPoiRestaurant = (IxPoiRestaurant) (ObjFactory
				.getInstance().createRow("IX_POI_RESTAURANT", this.objPid()));
		if (subrows.containsKey("IX_POI_RESTAURANT")) {
			subrows.get("IX_POI_RESTAURANT").add(ixPoiRestaurant);
		} else {
			List<BasicRow> ixPoiRestaurantList = new ArrayList<BasicRow>();
			ixPoiRestaurantList.add(ixPoiRestaurant);
			subrows.put("IX_POI_RESTAURANT", ixPoiRestaurantList);
		}
		return ixPoiRestaurant;
		// return
		// (IxPoiRestaurant)(ObjFactory.getInstance().createRow("IX_POI_RESTAURANT",
		// this.objPid()));
	}

	public List<IxPoiGasstation> getIxPoiGasstations() {
		return (List) subrows.get("IX_POI_GASSTATION");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiGasstation对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiGasstation对象的外键
	 */
	public IxPoiGasstation createIxPoiGasstation() throws Exception {
		IxPoiGasstation ixPoiGasstation = (IxPoiGasstation) (ObjFactory
				.getInstance().createRow("IX_POI_GASSTATION", this.objPid()));
		if (subrows.containsKey("IX_POI_GASSTATION")) {
			subrows.get("IX_POI_GASSTATION").add(ixPoiGasstation);
		} else {
			List<BasicRow> ixixPoiGasstationList = new ArrayList<BasicRow>();
			ixixPoiGasstationList.add(ixPoiGasstation);
			subrows.put("IX_POI_GASSTATION", ixixPoiGasstationList);
		}
		return ixPoiGasstation;
	}

	public List<IxPoiHotel> getIxPoiHotels() {
		return (List) subrows.get("IX_POI_HOTEL");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiHotel对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiHotel对象的外键
	 */
	public IxPoiHotel createIxPoiHotel() throws Exception {
		IxPoiHotel ixPoiHotel = (IxPoiHotel) (ObjFactory.getInstance()
				.createRow("IX_POI_HOTEL", this.objPid()));
		if (subrows.containsKey("IX_POI_HOTEL")) {
			subrows.get("IX_POI_HOTEL").add(ixPoiHotel);
		} else {
			List<BasicRow> ixPoiHotelList = new ArrayList<BasicRow>();
			ixPoiHotelList.add(ixPoiHotel);
			subrows.put("IX_POI_HOTEL", ixPoiHotelList);
		}
		return ixPoiHotel;
	}

	public List<IxPoiDetail> getIxPoiDetails() {
		return (List) subrows.get("IX_POI_DETAIL");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiDetail对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiDetail对象的外键
	 */
	public IxPoiDetail createIxPoiDetail() throws Exception {
		IxPoiDetail ixPoiDetail = (IxPoiDetail) (ObjFactory.getInstance()
				.createRow("IX_POI_DETAIL", this.objPid()));
		if (subrows.containsKey("IX_POI_DETAIL")) {
			subrows.get("IX_POI_DETAIL").add(ixPoiDetail);
		} else {
			List<BasicRow> ixPoiDetailList = new ArrayList<BasicRow>();
			ixPoiDetailList.add(ixPoiDetail);
			subrows.put("IX_POI_DETAIL", ixPoiDetailList);
		}
		return ixPoiDetail;
	}

	public List<IxPoiChildren> getIxPoiChildrens() {
		return (List) subrows.get("IX_POI_CHILDREN");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiChildren对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiChildren对象的外键
	 */
	public IxPoiChildren createIxPoiChildren(long groupId) throws Exception {
		IxPoiChildren ixPoiChildren = (IxPoiChildren) (ObjFactory.getInstance()
				.createRow("IX_POI_CHILDREN", this.objPid()));
		ixPoiChildren.setGroupId(groupId);
		if (subrows.containsKey("IX_POI_CHILDREN")) {
			subrows.get("IX_POI_CHILDREN").add(ixPoiChildren);
		} else {
			List<BasicRow> ixPoiChildrenList = new ArrayList<BasicRow>();
			ixPoiChildrenList.add(ixPoiChildren);
			subrows.put("IX_POI_CHILDREN", ixPoiChildrenList);
		}
		return ixPoiChildren;
	}

	public List<IxPoiParent> getIxPoiParents() {
		return (List) subrows.get("IX_POI_PARENT");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiParent对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiParent对象的外键
	 */
	public IxPoiParent createIxPoiParent() throws Exception {
		IxPoiParent ixPoiParent = (IxPoiParent) (ObjFactory.getInstance()
				.createRow("IX_POI_PARENT", this.objPid()));
		if (subrows.containsKey("IX_POI_PARENT")) {
			subrows.get("IX_POI_PARENT").add(ixPoiParent);
		} else {
			List<BasicRow> ixPoiParentList = new ArrayList<BasicRow>();
			ixPoiParentList.add(ixPoiParent);
			subrows.put("IX_POI_PARENT", ixPoiParentList);
		}
		return ixPoiParent;
	}

	public List<IxPoiParking> getIxPoiParkings() {
		return (List) subrows.get("IX_POI_PARKING");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiParking对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中
	 *             。 暂时没有维护IxPoiParking对象的外键
	 */
	public IxPoiParking createIxPoiParking() throws Exception {
		IxPoiParking ixPoiParking = (IxPoiParking) (ObjFactory.getInstance()
				.createRow("IX_POI_PARKING", this.objPid()));
		if (subrows.containsKey("IX_POI_PARKING")) {
			subrows.get("IX_POI_PARKING").add(ixPoiParking);
		} else {
			List<BasicRow> ixPoiParkingList = new ArrayList<BasicRow>();
			ixPoiParkingList.add(ixPoiParking);
			subrows.put("IX_POI_PARKING", ixPoiParkingList);
		}
		return ixPoiParking;
	}

	public List<IxPoiChargingstation> getIxPoiChargingstations() {
		return (List) subrows.get("IX_POI_CHARGINGSTATION");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiChargingstation对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiChargingstation对象的外键
	 */
	public IxPoiChargingstation createIxPoiChargingstation() throws Exception {
		IxPoiChargingstation ixPoiChargingstation = (IxPoiChargingstation) (ObjFactory
				.getInstance().createRow("IX_POI_CHARGINGSTATION",
				this.objPid()));
		if (subrows.containsKey("IX_POI_CHARGINGSTATION")) {
			subrows.get("IX_POI_CHARGINGSTATION").add(ixPoiChargingstation);
		} else {
			List<BasicRow> ixPoiChargingstationList = new ArrayList<BasicRow>();
			ixPoiChargingstationList.add(ixPoiChargingstation);
			subrows.put("IX_POI_CHARGINGSTATION", ixPoiChargingstationList);
		}
		return ixPoiChargingstation;
	}

	public List<IxPoiChargingplot> getIxPoiChargingplots() {
		return (List) subrows.get("IX_POI_CHARGINGPLOT");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiChargingplot对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiChargingplot对象的外键
	 */
	public IxPoiChargingplot createIxPoiChargingplot() throws Exception {
		IxPoiChargingplot ixPoiChargingplot = (IxPoiChargingplot) (ObjFactory
				.getInstance().createRow("IX_POI_CHARGINGPLOT", this.objPid()));
		if (subrows.containsKey("IX_POI_CHARGINGPLOT")) {
			subrows.get("IX_POI_CHARGINGPLOT").add(ixPoiChargingplot);
		} else {
			List<BasicRow> ixPoiChargingplotList = new ArrayList<BasicRow>();
			ixPoiChargingplotList.add(ixPoiChargingplot);
			subrows.put("IX_POI_CHARGINGPLOT", ixPoiChargingplotList);
		}
		return ixPoiChargingplot;
	}

	public List<IxPoiChargingplotPh> getIxPoiChargingplotPhs() {
		return (List) subrows.get("IX_POI_CHARGINGPLOT_PH");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiChargingplotPh对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiChargingplotPh对象的外键
	 */
	public IxPoiChargingplotPh createIxPoiChargingplotPh() throws Exception {
		IxPoiChargingplotPh ixPoiChargingplotPh = (IxPoiChargingplotPh) (ObjFactory
				.getInstance().createRow("IX_POI_CHARGINGPLOT_PH",
				this.objPid()));
		if (subrows.containsKey("IX_POI_CHARGINGPLOT_PH")) {
			subrows.get("IX_POI_CHARGINGPLOT_PH").add(ixPoiChargingplotPh);
		} else {
			List<BasicRow> ixPoiChargingplotPhList = new ArrayList<BasicRow>();
			ixPoiChargingplotPhList.add(ixPoiChargingplotPh);
			subrows.put("IX_POI_CHARGINGPLOT_PH", ixPoiChargingplotPhList);
		}
		return ixPoiChargingplotPh;
	}

	public List<IxPoiFlag> getIxPoiFlags() {
		return (List) subrows.get("IX_POI_FLAG");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiFlag对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiFlag对象的外键
	 */
	public IxPoiFlag createIxPoiFlag() throws Exception {
		IxPoiFlag ixPoiFlag = (IxPoiFlag) (ObjFactory.getInstance().createRow(
				"IX_POI_FLAG", this.objPid()));
		if (subrows.containsKey("IX_POI_FLAG")) {
			subrows.get("IX_POI_FLAG").add(ixPoiFlag);
		} else {
			List<BasicRow> ixPoiFlagList = new ArrayList<BasicRow>();
			ixPoiFlagList.add(ixPoiFlag);
			subrows.put("IX_POI_FLAG", ixPoiFlagList);
		}
		return ixPoiFlag;
	}

	public List<IxPoiEntryimage> getIxPoiEntryimages() {
		return (List) subrows.get("IX_POI_ENTRYIMAGE");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiEntryimage对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiEntryimage对象的外键
	 */
	public IxPoiEntryimage createIxPoiEntryimage() throws Exception {
		IxPoiEntryimage ixPoiEntryimage = (IxPoiEntryimage) (ObjFactory
				.getInstance().createRow("IX_POI_ENTRYIMAGE", this.objPid()));
		if (subrows.containsKey("IX_POI_ENTRYIMAGE")) {
			subrows.get("IX_POI_ENTRYIMAGE").add(ixPoiEntryimage);
		} else {
			List<BasicRow> ixPoiEntryimageList = new ArrayList<BasicRow>();
			ixPoiEntryimageList.add(ixPoiEntryimage);
			subrows.put("IX_POI_ENTRYIMAGE", ixPoiEntryimageList);
		}
		return ixPoiEntryimage;
	}

	public List<IxPoiIcon> getIxPoiIcons() {
		return (List) subrows.get("IX_POI_ICON");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiIcon对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiIcon对象的外键
	 */
	public IxPoiIcon createIxPoiIcon() throws Exception {
		IxPoiIcon ixPoiIcon = (IxPoiIcon) (ObjFactory.getInstance().createRow(
				"IX_POI_ICON", this.objPid()));
		if (subrows.containsKey("IX_POI_ICON")) {
			subrows.get("IX_POI_ICON").add(ixPoiIcon);
		} else {
			List<BasicRow> ixPoiIconList = new ArrayList<BasicRow>();
			ixPoiIconList.add(ixPoiIcon);
			subrows.put("IX_POI_ICON", ixPoiIconList);
		}
		return ixPoiIcon;
	}

	public List<IxPoiPhoto> getIxPoiPhotos() {
		return (List) subrows.get("IX_POI_PHOTO");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiPhoto对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiPhoto对象的外键
	 */
	public IxPoiPhoto createIxPoiPhoto() throws Exception {
		IxPoiPhoto ixPoiPhoto = (IxPoiPhoto) (ObjFactory.getInstance()
				.createRow("IX_POI_PHOTO", this.objPid()));
		if (subrows.containsKey("IX_POI_PHOTO")) {
			subrows.get("IX_POI_PHOTO").add(ixPoiPhoto);
		} else {
			List<BasicRow> ixPoiPhotoList = new ArrayList<BasicRow>();
			ixPoiPhotoList.add(ixPoiPhoto);
			subrows.put("IX_POI_PHOTO", ixPoiPhotoList);
		}
		return ixPoiPhoto;
	}

	public List<IxPoiAudio> getIxPoiAudios() {
		return (List) subrows.get("IX_POI_AUDIO");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiAudio对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiAudio对象的外键
	 */
	public IxPoiAudio createIxPoiAudio() throws Exception {
		IxPoiAudio ixPoiAudio = (IxPoiAudio) (ObjFactory.getInstance()
				.createRow("IX_POI_AUDIO", this.objPid()));
		if (subrows.containsKey("IX_POI_AUDIO")) {
			subrows.get("IX_POI_AUDIO").add(ixPoiAudio);
		} else {
			List<BasicRow> ixPoiAudioList = new ArrayList<BasicRow>();
			ixPoiAudioList.add(ixPoiAudio);
			subrows.put("IX_POI_AUDIO", ixPoiAudioList);
		}
		return ixPoiAudio;
	}

	public List<IxPoiVideo> getIxPoiVideos() {
		return (List) subrows.get("IX_POI_VIDEO");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiVideo对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiVideo对象的外键
	 */
	public IxPoiVideo createIxPoiVideo() throws Exception {
		IxPoiVideo ixPoiVideo = (IxPoiVideo) (ObjFactory.getInstance()
				.createRow("IX_POI_VIDEO", this.objPid()));
		if (subrows.containsKey("IX_POI_VIDEO")) {
			subrows.get("IX_POI_VIDEO").add(ixPoiVideo);
		} else {
			List<BasicRow> ixPoiVideoList = new ArrayList<BasicRow>();
			ixPoiVideoList.add(ixPoiVideo);
			subrows.put("IX_POI_VIDEO", ixPoiVideoList);
		}
		return ixPoiVideo;
	}

	public List<IxPoiTourroute> getIxPoiTourroutes() {
		return (List) subrows.get("IX_POI_TOURROUTE");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiTourroute对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiTourroute对象的外键
	 */
	public IxPoiTourroute createIxPoiTourroute() throws Exception {
		IxPoiTourroute ixPoiTourroute = (IxPoiTourroute) (ObjFactory
				.getInstance().createRow("IX_POI_TOURROUTE", this.objPid()));
		if (subrows.containsKey("IX_POI_TOURROUTE")) {
			subrows.get("IX_POI_TOURROUTE").add(ixPoiTourroute);
		} else {
			List<BasicRow> ixPoiTourrouteList = new ArrayList<BasicRow>();
			ixPoiTourrouteList.add(ixPoiTourroute);
			subrows.put("IX_POI_TOURROUTE", ixPoiTourrouteList);
		}
		return ixPoiTourroute;
	}

	public List<IxPoiEvent> getIxPoiEvents() {
		return (List) subrows.get("IX_POI_EVENT");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiEvent对象，完成主键赋值，完成objPid赋值，完成并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiEvent对象的外键
	 */
	public IxPoiEvent createIxPoiEvent() throws Exception {
		IxPoiEvent ixPoiEvent = (IxPoiEvent) (ObjFactory.getInstance()
				.createRow("IX_POI_EVENT", this.objPid()));
		if (subrows.containsKey("IX_POI_EVENT")) {
			subrows.get("IX_POI_EVENT").add(ixPoiEvent);
		} else {
			List<BasicRow> ixPoiEventList = new ArrayList<BasicRow>();
			ixPoiEventList.add(ixPoiEvent);
			subrows.put("IX_POI_EVENT", ixPoiEventList);
		}
		return ixPoiEvent;
	}

	public List<IxPoiBusinesstime> getIxPoiBusinesstimes() {
		return (List) subrows.get("IX_POI_BUSINESSTIME");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiBusinesstime对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiBusinesstime对象的外键
	 */
	public IxPoiBusinesstime createIxPoiBusinesstime() throws Exception {
		IxPoiBusinesstime ixPoiBusinesstime = (IxPoiBusinesstime) (ObjFactory
				.getInstance().createRow("IX_POI_BUSINESSTIME", this.objPid()));
		if (subrows.containsKey("IX_POI_BUSINESSTIME")) {
			subrows.get("IX_POI_BUSINESSTIME").add(ixPoiBusinesstime);
		} else {
			List<BasicRow> ixPoiBusinesstimeList = new ArrayList<BasicRow>();
			ixPoiBusinesstimeList.add(ixPoiBusinesstime);
			subrows.put("IX_POI_BUSINESSTIME", ixPoiBusinesstimeList);
		}
		return ixPoiBusinesstime;
	}

	public List<IxPoiBuilding> getIxPoiBuildings() {
		return (List) subrows.get("IX_POI_BUILDING");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiBuilding对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiBuilding对象的外键
	 */
	public IxPoiBuilding createIxPoiBuilding() throws Exception {
		IxPoiBuilding ixPoiBuilding = (IxPoiBuilding) (ObjFactory.getInstance()
				.createRow("IX_POI_BUILDING", this.objPid()));
		if (subrows.containsKey("IX_POI_BUILDING")) {
			subrows.get("IX_POI_BUILDING").add(ixPoiBuilding);
		} else {
			List<BasicRow> ixPoiBuildingList = new ArrayList<BasicRow>();
			ixPoiBuildingList.add(ixPoiBuilding);
			subrows.put("IX_POI_BUILDING", ixPoiBuildingList);
		}
		return ixPoiBuilding;
	}

	public List<IxPoiAdvertisement> getIxPoiAdvertisements() {
		return (List) subrows.get("IX_POI_ADVERTISEMENT");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiAdvertisement对象，完成主键赋值，完成objPid赋值，
	 *             并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiAdvertisement对象的外键
	 */
	public IxPoiAdvertisement createIxPoiAdvertisement() throws Exception {
		IxPoiAdvertisement ixPoiAdvertisement = (IxPoiAdvertisement) (ObjFactory
				.getInstance().createRow("IX_POI_ADVERTISEMENT", this.objPid()));
		if (subrows.containsKey("IX_POI_ADVERTISEMENT")) {
			subrows.get("IX_POI_ADVERTISEMENT").add(ixPoiAdvertisement);
		} else {
			List<BasicRow> ixPoiAdvertisementList = new ArrayList<BasicRow>();
			ixPoiAdvertisementList.add(ixPoiAdvertisement);
			subrows.put("IX_POI_ADVERTISEMENT", ixPoiAdvertisementList);
		}
		return ixPoiAdvertisement;
	}

	public List<IxPoiIntroduction> getIxPoiIntroductions() {
		return (List) subrows.get("IX_POI_INTRODUCTION");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiIntroduction对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiIntroduction对象的外键
	 */
	public IxPoiIntroduction createIxPoiIntroduction() throws Exception {
		IxPoiIntroduction ixPoiIntroduction = (IxPoiIntroduction) (ObjFactory
				.getInstance().createRow("IX_POI_INTRODUCTION", this.objPid()));
		if (subrows.containsKey("IX_POI_INTRODUCTION")) {
			subrows.get("IX_POI_INTRODUCTION").add(ixPoiIntroduction);
		} else {
			List<BasicRow> ixPoiIntroductionList = new ArrayList<BasicRow>();
			ixPoiIntroductionList.add(ixPoiIntroduction);
			subrows.put("IX_POI_INTRODUCTION", ixPoiIntroductionList);
		}
		return ixPoiIntroduction;
	}

	public List<IxPoiAttraction> getIxPoiAttractions() {
		return (List) subrows.get("IX_POI_ATTRACTION");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiAttraction对象，完成主键赋值，完成objPid赋值，
	 *             并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiAttraction对象的外键
	 */
	public IxPoiAttraction createIxPoiAttraction() throws Exception {
		IxPoiAttraction ixPoiAttraction = (IxPoiAttraction) (ObjFactory
				.getInstance().createRow("IX_POI_ATTRACTION", this.objPid()));
		if (subrows.containsKey("IX_POI_ATTRACTION")) {
			subrows.get("IX_POI_ATTRACTION").add(ixPoiAttraction);
		} else {
			List<BasicRow> ixPoiAttractionList = new ArrayList<BasicRow>();
			ixPoiAttractionList.add(ixPoiAttraction);
			subrows.put("IX_POI_ATTRACTION", ixPoiAttractionList);
		}
		return ixPoiAttraction;
	}

	public List<IxPoiCarrental> getIxPoiCarrentals() {
		return (List) subrows.get("IX_POI_CARRENTAL");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiCarrental对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxPoiCarrental对象的外键
	 */
	public IxPoiCarrental createIxPoiCarrental() throws Exception {
		IxPoiCarrental ixPoiCarrental = (IxPoiCarrental) (ObjFactory
				.getInstance().createRow("IX_POI_CARRENTAL", this.objPid()));
		if (subrows.containsKey("IX_POI_CARRENTAL")) {
			subrows.get("IX_POI_CARRENTAL").add(ixPoiCarrental);
		} else {
			List<BasicRow> ixPoiCarrentalList = new ArrayList<BasicRow>();
			ixPoiCarrentalList.add(ixPoiCarrental);
			subrows.put("IX_POI_CARRENTAL", ixPoiCarrentalList);
		}
		return ixPoiCarrental;
	}

	public List<IxSamepoiPart> getIxSamepoiParts() {
		return (List) subrows.get("IX_SAMEPOI_PART");
	}

	/**
	 * @return
	 * @throws Exception
	 *             创建一个IxSamepoiPart对象，完成主键赋值，完成objPid赋值，
	 *             完成并将其写入到IxPoi的subrows属性中。 暂时没有维护IxSamepoiPart对象的外键
	 */
	public IxSamepoiPart createIxSamepoiPart(long groupId) throws Exception {
		IxSamepoiPart ixSamepoiPart = (IxSamepoiPart) (ObjFactory.getInstance()
				.createRow("IX_SAMEPOI_PART", this.objPid()));
		ixSamepoiPart.setGroupId(groupId);
		if (subrows.containsKey("IX_SAMEPOI_PART")) {
			subrows.get("IX_SAMEPOI_PART").add(ixSamepoiPart);
		} else {
			List<BasicRow> ixSamepoiPartList = new ArrayList<BasicRow>();
			ixSamepoiPartList.add(ixSamepoiPart);
			subrows.put("IX_SAMEPOI_PART", ixSamepoiPartList);
		}
		return ixSamepoiPart;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiNameFlag对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiNameFlag对象的外键
	 */
	public IxPoiNameFlag createIxPoiNameFlag(long nameId) throws Exception {
		IxPoiNameFlag ixPoiNameFlag = (IxPoiNameFlag) (ObjFactory.getInstance()
				.createRow("IX_POI_NAME_FLAG", this.objPid()));
		ixPoiNameFlag.setNameId(nameId);
		if (subrows.containsKey("IX_POI_NAME_FLAG")) {
			subrows.get("IX_POI_NAME_FLAG").add(ixPoiNameFlag);
		} else {
			List<BasicRow> ixPoiNameFlagList = new ArrayList<BasicRow>();
			ixPoiNameFlagList.add(ixPoiNameFlag);
			subrows.put("IX_POI_NAME_FLAG", ixPoiNameFlagList);
		}
		return ixPoiNameFlag;
	}

	public List<IxPoiNameFlag> getIxPoiNameFlags() {
		return (List) subrows.get("IX_POI_NAME_FLAG");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxPoiNameTone对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxPoiNameTone对象的外键
	 */
	public IxPoiNameTone createIxPoiNameTone(long nameId) throws Exception {
		IxPoiNameTone ixPoiNameTone = (IxPoiNameTone) (ObjFactory.getInstance()
				.createRow("IX_POI_NAME_TONE", this.objPid()));
		ixPoiNameTone.setNameId(nameId);
		if (subrows.containsKey("IX_POI_NAME_TONE")) {
			subrows.get("IX_POI_NAME_TONE").add(ixPoiNameTone);
		} else {
			List<BasicRow> ixPoiNameToneList = new ArrayList<BasicRow>();
			ixPoiNameToneList.add(ixPoiNameTone);
			subrows.put("IX_POI_NAME_TONE", ixPoiNameToneList);
		}
		return ixPoiNameTone;
	}

	public List<IxPoiNameTone> getIxPoiNameTones() {
		return (List) subrows.get("IX_POI_NAME_TONE");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             创建一个IxSamepoi对象，完成主键赋值，完成objPid赋值，并将其写入到IxPoi的subrows属性中。
	 *             暂时没有维护IxSamepoi对象的外键
	 */
	public IxSamepoi createIxSamepoi() throws Exception {
		IxSamepoi ixSamepoi = (IxSamepoi) (ObjFactory.getInstance().createRow(
				"IX_SAMEPOI", this.objPid()));
		if (subrows.containsKey("IX_SAMEPOI")) {
			subrows.get("IX_SAMEPOI").add(ixSamepoi);
		} else {
			List<BasicRow> ixSamepoiList = new ArrayList<BasicRow>();
			ixSamepoiList.add(ixSamepoi);
			subrows.put("IX_SAMEPOI", ixSamepoiList);
		}
		return ixSamepoi;
	}

	public List<IxSamepoi> getIxSamepois() {
		return (List) subrows.get("IX_SAMEPOI");
	}

	/**
	 * 根据名称分类,名称类型,语言代码获取名称内容
	 * 
	 * @author Han Shaoming
	 * @param langCode
	 * @param nameClass
	 * @param nameType
	 * @return
	 */
	public IxPoiName getNameByLct(String langCode, int nameClass, int nameType) {
		List<BasicRow> rows = getRowsByName("IX_POI_NAME");
		if (rows != null && rows.size() > 0) {
			for (BasicRow row : rows) {
				//
				IxPoiName name = (IxPoiName) row;
				if (langCode.equals(name.getLangCode())
						&& name.getNameClass() == nameClass
						&& name.getNameType() == nameType) {
					return name;
				}
			}
		}
		return null;
	}

	/**
	 * 根据名称组号,语言代码获取地址全称
	 * 
	 * @author Han Shaoming
	 * @param nameGroupId
	 * @param langCode
	 * @return
	 */
	public IxPoiAddress getFullNameByLg(String langCode, int nameGroupId) {
		List<BasicRow> rows = getRowsByName("IX_POI_ADDRESS");
		if (rows != null && rows.size() > 0) {
			for (BasicRow row : rows) {
				//
				IxPoiAddress name = (IxPoiAddress) row;
				if (langCode.equals(name.getLangCode())
						&& name.getNameGroupid() == nameGroupId) {
					return name;
				}
			}
		}
		return null;
	}

	/**
	 * 根据语言代码获取楼层
	 * 
	 * @author Han Shaoming
	 * @return
	 */
	public IxPoiAddress getFloorByLangCode(String langCode) {
		List<BasicRow> rows = getRowsByName("IX_POI_ADDRESS");
		if (rows != null && rows.size() > 0) {
			for (BasicRow row : rows) {
				IxPoiAddress floor = (IxPoiAddress) row;
				if (langCode.equals(floor.getLangCode())) {
					return floor;
				}
			}
		}
		return null;
	}

	/**
	 * 官方原始中文名称CHI
	 */
	public IxPoiName getOfficeOriginCHIName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 1 && br.getNameType() == 2
					&& br.getLangCode().equals("CHI")) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 官方原始中文名称CHI/CHT
	 */
	public IxPoiName getOfficeOriginCHName() {
		List<IxPoiName> subRows = getIxPoiNames();
		if (subRows != null) {
			for (IxPoiName br : subRows) {
				if (br.getNameClass() == 1
						&& br.getNameType() == 2
						&& (br.getLangCode().equals("CHI") || br.getLangCode()
								.equals("CHT"))) {
					return br;
				}
			}
		}
		return null;
	}

	/**
	 * 官方标准中文名称
	 */
	public IxPoiName getOfficeStandardCHName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 1
					&& br.getNameType() == 1
					&& (br.getLangCode().equals("CHI") || br.getLangCode()
							.equals("CHT"))) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 官方标准化简体中文名称
	 */
	public IxPoiName getOfficeStandardCHIName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 1 && br.getNameType() == 1
					&& br.getLangCode().equals("CHI")) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 官方标准化简体中文名称
	 */
	public IxPoiName getOfficeStandardCHTName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 1 && br.getNameType() == 1
					&& br.getLangCode().equals("CHT")) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 标准化简称中文
	 */
	public IxPoiName getStandardShortName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 5 && br.getNameType() == 1
					&& br.getLangCode().equals("CHI")
					|| br.getLangCode().equals("CHT")) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 官方原始英文名
	 */
	public IxPoiName getOfficeOriginEngName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.isOfficeName() && br.isOriginName() && br.isEng()) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 官方原始葡萄名
	 */
	public IxPoiName getOfficeOriginPOTName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.isOfficeName() && br.isOriginName() && br.isPOT()) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 官方标准化葡萄名
	 */
	public IxPoiName getOfficeStandardPOTName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.isOfficeName() && br.isStandardName() && br.isPOT()) {
				return br;
			}
		}
		return null;
	}


	/**
	 * 官方标准英文名
	 */
	public IxPoiName getOfficeStandardEngName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.isOfficeName() && br.isStandardName() && br.isEng()) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 获取同组官方标准英文名
	 */
	public IxPoiName getOfficeStandardEngName(long nameGroupId) {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.isOfficeName() && br.isStandardName() && br.isEng()
					&& br.getNameGroupid() == nameGroupId) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 简称中文名称
	 */
	public List<IxPoiName> getShortCHNames() {
		List<IxPoiName> shortNames = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 5
					&& (br.getLangCode().equals("CHI") || br.getLangCode()
							.equals("CHT"))) {
				shortNames.add(br);
			}
		}
		return shortNames;
	}

	/*
	 * 简称标准中文名称组
	 */
	public List<IxPoiName> getShortStandardCHName() {
		List<IxPoiName> shortCHNameList = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 5
					&& br.getNameType() == 1
					&& (br.getLangCode().equals("CHI") || br.getLangCode()
							.equals("CHT"))) {
				shortCHNameList.add(br);
			}
		}
		return shortCHNameList;
	}

	/*
	 * 简称标准中文CHI名称组
	 */
	public List<IxPoiName> getShortStandardCHIName() {
		List<IxPoiName> shortCHNameList = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 5 && br.getNameType() == 1
					&& br.getLangCode().equals("CHI")) {
				shortCHNameList.add(br);
			}
		}
		return shortCHNameList;
	}

	/*
	 * 标准中文名称组
	 */
	public List<IxPoiName> getStandardCHName() {
		List<IxPoiName> standardCHName = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if ((br.getNameClass() == 1 || br.getNameClass() == 3
					|| br.getNameClass() == 5 || br.getNameClass() == 6)
					&& br.getNameType() == 1
					&& (br.getLangCode().equals("CHI") || br.getLangCode()
							.equals("CHT"))) {
				standardCHName.add(br);
			}
		}
		return standardCHName;
	}

	/*
	 * 标准化简体中文名称
	 */
	public List<IxPoiName> getStandardCHIName() {
		List<IxPoiName> standardCHName = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if ((br.getNameClass() == 1 || br.getNameClass() == 3
					|| br.getNameClass() == 5 || br.getNameClass() == 6)
					&& br.getNameType() == 1 && br.getLangCode().equals("CHI")) {
				standardCHName.add(br);
			}
		}
		return standardCHName;
	}

	/*
	 * 获取名称组中最大group_id
	 */
	public long getMaxGroupIdFromNames() {
		long groupId = 0;
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			long gId = br.getNameGroupid();
			if (groupId <= gId) {
				groupId = gId;
			}
		}
		return groupId;
	}

	/*
	 * 别名中文(name_class=3,name_type=1,lang_code='CHI')列表
	 */
	public List<IxPoiName> getAliasCHIName() {
		List<IxPoiName> aliasCHINameList = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 3 && br.getNameType() == 1
					&& br.getLangCode().equals("CHI")) {
				aliasCHINameList.add(br);
			}
		}
		return aliasCHINameList;
	}

	public IxPoiName getAliasCHITypeName() {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 3 && br.getNameType() == 1
					&& br.getLangCode().equals("CHI")) {
				return br;
			}
		}
		return null;
	}

	public IxPoiName getStandardAliasENGName(long nameGroupId) {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameGroupid() == nameGroupId && br.getNameClass() == 3
					&& br.getNameType() == 1 && br.getLangCode().equals("ENG")) {
				return br;
			}
		}
		return null;
	}

	public IxPoiName getOriginAliasENGName(long nameGroupId) {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameGroupid() == nameGroupId && br.getNameClass() == 3
					&& br.getNameType() == 2 && br.getLangCode().equals("ENG")) {
				return br;
			}
		}
		return null;
	}

	public List<IxPoiName> getAliasENGName() {
		List<IxPoiName> subRows = getIxPoiNames();
		List<IxPoiName> aliasENGNameList = new ArrayList<IxPoiName>();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 3 && br.getLangCode().equals("ENG")) {
				aliasENGNameList.add(br);
			}
		}
		return aliasENGNameList;
	}

	/**
	 * 获取原始英文别名列表
	 * 
	 * @param nameGroupId
	 * @return
	 */
	public List<IxPoiName> getOriginAliasENGNameList() {
		List<IxPoiName> originAliasENGNameList = new ArrayList<IxPoiName>();
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 3 && br.getNameType() == 2
					&& br.getLangCode().equals("ENG")) {
				originAliasENGNameList.add(br);
			}
		}
		return originAliasENGNameList;
	}

	/*
	 * 别名中文(name_class=3,name_type=1,lang_code='CHI')列表
	 */
	public IxPoiName getAliasCHIName(long nameGroupId) {
		List<IxPoiName> subRows = getIxPoiNames();
		for (IxPoiName br : subRows) {
			if (br.getNameClass() == 3 && br.getLangCode().equals("CHI")
					&& (br.getNameGroupid() == nameGroupId)) {
				return br;
			}
		}
		return null;
	}

	public IxPoiAddress getCHAddress() {
		List<IxPoiAddress> subRows = getIxPoiAddresses();
		if (subRows == null)
			return null;
		for (IxPoiAddress br : subRows) {
			if (br.getLangCode().equals("CHI")
					|| br.getLangCode().equals("CHT")) {
				return br;
			}
		}
		return null;
	}

	public IxPoiAddress getCHIAddress() {
		List<IxPoiAddress> subRows = getIxPoiAddresses();
		for (IxPoiAddress br : subRows) {
			if (br.getLangCode().equals("CHI") && br.isChanged()) {
				return br;
			}
		}
		return null;
	}

	public IxPoiAddress getChiAddress() {
		List<IxPoiAddress> subRows = getIxPoiAddresses();
		for (IxPoiAddress br : subRows) {
			if (br.getLangCode().equals("CHI")) {
				return br;
			}
		}
		return null;
	}

	public IxPoiAddress getENGAddress(long nameGroupId) {
		List<IxPoiAddress> subRows = getIxPoiAddresses();
		for (IxPoiAddress br : subRows) {
			if (br.getLangCode().equals("ENG")
					&& br.getNameGroupid() == nameGroupId) {
				return br;
			}
		}
		return null;
	}

	public IxPoiAddress getPORAddress(long nameGroupId) {
		List<IxPoiAddress> subRows = getIxPoiAddresses();
		for (IxPoiAddress br : subRows) {
			if (br.getLangCode().equals("POR")
					&& br.getNameGroupid() == nameGroupId) {
				return br;
			}
		}
		return null;
	}

	/**
	 * 街巷名翻译： 通过IX_POI_ADDRESS.STREET与道路名RD_NAME表中LANG_CODE=
	 * 'CHI'(港澳数据为CHT)对应的name进行关联，
	 * 若关联一条中文记录，则通过RD_NAME.name_groupid查询匹配的英文记录，则将英文RD_NAME
	 * .NAME赋值给IX_POI_ADDRESS.STREET； 若关联多条中文记录，
	 * 则通过IX_POI_ADDRESS.STREET_PHONETIC与中文对应的RD_NAME.NAME_PHONEETIC关联，
	 * 如果拼音一致且只有一条，则将这条中文对应的英文name赋值给IX_POI_ADDRESS.STREET；
	 * 如果拼音一致且多条记录，则取RD_NAME.SRC_FLAG=1的中文对应的英文name赋值给IX_POI_ADDRESS.STREET；
	 * 若没有SRC_FLAG=1的，则取NAME_GROUPID最小的中文对应的英文name赋值给IX_POI_ADDRESS.STREET；
	 * 如果拼音不一致，则取取NAME_GROUPID最小的中文对应的英文name赋值给IX_POI_ADDRESS.STREET；
	 * 如果找不到匹配的英文记录，则不更新取IX_POI_ADDRESS.STREET，也不新增记录；
	 * 
	 * @param conn
	 * @param nameGroupId
	 * @return
	 * @throws Exception
	 */
	public String getRdEngName(Connection conn, long nameGroupId, long pid)
			throws Exception {
		String strStreet = "";
		Statement stmt = null;
		ResultSet rs = null;
		String sql1 = "SELECT COUNT(1) total FROM ix_poi_address ad,rd_name r WHERE r.lang_code='CHI' AND ad.street=r.name AND ad.lang_code='CHI' and ad.name_groupid="
				+ nameGroupId + " and ad.poi_pid=" + pid;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql1);
			while (rs.next()) {
				if (rs.getInt("total") == 1) {
					ResultSet rs1 = null;
					try {
						rs1 = stmt
								.executeQuery("SELECT r.name FROM rd_name r WHERE r.lang_code='ENG' AND r.name_groupid=(SELECT n.name_groupid FROM ix_poi_address ad,rd_name n WHERE n.lang_code='CHI' AND ad.street=n.name AND ad.lang_code='CHI' AND ad.name_groupid="
										+ nameGroupId
										+ " and ad.poi_pid="
										+ pid + ")");
						while (rs1.next()) {
							return rs1.getString("name");
						}
					} finally {
						DbUtils.closeQuietly(rs1);
					}

				} else if (rs.getInt("total") > 1) {
					ResultSet rs2 = null;
					try {
						rs2 = stmt
								.executeQuery("SELECT COUNT(1) total FROM ix_poi_address ad,rd_name r WHERE r.lang_code='CHI' AND ad.STREET_PHONETIC=r.name_phonetic AND ad.lang_code='CHI' AND ad.name_groupid="
										+ nameGroupId
										+ " and ad.poi_pid="
										+ pid);
						while (rs2.next()) {
							if (rs2.getInt("total") == 0) {
								ResultSet rs3 = null;
								try {
									rs3 = stmt
											.executeQuery("SELECT r.name FROM rd_name r WHERE r.lang_code='ENG' AND r.name_groupid=(SELECT MIN(r.name_groupid) FROM ix_poi_address ad,rd_name r WHERE r.lang_code='CHI' AND ad.street=r.name AND ad.lang_code='CHI' AND ad.name_groupid="
													+ nameGroupId
													+ " and ad.poi_pid="
													+ pid
													+ ") ");
									while (rs3.next()) {
										return rs3.getString("name");
									}
								} finally {
									DbUtils.closeQuietly(rs3);
								}
							} else if (rs2.getInt("total") == 1) {
								ResultSet rs4 = null;
								try {
									rs4 = stmt
											.executeQuery("SELECT r.name FROM rd_name r WHERE r.lang_code='ENG' AND r.name_groupid=(SELECT r.name_groupid FROM ix_poi_address ad,rd_name r WHERE r.lang_code='CHI' AND ad.STREET_PHONETIC=r.name_phonetic AND ad.lang_code='CHI' AND ad.name_groupid="
													+ nameGroupId
													+ " and ad.poi_pid="
													+ pid
													+ ") ");
									while (rs4.next()) {
										return rs4.getString("name");
									}
								} finally {
									DbUtils.closeQuietly(rs4);
								}
							} else if (rs2.getInt("total") > 1) {
								ResultSet rs5 = null;
								try {
									rs5 = stmt
											.executeQuery("SELECT r.name_groupid FROM ix_poi_address ad,rd_name r WHERE r.lang_code='CHI' AND ad.STREET_PHONETIC=r.name_phonetic AND ad.lang_code='CHI' and r.src_flag=1 AND ad.name_groupid="
													+ nameGroupId
													+ " and ad.poi_pid=" + pid);
									while (rs5.next()) {
										ResultSet rs6 = null;
										try {
											rs6 = stmt
													.executeQuery("SELECT r.name FROM rd_name r WHERE r.lang_code='ENG' AND r.name_groupid="
															+ rs5.getInt("name_groupid")
															+ " and ad.poi_pid="
															+ pid);
											while (rs6.next()) {
												return rs6.getString("name");
											}
										} finally {
											DbUtils.closeQuietly(rs6);
										}
									}
								} finally {
									DbUtils.closeQuietly(rs5);
								}
								ResultSet rs7 = null;
								try {
									rs7 = stmt
											.executeQuery("SELECT r.name FROM rd_name r WHERE r.lang_code='ENG' AND r.name_groupid=(SELECT MIN(r.name_groupid) FROM ix_poi_address ad,rd_name r WHERE r.lang_code='CHI' AND ad.STREET_PHONETIC=r.name_phonetic AND ad.lang_code='CHI' AND ad.name_groupid="
													+ nameGroupId
													+ " and ad.poi_pid="
													+ pid
													+ ") ");
									while (rs7.next()) {
										return rs7.getString("name");
									}
								} finally {
									DbUtils.closeQuietly(rs7);
								}
							}
						}
					} finally {
						DbUtils.closeQuietly(rs2);
					}

				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}

			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		return strStreet;
	}

	@Override
	public String objName() {
		return ObjectName.IX_POI;
	}

	@Override
	public String objType() {
		return ObjType.FEATURE;
	}

	/**
	 * 根据json中的key创建对象
	 * 
	 * @throws Exception
	 */
	@Override
	public BasicRow createSubRowByName(String subRowName) throws Exception {
		if ("addresses".equals(subRowName)) {
			return this.createIxPoiAddress();
		} else if ("audioes".equals(subRowName)) {
			return this.createIxPoiAudio();
		} else if ("contacts".equals(subRowName)) {
			return this.createIxPoiContact();
		} else if ("entryImages".equals(subRowName)) {
			return this.createIxPoiEntryimage();
		} else if ("flags".equals(subRowName)) {
			return this.createIxPoiFlag();
		} else if ("icons".equals(subRowName)) {
			return this.createIxPoiIcon();
		} else if ("names".equals(subRowName)) {
			return this.createIxPoiName();
		} else if ("parents".equals(subRowName)) {
			return this.createIxPoiParent();
		} else if ("photos".equals(subRowName)) {
			return this.createIxPoiPhoto();
		} else if ("videoes".equals(subRowName)) {
			return this.createIxPoiVideo();
		} else if ("parkings".equals(subRowName)) {
			return this.createIxPoiParking();
		} else if ("tourroutes".equals(subRowName)) {
			return this.createIxPoiTourroute();
		} else if ("events".equals(subRowName)) {
			return this.createIxPoiEvent();
		} else if ("details".equals(subRowName)) {
			return this.createIxPoiDetail();
		} else if ("businesstimes".equals(subRowName)) {
			return this.createIxPoiBusinesstime();
		} else if ("chargingstations".equals(subRowName)) {
			return this.createIxPoiChargingstation();
		} else if ("chargingplots".equals(subRowName)) {
			return this.createIxPoiChargingplot();
		} else if ("chargingplotPhs".equals(subRowName)) {
			return this.createIxPoiChargingplotPh();
		} else if ("buildings".equals(subRowName)) {
			return this.createIxPoiBuilding();
		} else if ("advertisements".equals(subRowName)) {
			return this.createIxPoiAdvertisement();
		} else if ("gasstations".equals(subRowName)) {
			return this.createIxPoiGasstation();
		} else if ("introductions".equals(subRowName)) {
			return this.createIxPoiIntroduction();
		} else if ("attractions".equals(subRowName)) {
			return this.createIxPoiAttraction();
		} else if ("hotels".equals(subRowName)) {
			return this.createIxPoiHotel();
		} else if ("restaurants".equals(subRowName)) {
			return this.createIxPoiRestaurant();
		} else if ("carrentals".equals(subRowName)) {
			return this.createIxPoiCarrental();
		} else if ("samepois".equals(subRowName)) {
			return this.createIxSamepoi();
		} else {
			throw new Exception("字段名为:" + subRowName + "的子表未创建");
		}
	}

	/**
	 * 根据json中的key创建三级对象
	 */
	@Override
	public BasicRow createSubSubRowByName(String subRowName, long subId)
			throws Exception {
		// TODO Auto-generated method stub
		if ("nameFlags".equals(subRowName)) {
			return this.createIxPoiNameFlag(subId);
		} else if ("nameTones".equals(subRowName)) {
			return this.createIxPoiNameTone(subId);
		} else if ("samepoiParts".equals(subRowName)) {
			return this.createIxSamepoiPart(subId);
		} else if ("children".equals(subRowName)) {
			return this.createIxPoiChildren(subId);
		}
		return null;
	}

	/**
	 * 根据json中的key获取对象
	 */
	@Override
	public List<BasicRow> getSubRowByName(String subRowName) throws Exception {
		if ("addresses".equals(subRowName)) {
			return (List) subrows.get("IX_POI_ADDRESS");
		} else if ("audioes".equals(subRowName)) {
			return (List) subrows.get("IX_POI_AUDIO");
		} else if ("contacts".equals(subRowName)) {
			return (List) subrows.get("IX_POI_CONTACT");
		} else if ("entryImages".equals(subRowName)) {
			return (List) subrows.get("IX_POI_ENTRYIMAGE");
		} else if ("flags".equals(subRowName)) {
			return (List) subrows.get("IX_POI_FLAG");
		} else if ("icons".equals(subRowName)) {
			return (List) subrows.get("IX_POI_ICON");
		} else if ("names".equals(subRowName)) {
			return (List) subrows.get("IX_POI_NAME");
		} else if ("parents".equals(subRowName)) {
			return (List) subrows.get("IX_POI_PARENT");
		} else if ("children".equals(subRowName)) {
			return (List) subrows.get("IX_POI_CHILDREN");
		} else if ("photos".equals(subRowName)) {
			return (List) subrows.get("IX_POI_PHOTO");
		} else if ("videoes".equals(subRowName)) {
			return (List) subrows.get("IX_POI_VIDEO");
		} else if ("parkings".equals(subRowName)) {
			return (List) subrows.get("IX_POI_PARKING");
		} else if ("tourroutes".equals(subRowName)) {
			return (List) subrows.get("IX_POI_TOURROUTE");
		} else if ("events".equals(subRowName)) {
			return (List) subrows.get("IX_POI_EVENT");
		} else if ("details".equals(subRowName)) {
			return (List) subrows.get("IX_POI_DETAIL");
		} else if ("businesstimes".equals(subRowName)) {
			return (List) subrows.get("IX_POI_BUSINESSTIME");
		} else if ("chargingstations".equals(subRowName)) {
			return (List) subrows.get("IX_POI_CHARGINGSTATION");
		} else if ("chargingplots".equals(subRowName)) {
			return (List) subrows.get("IX_POI_CHARGINGPLOT");
		} else if ("chargingplotPhs".equals(subRowName)) {
			return (List) subrows.get("IX_POI_CHARGINGPLOT_PH");
		} else if ("buildings".equals(subRowName)) {
			return (List) subrows.get("IX_POI_BUILDING");
		} else if ("advertisements".equals(subRowName)) {
			return (List) subrows.get("IX_POI_ADVERTISEMENT");
		} else if ("gasstations".equals(subRowName)) {
			return (List) subrows.get("IX_POI_GASSTATION");
		} else if ("introductions".equals(subRowName)) {
			return (List) subrows.get("IX_POI_INTRODUCTION");
		} else if ("attractions".equals(subRowName)) {
			return (List) subrows.get("IX_POI_ATTRACTION");
		} else if ("hotels".equals(subRowName)) {
			return (List) subrows.get("IX_POI_HOTEL");
		} else if ("restaurants".equals(subRowName)) {
			return (List) subrows.get("IX_POI_RESTAURANT");
		} else if ("carrentals".equals(subRowName)) {
			return (List) subrows.get("IX_POI_CARRENTAL");
		} else if ("samepoiParts".equals(subRowName)) {
			return (List) subrows.get("IX_SAMEPOI_PART");
		} else if ("nameFlags".equals(subRowName)) {
			return (List) subrows.get("IX_POI_NAME_FLAG");
		} else if ("samepois".equals(subRowName)) {
			return (List) subrows.get("IX_SAMEPOI");
		} else if ("nameTones".equals(subRowName)) {
			return (List) subrows.get("IX_POI_NAME_TONE");
		} else if ("nameTones".equals(subRowName)) {
			return (List) subrows.get("IX_POI_FLAG_METHOD");
		} else {
			throw new Exception("字段名为:" + subRowName + "的子表未找到");
		}
	}

	/**
	 * 只包含一级子表
	 */
	@Override
	public BasicRow createSubRowByTableName(String tableName) throws Exception {
		if (IX_POI_ADDRESS.equals(tableName)) {
			return this.createIxPoiAddress();
		} else if (IX_POI_AUDIO.equals(tableName)) {
			return this.createIxPoiAudio();
		} else if (IX_POI_CONTACT.equals(tableName)) {
			return this.createIxPoiContact();
		} else if (IX_POI_ENTRYIMAGE.equals(tableName)) {
			return this.createIxPoiEntryimage();
		} else if (IX_POI_FLAG.equals(tableName)) {
			return this.createIxPoiFlag();
		} else if (IX_POI_ICON.equals(tableName)) {
			return this.createIxPoiIcon();
		} else if (IX_POI_NAME.equals(tableName)) {
			return this.createIxPoiName();
		} else if (IX_POI_PARENT.equals(tableName)) {
			return this.createIxPoiParent();
		} else if (IX_POI_PHOTO.equals(tableName)) {
			return this.createIxPoiPhoto();
		} else if (IX_POI_VIDEO.equals(tableName)) {
			return this.createIxPoiVideo();
		} else if (IX_POI_PARKING.equals(tableName)) {
			return this.createIxPoiParking();
		} else if (IX_POI_TOURROUTE.equals(tableName)) {
			return this.createIxPoiTourroute();
		} else if (IX_POI_EVENT.equals(tableName)) {
			return this.createIxPoiEvent();
		} else if (IX_POI_DETAIL.equals(tableName)) {
			return this.createIxPoiDetail();
		} else if (IX_POI_BUSINESSTIME.equals(tableName)) {
			return this.createIxPoiBusinesstime();
		} else if (IX_POI_CHARGINGSTATION.equals(tableName)) {
			return this.createIxPoiChargingstation();
		} else if (IX_POI_CHARGINGPLOT.equals(tableName)) {
			return this.createIxPoiChargingplot();
		} else if (IX_POI_CHARGINGPLOT_PH.equals(tableName)) {
			return this.createIxPoiChargingplotPh();
		} else if (IX_POI_BUILDING.equals(tableName)) {
			return this.createIxPoiBuilding();
		} else if (IX_POI_ADVERTISEMENT.equals(tableName)) {
			return this.createIxPoiAdvertisement();
		} else if (IX_POI_GASSTATION.equals(tableName)) {
			return this.createIxPoiGasstation();
		} else if (IX_POI_INTRODUCTION.equals(tableName)) {
			return this.createIxPoiIntroduction();
		} else if (IX_POI_ATTRACTION.equals(tableName)) {
			return this.createIxPoiAttraction();
		} else if (IX_POI_HOTEL.equals(tableName)) {
			return this.createIxPoiHotel();
		} else if (IX_POI_RESTAURANT.equals(tableName)) {
			return this.createIxPoiRestaurant();
		} else if (IX_POI_CARRENTAL.equals(tableName)) {
			return this.createIxPoiCarrental();
		} else if (IX_POI_FLAG_METHOD.equals(tableName)) {
			return this.createIxPoiFlagMethod();
		} else {
			throw new Exception("未知的子表名:" + tableName);
		}
	}

	public boolean isFreshFlag() {
		IxPoi ixPoi = (IxPoi) this.mainrow;
		if (!(ixPoi.getOpType().equals(OperationType.UPDATE)))
			return false;
		Map<String, Object> ixPoiOldValues = ixPoi.getOldValues();
		if (ixPoiOldValues != null) {
			for (String key : ixPoiOldValues.keySet()) {
				if (key.equals(IxPoi.FIELD_STATE) || key.equals(IxPoi.OLD_NAME)
						|| key.equals(IxPoi.OLD_ADDRESS)
						|| key.equals(IxPoi.OLD_KIND) || key.equals(IxPoi.LOG)
						|| key.equals(IxPoi.POI_MEMO)
						|| key.equals(IxPoi.COLLECT_TIME)
						|| key.equals(IxPoi.DATA_VERSION)
						|| key.equals(IxPoi.OLD_X_GUIDE)
						|| key.equals(IxPoi.OLD_Y_GUIDE)) {
					continue;
				} else {
					return false;
				}
			}
		}
		// 是否有任何子表变更
		for (Map.Entry<String, List<BasicRow>> entry : this.subrows.entrySet()) {
			// 过滤ix_poi_photo
			if (IxPoiObj.IX_POI_PHOTO.equals(entry.getKey())
					|| IxPoiObj.IX_POI_AUDIO.equals(entry.getKey())) {
				continue;
			}
			List<BasicRow> subrowList = entry.getValue();
			if (subrowList == null) {
				continue;
			}
			for (BasicRow basicRow : subrowList) {
				if (basicRow.isChanged()) {
					return false;
				}
			}
		}
		return true;
	}

	public static final String IX_POI = "IX_POI";
	public static final String IX_POI_NAME = "IX_POI_NAME";
	public static final String IX_POI_NAME_FLAG = "IX_POI_NAME_FLAG";
	public static final String IX_POI_NAME_TONE = "IX_POI_NAME_TONE";
	public static final String IX_POI_ADDRESS = "IX_POI_ADDRESS";
	public static final String IX_POI_CONTACT = "IX_POI_CONTACT";
	public static final String IX_POI_FLAG = "IX_POI_FLAG";
	public static final String IX_POI_ENTRYIMAGE = "IX_POI_ENTRYIMAGE";
	public static final String IX_POI_ICON = "IX_POI_ICON";
	public static final String IX_POI_PHOTO = "IX_POI_PHOTO";
	public static final String IX_POI_AUDIO = "IX_POI_AUDIO";
	public static final String IX_POI_VIDEO = "IX_POI_VIDEO";
	public static final String IX_POI_PARENT = "IX_POI_PARENT";
	public static final String IX_POI_CHILDREN = "IX_POI_CHILDREN";
	public static final String IX_POI_BUILDING = "IX_POI_BUILDING";
	public static final String IX_POI_DETAIL = "IX_POI_DETAIL";
	public static final String IX_POI_BUSINESSTIME = "IX_POI_BUSINESSTIME";
	public static final String IX_POI_INTRODUCTION = "IX_POI_INTRODUCTION";
	public static final String IX_POI_ADVERTISEMENT = "IX_POI_ADVERTISEMENT";
	public static final String IX_POI_GASSTATION = "IX_POI_GASSTATION";
	public static final String IX_POI_CHARGINGSTATION = "IX_POI_CHARGINGSTATION";
	public static final String IX_POI_CHARGINGPLOT = "IX_POI_CHARGINGPLOT";
	public static final String IX_POI_CHARGINGPLOT_PH = "IX_POI_CHARGINGPLOT_PH";
	public static final String IX_POI_PARKING = "IX_POI_PARKING";
	public static final String IX_POI_ATTRACTION = "IX_POI_ATTRACTION";
	public static final String IX_POI_HOTEL = "IX_POI_HOTEL";
	public static final String IX_POI_RESTAURANT = "IX_POI_RESTAURANT";
	public static final String IX_POI_TOURROUTE = "IX_POI_TOURROUTE";
	public static final String IX_POI_EVENT = "IX_POI_EVENT";
	public static final String IX_POI_CARRENTAL = "IX_POI_CARRENTAL";
	public static final String IX_POI_FLAG_METHOD = "IX_POI_FLAG_METHOD";

}
