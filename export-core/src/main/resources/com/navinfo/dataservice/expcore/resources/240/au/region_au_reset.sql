
UPDATE AU_SERIESPHOTO SET TASK_ID=0 where TASK_ID=${taskId} ;
UPDATE AU_ADAS_GPSTRACK SET TASK_ID=0 where TASK_ID=${taskId} ;

UPDATE AU_IX_POI SET GEO_TASK_ID=0,GEO_OPRSTATUS=0 where GEO_TASK_ID=${taskId};
UPDATE AU_IX_POI SET ATT_TASK_ID=0,ATT_OPRSTATUS=0 where ATT_TASK_ID=${taskId};
UPDATE AU_IX_POINTADDRESS SET GEO_TASK_ID=0,GEO_OPRSTATUS=0 where GEO_TASK_ID=${taskId};
UPDATE AU_IX_POINTADDRESS SET ATT_TASK_ID=0,ATT_OPRSTATUS=0 where ATT_TASK_ID=${taskId};
UPDATE AU_IX_ANNOTATION SET GEO_TASK_ID=0,GEO_OPRSTATUS=0 where GEO_TASK_ID=${taskId};
UPDATE AU_IX_ANNOTATION SET ATT_TASK_ID=0,ATT_OPRSTATUS=0 where ATT_TASK_ID=${taskId};

