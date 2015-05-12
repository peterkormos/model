CREATE TABLE MAK_PICTURES (
	MODEL_ID numeric(6) not null,
	PHOTO  MEDIUMBLOB not null
);

alter table MAK_CATEGORY add MASTER numeric(1);
alter table MAK_CATEGORY add MODEL_CLASS varchar(100);
alter table MAK_USERS add MODEL_CLASS varchar(100);

alter table MAK_CATEGORY_GROUP add category_language varchar(100);
update MAK_CATEGORY_GROUP set category_language='-';

alter table MAK_CATEGORY add category_language varchar(100);
update MAK_CATEGORY set category_language='-';

alter table mak_system modify param_value varchar(1000)