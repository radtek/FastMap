BEGIN
  FOR A IN (SELECT TABLE_NAME FROM USER_TABLES) LOOP
    BEGIN
      EXECUTE IMMEDIATE 'ALTER TABLE '|| A.TABLE_NAME ||' ADD (U_DATE VARCHAR2(14),ROW_ID RAW(16))';
    EXCEPTION
      WHEN OTHERS THEN
        EXECUTE IMMEDIATE 'ALTER TABLE '|| A.TABLE_NAME ||' ADD (ROW_ID RAW(16))';
   END;
  END LOOP;
END;
/
