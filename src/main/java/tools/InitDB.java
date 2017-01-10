package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitDB
{

  public InitDB() throws SQLException
  {
	final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	final String dbName = "makettDB";
	final String connectionURL = "jdbc:derby:" + dbName + ";create=true";

	final Connection conn = DriverManager.getConnection(connectionURL);

	final Statement s = conn.createStatement();
	s.execute(
	    "create table MAK_MODEL (MODEL_ID numeric(6) not null, USER_ID numeric(6) not null, CATEGORY_ID numeric(6) not null, MODEL_scale varchar(20) not null, MODEL_name varchar(100) not null, producer varchar(100) not null, comments varchar(1000), identification varchar(100), markings varchar(100), gluedToBase numeric(1), scratch_externalSurface numeric(1), scratch_cockpit numeric(1), scratch_engine numeric(1), scratch_undercarriage numeric(1), scratch_gearBay numeric(1), scratch_armament numeric(1), scratch_conversion numeric(1), resin_externalSurface numeric(1), resin_cockpit numeric(1), resin_engine numeric(1), resin_undercarriage numeric(1), resin_gearBay numeric(1), resin_armament numeric(1), resin_conversion numeric(1), photoEtched_externalSurface numeric(1), photoEtched_cockpit numeric(1), photoEtched_engine numeric(1), photoEtched_undercarriage numeric(1), photoEtched_gearBay numeric(1), photoEtched_armament numeric(1), photoEtched_conversion numeric(1), documentation_externalSurface numeric(1), documentation_cockpit numeric(1), documentation_engine numeric(1), documentation_undercarriage numeric(1), documentation_gearBay numeric(1), documentation_armament numeric(1), documentation_conversion numeric(1))");

	s.execute("create table MAK_SYSTEM (PARAM_NAME VARCHAR(20) not null, PARAM_VALUE VARCHAR(5000) not null)");
	s.execute("INSERT INTO MAK_SYSTEM ( PARAM_NAME, PARAM_VALUE ) VALUES ('ONSITEUSE','0')");
	s.execute("INSERT INTO MAK_SYSTEM ( PARAM_NAME, PARAM_VALUE ) VALUES ('REGISTRATION','1')");
	s.execute("INSERT INTO MAK_SYSTEM ( PARAM_NAME, PARAM_VALUE ) VALUES ('SYSTEMMESSAGE','-')");

	s.execute("CREATE TABLE MAK_PICTURES (MODEL_ID numeric(6) not null,PHOTO  BLOB not null)");

	s.execute("create table mak_awardedmodels(AWARD varchar(100),MODEL_ID numeric(6))");

	s.execute(
	    "create table MAK_CATEGORY (CATEGORY_ID numeric(6) primary key, CATEGORY_GROUP_ID numeric(6)  not null, CATEGORY_CODE varchar(200) not null, CATEGORY_DESCRIPTION varchar(200) not null, MASTER numeric(1),MODEL_CLASS varchar(100), AGEGROUP varchar(100))");

	s.execute(
	    "create table MAK_CATEGORY_GROUP (CATEGORY_group_ID numeric(6) primary key, MODEL_SHOW varchar(100) not null, CATEGORY_GROUP varchar(100) not null)");

	s.execute(
	    "create table MAK_USERS (USER_ID numeric(6) primary key, USER_PASSWORD varchar(100) not null, FIRST_NAME varchar(100) not null, LAST_NAME varchar(100) not null, USER_language varchar(100) not null, COUNTRY varchar(100), ADDRESS varchar(200), TELEPHONE varchar(200), EMAIL varchar(50) not null, USER_ENABLED numeric(1) not null, YEAR_OF_BIRTH numeric(4) not null,city varchar(100),MODEL_CLASS varchar(100))");

	s.execute(
	    "INSERT INTO MAK_USERS ( USER_ID, USER_PASSWORD, FIRST_NAME, LAST_NAME, USER_language, COUNTRY, ADDRESS, TELEPHONE, EMAIL, USER_ENABLED, YEAR_OF_BIRTH, city ) VALUES (1,'Xr4ilOzQ4PCOq3aQ0qbuaQ==','Peter','Kormos','ADMIN','Hungary','-','-','admin',1,1977,'-')");

	System.out.println("done..");
  }

  public static void main(String[] args) throws Exception
  {
	new InitDB();
  }

}
