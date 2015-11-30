UPDATE AU_MARK SET TASK_ID=${taskId} where MARK_ID IN (SELECT PID FROM TEMP_AU_MARK);

UPDATE AU_IX_POI SET GEO_TASK_ID=decode(GEO_TASK_ID,0,${taskId},decode(GEO_OPRSTATUS,0,${taskId},GEO_TASK_ID)),ATT_TASK_ID=decode(ATT_TASK_ID,0,${taskId},decode(ATT_OPRSTATUS,0,${taskId},ATT_TASK_ID)),GEO_OPRSTATUS=decode(GEO_OPRSTATUS,0,${dmsStatus},GEO_OPRSTATUS),ATT_OPRSTATUS=decode(ATT_OPRSTATUS,0,${dmsStatus},ATT_OPRSTATUS) where AUDATA_ID IN (SELECT PID FROM TEMP_IX_POI) AND (GEO_OPRSTATUS=0 OR ATT_OPRSTATUS=0) AND STATE <>0;
UPDATE AU_IX_POINTADDRESS SET GEO_TASK_ID=decode(GEO_TASK_ID,0,${taskId},decode(GEO_OPRSTATUS,0,${taskId},GEO_TASK_ID)),ATT_TASK_ID=decode(ATT_TASK_ID,0,${taskId},decode(ATT_OPRSTATUS,0,${taskId},ATT_TASK_ID)),GEO_OPRSTATUS=decode(GEO_OPRSTATUS,0,${dmsStatus},GEO_OPRSTATUS),ATT_OPRSTATUS=decode(ATT_OPRSTATUS,0,${dmsStatus},ATT_OPRSTATUS)  where AUDATA_ID IN (SELECT PID FROM TEMP_IX_POINTADDRESS)  AND (GEO_OPRSTATUS=0 OR ATT_OPRSTATUS=0) AND STATE <>0;
UPDATE AU_IX_ANNOTATION SET GEO_TASK_ID=decode(GEO_TASK_ID,0,${taskId},decode(GEO_OPRSTATUS,0,${taskId},GEO_TASK_ID)),ATT_TASK_ID=decode(ATT_TASK_ID,0,${taskId},decode(ATT_OPRSTATUS,0,${taskId},ATT_TASK_ID)),GEO_OPRSTATUS=decode(GEO_OPRSTATUS,0,${dmsStatus},GEO_OPRSTATUS),ATT_OPRSTATUS=decode(ATT_OPRSTATUS,0,${dmsStatus},ATT_OPRSTATUS)  where AUDATA_ID IN (SELECT PID FROM TEMP_IX_ANNOTATION)  AND (GEO_OPRSTATUS=0 OR ATT_OPRSTATUS=0) AND FIELD_MODIFY_FLAG IS NOT NULL ;

