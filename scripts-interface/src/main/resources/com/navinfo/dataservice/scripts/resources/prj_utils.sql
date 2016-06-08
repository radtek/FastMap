create global temporary table TMP_RESTRICT
(
  link_pid   INTEGER,
  s_node_pid INTEGER,
  e_node_pid INTEGER,
  direct     INTEGER
)
on commit delete rows;

create global temporary table TMP_RESTRICT2
(
  link_pid   INTEGER,
  s_node_pid INTEGER,
  e_node_pid INTEGER,
  direct     INTEGER,
  via_path   VARCHAR2(250)
)
on commit delete rows;
--RD_LINK
insert into user_sdo_geom_metadata
values
  ('RD_LINK',
   'GEOMETRY',
   mdsys.sdo_dim_array(SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       SDO_DIM_ELEMENT('XLAT', -90, 90, 0.005)),
   8307);

create index idx_rd_link_geometry
on rd_link(geometry)
indextype is mdsys.spatial_index;   
--RD_NODE
insert into user_sdo_geom_metadata
values
  ('RD_NODE',
   'GEOMETRY',
   mdsys.sdo_dim_array(SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       SDO_DIM_ELEMENT('XLAT', -90, 90, 0.005)),
   8307);
   
create index idx_rd_node_geometry
on rd_node(geometry)
indextype is mdsys.spatial_index;  
--AD_NODE
insert into user_sdo_geom_metadata
values
  ('AD_NODE',
   'GEOMETRY',
   mdsys.sdo_dim_array(SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       SDO_DIM_ELEMENT('XLAT', -90, 90, 0.005)),
   8307);
   
create index idx_ad_node_geometry
on ad_node(geometry)
indextype is mdsys.spatial_index; 

analyze table ad_node compute statistics;
--AD_FACE
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('AD_FACE',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_ad_face on ad_face(geometry) 
indextype is mdsys.spatial_index;

analyze table ad_face compute statistics;
--AD_LINK
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('AD_LINK',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_ad_link on ad_link(geometry) 
indextype is mdsys.spatial_index;

analyze table ad_link compute statistics;
--RW_LINK
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('RW_LINK',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_rw_link on rw_link(geometry) 
indextype is mdsys.spatial_index;

analyze table rw_link compute statistics;

--RW_NODE
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('RW_NODE',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_rw_link on rw_node(geometry) 
indextype is mdsys.spatial_index;

analyze table rw_node compute statistics;

--RD_GSC
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('RD_GSC',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_rd_gsc on rd_gsc(geometry) 
indextype is mdsys.spatial_index;

analyze table rd_gsc compute statistics;
--AD_ADMIN
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('AD_ADMIN',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_ad_admin on ad_admin(geometry) 
indextype is mdsys.spatial_index;

analyze table ad_admin compute statistics;

--IX_POI
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('IX_POI',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_ix_poi on ix_poi(geometry) 
indextype is mdsys.spatial_index;

analyze table ix_poi compute statistics;

--ZONE_FACE
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('ZONE_FACE',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_zone_face on zone_face(geometry)
indextype is mdsys.spatial_index;

analyze table zone_face compute statistics;

--ZONE_LINK
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('ZONE_LINK',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_zone_link on zone_link(geometry) 
indextype is mdsys.spatial_index;

analyze table zone_link compute statistics;

--ZONE_NODE
insert into user_sdo_geom_metadata
  (table_name, COLUMN_NAME, DIMINFO, SRID)
values
  ('ZONE_NODE',
   'GEOMETRY',
   MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('XLONG', -180, 180, 0.005),
                       MDSYS.SDO_DIM_ELEMENT('YLAT', -90, 90, 0.005)),
   8307);

create index idx_sdo_zone_node on zone_node(geometry) 
indextype is mdsys.spatial_index;

analyze table zone_node compute statistics;