TRUNCATE TABLE POI_EDIT_STATUS;
TRUNCATE TABLE POI_COLUMN_STATUS;
INSERT INTO POI_EDIT_STATUS(ROW_ID) SELECT ROW_ID FROM IX_POI;
INSERT INTO POI_EDIT_STATUS(PID) SELECT PID FROM IX_POI;
INSERT INTO POI_COLUMN_STATUS(ROW_ID) SELECT ROW_ID FROM IX_POI;
COMMIT;