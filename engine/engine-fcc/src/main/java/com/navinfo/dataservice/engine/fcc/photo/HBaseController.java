package com.navinfo.dataservice.engine.fcc.photo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.dbutils.DbUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.hbase.async.GetRequest;
import org.hbase.async.KeyValue;
import org.hbase.async.Scanner;

import ch.hsr.geohash.GeoHash;

import com.navinfo.dataservice.commons.constant.HBaseConstant;
import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.commons.mercator.MercatorProjection;
import com.navinfo.dataservice.commons.photo.Photo;
import com.navinfo.dataservice.commons.util.ByteUtils;
import com.navinfo.dataservice.commons.util.FileUtils;
import com.navinfo.dataservice.commons.util.UuidUtils;
import com.navinfo.dataservice.dao.fcc.HBaseConnector;

public class HBaseController {

	private ArrayList<KeyValue> getByRowkey(String tableName, String rowkey,
			String family, String... qualifiers) throws Exception {

		final GetRequest get = new GetRequest(tableName, rowkey);

		if (family != null) {
			get.family(family);
		}

		if (qualifiers.length > 0) {
			get.qualifiers(ByteUtils.toBytes(qualifiers));
		}

		ArrayList<KeyValue> list = HBaseConnector.getInstance().getClient()
				.get(get).joinUninterruptibly();

		return list;
	}

	public byte[] getPhotoByRowkey(String rowkey) throws Exception {

		List<KeyValue> list = getByRowkey("photo", rowkey, "data", "origin");

		for (KeyValue kv : list) {
			return kv.value();
		}

		return new byte[0];
	}

	public byte[] getPhotoDetailByRowkey(String rowkey) throws Exception {

		List<KeyValue> list = getByRowkey("photo", rowkey, "data", "attribute");

		for (KeyValue kv : list) {
			return kv.value();
		}

		return new byte[0];
	}

	private ArrayList<ArrayList<KeyValue>> scan(String tableName,
			String startKey, String stopKey, String family,
			String... qualifiers) throws Exception {

		Scanner scanner = HBaseConnector.getInstance().getClient()
				.newScanner(tableName);

		scanner.setStartKey(startKey);

		scanner.setStopKey(stopKey);

		if (family != null) {
			scanner.setFamily(family);
		}

		if (qualifiers.length > 0) {
			scanner.setQualifiers(ByteUtils.toBytes(qualifiers));
		}

		ArrayList<ArrayList<KeyValue>> rows;

		ArrayList<ArrayList<KeyValue>> result = new ArrayList<ArrayList<KeyValue>>();

		while ((rows = scanner.nextRows().joinUninterruptibly()) != null) {

			for (ArrayList<KeyValue> list : rows) {
				result.add(list);
			}
		}

		return result;
	}

	public ArrayList<ArrayList<KeyValue>> getPhotoBySpatial(String wkt)
			throws Exception {

		double[] mbr = GeoTranslator.getMBR(wkt);

		String startKey = GeoHash.geoHashStringWithCharacterPrecision(mbr[1],
				mbr[0], 12);

		String stopKey = GeoHash.geoHashStringWithCharacterPrecision(mbr[3],
				mbr[2], 12);

		ArrayList<ArrayList<KeyValue>> result = scan("photo", startKey,
				stopKey, "data", "brief");

		return result;
	}

	public ArrayList<ArrayList<KeyValue>> getPhotoByTileWithGap(int x, int y,
			int z, int gap) throws Exception {

		String wkt = MercatorProjection.getWktWithGap(x, y, z, gap);

		return getPhotoBySpatial(wkt);
	}

	public ArrayList<ArrayList<KeyValue>> getPhotoTile(double minLon,
			double minLat, double maxLon, double maxLat, int zoom)
			throws Exception {

		long xmin = MercatorProjection.longitudeToTileX(minLon, (byte) zoom);

		long xmax = MercatorProjection.longitudeToTileX(maxLon, (byte) zoom);

		long ymax = MercatorProjection.latitudeToTileY(minLat, (byte) zoom);

		long ymin = MercatorProjection.latitudeToTileY(maxLat, (byte) zoom);

		String startKey = String.format("%02d%08d%07d", zoom, xmin, ymin);

		String stopKey = String.format("%02d%08d%07d", zoom, xmax, ymax);

		ArrayList<ArrayList<KeyValue>> result = scan("photoTile", startKey,
				stopKey, "data", "photo");

		return result;
	}
	
	public void putPhoto(String rowkey, InputStream in) throws Exception{
		
		Photo photo = new Photo();
		
		photo.setRowkey(rowkey);
		
		int count = in.available();
		
		byte[] bytes = new byte[(int) count];

		in.read(bytes);
		
		byte[] sbytes = FileUtils.makeSmallImage(bytes);
		
		Connection hbaseConn = HBaseConnector.getInstance().getConnection();
		Table htab = null;
		try{
			htab = hbaseConn.getTable(TableName.valueOf(HBaseConstant.photoTab));
			
			Put put = new Put(rowkey.getBytes());
			
			put.addColumn("data".getBytes(), "attribute".getBytes(), JSONObject
					.fromObject(photo).toString().getBytes());
			
			put.addColumn("data".getBytes(), "origin".getBytes(), bytes);
			
			put.addColumn("data".getBytes(), "thumbnail".getBytes(), sbytes);
			
			htab.put(put);
		}catch (Exception e) {
			throw e;
		}finally {
			if(htab!=null){
				htab.close();
			}
		}
	}

	public String putPhoto(InputStream in) throws Exception{
		String rowkey = UuidUtils.genUuid();
		putPhoto(rowkey, in);
		return rowkey;
	}
}
