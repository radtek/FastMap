-- job result_msg
ALTER TABLE JOB_INFO MODIFY(RESULT_MSG VARCHAR2(1024));

COMMIT;
EXIT;
