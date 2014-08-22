alter table MAK_CATEGORY_GROUP add category_language varchar(100);
update MAK_CATEGORY_GROUP set category_language='-';

alter table MAK_CATEGORY add category_language varchar(100);
update MAK_CATEGORY set category_language='-';

alter table mak_system modify param_value varchar(1000)