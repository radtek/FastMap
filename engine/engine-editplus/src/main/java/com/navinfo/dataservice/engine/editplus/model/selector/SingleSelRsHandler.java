package com.navinfo.dataservice.engine.editplus.model.selector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;

import com.navinfo.dataservice.commons.geom.GeoTranslator;
import com.navinfo.dataservice.engine.editplus.glm.GlmColumn;
import com.navinfo.dataservice.engine.editplus.glm.GlmTable;
import com.navinfo.dataservice.engine.editplus.model.BasicRow;
import com.navinfo.dataservice.engine.editplus.utils.ResultSetGetter;

import oracle.sql.STRUCT;

/** 
 * @ClassName: SingleSelRsHandler
 * @author songdongyan
 * @date 2016年11月17日
 * @Description: SingleSelRsHandler.java
 */
public class SingleSelRsHandler implements ResultSetHandler<BasicRow> {

	private GlmTable glmTable;
	private long objPid;
	public SingleSelRsHandler(GlmTable glmTable,long objPid){
		this.glmTable=glmTable;
		this.objPid=objPid;
	}

	@Override
	public BasicRow handle(ResultSet rs) throws SQLException {
		BasicRow row = null;
		try{
			row = (BasicRow)Class.forName(glmTable.getModelClassName()).getConstructor(new Class[]{long.class}).newInstance(objPid);
			while(rs.next()){
				for(Map.Entry<String, GlmColumn> entry:glmTable.getColumns().entrySet()){
					String columName = entry.getValue().getName();
					/*--------测试使用start------*/
					System.out.println(entry.getValue().getName());
					if(columName.equals("GEOMETRY")){
						System.out.println(entry.getValue().getName());
					}
					/*--------测试使用end------*/
					String type = entry.getValue().getType();
					int dataPrecision = entry.getValue().getDataPrecision();
					int dataScale = entry.getValue().getDataScale();
					
					if(type.equals(GlmColumn.TYPE_NUMBER)){
						if(dataScale > 0){
							row.setAttrByCol(entry.getKey(), rs.getDouble(columName));
						}else{
							if(dataPrecision>8){
								row.setAttrByCol(entry.getKey(), rs.getLong(columName));
							}else{
								row.setAttrByCol(entry.getKey(), rs.getInt(columName));
							}
						}
					}else if(type.equals(GlmColumn.TYPE_VARCHAR)){
						row.setAttrByCol(entry.getKey(), rs.getString(columName));
					}else if(type.equals(GlmColumn.TYPE_GEOMETRY)){
						STRUCT struct = (STRUCT) rs.getObject(columName);
						row.setAttrByCol(entry.getKey(), GeoTranslator.struct2Jts(struct));
					}else if(type.equals(GlmColumn.TYPE_RAW)){
						row.setAttrByCol(entry.getKey(), rs.getString(columName));
					}else if(type.equals(GlmColumn.TYPE_TIMESTAMP)){
						Date date = new Date();
						date = rs.getTimestamp(columName);
						row.setAttrByCol(entry.getKey(), date);
					}
				}
			}
			return row;
		}catch(Exception e){
			throw new SQLException(e.getMessage(),e);
		}
	}
	
	

}
