package com.navinfo.dataservice.dao.plus.model.ixpoi;

import com.navinfo.dataservice.dao.plus.model.basic.BasicRow;

/** 
* @ClassName:  IxPoiEntryimage 
* @author code generator
* @date 2016-11-18 11:27:47 
* @Description: TODO
*/
public class IxPoiEntryimage extends BasicRow {
	protected long poiPid ;
	protected String imageCode ;
	protected int xPixelR4 ;
	protected int yPixelR4 ;
	protected int xPixelR5 ;
	protected int yPixelR5 ;
	protected int xPixel35 ;
	protected int yPixel35 ;
	protected String memo ;
	protected long mainPoiPid ;
	
	public IxPoiEntryimage (long objPid){
		super(objPid);
		setPoiPid(objPid);
	}
	
	public long getPoiPid() {
		return poiPid;
	}
	public void setPoiPid(long poiPid) {
		if(this.checkValue("POI_PID",this.poiPid,poiPid)){
			this.poiPid = poiPid;
		}
	}
	public String getImageCode() {
		return imageCode;
	}
	public void setImageCode(String imageCode) {
		if(this.checkValue("IMAGE_CODE",this.imageCode,imageCode)){
			this.imageCode = imageCode;
		}
	}
	public int getXPixelR4() {
		return xPixelR4;
	}
	public void setXPixelR4(int xPixelR4) {
		if(this.checkValue("X_PIXEL_R4",this.xPixelR4,xPixelR4)){
			this.xPixelR4 = xPixelR4;
		}
	}
	public int getYPixelR4() {
		return yPixelR4;
	}
	public void setYPixelR4(int yPixelR4) {
		if(this.checkValue("Y_PIXEL_R4",this.yPixelR4,yPixelR4)){
			this.yPixelR4 = yPixelR4;
		}
	}
	public int getXPixelR5() {
		return xPixelR5;
	}
	public void setXPixelR5(int xPixelR5) {
		if(this.checkValue("X_PIXEL_R5",this.xPixelR5,xPixelR5)){
			this.xPixelR5 = xPixelR5;
		}
	}
	public int getYPixelR5() {
		return yPixelR5;
	}
	public void setYPixelR5(int yPixelR5) {
		if(this.checkValue("Y_PIXEL_R5",this.yPixelR5,yPixelR5)){
			this.yPixelR5 = yPixelR5;
		}
	}
	public int getXPixel35() {
		return xPixel35;
	}
	public void setXPixel35(int xPixel35) {
		if(this.checkValue("X_PIXEL_35",this.xPixel35,xPixel35)){
			this.xPixel35 = xPixel35;
		}
	}
	public int getYPixel35() {
		return yPixel35;
	}
	public void setYPixel35(int yPixel35) {
		if(this.checkValue("Y_PIXEL_35",this.yPixel35,yPixel35)){
			this.yPixel35 = yPixel35;
		}
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		if(this.checkValue("MEMO",this.memo,memo)){
			this.memo = memo;
		}
	}
	public long getMainPoiPid() {
		return mainPoiPid;
	}
	public void setMainPoiPid(long mainPoiPid) {
		if(this.checkValue("MAIN_POI_PID",this.mainPoiPid,mainPoiPid)){
			this.mainPoiPid = mainPoiPid;
		}
	}
	
	@Override
	public String tableName() {
		return "IX_POI_ENTRYIMAGE";
	}
	
	public static final String POI_PID = "POI_PID";
	public static final String IMAGE_CODE = "IMAGE_CODE";
	public static final String X_PIXEL_R4 = "X_PIXEL_R4";
	public static final String Y_PIXEL_R4 = "Y_PIXEL_R4";
	public static final String X_PIXEL_R5 = "X_PIXEL_R5";
	public static final String Y_PIXEL_R5 = "Y_PIXEL_R5";
	public static final String X_PIXEL_35 = "X_PIXEL_35";
	public static final String Y_PIXEL_35 = "Y_PIXEL_35";
	public static final String MEMO = "MEMO";
	public static final String MAIN_POI_PID = "MAIN_POI_PID";

}
