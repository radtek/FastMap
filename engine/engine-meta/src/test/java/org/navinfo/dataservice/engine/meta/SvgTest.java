/**
 * 
 */
package org.navinfo.dataservice.engine.meta;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.dbutils.DbUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.navinfo.dataservice.bizcommons.datasource.DBConnector;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.engine.meta.patternimage.PatternImageExporter;
//import com.navinfo.dataservice.engine.meta.patternimage.PatternImageImporter;
import net.sf.json.JSONObject;

/** 
* @ClassName: SvgTest 
* @author Zhang Xiaolong
* @date 2016年12月23日 上午9:31:06 
* @Description: TODO
*/
public class SvgTest {
	
	@Before
	public void before() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "dubbo-consumer-datahub-test.xml" });
		context.start();
		new ApplicationContextUtil().setApplicationContext(context);
	}
	
	//@Test
	public void testUpdateSvgData() throws Exception
	{
		String sql = "update SC_VECTOR_MATCH set file_content = ? where file_name = 'S0CLL15OC91B'";
		//String sql = "update SC_VECTOR_MATCH set memo = ? where file_name = 'S0CYZ139NE28'";

		PreparedStatement pstmt = null;

		ResultSet resultSet = null;

		Connection conn = null;
		FileInputStream in = null;
		try {

			conn = DBConnector.getInstance().getMetaConnection();

			pstmt = conn.prepareStatement(sql);
			File f = new File("f:/S0CLL15OC91B.svg");
			 in = new FileInputStream(f);
			int length = in.available();
			pstmt.setBinaryStream(1, in);
			//pstmt.setString(1, "测试");
			pstmt.executeUpdate();
			
			conn.commit();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			in.close();
			DbUtils.closeQuietly(conn, pstmt, resultSet);
		}
	}
	@Test
	public void testGetDataByName()
	{
		PatternImageExporter export = new PatternImageExporter();
		Set<String> set = new HashSet<String>();
		 set.add("00ff000a");
		 set.add("00ff000d");
		 set.add("S0CLL15OC91A");
		 set.add("S0CLL15OC91B");
		 set.add("S0CLL15OCA0A");
		 set.add("S0CLL15OCA0D");
		try {
			export.export2SqliteByNames("f:/PatternImg", set);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//@Test
	public void testGetSvgData()
	{
		String parameter = "{\"name\":\"S0CYZ139NE29\",\"pageNum\":0,\"pageSize\":6}";

        try {
            JSONObject jsonReq = JSONObject.fromObject(parameter);

            String name = jsonReq.getString("name");

            int pageSize = jsonReq.getInt("pageSize");

            int pageNum = jsonReq.getInt("pageNum");

           // SvgImageSelector selector = new SvgImageSelector();

          //  JSONObject data = selector.searchByName(name, pageSize, pageNum);
            
          //  System.out.println(data);
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}	
	//@Test
	public void testUpdateSvgExp() throws Exception
	{
		String path = "f:/";
		PatternImageExporter patternImageExporter=new PatternImageExporter();
		patternImageExporter.export2Sqlite(path);
	}
	//@Test
	public void testUpdateSvgImp() throws Exception
	{
		/*String path = "f:/PatternImg";
		//PatternImageImporter patternImageImp = new PatternImageImporter();
		Connection conn = null;
		PreparedStatement pstmt =null;
		PreparedStatement pstmtSvg=null;
		int counter = 0;
		String sql = "update SC_MODEL_MATCH_G set format=:1,file_content=:2 where file_name=:3";
		String sqlSvg = "update SC_VECTOR_MATCH set format=:1,file_content=:2 where file_name=:3";

		conn = DBConnector.getInstance().getMetaConnection();
		conn.setAutoCommit(false);

		pstmt = conn.prepareStatement(sql);
		pstmtSvg = conn.prepareStatement(sqlSvg);
		PatternImageImporter patternImageImporter = new PatternImageImporter(pstmt,pstmtSvg);
		patternImageImporter.readDataImg(path);

		patternImageImporter.readDataSvg(path);
		
		conn.commit();

		conn.close();
		
		System.out.println("Done. Total:"+counter);*/
	}
	
	
	
	
	/*public static void main(String[] args) throws IOException {
		File file = new File("f:/S0CLL15OC91B.svg");
		ImageInputStream is = null;
		try {
			is = ImageIO.createImageInputStream(file);
			String[] splits = file.getName().split("\\.");
			 if(null == is)  
	            {  
				 System.out.println("xxxx:  "+splits[0]);
	            }  
			 String format = splits[splits.length - 1];
			 System.out.println("format : "+format);
			 System.out.println(splits[1]+"  yy:  "+splits[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//outSteam.close();  
		    is.close(); 
		}  
	}*/
	public static void main(String[] args) {
		List<String> ruleList=new ArrayList<String>();
		ruleList.add("CHR60009");
		ruleList.add("CHR60010");
		ruleList.add("CHR60011");
		ruleList.add("CHR60012");
		ruleList.add("CHR60013");
		ruleList.add("CHR60014");
		ruleList.add("CHR61009");
		ruleList.add("CHR61010");
		ruleList.add("CHR61011");
		ruleList.add("CHR61012");
		ruleList.add("CHR61013");
		ruleList.add("CHR61014");
		ruleList.add("CHR63415");
		ruleList.add("CHR63419");
		ruleList.add("CHR63423");
		ruleList.add("CHR63428");
		ruleList.add("CHR63436");
		ruleList.add("CHR70107");
		ruleList.add("CHR70108");
		ruleList.add("CHR70109");
		ruleList.add("CHR70110");
		ruleList.add("CHR70111");
		ruleList.add("CHR70112");
		ruleList.add("CHR71024");
		ruleList.add("CHR71025");
		ruleList.add("CHR71026");
		ruleList.add("CHR71027");
		ruleList.add("CHR71028");
		ruleList.add("CHR71041");
		ruleList.add("CHR73040");
		ruleList.add("CHR73041");
		ruleList.add("CHR73042");
		ruleList.add("CHR73043");
		ruleList.add("CHR73044");
		ruleList.add("CHR73045");
		ruleList.add("COM01001");
		ruleList.add("COM01003");
		ruleList.add("COM20552");
		ruleList.add("COM60104");
		JSONObject metaValidationRequestJSON=new JSONObject();
		metaValidationRequestJSON.put("executeDBId", 106);//元数据库dbId
		metaValidationRequestJSON.put("kdbDBId", 106);//元数据库dbId
		metaValidationRequestJSON.put("ruleIds", ruleList);
		metaValidationRequestJSON.put("timeOut", 600);
		
		System.out.println(metaValidationRequestJSON);
	}
	
}
