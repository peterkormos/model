drop table MAK_MODEL;
drop table MAK_CATEGORY;
drop table MAK_CATEGORY_GROUP;
drop table MAK_USERS;

create table MAK_USERS (
				USER_ID number(6) primary key, 
				USER_PASSWORD VARCHAR2(100) not null, 
				FIRST_NAME VARCHAR2(100) not null, 
				LAST_NAME VARCHAR2(100) not null, 
				USER_language VARCHAR2(100) not null, 
				COUNTRY VARCHAR2(100), 
				ADDRESS VARCHAR2(200), 
				TELEPHONE VARCHAR2(200), 
				EMAIL VARCHAR2(50) not null, 
				USER_ENABLED number(1) not null, 
				YEAR_OF_BIRTH number(4) not null,
				city VARCHAR2(100)
);


create table MAK_CATEGORY_GROUP (
				CATEGORY_group_ID number(6) primary key, 

				MODEL_SHOW VARCHAR2(100) not null, 
				CATEGORY_GROUP VARCHAR2(100) not null
);

create table MAK_CATEGORY (
				CATEGORY_ID number(6) primary key, 
				CATEGORY_GROUP_ID number(6)  not null, 
				CATEGORY_CODE VARCHAR2(200) not null, 
				CATEGORY_DESCRIPTION VARCHAR2(200) not null
);



create table MAK_MODEL (
				MODEL_ID number(6) not null, 
				USER_ID number(6) not null, 
				CATEGORY_ID number(6) not null, 
					
				MODEL_scale VARCHAR2(20) not null, 
				MODEL_name VARCHAR2(100) not null, 
				producer VARCHAR2(100) not null, 
				comments VARCHAR2(1000), 

identification VARCHAR2(100),
markings VARCHAR2(100),
gluedToBase number(1),

scratch_externalSurface number(1),
scratch_cockpit number(1),
scratch_engine number(1),
scratch_undercarriage number(1),
scratch_gearBay number(1),
scratch_armament number(1),
scratch_conversion number(1),

resin_externalSurface number(1),
resin_cockpit number(1),
resin_engine number(1),
resin_undercarriage number(1),
resin_gearBay number(1),
resin_armament number(1),
resin_conversion number(1),


photoEtched_externalSurface number(1),
photoEtched_cockpit number(1),
photoEtched_engine number(1),
photoEtched_undercarriage number(1),
photoEtched_gearBay number(1),
photoEtched_armament number(1),
photoEtched_conversion number(1),


documentation_externalSurface number(1),
documentation_cockpit number(1),
documentation_engine number(1),
documentation_undercarriage number(1),
documentation_gearBay number(1),
documentation_armament number(1),
documentation_conversion number(1)
);


create table MAK_SYSTEM (PARAM_NAME VARCHAR2(20) not null,
			PARAM_VALUE VARCHAR2(20) not null);

insert into MAK_SYSTEM ("PARAM_NAME", "PARAM_VALUE") values ('REGISTRATION', '1');
