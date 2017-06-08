package com.navinfo.dataservice.engine.fcc.tips.check;

import com.navinfo.dataservice.dao.fcc.SolrController;
import com.navinfo.nirobot.common.storage.SolrConnector;
import com.navinfo.nirobot.common.storage.SolrOperator;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhangjunfang on 2017/6/8.
 */
public class TipsPreCheckUtils {
    /**
     * link是否在GDB中存在
     * @param conn
     * @param id
     * @return
     * @throws Exception
     */
    public static boolean hasInGdb(Connection conn, String id) throws Exception{
        String sqlLink = "select count(1) ct from rd_link rl where rl.link_pid = :1 and rl.MULTI_DIGITIZED = 1 and rl.u_record <> 2";
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = conn.prepareStatement(sqlLink);
            pstmt.setString(1, id);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()){//有记录
                int ct = resultSet.getInt("ct");
                if(ct > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(pstmt != null) {
                pstmt.close();
            }
            if(resultSet != null) {
                resultSet.close();
            }
        }
        return false;
    }

    /**
     * 测线是否存在
     * @param id
     * @return
     * @throws Exception
     */
    public static boolean hasInSolr(SolrController solr, String id) throws Exception {
        String query = "s_sourceType:1501 AND -t_lifecycle:3 AND relate_links:*|" + id + "|*";
        SolrDocumentList solresult = solr.queryTipsSolrDocFilter(query, null);
        if (solresult.size() > 0){
            return true;
        }
        return false;
    }
}
