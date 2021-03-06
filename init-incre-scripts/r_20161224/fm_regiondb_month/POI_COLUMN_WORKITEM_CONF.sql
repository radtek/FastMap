-- Create table
create table POI_COLUMN_WORKITEM_CONF
(
  ID               VARCHAR2(100) not null,
  FIRST_WORK_ITEM  VARCHAR2(50),
  SECOND_WORK_ITEM VARCHAR2(50),
  WORK_ITEM_ID     VARCHAR2(50),
  TYPE             NUMBER(1),
  CHECK_FLAG       NUMBER(1)
);
insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('2', 'poi_name', 'nameUnify', 'FM-A04-05', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('3', 'poi_name', 'nameUnify', 'FM-A04-08', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('4', 'poi_name', 'nameUnify', 'FM-A04-10', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('5', 'poi_name', 'nameUnify', 'FM-A04-21', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('6', 'poi_name', 'nameUnify', 'FM-A04-09', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('7', 'poi_name', 'shortName', 'FM-A07-01', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('8', 'poi_name', 'shortName', 'FM-A07-03', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('9', 'poi_name', 'shortName', 'FM-A07-02', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('10', 'poi_name', 'shortName', 'FM-A07-11', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('11', 'poi_name', 'shortName', 'FM-A07-12', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('12', 'poi_name', 'namePinyin', 'FM-A04-18', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('13', 'poi_address', 'addrSplit', 'FM-A09-01', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('14', 'poi_address', 'addrPinyin', 'FM-YW-20-026', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('15', 'poi_englishname', 'photoEngName', 'FM-YW-20-012', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('16', 'poi_englishname', 'chiEngName', 'FM-YW-20-013', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('17', 'poi_englishname', 'confirmEngName', 'FM-YW-20-014', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('18', 'poi_englishname', 'officalStandardEngName', 'FM-YW-20-053', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('19', 'poi_englishname', 'nonImportantLongEngName', 'FM-YW-20-052', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('20', 'poi_englishaddress', 'engMapAddress', 'FM-YW-20-018', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('21', 'poi_englishaddress', 'nonImportantLongEngAddress', 'FM-GLM60189', 1, 3);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('22', 'poi_name', 'aliasName', 'FM-M01-01', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('23', 'poi_englishname', 'netEngName', 'FM-M01-02', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('27', 'poi_name', '', 'FM-A07-02-01', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('24', 'poi_englishname', 'aliasOriEngName', 'FM-M01-03', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('25', 'poi_englishname', 'aliasStdEngName', 'FM-M01-04', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('26', 'poi_englishaddress', 'nonImportantEngAddress', 'FM-YW-20-017', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('28', 'poi_name', '', 'FM-14Sum-12-03', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('29', 'poi_name', '', 'FM-14Sum-12-08', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('30', 'poi_name', '', 'FM-A04-12', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('31', 'poi_name', '', 'FM-A04-13', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('32', 'poi_name', '', 'FM-CHR73001', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('33', 'poi_name', '', 'FM-GLM60154', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('34', 'poi_name', '', 'FM-GLM60211', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('35', 'poi_name', '', 'FM-A04-17', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('36', 'poi_name', '', 'FM-A04-19', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('37', 'poi_name', '', 'FM-A04-20', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('38', 'poi_name', '', 'FM-A07-14', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('39', 'poi_name', '', 'FM-GLM60254', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('40', 'poi_name', '', 'FM-GLM60304', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('41', 'poi_name', '', 'FM-GLM60407', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('42', 'poi_name', '', 'FM-YW-20-036', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('43', 'poi_name', '', 'FM-YW-20-058', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('44', 'poi_name', '', 'FM-YW-20-060', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('45', 'poi_name', '', 'FM-YW-20-146', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('46', 'poi_name', '', 'FM-YW-20-147', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('47', 'poi_name', '', 'FM-YW-20-148', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('48', 'poi_name', '', 'FM-YW-20-149', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('74', 'poi_address', '', 'FM-ZY-20-136', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('75', 'poi_address', '', 'FM-ZY-20-137', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('76', 'poi_address', '', 'FM-ZY-20-138', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('77', 'poi_address', '', 'FM-14Sum-06-03', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('78', 'poi_address', '', 'FM-A09-13', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('79', 'poi_address', '', 'FM-A09-14', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('80', 'poi_address', '', 'FM-A09-03', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('81', 'poi_address', '', 'FM-A09-04', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('82', 'poi_address', '', 'FM-A09-05', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('83', 'poi_address', '', 'FM-A09-06', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('84', 'poi_address', '', 'FM-A09-07', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('49', 'poi_address', '', 'FM-A09-08', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('50', 'poi_address', '', 'FM-A09-09', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('51', 'poi_address', '', 'FM-A09-10', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('52', 'poi_address', '', 'FM-A09-11', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('53', 'poi_address', '', 'FM-A09-12', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('54', 'poi_address', '', 'FM-A09-16', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('55', 'poi_address', '', 'FM-A09-17', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('56', 'poi_address', '', 'FM-CHR73003', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('57', 'poi_address', '', 'FM-CHR73002', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('58', 'poi_address', '', 'FM-YW-20-008', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('59', 'poi_address', '', 'FM-YW-20-009', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('60', 'poi_address', '', 'FM-GLM60302', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('61', 'poi_address', '', 'FM-YW-20-059', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('62', 'poi_address', '', 'FM-YW-20-061', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('63', 'poi_address', '', 'FM-YW-20-065', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('64', 'poi_address', '', 'FM-GLM60377', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('65', 'poi_address', '', 'FM-YW-20-038', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('66', 'poi_address', '', 'FM-YW-20-039', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('67', 'poi_address', '', 'FM-ZY-20-135', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('68', 'poi_address', '', 'FM-YW-20-078', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('69', 'poi_address', '', 'FM-YW-20-079', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('70', 'poi_address', '', 'FM-YW-20-080', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('71', 'poi_address', '', 'FM-YW-20-081', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('72', 'poi_address', '', 'FM-YW-20-082', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('73', 'poi_address', '', 'FM-YW-20-083', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('74', 'poi_englishname', '', 'FM-GLM60335', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('75', 'poi_englishname', '', 'FM-CHR71011', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('76', 'poi_englishname', '', 'FM-GLM60174', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('77', 'poi_englishname', '', 'FM-GLM60173', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('78', 'poi_englishname', '', 'FM-GLM60178', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('79', 'poi_englishname', '', 'FM-GLM60172', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('80', 'poi_englishname', '', 'FM-GLM60314', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('81', 'poi_englishname', '', 'FM-GLM60996', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('82', 'poi_englishname', '', 'FM-YW-20-028', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('83', 'poi_englishname', '', 'FM-YW-20-033', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('84', 'poi_englishname', '', 'FM-GLM60406', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('85', 'poi_englishname', '', 'FM-YW-20-050', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('86', 'poi_englishname', '', 'FM-YW-20-084', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('87', 'poi_englishname', '', 'FM-YW-20-085', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('88', 'poi_englishname', '', 'FM-YW-20-086', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('89', 'poi_englishname', '', 'FM-YW-20-087', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('90', 'poi_englishname', '', 'FM-YW-20-088', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('91', 'poi_englishname', '', 'FM-YW-20-090', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('92', 'poi_englishname', '', 'FM-YW-20-091', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('93', 'poi_englishname', '', 'FM-YW-20-092', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('94', 'poi_englishaddress', '', 'FM-CHR71040', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('95', 'poi_englishaddress', '', 'FM-GLM60190', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('96', 'poi_englishaddress', '', 'FM-YW-20-034', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('97', 'poi_englishaddress', '', 'FM-YW-20-029', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('98', 'poi_englishaddress', '', 'FM-YW-20-064', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('129', 'poi_deep', 'deepDetail', 'FM-DETAIL', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('100', 'poi_englishaddress', '', 'FM-YW-20-099', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('101', 'poi_englishaddress', '', 'FM-YW-20-100', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('102', 'poi_englishaddress', '', 'FM-YW-20-101', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('103', 'poi_englishaddress', '', 'FM-YW-20-110', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('104', 'poi_englishaddress', '', 'FM-YW-20-115', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('105', 'poi_englishaddress', '', 'FM-YW-20-117', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('106', 'poi_englishaddress', '', 'FM-YW-20-118', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('107', 'poi_englishaddress', '', 'FM-YW-20-212', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('1', 'poi_name', 'nameUnify', 'FM-A04-04', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('129', 'poi_deep', 'deepCarrental', 'FM-CARRENTAL', 1, 1);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('108', 'poi_deep', 'deepParking', 'FM-ZY-20-153', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('109', 'poi_deep', 'deepDetail', 'FM-YW-20-218', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('110', 'poi_deep', 'deepDetail', 'FM-ZY-20-237', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('111', 'poi_deep', 'deepDetail', 'FM-YW-20-219', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('112', 'poi_deep', 'deepDetail', 'FM-YW-20-220', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('113', 'poi_deep', 'deepDetail', 'FM-YW-20-221', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('114', 'poi_deep', 'deepParking', 'FM-ZY-20-155', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('115', 'poi_deep', 'deepParking', 'FM-YW-20-225', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('116', 'poi_deep', 'deepCarrental', 'FM-ZY-20-198', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('117', 'poi_deep', 'deepCarrental', 'FM-ZY-20-199', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('118', 'poi_deep', 'deepCarrental', 'FM-ZY-20-238', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('119', 'poi_deep', 'deepParking', 'FM-YW-20-227', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('120', 'poi_deep', 'deepParking', 'FM-YW-20-235', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('121', 'poi_deep', 'deepDetail', 'FM-YW-20-222', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('122', 'poi_deep', 'deepParking', 'FM-YW-20-224', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('123', 'poi_deep', 'deepParking', 'FM-ZY-20-149', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('124', 'poi_deep', 'deepParking', 'FM-ZY-20-151', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('125', 'poi_deep', 'deepParking', 'FM-ZY-20-152', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('126', 'poi_deep', 'deepParking', 'FM-ZY-20-154', 1, 2);

insert into POI_COLUMN_WORKITEM_CONF (ID, FIRST_WORK_ITEM, SECOND_WORK_ITEM, WORK_ITEM_ID, TYPE, CHECK_FLAG)
values ('128', 'poi_deep', 'deepParking', 'FM-PARKING', 1, 1);
commit;

exit;