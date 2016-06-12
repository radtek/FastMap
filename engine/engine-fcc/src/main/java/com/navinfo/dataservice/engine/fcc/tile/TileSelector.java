package com.navinfo.dataservice.engine.fcc.tile;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.hbase.async.KeyValue;

import com.navinfo.dataservice.dao.fcc.HBaseController;
import com.navinfo.dataservice.dao.glm.iface.ObjType;

/**
 * 瓦片的查询类
 */
public class TileSelector {

	/**
	 * 获取某瓦片内的数据
	 * 
	 * @param x
	 *            瓦片x
	 * @param y
	 *            瓦片y
	 * @param z
	 *            瓦片等级
	 * @return 瓦片数据列表
	 * @throws Exception
	 */
	public static List<String> getRdLinkTiles(int x, int y, int z, int dbId) throws Exception {

		List<String> listResult = new ArrayList<String>();

		String key = String.format("%02d", z) + String.format("%08d", x)
				+ String.format("%07d", y);

		HBaseController control = new HBaseController();
		
		ArrayList<KeyValue> list = control.getByRowkey("RDLINK_"+dbId, key, null);

		for (KeyValue kv : list) {
			listResult.add(new String(kv.value()));
		}

		return listResult;
	}
	
	/**
	 * 获取瓦片内的铁路数据
	 * 
	 * @param x
	 *            瓦片x
	 * @param y
	 *            瓦片y
	 * @param z
	 *            瓦片等级
	 * @return 瓦片数据列表
	 * @throws Exception
	 */
	public static List<String> getRwLinkTiles(int x, int y, int z, int dbId) throws Exception {

		List<String> listResult = new ArrayList<String>();

		String key = String.format("%02d", z) + String.format("%08d", x)
				+ String.format("%07d", y);

		HBaseController control = new HBaseController();
		
		ArrayList<KeyValue> list = control.getByRowkey("RWLINK_"+dbId, key, null);

		for (KeyValue kv : list) {
			listResult.add(new String(kv.value()));
		}

		return listResult;
	}
	
	/**
	 * 获取瓦片内的行政区划线数据
	 * 
	 * @param x
	 *            瓦片x
	 * @param y
	 *            瓦片y
	 * @param z
	 *            瓦片等级
	 * @return 瓦片数据列表
	 * @throws Exception
	 */
	public static List<String> getAdLinkTiles(int x, int y, int z, int dbId) throws Exception {

		List<String> listResult = new ArrayList<String>();

		String key = String.format("%02d", z) + String.format("%08d", x)
				+ String.format("%07d", y);

		HBaseController control = new HBaseController();
		
		ArrayList<KeyValue> list = control.getByRowkey("ADLINK_"+dbId, key, null);

		for (KeyValue kv : list) {
			listResult.add(new String(kv.value()));
		}

		return listResult;
	}
	
	public static JSONObject getByTiles(List<ObjType> types, int x, int y, int z, int dbId) throws Exception{
		
		JSONObject data = new JSONObject();
		
		for(ObjType type : types){
			switch(type){
			case RDLINK:
				data.put("RDLINK", getRdLinkTiles(x, y, z, dbId));
				break;
			case RWLINK:
				data.put("RWLINK", getRwLinkTiles(x, y, z, dbId));
				break;
			case ADLINK:
				data.put("ADLINK", getAdLinkTiles(x, y, z, dbId));
			}
		}
		
		return data;
		
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getRdLinkTiles(53966, 24852, 16, 11));
	}
}