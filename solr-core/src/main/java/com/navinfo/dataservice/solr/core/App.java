package com.navinfo.dataservice.solr.core;

import net.sf.json.JSONObject;

import org.json.JSONException;

import com.navinfo.dataservice.commons.geom.GeoTranslator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JSONException
    {
       String geostr = "{\"type\":\"Point\",\"coordinates\":[115.43476117949884,39.71905835958014]}";
       
       String wkt = GeoTranslator.jts2Wkt(GeoTranslator.geojson2Jts(JSONObject.fromObject(geostr)));
       
       System.out.println(wkt);
    }
}
