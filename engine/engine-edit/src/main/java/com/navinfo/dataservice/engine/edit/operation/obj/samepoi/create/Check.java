package com.navinfo.dataservice.engine.edit.operation.obj.samepoi.create;

import com.navinfo.dataservice.commons.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.navinfo.dataservice.api.metadata.iface.MetadataApi;
import com.navinfo.dataservice.api.metadata.model.MetadataMap;
import com.navinfo.dataservice.commons.springmvc.ApplicationContextUtil;
import com.navinfo.dataservice.dao.glm.model.poi.index.IxPoi;

import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

/**
 * @author zhangyt
 * @Title: Check.java
 * @Description: TODO
 * @date: 2016年9月7日 下午5:11:35
 * @version: v1.0
 */
public class Check {

    private static Logger logger = LoggerFactory.getLogger(Check.class);

    public Check() {
    }

    public String checkKindOfPOI(IxPoi poi, IxPoi otherPoi) throws Exception {
        if (checkInMetadata(poi, otherPoi) || checkInVariable(poi, otherPoi))
            return null;
        else {
            throw new Exception("创建同一POI失败，创建原则:1.\"两方分类在SC_POINT_KIND_NEW中TYPE=5的POIKIND和R_KIND" +
                    "中存在\" 2.\"一方分类为180400，另一方分类为除了210304外的其他分类\"");
        }
    }

    private boolean checkInMetadata(IxPoi poi, IxPoi otherPoi) {
        boolean result = false;
        MetadataApi apiService = (MetadataApi) ApplicationContextUtil.getBean("metadataApi");
        MetadataMap metaData = null;
        try {
            metaData = apiService.getMetadataMap();
        } catch (Exception e) {
            logger.error("无法获取元数据库POI的Kind数据", e);
        }
        Map<String, String> kind = metaData.getKind();
        for (Object obj : kind.keySet()) {
            String key = obj.toString();
            String value = kind.get(key);
            if (key.equals(poi.getKindCode()) && Arrays.asList(value.split(",")).contains(otherPoi.getKindCode())) {
                result = true;
                break;
            } else if (key.equals(otherPoi.getKindCode()) && Arrays.asList(value.split(",")).contains(poi.getKindCode())) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean checkInVariable(IxPoi poi, IxPoi otherPoi) throws Exception {
        boolean result = false;
        if ("180400".equals(poi.getKindCode()) && !"210304".equals(otherPoi.getKindCode())) {
            result = true;
        } else if (!"210304".equals(poi.getKindCode()) && "180400".equals(otherPoi.getKindCode())) {
            result = true;
        }
        return result;
    }

    /**
     * 检查Poi是否已存在同一关系
     *
     * @param poi
     * @param otherPoi
     * @throws Exception
     */
    public void checkIsSamePoi(IxPoi poi, IxPoi otherPoi) throws Exception {
        if (!poi.getSamepoiParts().isEmpty())
            throw new Exception(poi.pid() + "已存在同一关系，若想创建，请先解除，再创建。");
        if (!otherPoi.getSamepoiParts().isEmpty())
            throw new Exception(otherPoi.pid() + "已存在同一关系，若想创建，请先解除，再创建。");
    }
}
