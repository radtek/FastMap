package com.navinfo.dataservice.FosEngine.test;

import com.navinfo.dataservice.commons.util.GridUtils;

public class Test4 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
//		116.25006,39.967934
		//System.out.println(MercatorProjection.latitudeToTileY(39.967934, (byte)17));
		//System.out.println(MercatorProjection.longitudeToTileX(116.25006,(byte) 17));

	//ZipUtils.unzipFile("c:/2/tips_10000_20151030095205.zip", "c:/2");
	     double[] a =GridUtils.geohash2Lonlat("DD94C8988A254A759FA454C9A75409A315402015111313302952".substring(0,12));
	     
	     System.out.println(a[0]);
	     System.out.println(a[1]);
	}

}
